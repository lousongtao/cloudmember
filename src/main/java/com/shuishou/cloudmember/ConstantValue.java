package com.shuishou.cloudmember;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class ConstantValue {
	public static final DateFormat DFYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final DateFormat DFHMS = new SimpleDateFormat("HH:mm:ss");
	public static final DateFormat DFYMD = new SimpleDateFormat("yyyy-MM-dd");
	public static final DateFormat DFWEEK = new SimpleDateFormat("EEE");
	public static final DateFormat DFYMDHMS_2 = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public static final String DATE_PATTERN_YMD = "yyyy-MM-dd";
	public static final String DATE_PATTERN_YMDHMS = "yyyy-MM-dd HH:mm:ss";
	
	public static final String FORMAT_DOUBLE = "%.2f";
	
	public static final String PERMISSION_QUERY_USER = "QUERY_USER";
	public static final String PERMISSION_CREATE_USER = "CREATE_USER";
	public static final String PERMISSION_ADD_CUSTOMER = "ADD_CUSTOMER";
	public static final String PERMISSION_STOP_CUSTOMER = "STOP_CUSTOMER";
	
	public final static String SPLITTAG_PERMISSION = ";";
	
	public static final int CUSTOMER_STATUS_NORMAL = 1;
	public static final int CUSTOMER_STATUS_STOPPED = 2;
	
	public static final int MEMBERSCORE_CONSUM = 1;//积分类型
	public static final int MEMBERSCORE_REFUND = 2;//积分类型
	public static final int MEMBERDEPOSIT_CONSUM = 1;//消费余额类型
	public static final int MEMBERDEPOSIT_REFUND = 2;//消费余额类型
	
}
