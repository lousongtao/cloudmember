/**
 * Copyright 2012 - 2013 Fglinxun Studios, Inc.
 * All rights reserved.
 */
package com.shuishou.cloudmember.customer.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shuishou.cloudmember.BaseController;
import com.shuishou.cloudmember.ConstantValue;
import com.shuishou.cloudmember.account.models.Permission;
import com.shuishou.cloudmember.account.models.UserData;
import com.shuishou.cloudmember.account.services.IAccountService;
import com.shuishou.cloudmember.account.services.IPermissionService;
import com.shuishou.cloudmember.account.views.GetAccountsResult;
import com.shuishou.cloudmember.account.views.GetPermissionResult;
import com.shuishou.cloudmember.account.views.LoginResult;
import com.shuishou.cloudmember.customer.services.ICustomerService;
import com.shuishou.cloudmember.views.ObjectListResult;
import com.shuishou.cloudmember.views.ObjectResult;
import com.shuishou.cloudmember.views.Result;

@Controller
public class CustomerController extends BaseController {

	/**
	 * the logger.
	 */
	private final static Logger logger = LoggerFactory.getLogger(CustomerController.class);

	/**
	 * the account service.
	 */
	@Autowired
	private ICustomerService customerService;
	
	@Autowired
	private IPermissionService permissionService;

	@RequestMapping(value = "/customer/addcustomer", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody ObjectResult addCustomer(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "name", required = true) String name ) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_ADD_CUSTOMER)){
			return new ObjectResult("no_permission", false);
		}
		
		ObjectResult result = customerService.createCustomer(userId, name);
		
		return result;
	}

	@RequestMapping(value = "/customer/stopcustomer", method = {RequestMethod.POST})
	public @ResponseBody ObjectResult stopCustomer(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "name", required = true) String name ) throws Exception{
		return null;
	}
}
