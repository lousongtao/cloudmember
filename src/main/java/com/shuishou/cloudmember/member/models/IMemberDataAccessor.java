package com.shuishou.cloudmember.member.models;

import java.util.Date;
import java.util.List;

public interface IMemberDataAccessor {

	Member getMemberById(String customerName, int id);
	Member getMemberByCard(String customerName, String card);
	
	List<Member> queryMember(String customerName, String name, String memberCard, String address, String postCode, String telephone);
	List<Member> queryAllMember(String customerName);
	
	int queryMemberCount(String customerName, String name, String memberCard, String address, String postCode, String telephone);
	
	void save(String customerName, Member m);
	
	void delete(String customerName, Member m);
	
	void createMemberTableByCustomer(String customerName);
}
