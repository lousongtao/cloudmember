package com.shuishou.cloudmember.member.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.shuishou.cloudmember.DataCheckException;
import com.shuishou.cloudmember.member.models.IMemberBalanceDataAccessor;
import com.shuishou.cloudmember.member.models.IMemberDataAccessor;
import com.shuishou.cloudmember.member.models.IMemberScoreDataAccessor;
//import com.shuishou.cloudmember.DataCheckException;
import com.shuishou.cloudmember.member.models.Member;
import com.shuishou.cloudmember.member.models.MemberBalance;
import com.shuishou.cloudmember.member.models.MemberScore;
import com.shuishou.cloudmember.member.views.MemberBalanceInfo;
import com.shuishou.cloudmember.member.views.MemberInfo;
import com.shuishou.cloudmember.member.views.MemberListResult;
import com.shuishou.cloudmember.member.views.MemberResult;
import com.shuishou.cloudmember.member.views.MemberScoreInfo;
import com.shuishou.cloudmember.member.views.MemberStatInfo;
import com.shuishou.cloudmember.views.ObjectListResult;
import com.shuishou.cloudmember.views.ObjectResult;
import com.shuishou.cloudmember.views.Result;

@Service
public class MemberService implements IMemberService{

	private Logger log = Logger.getLogger("MemberService");
	
	@Autowired
	private IMemberDataAccessor memberDA;
	
	@Autowired
	private IMemberBalanceDataAccessor memberBalanceDA;
	
	@Autowired
	private IMemberScoreDataAccessor memberScoreDA;

	@Override
	public ObjectResult addMember(String customerName, String name, String memberCard, String address, String postCode,
			String telephone, Date birth, double discountRate, String password) {
		long l1 = System.currentTimeMillis();
		Member m = new Member();
		m.setName(name);
		m.setMemberCard(memberCard);
		if (address != null)
			m.setAddress(address);
		if (postCode != null)
			m.setPostCode(postCode);
		if (telephone != null)
			m.setTelephone(telephone);
		if (birth != null)
			m.setBirth(birth);
		m.setDiscountRate(discountRate);
		m.setCreateTime(new Date());
		m.setPassword(password);
		memberDA.setInterceptorSession(customerName);
		Transaction tx = memberDA.getSession().beginTransaction();
		try{
			
			
			memberDA.save(customerName, m);
			tx.commit();
			long l2 = System.currentTimeMillis();
			log.debug((l2 - l1) + "ms consume, add " + customerName + ".member : name = " + name + ", memberCard = " + memberCard);
			
			return new ObjectResult(Result.OK, true,m);
		} catch (Exception e){
			tx.rollback();
			log.error("Rollback for add " + customerName + ".member : name = " + name + ", memberCard = " + memberCard, e);
			return new ObjectResult(e.getMessage(), false, null);
		} finally{
			memberDA.closeSession();
		}
	}

	@Override
	public ObjectResult updateMember(String customerName, int id, String name, String memberCard, String address,
			String postCode, String telephone, Date birth, double discountRate) {
		long l1 = System.currentTimeMillis();
		memberDA.setInterceptorSession(customerName);
		Transaction tx = memberDA.getSession().beginTransaction();
		try{
			Member m = memberDA.getMemberById(customerName, id);
			if (m == null)
				return new ObjectResult("cannot find member by id : "+ id + ", customerName : " + customerName, false, null);
			m.setName(name);
			m.setMemberCard(memberCard);
			m.setAddress(address);
			m.setPostCode(postCode);
			m.setTelephone(telephone);
			m.setBirth(birth);
			m.setDiscountRate(discountRate);
			
			
			memberDA.save(customerName, m);
			tx.commit();
			long l2 = System.currentTimeMillis();
			log.debug((l2 - l1) + "ms consume, update "+customerName+".member by name : " + name + ", memberCard : " + memberCard
					+ ", address : " + address + ", postCode : " + postCode + ", telephone : " + telephone);
			return new ObjectResult(Result.OK, true, m);
		} catch (Exception e){
			tx.rollback();
			log.error("update "+customerName+".member by name : " + name + ", memberCard : " + memberCard
					+ ", address : " + address + ", postCode : " + postCode + ", telephone : " + telephone, e);
			return new ObjectResult(e.getMessage(), false, null);
		} finally{
			memberDA.closeSession();
		}
	}

