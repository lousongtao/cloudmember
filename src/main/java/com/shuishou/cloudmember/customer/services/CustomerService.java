/**
 * Copyright 2012 - 2013 Fglinxun Studios, Inc.
 * All rights reserved.
 */
package com.shuishou.cloudmember.customer.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.shuishou.cloudmember.ConstantValue;
import com.shuishou.cloudmember.account.models.IPermissionDataAccessor;
import com.shuishou.cloudmember.account.models.IUserDataAccessor;
import com.shuishou.cloudmember.account.models.IUserPermissionDataAccessor;
import com.shuishou.cloudmember.account.models.Permission;
import com.shuishou.cloudmember.account.models.UserData;
import com.shuishou.cloudmember.account.models.UserPermission;
import com.shuishou.cloudmember.account.views.GetAccountsResult;
import com.shuishou.cloudmember.account.views.LoginResult;
import com.shuishou.cloudmember.customer.models.Customer;
import com.shuishou.cloudmember.customer.models.ICustomerDataAccessor;
import com.shuishou.cloudmember.member.models.IMemberDataAccessor;
import com.shuishou.cloudmember.views.ObjectListResult;
import com.shuishou.cloudmember.views.ObjectResult;
import com.shuishou.cloudmember.views.Result;

@Service
@Transactional(readOnly = true)
public class CustomerService implements ICustomerService {

	/**
	 * the logger.
	 */
	private final static Logger logger = LogManager.getLogger(CustomerService.class);

	@Autowired
	private ICustomerDataAccessor customerDA;
	
	@Autowired
	private IMemberDataAccessor memberDA;
	
	@Autowired
	private IUserDataAccessor userDA;
	
	@Override
	@Transactional
	public ObjectResult createCustomer(int operatorId, String name) {
		UserData user = userDA.getUserById(operatorId);
		Customer c = new Customer();
		c.setCreatetime(new Date());
		c.setStatus(ConstantValue.CUSTOMER_STATUS_NORMAL);
		c.setName(name);
		customerDA.saveCustomer(c);
		
		//generate table for this customer
		memberDA.createMemberTableByCustomer(name);
		
		logger.debug("user " + user +" create customer " + name + " at " + ConstantValue.DFYMDHMS.format(new Date()));
		return new ObjectResult(Result.OK, true, c);
	}

	@Override
	@Transactional
	public ObjectResult getCustomerById(int id) {
		Customer c = customerDA.getCustomerById(id);
		if (c == null)
			return new ObjectResult("Cannot find customer by id " + id, false);
		return new ObjectResult(Result.OK, true, c);
	}

	@Override
	@Transactional
	public ObjectResult getCustomerByName(String name) {
		Customer c = customerDA.getCustomerByName(name);
		if (c == null)
			return new ObjectResult("Cannot find customer by name " + name, false);
		return new ObjectResult(Result.OK, true, c);
	}

	@Override
	@Transactional
	public ObjectResult stopCustomer(int operatorId, String name) {
		UserData user = userDA.getUserById(operatorId);
		
		Customer c = customerDA.getCustomerByName(name);
		if (c == null)
			return new ObjectResult("Cannot find customer by name " + name, false);
		c.setStatus(ConstantValue.CUSTOMER_STATUS_STOPPED);
		customerDA.saveCustomer(c);
		logger.debug("user " + user +" stop customer " + name + " at " + ConstantValue.DFYMDHMS.format(new Date()));
		return new ObjectResult(Result.OK, true, c);
	}
	

}
