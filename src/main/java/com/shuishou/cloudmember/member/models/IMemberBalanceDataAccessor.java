package com.shuishou.cloudmember.member.models;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;

public interface IMemberBalanceDataAccessor {

	List<MemberBalance> getMemberBalanceByMemberId(String customerName, int memberId);
	
	List<MemberBalance> getMemberBalance(String customerName, Date startTime, Date endTime, String type);
	
	void save(String customerName, MemberBalance mb);
	
	void delete(String customerName, MemberBalance mb);
	
	void deleteByMember(String customerName, int memberId);
	
	void closeSession();
	
	Session getSession();
	void setInterceptorSession(String customerName);
}
