package com.shuishou.cloudmember.autobackup;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.shuishou.cloudmember.ConstantValue;
import com.shuishou.cloudmember.customer.models.Customer;
import com.shuishou.cloudmember.customer.models.ICustomerDataAccessor;
import com.shuishou.cloudmember.customer.services.ICustomerService;

/**
 * Tomcat 启动时备份一次完整数据库, 然后设定每小时备份一次重要数据, 比如会员; 每天备份一次整库;
 * 比较已备份的数据, 超过某一定时限的备份数据自动删除. 目前保留两周数据
 * 备份数据自动压缩.
 * @author Administrator
 *
 */
@Component
public class AutoBackupDB implements InitializingBean{
	private final static Logger logger = LoggerFactory.getLogger(AutoBackupDB.class);
	private int memberTimerDelay = 10 * 1000;
	private int memberTimerRepeat = 60 * 60 * 1000;
	private int logKeepDays = 15;
	
	private String configFile = "/server_config.properties";
	
	public static Properties prop = new Properties();
	private String mysqlDirectory;
	private String username = "root";
	private String password = "root";
	
	@Autowired
	private ICustomerService customerService;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		//启动定时器, 定期备份会员数据
		Timer timerMember = new Timer();
		timerMember.schedule(new TimerTask(){
			@Override
			public void run() {
				doBackup();
			}
		}, memberTimerDelay, memberTimerRepeat);
	}
	
	private void doBackup(){
		String osname = System.getProperty("os.name");
		
		String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		if (osname.toLowerCase().startsWith("windows")){
			path = path.substring(1);//remove the first char '/'
		}
		final String dbdirPath = path + "../../../../" + ConstantValue.CATEGORY_BACKUPDB;//数据库备份目录在Tomcat 目录下
		File dbdir = new File(dbdirPath);
		if (!dbdir.exists()){
			dbdir.mkdirs();
		}
		
		//检查旧的备份文件, 删除两周以前的
		deleteOldFile(dbdir);
		
		readConfig();
		
		List<Customer> customers = customerService.getAllCustomer();
		if (customers == null || customers.isEmpty())
			return;
		
		mysqlDirectory = prop.getProperty("MySQLDirectory") + "\\bin";
		
		String dumpCmd = null;
		
		if (osname.toLowerCase().startsWith("windows")){
			dumpCmd = "cmd.exe /c " + mysqlDirectory + "\\mysqldump";
		} else if (osname.toLowerCase().startsWith("mac")) {
			dumpCmd = "mysqldump";
		} else if (osname.toLowerCase().startsWith("linux")){
			dumpCmd = "mysqldump";
		}
		final String dumpCommand = dumpCmd;
		String dumpParam = " -u"+username+" -p"+password+" cloudmember";
		for(Customer c : customers){
			dumpParam += " member_" + c.getName();
		}
		
		//dump member data
		String dbfile = dbdirPath + "/" +ConstantValue.DFYMDHMS_2.format(new Date()) + ".sql";
		String dump = dumpCommand + dumpParam + " > " + dbfile; 
		logger.debug("backup member data : "+ dump);
		Runtime runtime = Runtime.getRuntime();
		try {
			if (osname.toLowerCase().startsWith("windows")){
				runtime.exec(dump);
			} else if (osname.toLowerCase().startsWith("mac")) {
				runtime.exec(new String[]{"/bin/sh", "-c", dump});
			} else if (osname.toLowerCase().startsWith("linux")){
				runtime.exec(new String[]{"/bin/sh", "-c", dump});
			}
			
		} catch (IOException e) {
			logger.error("", e);
		} 
	}
	
	private void readConfig(){
		InputStream input = null;
		try {
			input = this.getClass().getClassLoader().getResourceAsStream(configFile);
			prop.load(input);
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//删除过老的文件
	private void deleteOldFile(File directory){
		File[] dbfiles = directory.listFiles();
		if (dbfiles != null && dbfiles.length > 0){
			for (File file : dbfiles){
				String filename = file.getName();
				//file name like 20180514225150.sql
				String timename = filename.split("\\.")[0];
				try {
					Date filetime = ConstantValue.DFYMDHMS_2.parse(timename);
					if ((new Date().getTime() - filetime.getTime()) / (24*60*60*1000) > logKeepDays){
						file.delete();
					}
				} catch (ParseException e) {
					logger.error("", e);
				}
			}
		}
	}
}
