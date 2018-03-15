package com.shuishou.cloudmember.member.controllers;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shuishou.cloudmember.BaseController;
import com.shuishou.cloudmember.ConstantValue;
import com.shuishou.cloudmember.account.services.IPermissionService;
import com.shuishou.cloudmember.member.services.IMemberService;
import com.shuishou.cloudmember.member.views.MemberListResult;
import com.shuishou.cloudmember.member.views.MemberResult;
import com.shuishou.cloudmember.views.ObjectListResult;
import com.shuishou.cloudmember.views.ObjectResult;

@Controller
public class MemberController extends BaseController {
	private Logger log = Logger.getLogger("MemberController");
	
	@Autowired
	private IPermissionService permissionService;
	
	@Autowired
	private IMemberService memberService;
	
	/**
	 * 
	 * @param customerName 客户名, 每个客户的唯一标识, 根据该客户去找对应的会员表
	 * @param memberCard
	 * @param name
	 * @param address
	 * @param postCode
	 * @param telephone
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/member/querymember", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody MemberListResult queryMember(
			@RequestParam(value = "customerName", required = true) String customerName, 
			@RequestParam(value = "memberCard", required = false, defaultValue = "") String memberCard,
			@RequestParam(value = "name", required = false, defaultValue = "") String name, 
			@RequestParam(value = "address", required = false, defaultValue = "") String address, 
			@RequestParam(value = "postCode", required = false, defaultValue = "") String postCode, 
			@RequestParam(value = "telephone", required = false, defaultValue = "") String telephone ) throws Exception{
		
		MemberListResult result = memberService.queryMember(customerName, name, memberCard, address, postCode, telephone);
		
		return result;
		
	}
	
	@RequestMapping(value = "/member/querymemberhazily", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody MemberListResult queryMember(
			@RequestParam(value = "customerName", required = true) String customerName, 
			@RequestParam(value = "key", required = true) String key) throws Exception{
		MemberListResult result = memberService.queryMemberHazily(customerName, key);
		
		return result;
		
	}
	
	@RequestMapping(value = "/member/queryallmember", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody MemberListResult queryAllMember(
			@RequestParam(value = "customerName", required = true) String customerName) throws Exception{
		@SuppressWarnings("rawtypes")
		MemberListResult result = memberService.queryAllMember(customerName);
		
		return result;
		
	}
	
	@RequestMapping(value = "/member/querymemberscore", method = {RequestMethod.POST})
	public @ResponseBody ObjectListResult queryMemberScore(
			@RequestParam(value = "customerName", required = true) String customerName, 
			@RequestParam(value = "memberId", required = true) int memberId) throws Exception{
			return memberService.queryMemberScore(customerName, memberId);
	}
	
	@RequestMapping(value = "/member/querymemberbalance", method = {RequestMethod.POST})
	public @ResponseBody ObjectListResult queryMemberBalance(
			@RequestParam(value = "customerName", required = true) String customerName, 
			@RequestParam(value = "memberId", required = true) int memberId) throws Exception{
			return memberService.queryMemberBalance(customerName, memberId);
	}
	
	@RequestMapping(value = "/member/addmember", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody MemberResult addMember(
			@RequestParam(value = "customerName", required = true) String customerName, 
			@RequestParam(value = "memberCard", required = true) String memberCard,
			@RequestParam(value = "name", required = true) String name, 
			@RequestParam(value = "address", required = false, defaultValue = "") String address, 
			@RequestParam(value = "postCode", required = false, defaultValue = "") String postCode, 
			@RequestParam(value = "telephone", required = false, defaultValue = "") String telephone,
			@RequestParam(value = "password", required = false, defaultValue = "") String password,
			@RequestParam(value = "discountRate", required = true) double discountRate,
			@RequestParam(value = "birth", required = false, defaultValue = "") String sBirth) throws Exception{
		Date birth = null;
		if (sBirth != null && sBirth.length() > 0){
			birth = ConstantValue.DFYMD.parse(sBirth);
		}
		try{
			MemberResult result = memberService.addMember(customerName, name, memberCard, address, postCode, telephone, birth, discountRate, password);
		
			return result;
		} catch(Exception e){
			log.error(ConstantValue.DFYMDHMS.format(new Date()));
	        log.error("", e);
	        e.printStackTrace();
			return new MemberResult(e.getMessage()+"\n"+e.getCause(), false);
		}
	}
	
	@RequestMapping(value = "/member/updatemember", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody MemberResult updateMember(
			@RequestParam(value = "customerName", required = true) String customerName, 
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "memberCard", required = true) String memberCard,
			@RequestParam(value = "name", required = true) String name, 
			@RequestParam(value = "address", required = false, defaultValue = "") String address, 
			@RequestParam(value = "postCode", required = false, defaultValue = "") String postCode, 
			@RequestParam(value = "telephone", required = false, defaultValue = "") String telephone,
			@RequestParam(value = "discountRate", required = false, defaultValue = "") double discountRate,
			@RequestParam(value = "birth", required = false, defaultValue = "") String sBirth) throws Exception{
		Date birth = null;
		if (sBirth != null && sBirth.length() > 0){
			birth = ConstantValue.DFYMD.parse(sBirth);
		}
		try{
			MemberResult result = memberService.updateMember(customerName, id, name, memberCard, address, postCode, telephone, birth, discountRate);
		
			return result;
		} catch(Exception e){
			log.error(ConstantValue.DFYMDHMS.format(new Date()));
	        log.error("", e);
	        e.printStackTrace();
			return new MemberResult(e.getMessage()+"\n"+e.getCause(), false);
		}
	}
	
	@RequestMapping(value = "/member/updatememberscore", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody MemberResult updateMemberScore(
			@RequestParam(value = "customerName", required = true) String customerName, 
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "newScore", required = true) double newScore) throws Exception{
		MemberResult result = memberService.updateMemberScore(customerName, id, newScore);
		
		return result;
	}
	
	@RequestMapping(value = "/member/updatememberbalance", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody MemberResult updateMemberBalance(
			@RequestParam(value = "customerName", required = true) String customerName, 
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "newBalance", required = true) double newBalance) throws Exception{
		MemberResult result = memberService.updateMemberBalance(customerName, id, newBalance);
		
		return result;
	}
	
	@RequestMapping(value = "/member/updatememberdiscountrate", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody MemberResult updateMemberDiscountRate(
			@RequestParam(value = "customerName", required = true) String customerName, 
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "discountRate", required = true) double discountRate) throws Exception{
		MemberResult result = memberService.updateMemberDiscountRate(customerName, id, discountRate);
		
		return result;
	}
	
	@RequestMapping(value = "/member/memberrecharge", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody MemberResult memberRecharge(
			@RequestParam(value = "customerName", required = true) String customerName, 
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "rechargeValue", required = true) double rechargeValue) throws Exception{
		return memberService.memberRecharge(customerName, id, rechargeValue);
	}
	
	@RequestMapping(value = "/member/recordconsumption", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody MemberResult recordMemberConsumption(
			@RequestParam(value = "customerName", required = true) String customerName,
			@RequestParam(value = "consumptionPrice", required = true) double consumptionPrice,
			@RequestParam(value = "scorePerDollar", required = true) double scorePerDollar,
			@RequestParam(value = "byDeposit", required = true) boolean byDeposit,
			@RequestParam(value = "branchName", required = true) String branchName,
			@RequestParam(value = "memberCard", required = true) String memberCard,
			@RequestParam(value = "memberPassword", required = false) String memberPassword,
			@RequestParam(value = "byScore", required = true) boolean byScore) throws Exception{
		return memberService.recordMemberConsumption(customerName, memberCard, memberPassword, consumptionPrice, byScore, scorePerDollar, byDeposit, branchName);
	}
	
	@RequestMapping(value = "/member/updatememberpassword", method = {RequestMethod.POST})
	public @ResponseBody MemberResult updateMemberPassword(
			@RequestParam(value = "customerName", required = true) String customerName, 
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "oldPassword", required = true) String oldPassword,
			@RequestParam(value = "newPassword", required = true) String newPassword) throws Exception{
			return memberService.updateMemberPassword(customerName, id, oldPassword, newPassword);
	}
	
	@RequestMapping(value = "/member/resetmemberpassword111111", method = {RequestMethod.POST})
	public @ResponseBody MemberResult resetMemberPassword111111(
			@RequestParam(value = "customerName", required = true) String customerName, 
			@RequestParam(value = "id", required = true) int id) throws Exception{
			return memberService.resetMemberPassword111111(customerName, id);
	}
}