	@Override
	public ObjectResult updateMemberScore(String customerName, int id, double newScore, String branchName) {
		long l1 = System.currentTimeMillis();
		memberScoreDA.setInterceptorSession(customerName);
		Transaction tx = memberDA.getSession().beginTransaction();
		try{
			Member m = memberDA.getMemberById(customerName, id);
			
			if (m == null)
				return new ObjectResult("cannot find member by id : "+ id + ", customerName : " + customerName, false, null);
			
			double oldScore = m.getScore();
			m.setScore(newScore);
			
			
			memberDA.save(customerName, m);
			MemberScore ms = new MemberScore();
			ms.setAmount(newScore - oldScore);
			ms.setDate(new Date());
			ms.setMember(m);
			ms.setNewValue(newScore);
			ms.setPlace(branchName);
			ms.setType(ConstantValue.MEMBERSCORE_ADJUST);
			
			memberScoreDA.save(customerName, ms);
			tx.commit();
			long l2 = System.currentTimeMillis();
			log.debug((l2 - l1) + "ms consume, update " + customerName + ".member.id " + id + " score from "+ oldScore +" to " + newScore);
			return new ObjectResult(Result.OK, true, m);
		} catch (Exception e){
			tx.rollback();
			log.error("update " + customerName + ".member.id " + id + " score to " + newScore, e);
			return new ObjectResult(e.getMessage(), false, null);
		} finally{
			memberScoreDA.closeSession();
		}
		
	}
	
	@Override
	public ObjectResult updateMemberBalance(String customerName, int id, double newBalance, String branchName) {
		long l1 = System.currentTimeMillis();
		memberBalanceDA.setInterceptorSession(customerName);
		Transaction tx = memberDA.getSession().beginTransaction();
		try{
			Member m = memberDA.getMemberById(customerName, id);
			if (m == null)
				return new ObjectResult("cannot find member by id : "+ id + ", customerName : " + customerName, false, null);
			
			double oldBalance = m.getBalanceMoney();
			m.setBalanceMoney(newBalance);
			
			memberDA.save(customerName, m);
			MemberBalance mb = new MemberBalance();
			mb.setAmount(newBalance - oldBalance);
			mb.setDate(new Date());
			mb.setMember(m);
			mb.setNewValue(newBalance);
			mb.setPlace(branchName);
			mb.setType(ConstantValue.MEMBERDEPOSIT_ADJUST);
			memberBalanceDA.save(customerName, mb);
			tx.commit();
			long l2 = System.currentTimeMillis();
			log.debug((l2 - l1)+"ms consume, update " + customerName +".member.id " + id + " balance from "+ oldBalance +" to " + newBalance);
			return new ObjectResult(Result.OK, true, m);
		} catch (Exception e){
			tx.rollback();
			log.error("update " + customerName +".member.id " + id + " balance to " + newBalance, e);
			return new ObjectResult(e.getMessage(), false, null);
		} finally{
			memberBalanceDA.closeSession();
		}
		
	}
	
	@Override
	public ObjectResult updateMemberDiscountRate(String customerName, int id, double discountRate, String branchName) {
		long l1 = System.currentTimeMillis();
		memberDA.setInterceptorSession(customerName);
		Transaction tx = memberDA.getSession().beginTransaction();
		try{
			Member m = memberDA.getMemberById(customerName, id);
			if (m == null)
				return new ObjectResult("cannot find member by id : "+ id + ", customerName : " + customerName, false, null);
			double oldDiscountRate = m.getDiscountRate();
			m.setDiscountRate(discountRate);
			
			memberDA.save(customerName, m);
			tx.commit();
			long l2 = System.currentTimeMillis();
			log.debug((l2 - l1) + "ms consume, update " + customerName +".member.id " + id + " discountRate from "+ oldDiscountRate +" to " + discountRate + " at branch " + branchName);
			
			return new ObjectResult(Result.OK, true, m);
		} catch (Exception e){
			tx.rollback();
			log.error("update " + customerName +".member.id " + id + " discountRate to " + discountRate, e);
			return new ObjectResult(e.getMessage(), false, null);
		} finally{
			memberDA.closeSession();
		}
		
	}

