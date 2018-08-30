package com.shuishou.cloudmember.common.services;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.springframework.stereotype.Service;

import com.shuishou.cloudmember.ConstantValue;
import com.shuishou.cloudmember.views.ObjectResult;
import com.shuishou.cloudmember.views.Result;

@Service
public class CommonService implements ICommonService{

	@Override
	public ObjectResult getLastBackupFileName() {
		String osname = System.getProperty("os.name");
		
		String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		if (osname.toLowerCase().startsWith("windows")){
			path = path.substring(1);//remove the first char '/'
		}
		final String dbdirPath = path + "../../../" + ConstantValue.CATEGORY_BACKUPDB;//数据库备份目录在Tomcat 目录下
		File dbdir = new File(dbdirPath);
		File[] dbfiles = dbdir.listFiles();
		if (dbfiles != null){
			ArrayList<String> filenames = new ArrayList<>();
			for (int i = 0; i < dbfiles.length; i++) {
				filenames.add(dbfiles[i].getName());
			}
			Collections.sort(filenames, new Comparator<String>(){

				@Override
				public int compare(String o1, String o2) {
					return o2.compareTo(o1);
				}});
			for (int i = 0; i < filenames.size(); i++) {
				if (filenames.get(i).indexOf("Member") > 0)
					return new ObjectResult(Result.OK, true, filenames.get(i));
			}
		}
		return new ObjectResult(Result.OK, true, null);
	}

	@Override
	public ObjectResult getLastWholeBackupFileName() {
		String osname = System.getProperty("os.name");
		
		String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		if (osname.toLowerCase().startsWith("windows")){
			path = path.substring(1);//remove the first char '/'
		}
		final String dbdirPath = path + "../../../" + ConstantValue.CATEGORY_BACKUPDB;//数据库备份目录在Tomcat 目录下
		File dbdir = new File(dbdirPath);
		File[] dbfiles = dbdir.listFiles();
		if (dbfiles != null){
			ArrayList<String> filenames = new ArrayList<>();
			for (int i = 0; i < dbfiles.length; i++) {
				filenames.add(dbfiles[i].getName());
			}
			Collections.sort(filenames, new Comparator<String>(){

				@Override
				public int compare(String o1, String o2) {
					return o2.compareTo(o1);
				}});
			for (int i = 0; i < filenames.size(); i++) {
				if (filenames.get(i).indexOf("Whole") > 0)
					return new ObjectResult(Result.OK, true, filenames.get(i));
			}
		}
		return new ObjectResult(Result.OK, true, null);
	}

}
