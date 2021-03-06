package com.shuishou.cloudmember.member.models;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;

public interface IMemberScoreDataAccessor {

	List<MemberScore> getMemberScoreByMemberId(String customerName, int memberId);
	
	void save(String customerName, MemberScore ms);
	
	void delete(String customerName, MemberScore ms);
	
	void deleteByMember(String customerName, int memberId);
	
	void closeSession();
	Session getSession();
	void setInterceptorSession(String customerName);
}