	@Override
	public ObjectResult resetMemberPassword111111(String customerName, int id) {
		memberDA.setInterceptorSession(customerName);
		Transaction tx = memberDA.getSession().beginTransaction();
		try{
			Member m = memberDA.getMemberById(customerName, id);
			
			if (m == null)
				return new ObjectResult("cannot find member by id "+ id, false, null);
			m.setPassword(toSHA1("111111".getBytes()));
			memberDA.save(customerName, m);
			tx.commit();
			
			log.debug("change member [id = " + id +"] password to 111111 for customer : "+ customerName);
			return new ObjectResult(Result.OK, true, m);
		} catch (Exception e){
			tx.rollback();
			log.error("change member [id = " + id +"] password to 111111 for customer : "+ customerName, e);
			return new ObjectResult(e.getMessage(), false, null);
		} finally{
			memberDA.closeSession();
		}
	}

	@Override
	public ObjectResult updateMemberPassword(String customerName, int id, String oldPassword, String newPassword) {
		memberDA.setInterceptorSession(customerName);
		Transaction tx = memberDA.getSession().beginTransaction();
		try{
			Member m = memberDA.getMemberById(customerName, id);
			
			if (m == null)
				return new ObjectResult("cannot find member by id : "+ id + ", customerName : " + customerName, false, null);
			if (!oldPassword.equals(m.getPassword())){
				return new ObjectResult("the old password is wrong", false, null);
			}
			m.setPassword(newPassword);
			
			memberDA.save(customerName, m);
			tx.commit();
			log.debug("change member [id = " + id +"] password for customer : "+ customerName);
			return new ObjectResult(Result.OK, true, m);
		} catch (Exception e){
			tx.rollback();
			log.error("change member [id = " + id +"] password for customer : "+ customerName, e);
			return new ObjectResult(e.getMessage(), false, null);
		} finally{
			memberDA.closeSession();
		}
		
	}

	@Override
	public ObjectResult memberRecharge(String customerName, int id, double recharge, String branchName, String payway) {
		long l1 = System.currentTimeMillis();
		memberBalanceDA.setInterceptorSession(customerName);
		Transaction tx = memberDA.getSession().beginTransaction();
		try{
			Member m = memberDA.getMemberById(customerName, id);
			
			if (m == null)
				return new ObjectResult("cannot find member by id : "+ id + ", customerName : " + customerName, false, null);
			double oldBalance = m.getBalanceMoney();
			m.setBalanceMoney(m.getBalanceMoney() + recharge);
			
			memberDA.save(customerName, m);
			MemberBalance mb = new MemberBalance();
			mb.setAmount(recharge);
			mb.setDate(new Date());
			mb.setMember(m);
			mb.setNewValue(m.getBalanceMoney());
			mb.setPlace(branchName);
			mb.setType(ConstantValue.MEMBERDEPOSIT_RECHARGE);
			mb.setPayway(payway);
			memberBalanceDA.save(customerName, mb);
			tx.commit();
			long l2 = System.currentTimeMillis();
			log.debug((l2 - l1) + "ms consume, do " + customerName + ".member " + m.getMemberCard() + " recharge "+ recharge +", new balance = " + (oldBalance + recharge) + ", branchName = " + branchName);
			return new ObjectResult(Result.OK, true, m);
		} catch (Exception e){
			tx.rollback();
			log.error("do " + customerName + ".member.id " + id + " recharge "+ recharge +", branchName = " + branchName, e);
			return new ObjectResult(e.getMessage(), false, null);
		} finally{
			memberBalanceDA.closeSession();
		}
		
	}

	@Override
	@Transactional
	public ObjectResult deleteMember(String customerName, int id) {
		long l1 = System.currentTimeMillis();
//		memberDA.setInterceptorSession(customerName);
//		Transaction tx = memberDA.getSession().beginTransaction();
		try{
			
//			Member m = memberDA.getMemberById(customerName, id);
//			if (m == null)
//				return new ObjectResult("cannot find member by id : "+ id + ", customerName : " + customerName, false, null);
//			
//			memberDA.delete(customerName, m);
//			tx.commit();
			memberDA.delete(customerName, id);
			long l2 = System.currentTimeMillis();
//			log.debug((l2 - l1) + "ms consume, delete "+customerName +".member by id : " + id + ",name : " + m.getName());
			return new ObjectResult(Result.OK, true);
		} catch (Exception e){
//			tx.rollback();
			log.error("delete "+customerName +".member by id : " + id, e);
			return new ObjectResult(e.getMessage(), false, null);
		} finally{
			memberDA.closeSession();
		}
	}

