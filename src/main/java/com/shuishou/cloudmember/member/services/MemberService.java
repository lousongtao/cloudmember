package com.shuishou.cloudmember.member.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shuishou.cloudmember.ConstantValue;
import com.shuishou.cloudmember.DataCheckException;
import com.shuishou.cloudmember.member.models.Member;
import com.shuishou.cloudmember.member.views.MemberInfo;
import com.shuishou.cloudmember.member.views.MemberListResult;
import com.shuishou.cloudmember.member.views.MemberResult;
import com.shuishou.cloudmember.views.ObjectListResult;
import com.shuishou.cloudmember.views.Result;

@Service
public class MemberService implements IMemberService{

	private Logger log = Logger.getLogger("MemberService");
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public MemberResult addMember(String customerName, String name, String memberCard, String address, String postCode,
			String telephone, Date birth, double discountRate) {
		MemberInfo m = new MemberInfo();
		m.name = name;
		m.memberCard = memberCard;
		m.address=address;
		m.postCode=postCode;
		m.telephone=telephone;
		m.birth = birth == null ? "" :ConstantValue.DFYMD.format(birth);
		m.discountRate =discountRate;
		m.createTime = ConstantValue.DFYMD.format(new Date());
		
		String sql_field = "insert into member_" + customerName + "(name, memberCard, discountRate, createTime";
		String sql_value = " values ( '" + name + "', '" + memberCard + "'," + discountRate + ", ?";
		if (address != null && address.length() > 0){
			sql_field += ", address";
			sql_value += ", '" + address + "'";
		}
		if (postCode != null && postCode.length() > 0){
			sql_field += ", postCode";
			sql_value += ", '" + postCode + "'";
		}
		if (telephone != null && telephone.length() > 0){
			sql_field += ", telephone";
			sql_value += ", '" + telephone + "'";
		}
		if (birth != null){
			sql_field += ", birth";
			sql_value += ", ?";
		}
		sql_field += ") ";
		sql_value += ");";
		
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		SQLQuery query = session.createSQLQuery(sql_field + sql_value);
		query.setDate(0, new Date());
		if (birth != null){
			query.setDate(1, birth);
		}
		try{
			query.executeUpdate();
			tx.commit();
			int id = ((java.math.BigInteger) session.createSQLQuery("select LAST_INSERT_ID()").uniqueResult()).intValue();
			m.id = id;
		} catch(Exception e){
			tx.rollback();
			log.error("sql = " + sql_field + sql_value);
			log.error("", e);
			return new MemberResult(e.getMessage() + sql_field + sql_value, false);
		} finally{
			session.close();
		}
		
		log.debug("add member : " + name + " for customer " + customerName);
		return new MemberResult(Result.OK, true, m);
	}
	
