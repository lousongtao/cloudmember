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
	
	public static final String DATABASE = "cloudmember";
	public static final String CATEGORY_BACKUPDB = "backupDB";
	
	public static final String PERMISSION_QUERY_USER = "QUERY_USER";
	public static final String PERMISSION_CREATE_USER = "CREATE_USER";
	public static final String PERMISSION_ADD_CUSTOMER = "ADD_CUSTOMER";
	public static final String PERMISSION_STOP_CUSTOMER = "STOP_CUSTOMER";
	
	public final static String SPLITTAG_PERMISSION = ";";
	
	public static final int CUSTOMER_STATUS_NORMAL = 1;
	public static final int CUSTOMER_STATUS_STOPPED = 2;
	
	public static final int MEMBERSCORE_CONSUM = 1;//积分类型-消费
	public static final int MEMBERSCORE_REFUND = 2;//积分类型-退货
	public static final int MEMBERSCORE_ADJUST = 3;//积分类型-调整
	public static final int MEMBERDEPOSIT_CONSUM = 1;//消费余额类型-消费
	public static final int MEMBERDEPOSIT_REFUND = 2;//消费余额类型-退款
	public static final int MEMBERDEPOSIT_RECHARGE = 3;//消费余额类型-充值
	public static final int MEMBERDEPOSIT_ADJUST = 4;//消费余额类型-调整
	
	public static final String MEMBERBALANCE_QUERYTYPE_CONSUME = "CONSUME";
	public static final String MEMBERBALANCE_QUERYTYPE_RECHARGE = "RECHARGE";
	public static final String MEMBERBALANCE_QUERYTYPE_ADJUST = "ADJUST";

	public static final int CUSTOMERLICENSE_VALIDATEHISTORY_SUCCESS = 1; //license 验证成功
    public static final int CUSTOMERLICENSE_VALIDATEHISTORY_WRONGKEY = 2; //license 验证, key 错误
    public static final int CUSTOMERLICENSE_VALIDATEHISTORY_OTHEREXCEPTION = 3;//license 验证, 其他异常
	
}
