package com.shuishou.cloudmember.member.services;

import java.util.Date;

import com.shuishou.cloudmember.DataCheckException;
import com.shuishou.cloudmember.member.views.MemberListResult;
import com.shuishou.cloudmember.member.views.MemberResult;
import com.shuishou.cloudmember.views.ObjectListResult;
import com.shuishou.cloudmember.views.ObjectResult;

public interface IMemberService {

	MemberResult addMember(String customerName, String name, String memberCard, String address, String postCode, String telephone, Date birth, double discountRate, String password);
	MemberResult updateMember(String customerName, int id, String name, String memberCard, String address, String postCode, String telephone, Date birth,double discountRate);
	MemberResult updateMemberScore(String customerName, int id, double newScore);
	MemberResult updateMemberBalance(String customerName, int id, double newBalance);
	MemberResult resetMemberPassword111111(String customerName, int id);
	MemberResult updateMemberPassword(String customerName, int id, String oldPassword, String newPassword);
	MemberResult memberRecharge(String customerName, int id, double recharge);
	MemberResult deleteMember(String customerName, int id);
	MemberListResult queryMember(String customerName, String name, String memberCard, String address, String postCode, String telephone);
	MemberListResult queryMemberHazily(String customerName, String key);
	MemberListResult queryAllMember(String customerName);
	ObjectListResult queryMemberBalance(String customerName, int id);
	ObjectListResult queryMemberScore(String customerName, int id);
	MemberResult recordMemberConsumption(String customerName, String memberCard, String memberPassword, double consumptionPrice, boolean byScore, double scorePerDollar, boolean byDeposit, String branchName);
	
}