	@Override
	public MemberListResult queryMember(String customerName, String name, String memberCard, String address, String postCode, String telephone) {
		String sql_field = "select id, address, balanceMoney, birth, createTime, discountRate, memberCard, name, postCode, score, telephone from member_" + customerName;
		String sql_where = " where ";
		if (name != null && name.length() > 0){
			if (sql_where.length() > 10)
				sql_where += " and ";
			sql_where += "name like '%" + name + "%'";
		}
		if (memberCard != null && memberCard.length() > 0){
			if (sql_where.length() > 10)
				sql_where += " and ";
			sql_where += " memberCard like '%" + memberCard + "%'";
		}
		if (address != null && address.length() > 0){
			if (sql_where.length() > 10)
				sql_where += " and ";
			sql_where += " address like '%" + address + "%'";
		}
		if (postCode != null && postCode.length() > 0){
			if (sql_where.length() > 10)
				sql_where += " and ";
			sql_where += " postCode = '%" + postCode + "%'";
		}
		if (telephone != null && telephone.length() > 0){
			if (sql_where.length() > 10)
				sql_where += " and ";
			sql_where += " telephone like '%" + telephone + "%'";
		}
		if (sql_where.length() > 10)
			sql_field += sql_where;
		
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		SQLQuery query = session.createSQLQuery(sql_field);
		query.addScalar("id", StandardBasicTypes.INTEGER);
		query.addScalar("address", StandardBasicTypes.STRING);
		query.addScalar("balanceMoney", StandardBasicTypes.DOUBLE);
		query.addScalar("birth", StandardBasicTypes.DATE);
		query.addScalar("createTime", StandardBasicTypes.DATE);
		query.addScalar("discountRate", StandardBasicTypes.DOUBLE);
		query.addScalar("memberCard", StandardBasicTypes.STRING);
		query.addScalar("name", StandardBasicTypes.STRING);
		query.addScalar("postCode", StandardBasicTypes.STRING);
		query.addScalar("score", StandardBasicTypes.DOUBLE);
		query.addScalar("telephone", StandardBasicTypes.STRING);
		try{
			List ms = query.list();
			ArrayList<MemberInfo> members = assembleMemberObject(ms);
			return new MemberListResult(Result.OK, true, members);
		} catch(Exception e){
			tx.rollback();
			log.error("sql = " + sql_field);
			log.error("", e);
			return new MemberListResult(e.getMessage() + sql_field, false, null);
		} finally{
			session.close();
		}
	}
	
	private ArrayList<MemberInfo> assembleMemberObject(List ms){
		ArrayList<MemberInfo> members = new ArrayList<>();
		for (int i = 0; i < ms.size(); i++) {
			Object[] os = (Object[]) ms.get(i);
			MemberInfo m = new MemberInfo();
			m.id = (int)os[0];
			m.address = (String)os[1];
			m.balanceMoney = (double)os[2];
			m.birth = os[3] == null ? "" :ConstantValue.DFYMD.format(os[3]);
			m.createTime = ConstantValue.DFYMD.format(os[4]);
			m.discountRate = (double)os[5];
			m.memberCard = (String)os[6];
			m.name = (String)os[7];
			m.postCode = (String)os[8];
			m.score = (double)os[9];
			m.telephone = (String)os[10];
			members.add(m);
		}
		return members;
	}
	
	@Override
	public MemberListResult queryAllMember(String customerName) {
		String sql_field = "select id, address, balanceMoney, birth, createTime, discountRate, memberCard, name, postCode, score, telephone from member_" + customerName;
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		SQLQuery query = session.createSQLQuery(sql_field);
		query.addScalar("id", StandardBasicTypes.INTEGER);
		query.addScalar("address", StandardBasicTypes.STRING);
		query.addScalar("balanceMoney", StandardBasicTypes.DOUBLE);
		query.addScalar("birth", StandardBasicTypes.DATE);
		query.addScalar("createTime", StandardBasicTypes.DATE);
		query.addScalar("discountRate", StandardBasicTypes.DOUBLE);
		query.addScalar("memberCard", StandardBasicTypes.STRING);
		query.addScalar("name", StandardBasicTypes.STRING);
		query.addScalar("postCode", StandardBasicTypes.STRING);
		query.addScalar("score", StandardBasicTypes.DOUBLE);
		query.addScalar("telephone", StandardBasicTypes.STRING);
		try{
			List ms = query.list();
			ArrayList<MemberInfo> members = assembleMemberObject(ms);
			return new MemberListResult(Result.OK, true, members);
		} catch(Exception e){
			tx.rollback();
			log.error("sql = " + sql_field);
			log.error("", e);
			return new MemberListResult(e.getMessage() + sql_field, false, null);
		} finally{
			session.close();
		}
	}
	