	@Override
	public ObjectListResult queryMember(String customerName, String name, String memberCard, String address,
			String postCode, String telephone) {
		try{
			long l1 = System.currentTimeMillis();
			List<Member> members = memberDA.queryMember(customerName, name, memberCard, address, postCode, telephone);
			long l2 = System.currentTimeMillis();
			log.debug((l2 - l1) + "ms consume, query "+customerName+".member by name : " + name + ", memberCard : " + memberCard
							+ ", address : " + address + ", postCode : " + postCode + ", telephone : " + telephone);
			return new ObjectListResult(Result.OK, true, members, 0);
		} catch (Exception e){
			log.error("query "+customerName+".member by name : " + name + ", memberCard : " + memberCard
							+ ", address : " + address + ", postCode : " + postCode + ", telephone : " + telephone, e);
			return new ObjectListResult(e.getMessage(), false, null);
		} finally{
			memberDA.closeSession();
		}
		
	}

	@Override
	public ObjectListResult queryMemberHazily(String customerName, String key) {
		try{
			long l1 = System.currentTimeMillis();
			List<Member> members = memberDA.queryMemberHazily(customerName, key);
			long l2 = System.currentTimeMillis();
			log.debug((l2 - l1) + "ms consume, query "+customerName+".member hazily by key : " + key);
			return new ObjectListResult(Result.OK, true, members, 0);
		} catch (Exception e){
			log.error("query "+customerName+".member hazily by key : " + key, e);
			return new ObjectListResult(e.getMessage(), false, null);
		} finally{
			memberDA.closeSession();
		}
		
	}

	@Override
	public ObjectListResult queryAllMember(String customerName) {
		try{
			long l1 = System.currentTimeMillis();
			List<Member> members = memberDA.queryAllMember(customerName);
			long l2 = System.currentTimeMillis();
			log.debug((l2 - l1) + "ms consume, query all "+customerName+".member");
			return new ObjectListResult(Result.OK, true, members, members.size());
		} catch (Exception e){
			log.error("query all "+customerName+".member", e);
			return new ObjectListResult(e.getMessage(), false, null);
		} finally{
			memberDA.closeSession();
		}
	}

	@Override
	public ObjectListResult queryMemberBalance(String customerName, int id) {
		try{
			long l1 = System.currentTimeMillis();
			memberBalanceDA.setInterceptorSession(customerName);
			List<MemberBalance> mbs = memberBalanceDA.getMemberBalanceByMemberId(customerName, id);
			long l2 = System.currentTimeMillis();
			log.debug((l2 - l1) + "ms consume, query MemberBalance "+customerName+".member for id = "+ id);
			return new ObjectListResult(Result.OK, true, mbs, mbs.size());
		} catch (Exception e){
			log.error("query MemberBalance "+customerName+".member for id = "+ id, e);
			return new ObjectListResult(e.getMessage(), false, null);
		} finally{
			memberBalanceDA.closeSession();
		}
		
	}

