package com.shuishou.cloudmember;

import java.util.ArrayList;

import org.hibernate.EmptyInterceptor;

public class MemberInterceptor extends EmptyInterceptor {
	
	/**
	 * 在操作一个表数据时, hibernate可能会查询多个表的数据, 如查询MemberScore时, 同时会查询Member
	 * 所以不能单单替换MemberScore, 同时还要将Member替换掉.
	 * replaceTablePair里每个元素都是一个替换表对, 其第一个元素为原始表名, 第二个元素为目标表名.
	 */
	private ArrayList<String[]> replaceTablePair = new ArrayList<>();
	
	public void addReplacePair(String[] pair){
		replaceTablePair.add(pair);
	}
	
	public void clearReplacePair(){
		replaceTablePair.clear();
	}
	
	/**
	 * 替换sql语句中包含的className为targetName, 切记只可以替换from 子句中的表名, 
	 * 对于select子句, where子句, order子句 中出现的className不能替换, 否则会导致sql语句错误;
	 * 1. 把sql中包含的hql注释部分剔除
	 * 1. 找到前后带空格的from, 即" from "
	 * 2. 向后查找" where " 或 " order by " 或 " having ", 第一个发现的就是 from 子句结束的位置
	 * 3. 替换from子句中的内容
	 * 5. 拼接各段子句, 并返回
	 */
	@Override
	public String onPrepareStatement(String sql){
		String hql = "";
		if (sql.indexOf("*/") > 0){
			hql = sql.substring(0, sql.indexOf("*/") + 2);
			sql = sql.substring(sql.indexOf("*/") + 2);
		}
		if (sql.indexOf("select ") >= 0){
			return hql + replaceSelectSQL(sql);
		} else if (sql.indexOf("insert into ") >= 0){
			return hql + replaceInsertSQL(sql);
		} else if (sql.indexOf("update ") >= 0){
			return hql + replaceUpdateSQL(sql);
		} else if (sql.indexOf("delete") >= 0){
			return hql + replaceDeleteSQL(sql);
		}
		return "";
	}

	private String replaceInsertSQL(String sql){
		for (int i = 0; i < replaceTablePair.size(); i++) {
			String[] pair = replaceTablePair.get(i);
			String className = pair[0];
			String targetName = pair[1];
			if (sql.indexOf("insert into " + className + " ") >= 0){
				return sql.replaceFirst("insert into " + className + " ", "insert into " + targetName + " ");
			}
		}
		return "";
	}
	
	private String replaceUpdateSQL(String sql){
		for (int i = 0; i < replaceTablePair.size(); i++) {
			String[] pair = replaceTablePair.get(i);
			String className = pair[0];
			String targetName = pair[1];
			if (sql.indexOf("update " + className + " ") >= 0){
				return sql.replaceFirst("update " + className + " ", "update " + targetName + " ");
			}
		}
		return "";
	}
	
	private String replaceDeleteSQL(String sql){
		for (int i = 0; i < replaceTablePair.size(); i++) {
			String[] pair = replaceTablePair.get(i);
			String className = pair[0];
			String targetName = pair[1];
			if (sql.indexOf("delete " + className + " ") >= 0){
				return sql.replaceFirst("delete " + className + " ", "delete " + targetName + " ");
			}
		}
		return "";
	}
	
	private String replaceSelectSQL(String sql){
		int fromStart = sql.toLowerCase().indexOf(" from ");
		if (fromStart <= 0)
			return sql;
		int fromEnd = sql.length();
		int whereStart = sql.toLowerCase().indexOf(" where ");
		int orderbyStart = sql.toLowerCase().indexOf(" order by ");
		int havingStart = sql.toLowerCase().indexOf(" having ");
		if (whereStart > 0 && whereStart < fromEnd)
			fromEnd = whereStart;
		if (orderbyStart > 0 && orderbyStart < fromEnd)
			fromEnd = orderbyStart;
		if (havingStart > 0 && havingStart < fromEnd)
			fromEnd = havingStart;
		String fromClause = sql.substring(fromStart, fromEnd);
		fromClause = fromClause.substring(fromClause.indexOf("from ") + 5);//remove "from" keyword
		String[] tables = fromClause.split(",");//seperate every tables, the format will be as "member member0_", which first part is the tablename and the second one is the alias
		for (int i = 0; i < replaceTablePair.size(); i++) {
			String[] pair = replaceTablePair.get(i);
			String className = pair[0];
			String targetName = pair[1];
			for (int j = 0; j < tables.length; j++) {
				String tablepart = tables[j];
				tablepart = tablepart.trim();
				String[] tabpair = tablepart.split(" ");
				if (tabpair[0].toLowerCase().equals(className.toLowerCase())){
					tables[j] = tablepart.toLowerCase().replaceFirst(className.toLowerCase(), targetName.toLowerCase());//only replace the first one, because the second one, if exist, maybe is the alias
				}
			}
			
		}
		fromClause = " from ";
		for (int i = 0; i < tables.length; i++) {
			if (i != 0)
				fromClause += ", ";
			fromClause += tables[i];
		}
		return sql.substring(0, fromStart) + fromClause + sql.substring(fromEnd);
	}

}