	private Member queryMemberBySql(String sql){
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		SQLQuery query = session.createSQLQuery(sql);
		query.addScalar("id", StandardBasicTypes.INTEGER);
		query.addScalar("address", StandardBasicTypes.STRING);
		query.addScalar("balanceMoney", StandardBasicTypes.DOUBLE);
		query.addScalar("birth", StandardBasicTypes.DATE);
		query.addScalar("createTime", StandardBasicTypes.DATE);
		query.addScalar("discountRate", StandardBasicTypes.DOUBLE);
		query.addScalar("memberCard", StandardBasicTypes.STRING);
		query.addScalar("name", StandardBasicTypes.STRING);
		query.addScalar("postCode", StandardBasicTypes.STRING);
		query.addScalar("score", StandardBasicTypes.DOUBLE);
		query.addScalar("telephone", StandardBasicTypes.STRING);
		try{
			Object[] os = (Object[]) query.uniqueResult();
			if (os == null || os.length == 0)
				return null;
			Member m = new Member();
			m.setId((int)os[0]);
			m.setAddress((String)os[1]);
			m.setBalanceMoney((double)os[2]);
			m.setBirth((Date)os[3]);
			m.setCreateTime((Date)os[4]);
			m.setDiscountRate((double)os[5]);
			m.setMemberCard((String)os[6]);
			m.setName((String)os[7]);
			m.setPostCode((String)os[8]);
			m.setScore((double)os[9]);
			m.setTelephone((String)os[10]);
			return m;
		} catch(Exception e){
			tx.rollback();
			log.error("sql = " + sql);
			log.error("", e);
			return null;
		} finally{
			session.close();
		}
	}

	@Override
	public MemberResult updateMember(String customerName, int id, String name, String memberCard, String address,
			String postCode, String telephone, Date birth, double discountRate) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sql = "update member_" + customerName + " set name = '" + name + "', memberCard='" + memberCard
				+ "', address = '" + address + "', postCode = '" + postCode + "', telephone='" + telephone
				+ "', discountRate=" + discountRate;
		if (birth != null) {
			sql += ", birth = ?";
		}
		sql += " where id = " + id;
		Member member = null;
		try{
			member = queryMemberBySql("select id, address, balanceMoney, birth, createTime, discountRate, memberCard, name, postCode, score, telephone from member_" + customerName +" where id = " + id);
			if (member == null)
				return new MemberResult("cannot find member by id "+ id, false, null);
			SQLQuery query = session.createSQLQuery(sql);
			if (birth != null){
				query.setDate(0, birth);
			}
			query.executeUpdate();
			tx.commit();
		} catch(Exception e){
			tx.rollback();
			log.error("sql = " + sql);
			log.error("", e);
			return new MemberResult(e.getMessage() + sql, false);
		} finally{
			session.close();
		}
		
		MemberInfo m = new MemberInfo();
		m.name = name;
		m.memberCard = memberCard;
		m.address=address;
		m.postCode=postCode;
		m.telephone=telephone;
		m.birth = member.getBirth() == null ? "" :ConstantValue.DFYMD.format(member.getBirth());
		m.discountRate =discountRate;
		m.createTime = ConstantValue.DFYMD.format(member.getCreateTime());
		m.id = id;
		m.score = member.getScore();
		m.balanceMoney = member.getBalanceMoney();
		
