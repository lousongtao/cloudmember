package com.shuishou.cloudmember.member.services;

import java.util.Date;

import com.shuishou.cloudmember.DataCheckException;
import com.shuishou.cloudmember.member.views.MemberListResult;
import com.shuishou.cloudmember.member.views.MemberResult;
import com.shuishou.cloudmember.views.ObjectListResult;
import com.shuishou.cloudmember.views.ObjectResult;

public interface IMemberService {

	MemberResult addMember(String customerName, String name, String memberCard, String address, String postCode, String telephone, Date birth, double discountRate);
	MemberResult updateMember(String customerName, int id, String name, String memberCard, String address, String postCode, String telephone, Date birth,double discountRate);
	MemberResult updateMemberScore(String customerName, int id, double newScore);
	MemberResult updateMemberBalance(String customerName, int id, double newBalance);
	MemberResult memberRecharge(String customerName, int id, double recharge);
	MemberResult deleteMember(String customerName, int id);
	MemberListResult queryMember(String customerName, String name, String memberCard, String address, String postCode, String telephone);
	MemberListResult queryAllMember(String customerName);
	MemberListResult queryPurchase(String customerName, int id);
	MemberListResult queryRecharge(String customerName, int id);
	MemberListResult queryScore(String customerName, int id);
	MemberResult recordMemberConsumption(String customerName, String memberCard, double consumptionPrice, boolean byScore, double scorePerDollar, boolean byDeposit, String branchName) throws DataCheckException;
	
}