	@Override
	public ObjectListResult queryMemberBalance(String customerName, Date startTime, Date endTime, String type) {
		try{
			long l1 = System.currentTimeMillis();
			memberBalanceDA.setInterceptorSession(customerName);
			List<MemberBalance> mbs = memberBalanceDA.getMemberBalance(customerName, startTime, endTime, type);
			List<MemberBalanceInfo> mbis = new ArrayList<>();
			if (mbs != null){
				List<Member> members = memberDA.queryAllMember(customerName);
				for (int i = 0; i < mbs.size(); i++) {
					MemberBalance mb = mbs.get(i);
					MemberBalanceInfo mbi = new MemberBalanceInfo();
					mbi.id = mb.getId();
					mbi.amount = mb.getAmount();
					mbi.date = mb.getDate();
					mbi.memberId = mb.getMember().getId();
					mbi.newValue = mb.getNewValue();
					mbi.place = mb.getPlace();
					mbi.type = mb.getType();
					mbi.payway = mb.getPayway();
					for (int j = 0; j < members.size(); j++) {
						if (mbi.memberId == members.get(j).getId()){
							mbi.memberCard = members.get(j).getMemberCard();
							mbi.memberName = members.get(j).getName();
							break;
						}
					}
					mbis.add(mbi);
				}
			}
			long l2 = System.currentTimeMillis();
			String sLog = (l2 - l1) + "ms consume, query MemberBalance "+customerName+".member from startTime = "
					+ (startTime == null ? "" : ConstantValue.DFYMD.format(startTime)) + " to endTime = " 
					+ (endTime == null ? "" : ConstantValue.DFYMD.format(endTime));
			log.debug(sLog);
			return new ObjectListResult(Result.OK, true, mbis, mbis.size());
		} catch (Exception e){
			String sLog = "query MemberBalance "+customerName+".member from startTime = "
					+ (startTime == null ? "" : ConstantValue.DFYMD.format(startTime)) + " to endTime = " 
					+ (endTime == null ? "" : ConstantValue.DFYMD.format(endTime));
			log.error(sLog, e);
			return new ObjectListResult(e.getMessage(), false, null);
		} finally{
			memberBalanceDA.closeSession();
		}
		
	}
	
	@Override
	public ObjectListResult statMemberByTime(String customerName, Date startTime, Date endTime){
		try{
			long l1 = System.currentTimeMillis();
			String type = ConstantValue.MEMBERBALANCE_QUERYTYPE_RECHARGE + ConstantValue.MEMBERBALANCE_QUERYTYPE_ADJUST + ConstantValue.MEMBERBALANCE_QUERYTYPE_CONSUME;
			memberBalanceDA.setInterceptorSession(customerName);
			List<MemberBalance> mbs = memberBalanceDA.getMemberBalance(customerName, startTime, endTime, type);
			List<Member> members = memberDA.queryAllMember(customerName);
			HashMap<Integer, Member> hmMember = new HashMap<>();//把member数据放入map中, 可快速查询
			for (int i = 0; i < members.size(); i++) {
				Member m = members.get(i);
				hmMember.put(m.getId(), m);
			}
			HashMap<Integer, MemberStatInfo> hmStat = new HashMap<>();//key=member.id
			if (mbs != null){
				for (int i = 0; i < mbs.size(); i++) {
					MemberBalance mb = mbs.get(i);
					int memberid = mb.getMember().getId();
					Member m = hmMember.get(memberid);
					if (m == null)
						continue;
					MemberStatInfo ms = hmStat.get(memberid);
					if (ms == null) {
						ms = new MemberStatInfo(m.getMemberCard(), m.getName(), m.getCreateTime());
						hmStat.put(memberid, ms);
					}
					if (mb.getType() == ConstantValue.MEMBERDEPOSIT_RECHARGE) {
						ms.addRecharge(mb.getAmount());
					}
					if (mb.getType() == ConstantValue.MEMBERDEPOSIT_ADJUST) {
						ms.addAdjust(mb.getAmount());
					}
					if (mb.getType() == ConstantValue.MEMBERDEPOSIT_CONSUM) {
						ms.addConsume(mb.getAmount());
					}
				}
			}
			List<MemberStatInfo> msis = new ArrayList<>();
			msis.addAll(hmStat.values());
			long l2 = System.currentTimeMillis();
			String sLog = (l2 - l1) + "ms consume, Statistics member for "+customerName+" from startTime = "
					+ (startTime == null ? "" : ConstantValue.DFYMD.format(startTime)) + " to endTime = " 
					+ (endTime == null ? "" : ConstantValue.DFYMD.format(endTime));
			log.debug(sLog);
			return new ObjectListResult(Result.OK, true, msis, msis.size());
		} catch (Exception e){
			String sLog = "Statistics member for "+customerName+" from startTime = "
					+ (startTime == null ? "" : ConstantValue.DFYMD.format(startTime)) + " to endTime = " 
					+ (endTime == null ? "" : ConstantValue.DFYMD.format(endTime));
			log.error(sLog, e);
			return new ObjectListResult(e.getMessage(), false, null);
		} finally{
			memberBalanceDA.closeSession();
		}
	}
	
