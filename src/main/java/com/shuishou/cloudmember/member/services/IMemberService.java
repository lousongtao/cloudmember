package com.shuishou.cloudmember.member.services;

import java.util.Date;

import com.shuishou.cloudmember.DataCheckException;
import com.shuishou.cloudmember.member.views.MemberListResult;
import com.shuishou.cloudmember.member.views.MemberResult;
import com.shuishou.cloudmember.views.ObjectListResult;
import com.shuishou.cloudmember.views.ObjectResult;

public interface IMemberService {

	ObjectResult addMember(String customerName, String name, String memberCard, String address, String postCode, String telephone, Date birth, double discountRate, String password);
	ObjectResult updateMember(String customerName, int id, String name, String memberCard, String address, String postCode, String telephone, Date birth,double discountRate);
	ObjectResult updateMemberScore(String customerName, int id, double newScore, String branchName);
	ObjectResult updateMemberDiscountRate(String customerName, int id, double discountRate, String branchName);
	ObjectResult updateMemberBalance(String customerName, int id, double newBalance, String branchName);
	ObjectResult resetMemberPassword111111(String customerName, int id);
	ObjectResult updateMemberPassword(String customerName, int id, String oldPassword, String newPassword);
	ObjectResult memberRecharge(String customerName, int id, double recharge, String branchName, String payway);
	ObjectResult deleteMember(String customerName, int id);
	ObjectListResult queryMember(String customerName, String name, String memberCard, String address, String postCode, String telephone);
	ObjectListResult queryMemberHazily(String customerName, String key);
	ObjectListResult queryAllMember(String customerName);
	ObjectListResult queryMemberBalance(String customerName, int id);
	ObjectListResult queryMemberRecharge(String customerName, Date startTime, Date endTime);
	ObjectListResult queryMemberScore(String customerName, int id);
	ObjectResult recordMemberConsumption(String customerName, String memberCard, String memberPassword, double consumptionPrice, 
			boolean byScore, double scorePerDollar, boolean byDeposit, String branchName);
	
}