		log.debug(" update member info to name : " + name
				+ ", memberCard : " + memberCard + ", address : " + address + ", postCode : "+ postCode + ", telephone : "+ telephone
				+ ", birth" + (birth == null ? "": ConstantValue.DFYMD.format(birth)));
		return new MemberResult(Result.OK, true, m);
	}
	
	@Override
	public MemberResult updateMemberScore(String customerName, int id, double newScore) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sql = "update member_" + customerName + " set score = " + newScore + " where id = " + id;
		Member member;
		double oldScore=0;
		try{
			member = queryMemberBySql("select id, address, balanceMoney, birth, createTime, discountRate, memberCard, name, postCode, score, telephone from member_" + customerName +" where id = " + id);
			if (member == null)
				return new MemberResult("cannot find member by id "+ id, false, null);
			oldScore = member.getScore();
			SQLQuery query = session.createSQLQuery(sql);
			query.executeUpdate();
			tx.commit();
		} catch(Exception e){
			tx.rollback();
			log.error("sql = " + sql);
			log.error("", e);
			return new MemberResult(e.getMessage() + sql, false);
		} finally{
			session.close();
		}
		MemberInfo m = new MemberInfo();
		m.name = member.getName();
		m.memberCard = member.getMemberCard();
		m.address=member.getAddress();
		m.postCode=member.getAddress();
		m.telephone=member.getTelephone();
		m.birth = member.getBirth() == null ? "" :ConstantValue.DFYMD.format(member.getBirth());
		m.discountRate =member.getDiscountRate();
		m.createTime = ConstantValue.DFYMD.format(member.getCreateTime());
		m.id = id;
		m.score = newScore;
		m.balanceMoney = member.getBalanceMoney();
		
		log.debug(" update member score "+ oldScore +" to " + newScore);
		return new MemberResult(Result.OK, true, m);
	}
	
	@Override
	public MemberResult updateMemberBalance(String customerName, int id, double newBalance) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sql = "update member_" + customerName + " set balanceMoney = " + newBalance + " where id = " + id;
		Member member;
		double oldBalance=0;
		try{
			member = queryMemberBySql("select id, address, balanceMoney, birth, createTime, discountRate, memberCard, name, postCode, score, telephone from member_" + customerName +" where id = " + id);
			if (member == null)
				return new MemberResult("cannot find member by id "+ id, false, null);
			oldBalance = member.getBalanceMoney();
			SQLQuery query = session.createSQLQuery(sql);
			query.executeUpdate();
			tx.commit();
		} catch(Exception e){
			tx.rollback();
			log.error("sql = " + sql);
			log.error("", e);
			return new MemberResult(e.getMessage() + sql, false);
		} finally{
			session.close();
		}
		MemberInfo m = new MemberInfo();
		m.name = member.getName();
		m.memberCard = member.getMemberCard();
		m.address=member.getAddress();
		m.postCode=member.getAddress();
		m.telephone=member.getTelephone();
		m.birth = member.getBirth() == null ? "" :ConstantValue.DFYMD.format(member.getBirth());
		m.discountRate =member.getDiscountRate();
		m.createTime = ConstantValue.DFYMD.format(member.getCreateTime());
		m.id = id;
		m.score = member.getScore();
		m.balanceMoney = newBalance;
		
		log.debug("update member balance "+ oldBalance +" to " + newBalance);
		return new MemberResult(Result.OK, true, m);
	}
	
	@Override
	public MemberResult memberRecharge(String customerName, int id, double rechargeValue) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sql = "update member_" + customerName + " set balanceMoney = ? where id = " + id;
		Member member;
		double oldBalance=0;
		try{
			member = queryMemberBySql("select id, address, balanceMoney, birth, createTime, discountRate, memberCard, name, postCode, score, telephone from member_" + customerName +" where id = " + id);
			if (member == null)
				return new MemberResult("cannot find member by id "+ id, false, null);
			oldBalance = member.getBalanceMoney();
			
			SQLQuery query = session.createSQLQuery(sql);
			query.setDouble(0, oldBalance + rechargeValue);
			query.executeUpdate();
			tx.commit();
		} catch(Exception e){
			tx.rollback();
			log.error("sql = " + sql);
			log.error("", e);
			return new MemberResult(e.getMessage() + sql, false, null);
		} finally{
			session.close();
		}
		MemberInfo m = new MemberInfo();
		m.name = member.getName();
		m.memberCard = member.getMemberCard();
		m.address=member.getAddress();
		m.postCode=member.getAddress();
		m.telephone=member.getTelephone();
		m.birth = member.getBirth() == null ? "" :ConstantValue.DFYMD.format(member.getBirth());
		m.discountRate =member.getDiscountRate();
		m.createTime = ConstantValue.DFYMD.format(member.getCreateTime());
		m.id = id;
		m.score = member.getScore();
		m.balanceMoney = oldBalance + rechargeValue;
		
		log.debug("member recharge "+ oldBalance +" to " + (oldBalance + rechargeValue));
		return new MemberResult(Result.OK, true, m);
	}

	@Override
	public MemberResult deleteMember(String customerName, int id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sql = "delete from member_" + customerName + " where id = " + id;
		Member m;
		double oldBalance=0;
		try{
			m = queryMemberBySql("select id, address, balanceMoney, birth, createTime, discountRate, memberCard, name, postCode, score, telephone from member_" + customerName +" where id = " + id);
			if (m == null)
				return new MemberResult("cannot find member by id "+ id, false, null);
			
			SQLQuery query = session.createSQLQuery(sql);
			query.executeUpdate();
			tx.commit();
		} catch(Exception e){
			tx.rollback();
			log.error("sql = " + sql);
			log.error("", e);
			return new MemberResult(e.getMessage() + sql, false);
		} finally{
			session.close();
		}
		log.debug("delete member  : " + m.getName());
		return new MemberResult(Result.OK, true);
	}
	
	@Override
	public MemberResult recordMemberConsumption(String customerName, String memberCard, double consumptionPrice, boolean byScore, 
			double scorePerDollar, boolean byDeposit, String branchName) throws DataCheckException {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Member m = queryMemberBySql("select id, address, balanceMoney, birth, createTime, discountRate, memberCard, name, postCode, score, telephone from member_" + customerName +" where memberCard = '" + memberCard + "'");
		if (m == null)
			return new MemberResult("cannot find member by memberCard "+ memberCard, false, null);
		Date time = new Date();
		ArrayList<String> sqls = new ArrayList<>();
		if (byScore && scorePerDollar > 0){
			String sql = "insert into member_score_" + customerName + " (date, amount, place, type, member_id) values (?, " 
					+ String.format(ConstantValue.FORMAT_DOUBLE, consumptionPrice* scorePerDollar) 
					+ "'" + branchName+"', ";
			if (consumptionPrice > 0)
				sql += ConstantValue.MEMBERSCORE_CONSUM;
			else 
				sql += ConstantValue.MEMBERSCORE_REFUND;
			sql += ", "+ m.getId()+")";
			sqls.add(sql);
			sql = "update member_" + customerName + " set score = " + (m.getScore() + consumptionPrice * scorePerDollar) + " where id = " + m.getId();
			sqls.add(sql);
		}
		if (byDeposit){
			if (m.getBalanceMoney() < consumptionPrice){
				throw new DataCheckException("Meber's balance is not enought to pay");
			}
			String sql = "insert into member_consumption_" + customerName + " (date, amount, place, type, member_id) values (?, " 
					+ consumptionPrice 
					+ "'" + branchName+"', ";
			if (consumptionPrice > 0)
				sql += ConstantValue.MEMBERDEPOSIT_CONSUM;
			else 
				sql += ConstantValue.MEMBERDEPOSIT_REFUND;
			sql += ", "+ m.getId()+")";
			sqls.add(sql);
			sql = "update member_" + customerName + " set balanceMoney = " + (m.getBalanceMoney() - consumptionPrice * scorePerDollar) + " where id = " + m.getId();
			sqls.add(sql);
		}
		
		try{
			for (int i = 0; i < sqls.size(); i++) {
				SQLQuery query = session.createSQLQuery(sqls.get(i));
				query.setDate(0, time);
				query.executeUpdate();
			}
			
			tx.commit();
		} catch(Exception e){
			tx.rollback();
			log.error("sql = " + sqls);
			log.error("", e);
			return new MemberResult(e.getMessage() + sqls, false);
		} finally{
			session.close();
		}
		log.debug("record member consumption : " + sqls.toString());
		return new MemberResult(Result.OK, true);
	}

	@Override
	public MemberListResult queryPurchase(String customerName, int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MemberListResult queryRecharge(String customerName, int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MemberListResult queryScore(String customerName, int id) {
		// TODO Auto-generated method stub
		return null;
	}

}