	@Override
	public ObjectListResult queryMemberScore(String customerName, int id) {
		try{
			long l1 = System.currentTimeMillis();
			memberScoreDA.setInterceptorSession(customerName);
			List<MemberScore> mss = memberScoreDA.getMemberScoreByMemberId(customerName, id);
			long l2 = System.currentTimeMillis();
			log.debug((l2 - l1) + "ms consume, query MemberScore "+customerName+".member for id = "+ id);
			return new ObjectListResult(Result.OK, true, mss, mss.size());
		} catch (Exception e){
			log.error("query MemberScore "+customerName+".member for id = "+ id, e);
			return new ObjectListResult(e.getMessage(), false, null);
		} finally{
			memberScoreDA.closeSession();
		}
		
	}

	@Override
	public ObjectResult recordMemberConsumption(String customerName, String memberCard, String memberPassword,
			double consumptionPrice, boolean byScore, double scorePerDollar, boolean byDeposit, String branchName){
		long l1 = System.currentTimeMillis();
		memberScoreDA.setInterceptorSession(customerName);//只要一个初始化interceptor即可, 因为
		Transaction tx = memberDA.getSession().beginTransaction();
		try{
			Member member = memberDA.getMemberByCard(customerName, memberCard);
			if (member == null){
				throw new DataCheckException("cannot find member by card : " + memberCard + ", customerName : "+ customerName);
			}
			if (memberPassword != null && !memberPassword.equals(member.getPassword())){
				throw new DataCheckException("password is wrong");
			}
			Date time = new Date();
			if (byScore && scorePerDollar > 0){
				MemberScore ms = new MemberScore();
				ms.setDate(time);
				ms.setAmount(scorePerDollar * consumptionPrice);
				ms.setPlace(branchName);
				if (consumptionPrice > 0)
					ms.setType(ConstantValue.MEMBERSCORE_CONSUM);
				else 
					ms.setType(ConstantValue.MEMBERSCORE_REFUND);
				
				ms.setMember(member);
				ms.setNewValue(member.getScore() + ms.getAmount());
				memberScoreDA.save(customerName, ms);
				member.setScore(member.getScore() + ms.getAmount());
				memberDA.save(customerName, member);
			}
			if (byDeposit){
				if (member.getBalanceMoney() < consumptionPrice){
					throw new DataCheckException("Meber's balance is not enought to pay");
				}
				MemberBalance mc = new MemberBalance();
				mc.setAmount(consumptionPrice);
				mc.setDate(time);
				mc.setMember(member);
				mc.setPlace(branchName);
				if (consumptionPrice > 0)
					mc.setType(ConstantValue.MEMBERDEPOSIT_CONSUM);
				else 
					mc.setType(ConstantValue.MEMBERDEPOSIT_REFUND);
				mc.setNewValue(member.getBalanceMoney() - consumptionPrice);
				memberBalanceDA.save(customerName, mc);
				member.setBalanceMoney(member.getBalanceMoney() - consumptionPrice);
				memberDA.save(customerName, member);
			}
			tx.commit();
			long l2 = System.currentTimeMillis();
			log.debug((l2 - l1) + "ms consume, recordMemberConsumption "+customerName+".member for memberCard = "+ memberCard + ", consumptionPrice = " + consumptionPrice
					+ ", byScore = " + byScore + ", scorePerDollar = "+ scorePerDollar + ", byDeposit = " + byDeposit + ", branchName = " +branchName );
			return new ObjectResult(Result.OK, true, member);
		} catch (Exception e){
			tx.rollback();
			log.error("recordMemberConsumption "+customerName+".member for memberCard = "+ memberCard + ", consumptionPrice = " + consumptionPrice
					+ ", byScore = " + byScore + ", scorePerDollar = "+ scorePerDollar + ", byDeposit = " + byDeposit + ", branchName = " +branchName, e);
			return new ObjectResult(e.getMessage(), false, null);
		} finally{
			memberScoreDA.closeSession();
		}
		
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

}
