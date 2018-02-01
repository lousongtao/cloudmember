/**
 * Copyright 2012 - 2013 Fglinxun Studios, Inc.
 * All rights reserved.
 */
package com.shuishou.cloudmember.customer.services;

import com.shuishou.cloudmember.account.models.UserData;
import com.shuishou.cloudmember.account.views.GetAccountsResult;
import com.shuishou.cloudmember.account.views.LoginResult;
import com.shuishou.cloudmember.customer.models.Customer;
import com.shuishou.cloudmember.views.ObjectListResult;
import com.shuishou.cloudmember.views.ObjectResult;
import com.shuishou.cloudmember.views.Result;



public interface ICustomerService {
  
	ObjectResult createCustomer(int operatorId, String name);
  
	ObjectResult getCustomerById(int id);
  
	ObjectResult getCustomerByName(String name);
  
	ObjectResult stopCustomer(int operatorId, String name);


}
