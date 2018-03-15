package com.shuishou.cloudmember.member.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import org.springframework.transaction.annotation.Transactional;

import com.shuishou.cloudmember.ConstantValue;
//import com.shuishou.cloudmember.DataCheckException;
import com.shuishou.cloudmember.member.models.Member;
import com.shuishou.cloudmember.member.views.MemberBalanceInfo;
import com.shuishou.cloudmember.member.views.MemberInfo;
import com.shuishou.cloudmember.member.views.MemberListResult;
import com.shuishou.cloudmember.member.views.MemberResult;
import com.shuishou.cloudmember.member.views.MemberScoreInfo;
import com.shuishou.cloudmember.views.ObjectListResult;
import com.shuishou.cloudmember.views.Result;

@Service
public class MemberService implements IMemberService{

	private Logger log = Logger.getLogger("MemberService");
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public MemberResult addMember(String customerName, String name, String memberCard, String address, String postCode,
			String telephone, Date birth, double discountRate, String password) {
		long l1 = System.currentTimeMillis();
		MemberInfo m = new MemberInfo();
		m.name = name;
		m.memberCard = memberCard;
		m.address=address;
		m.postCode=postCode;
		m.telephone=telephone;
		m.birth = birth == null ? null :ConstantValue.DFYMD.format(birth);
		m.discountRate =discountRate;
		m.createTime = ConstantValue.DFYMDHMS.format(new Date());
		
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
		if (password != null && password.length() > 0){
			sql_field += ", password";
			sql_value += ", '"+password+"'";
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
		
		long l2 = System.currentTimeMillis();
		log.debug((l2 - l1) + "ms consume, add " + customerName + ".member : " + name + " for customer " + customerName);
		return new MemberResult(Result.OK, true, m);
	}
	
	@Override
	public MemberListResult queryMember(String customerName, String name, String memberCard, String address, String postCode, String telephone) {
		long l1 = System.currentTimeMillis();
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
		if (sql_where.length() < 10)
			sql_where = "";
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		SQLQuery query = generateMemberQuery(session, customerName, sql_where);
		try{
			List ms = query.list();
			ArrayList<MemberInfo> members = assembleMemberObject(ms);
			long l2 = System.currentTimeMillis();
			log.debug((l2 - l1) + "ms consume, query "+customerName+".member by name : " + name + ", memberCard : " + memberCard
					+ ", address : " + address + ", postCode : " + postCode + ", telephone : " + telephone);
			return new MemberListResult(Result.OK, true, members);
		} catch(Exception e){
			tx.rollback();
			log.error("sql_where = " + sql_where);
			log.error("", e);
			return new MemberListResult(e.getMessage() + sql_where, false, null);
		} finally{
			session.close();
		}
		
	}
	
	/**
	 * 生成Member的查询Query对象
	 * @return
	 */
	private SQLQuery generateMemberQuery(Session session, String customerName, String whereClause){
		String sql = "select id, address, balanceMoney, birth, createTime, discountRate, memberCard, name, postCode, score, telephone, password from member_" + customerName + " " + whereClause;
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
		query.addScalar("password", StandardBasicTypes.STRING);
		return query;
	}
	
	@Override
	public MemberListResult queryMemberHazily(String customerName, String key) {
		long l1 = System.currentTimeMillis();
		String whereClause = " where name like '%" + key + "%' or memberCard like '%" + key + "%' or telephone like '%" + key + "%'";
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		SQLQuery query = generateMemberQuery(session, customerName, whereClause);
		try{
			List ms = query.list();
			ArrayList<MemberInfo> members = assembleMemberObject(ms);
			long l2 = System.currentTimeMillis();
			log.debug((l2 - l1) + "ms consume, query "+customerName+".member by key : " + key);
			return new MemberListResult(Result.OK, true, members);
		} catch(Exception e){
			tx.rollback();
			log.error("sql = " + whereClause);
			log.error("", e);
			return new MemberListResult(e.getMessage() + whereClause, false, null);
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
			m.birth = os[3] == null ? null :ConstantValue.DFYMD.format(os[3]);
			m.createTime = ConstantValue.DFYMDHMS.format(os[4]);
			m.discountRate = (double)os[5];
			m.memberCard = (String)os[6];
			m.name = (String)os[7];
			m.postCode = (String)os[8];
			m.score = (double)os[9];
			m.telephone = (String)os[10];
			m.password = (String)os[11];
			members.add(m);
		}
		return members;
	}
	
	private Member assembleMemberObject(Object[] os){
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
		m.setPassword((String)os[11]);
		return m;
	}
	
	@Override
	public MemberListResult queryAllMember(String customerName) {
		long l1 = System.currentTimeMillis();
		String whereClause = "";
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		SQLQuery query = generateMemberQuery(session, customerName, whereClause);
		try{
			List ms = query.list();
			ArrayList<MemberInfo> members = assembleMemberObject(ms);
			long l2 = System.currentTimeMillis();
			log.debug((l2 - l1) + "ms consume, query all "+customerName+".member");
			return new MemberListResult(Result.OK, true, members);
		} catch(Exception e){
			tx.rollback();
			log.error("", e);
			return new MemberListResult(e.getMessage() , false, null);
		} finally{
			session.close();
		}
	}
	
	private Member queryMemberById(String customerName, int id){
		String whereClause = " where id = " + id;
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		SQLQuery query = generateMemberQuery(session, customerName, whereClause);
		try{
			Object[] os = (Object[]) query.uniqueResult();
			
			return assembleMemberObject(os);
		} catch(Exception e){
			tx.rollback();
			log.error("sql = " + whereClause);
			log.error("", e);
			return null;
		} finally{
			session.close();
		}
	}
	
	private Member queryMemberByCard(String customerName, String memberCard){
		String whereClause = " where memberCard = '" + memberCard + "'";
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		SQLQuery query = generateMemberQuery(session, customerName, whereClause);
		try{
			Object[] os = (Object[]) query.uniqueResult();
			return assembleMemberObject(os);
		} catch(Exception e){
			tx.rollback();
			log.error("sql = " + whereClause);
			log.error("", e);
			return null;
		} finally{
			session.close();
		}
	}

	@Override
	public MemberResult updateMember(String customerName, int id, String name, String memberCard, String address,
			String postCode, String telephone, Date birth, double discountRate) {
		long l1 = System.currentTimeMillis();
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
			member = queryMemberById(customerName, id);
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
		m.birth = birth == null ? null :ConstantValue.DFYMD.format(birth);
		m.discountRate =discountRate;
		m.createTime = ConstantValue.DFYMDHMS.format(member.getCreateTime());
		m.id = id;
		m.score = member.getScore();
		m.balanceMoney = member.getBalanceMoney();
		
		long l2 = System.currentTimeMillis();
		log.debug((l2 - l1) + " ms consume, update " + customerName + ".member info to name : " + name
				+ ", memberCard : " + memberCard + ", address : " + address + ", postCode : "+ postCode + ", telephone : "+ telephone
				+ ", birth" + (birth == null ? "": ConstantValue.DFYMD.format(birth)));
		return new MemberResult(Result.OK, true, m);
	}
	
	@Override
	public MemberResult updateMemberScore(String customerName, int id, double newScore) {
		long l1 = System.currentTimeMillis();
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sql = "update member_" + customerName + " set score = " + newScore + " where id = " + id;
		String sql_insertscore = "insert into member_score_" + customerName + " (amount, date, newValue, place, type, member_id) ";
		Member member;
		double oldScore=0;
		try{
			member = queryMemberById(customerName, id);
			if (member == null)
				return new MemberResult("cannot find member by id "+ id, false, null);
			oldScore = member.getScore();
			sql_insertscore += " values (" + (newScore - oldScore)
					+ ", ?"
					+ ", " + newScore
					+ ", ''"
					+ ", " +ConstantValue.MEMBERSCORE_ADJUST
					+ ", " + id + ")";
			SQLQuery query = session.createSQLQuery(sql);
			query.executeUpdate();
			query = session.createSQLQuery(sql_insertscore);
			query.setDate(0, new Date());
			query.executeUpdate();
			tx.commit();
		} catch(Exception e){
			tx.rollback();
			log.error("sql = " + sql + "; " + sql_insertscore);
			log.error("", e);
			return new MemberResult(e.getMessage() + sql + "; " + sql_insertscore, false);
		} finally{
			session.close();
		}
		MemberInfo m = new MemberInfo();
		m.name = member.getName();
		m.memberCard = member.getMemberCard();
		m.address=member.getAddress();
		m.postCode=member.getAddress();
		m.telephone=member.getTelephone();
		m.birth = member.getBirth() == null ? null :ConstantValue.DFYMD.format(member.getBirth());
		m.discountRate =member.getDiscountRate();
		m.createTime = ConstantValue.DFYMDHMS.format(member.getCreateTime());
		m.id = id;
		m.score = newScore;
		m.balanceMoney = member.getBalanceMoney();
		
		long l2 = System.currentTimeMillis();
		log.debug((l2 - l1) + "ms consume, update " + customerName + ".member." + id + " score from "+ oldScore +" to " + newScore);
		return new MemberResult(Result.OK, true, m);
	}
	
	@Override
	public MemberResult updateMemberBalance(String customerName, int id, double newBalance) {
		long l1 = System.currentTimeMillis();
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sql = "update member_" + customerName + " set balanceMoney = " + newBalance + " where id = " + id;
		String sql_insertbalance = "insert into member_balance_" + customerName + " (amount, date, newValue, place, type, member_id) ";
		Member member;
		double oldBalance=0;
		try{
			member = queryMemberById(customerName, id);
			if (member == null)
				return new MemberResult("cannot find member by id "+ id, false, null);
			oldBalance = member.getBalanceMoney();
			sql_insertbalance += "values (" + (newBalance - oldBalance) 
					+ ", ?"
					+ ", " + newBalance
					+ ", ''" 
					+ ", " + ConstantValue.MEMBERDEPOSIT_ADJUST
					+ ", " + id + ")";
			SQLQuery query = session.createSQLQuery(sql);
			query.executeUpdate();
			query = session.createSQLQuery(sql_insertbalance);
			query.setDate(0, new Date());
			query.executeUpdate();
			tx.commit();
		} catch(Exception e){
			tx.rollback();
			log.error("sql = " + sql + "; " + sql_insertbalance);
			log.error("", e);
			return new MemberResult(e.getMessage() + sql + "; " + sql_insertbalance, false);
		} finally{
			session.close();
		}
		MemberInfo m = new MemberInfo();
		m.name = member.getName();
		m.memberCard = member.getMemberCard();
		m.address=member.getAddress();
		m.postCode=member.getAddress();
		m.telephone=member.getTelephone();
		m.birth = member.getBirth() == null ? null :ConstantValue.DFYMD.format(member.getBirth());
		m.discountRate =member.getDiscountRate();
		m.createTime = ConstantValue.DFYMDHMS.format(member.getCreateTime());
		m.id = id;
		m.score = member.getScore();
		m.balanceMoney = newBalance;
		
		long l2 = System.currentTimeMillis();
		log.debug((l2 - l1)+"ms consume, update " + customerName +".member." + id + " balance from "+ oldBalance +" to " + newBalance);
		return new MemberResult(Result.OK, true, m);
	}
	
	@Override
	public MemberResult memberRecharge(String customerName, int id, double rechargeValue) {
		long l1 = System.currentTimeMillis();
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sql = "update member_" + customerName + " set balanceMoney = ? where id = " + id;
		String sql_insertbalance = "insert into member_balance_" + customerName + " (amount, date, newValue, place, type, member_id) ";
		Member member;
		double oldBalance=0;
		try{
			member = queryMemberById(customerName, id);
			if (member == null)
				return new MemberResult("cannot find member by id "+ id, false, null);
			oldBalance = member.getBalanceMoney();
			sql_insertbalance += "values (" + rechargeValue
					+ ", ?"
					+ ", " + (rechargeValue + oldBalance)
					+ ", ''" 
					+ ", " + ConstantValue.MEMBERDEPOSIT_RECHARGE
					+ ", " + id + ")";
			SQLQuery query = session.createSQLQuery(sql);
			query.setDouble(0, oldBalance + rechargeValue);
			query.executeUpdate();
			query = session.createSQLQuery(sql_insertbalance);
			query.setDate(0, new Date());
			query.executeUpdate();
			tx.commit();
		} catch(Exception e){
			tx.rollback();
			log.error("sql = " + sql + "; " + sql_insertbalance);
			log.error("", e);
			return new MemberResult(e.getMessage() + sql + "; " + sql_insertbalance, false, null);
		} finally{
			session.close();
		}
		MemberInfo m = new MemberInfo();
		m.name = member.getName();
		m.memberCard = member.getMemberCard();
		m.address=member.getAddress();
		m.postCode=member.getAddress();
		m.telephone=member.getTelephone();
		m.birth = member.getBirth() == null ? null :ConstantValue.DFYMD.format(member.getBirth());
		m.discountRate =member.getDiscountRate();
		m.createTime = ConstantValue.DFYMDHMS.format(member.getCreateTime());
		m.id = id;
		m.score = member.getScore();
		m.balanceMoney = oldBalance + rechargeValue;
		long l2 = System.currentTimeMillis();
		log.debug((l2 - l1) + "ms consume, do " + customerName + ".member." + id + " recharge "+ rechargeValue +", new balance is " + (oldBalance + rechargeValue));
		return new MemberResult(Result.OK, true, m);
	}

	@Override
	public MemberResult deleteMember(String customerName, int id) {
		long l1 = System.currentTimeMillis();
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sql = "delete from member_" + customerName + " where id = " + id;
		Member m;
		double oldBalance=0;
		try{
			m = queryMemberById(customerName, id);
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
		long l2 = System.currentTimeMillis();
		log.debug((l2 - l1) + "ms consume, delete "+customerName +".member by id : " + id + ",name : " + m.getName());
		return new MemberResult(Result.OK, true);
	}
	
	/**
	 * @param memberPassword 如果该参数为空, 就不再比较密码
	 */
	@Override
	public MemberResult recordMemberConsumption(String customerName, String memberCard, String memberPassword, double consumptionPrice, boolean byScore, 
			double scorePerDollar, boolean byDeposit, String branchName) {
		long l1 = System.currentTimeMillis();
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Member m = queryMemberByCard(customerName, memberCard);
		if (m == null)
			return new MemberResult("cannot find member by memberCard "+ memberCard, false, null);
		if (memberPassword != null && !memberPassword.equals(m.getPassword())){
			return new MemberResult("password is wrong", false, null);
		}
		Date time = new Date();
//		ArrayList<String> sqls = new ArrayList<>();
		String sql = null;
		
		try{
			SQLQuery query = null;
			if (byScore && scorePerDollar > 0){
				sql = "insert into member_score_" + customerName + " (date, amount, place, type, newValue, member_id) values (?, " 
						+ String.format(ConstantValue.FORMAT_DOUBLE, consumptionPrice* scorePerDollar) 
						+ ", '" + branchName+"', ";
				if (consumptionPrice > 0)
					sql += ConstantValue.MEMBERSCORE_CONSUM;
				else 
					sql += ConstantValue.MEMBERSCORE_REFUND;
				sql += ", " + String.format(ConstantValue.FORMAT_DOUBLE, consumptionPrice* scorePerDollar + m.getScore());
				sql += ", "+ m.getId()+")";
				query = session.createSQLQuery(sql);
				query.setDate(0, time);
				query.executeUpdate();
				
				sql = "update member_" + customerName + " set score = " + String.format(ConstantValue.FORMAT_DOUBLE, m.getScore() + consumptionPrice * scorePerDollar) + " where id = " + m.getId();
				query = session.createSQLQuery(sql);
				query.executeUpdate();
			}
			if (byDeposit){
				if (m.getBalanceMoney() < consumptionPrice){
					return new MemberResult("Meber's balance is not enought to pay", false, null);
				}
				sql = "insert into member_balance_" + customerName + " (date, amount, place, type, newValue, member_id) values (?, " 
						+ consumptionPrice 
						+ ", '" + branchName+"', ";
				if (consumptionPrice > 0)
					sql += ConstantValue.MEMBERDEPOSIT_CONSUM;
				else 
					sql += ConstantValue.MEMBERDEPOSIT_REFUND;
				sql += ", " + String.format(ConstantValue.FORMAT_DOUBLE, m.getBalanceMoney() - consumptionPrice);
				sql += ", " + m.getId()+")";
				query = session.createSQLQuery(sql);
				query.setDate(0, time);
				query.executeUpdate();

				sql = "update member_" + customerName + " set balanceMoney = " + String.format(ConstantValue.FORMAT_DOUBLE, m.getBalanceMoney() - consumptionPrice) + " where id = " + m.getId();
				query = session.createSQLQuery(sql);
				query.executeUpdate();
			}
			
			tx.commit();
		} catch(Exception e){
			tx.rollback();
			log.error("sql = " + sql);
			log.error("", e);
			return new MemberResult(e.getMessage() + sql, false);
		} finally{
			session.close();
		}
		Member member = queryMemberByCard(customerName, memberCard);//reload new score and balance
		MemberInfo mi = new MemberInfo();
		mi.name = member.getName();
		mi.memberCard = member.getMemberCard();
		mi.address=member.getAddress();
		mi.postCode=member.getAddress();
		mi.telephone=member.getTelephone();
		mi.birth = member.getBirth() == null ? "" :ConstantValue.DFYMD.format(member.getBirth());
		mi.discountRate =member.getDiscountRate();
		mi.createTime = ConstantValue.DFYMD.format(member.getCreateTime());
		mi.id = member.getId();
		mi.score = member.getScore();
		mi.balanceMoney = member.getBalanceMoney();
		long l2 = System.currentTimeMillis();
		log.debug((l2 - l1) + "ms consume, record "+customerName+".member consumption : customerName = " + customerName + ", memberCard = " + memberCard + ", consumptionPrice = " + consumptionPrice + ", sql = " + sql);
		return new MemberResult(Result.OK, true, mi);
	}

	@Override
	public ObjectListResult queryMemberBalance(String customerName, int id) {
		long l1 = System.currentTimeMillis();
		String sql = "select id, amount, date, newValue, place, type from member_balance_"+customerName + " where member_id = "+id;
		Session session = null;
		Transaction tx = null;
		try{
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			SQLQuery query = session.createSQLQuery(sql);
			/**
			 * query.addScalar 的循序 跟 查询结果的循序是一样的
			 */
			query.addScalar("id", StandardBasicTypes.INTEGER);
			query.addScalar("amount", StandardBasicTypes.DOUBLE);
			query.addScalar("date", StandardBasicTypes.DATE);
			query.addScalar("newValue", StandardBasicTypes.DOUBLE);
			query.addScalar("place", StandardBasicTypes.STRING);
			query.addScalar("type", StandardBasicTypes.INTEGER);
			List oss = query.list();
			if (oss == null || oss.size() == 0)
				return new ObjectListResult(Result.OK, true, null);
			ArrayList<MemberBalanceInfo> mbis = new ArrayList<>();
			for (int i = 0; i < oss.size(); i++) {
				Object[] oi = (Object[])oss.get(i);
				MemberBalanceInfo mbi = new MemberBalanceInfo();
				mbi.id = (int)oi[0];
				mbi.amount = (double)oi[1];
				mbi.date = (Date)oi[2];
				mbi.newValue = (double)oi[3];
				mbi.place = (String)oi[4];
				mbi.type = (int)oi[5];
				mbi.memberId = id;
				mbis.add(mbi);
			}
			long l2 = System.currentTimeMillis();
			log.debug((l2 - l1) + "ms consume, query " + customerName + ".memberbalance by memberid : " + id); 
			return new ObjectListResult(Result.OK, true, mbis);
			
		} catch(Exception e){
			tx.rollback();
			log.error("sql = " + sql);
			log.error("", e);
			return new ObjectListResult(e.getMessage() + sql, false);
		} finally{
			session.close();
		}
	}

	@Override
	public ObjectListResult queryMemberScore(String customerName, int id) {
		long l1 = System.currentTimeMillis();
		String sql = "select id, amount, date, newValue, place, type from member_score_"+customerName + " where member_id = "+id;
		Session session = null;
		Transaction tx = null;
		try{
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			SQLQuery query = session.createSQLQuery(sql);
			/**
			 * query.addScalar 的循序 跟 查询结果的循序是一样的
			 */
			query.addScalar("id", StandardBasicTypes.INTEGER);
			query.addScalar("amount", StandardBasicTypes.DOUBLE);
			query.addScalar("date", StandardBasicTypes.DATE);
			query.addScalar("newValue", StandardBasicTypes.DOUBLE);
			query.addScalar("place", StandardBasicTypes.STRING);
			query.addScalar("type", StandardBasicTypes.INTEGER);
			List oss = query.list();
			if (oss == null || oss.size() == 0)
				return new ObjectListResult(Result.OK, true, null);
			ArrayList<MemberScoreInfo> mbis = new ArrayList<>();
			for (int i = 0; i < oss.size(); i++) {
				Object[] oi = (Object[])oss.get(i);
				MemberScoreInfo mbi = new MemberScoreInfo();
				mbi.id = (int)oi[0];
				mbi.amount = (double)oi[1];
				mbi.date = (Date)oi[2];
				mbi.newValue = (double)oi[3];
				mbi.place = (String)oi[4];
				mbi.type = (int)oi[5];
				mbi.memberId = id;
				mbis.add(mbi);
			}
			long l2 = System.currentTimeMillis();
			log.debug((l2 - l1) + "ms consume, query " + customerName + ".memberscore by memberid : " + id); 
			return new ObjectListResult(Result.OK, true, mbis);
			
		} catch(Exception e){
			tx.rollback();
			log.error("sql = " + sql);
			log.error("", e);
			return new ObjectListResult(e.getMessage() + sql, false);
		} finally{
			session.close();
		}
	}

	@Override
	public MemberResult updateMemberPassword(String customerName, int id, String oldPassword, String newPassword){
		String sql = "update member_" + customerName + " set password = '"+ newPassword+"' where id = "+ id;
		String membername = null;
		Session session = null;
		Transaction tx = null;
		try{
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			Member m = queryMemberById(customerName, id);
			if (m == null)
				return new MemberResult("cannot find member by id "+ id, false, null);
			if (!oldPassword.equals(m.getPassword())){
				return new MemberResult("old password is wrong", false, null);
			}
			
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
		log.debug("change member password [" + membername +"] for customer : "+ customerName);
		return new MemberResult(Result.OK, true);
	}
	
	@Override
	public MemberResult resetMemberPassword111111(String customerName, int id){
		
		String membername = null;
		String sql = null;
		try {
			sql = "update member_" + customerName + " set password = '"+ toSHA1("111111".getBytes())+"' where id = "+ id;
		} catch (NoSuchAlgorithmException e1) {
			return new MemberResult(e1.getMessage(), false);
		}
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		try{
			Member m = queryMemberById(customerName, id);
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
		log.debug("change member password [" + membername +"] for customer : "+ customerName);
		return new MemberResult(Result.OK, true);
	}
	
	private String toSHA1(byte[] data) throws NoSuchAlgorithmException {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException ex) {
			throw ex;
		}
		return toHex(md.digest(data));
	}

	/**
	 * Bytes array to string.
	 * 
	 * @param digest
	 * @return final string.
	 */
	private String toHex(byte[] digest) {
		StringBuilder sb = new StringBuilder();
		for (byte b : digest) {
			sb.append(String.format("%1$02X", b));
		}

		return sb.toString();
	}

	@Override
	public MemberResult updateMemberDiscountRate(String customerName, int id, double discountRate) {
		long l1 = System.currentTimeMillis();
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sql = "update member_" + customerName + " set discountRate = " + discountRate + " where id = " + id;
		Member member;
		double oldDiscountRate=0;
		try{
			member = queryMemberById(customerName, id);
			if (member == null)
				return new MemberResult("cannot find member by id "+ id, false, null);
			oldDiscountRate = member.getDiscountRate();
			SQLQuery query = session.createSQLQuery(sql);
			query.executeUpdate();
			tx.commit();
		} catch(Exception e){
			tx.rollback();
			log.error("sql = " + sql );
			log.error("", e);
			return new MemberResult(e.getMessage() + sql , false);
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
		m.discountRate =discountRate;
		m.createTime = ConstantValue.DFYMD.format(member.getCreateTime());
		m.id = id;
		m.score = member.getScore();
		m.balanceMoney = member.getBalanceMoney();
		long l2 = System.currentTimeMillis();
		log.debug((l2 - l1) + "ms consume, update " + customerName +".member." + id + " discountRate from "+ oldDiscountRate +" to " + discountRate);
		return new MemberResult(Result.OK, true, m);
	}
}
