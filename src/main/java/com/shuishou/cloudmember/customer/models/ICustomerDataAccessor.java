/**
 * Copyright 2012 - 2013 Fglinxun Studios, Inc.
 * All rights reserved.
 */
package com.shuishou.cloudmember.customer.models;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

public interface ICustomerDataAccessor {

  Session getSession();

  void persistCustomer(Customer c);

  void updateCustomer(Customer c);

  void deleteCustomer(Customer c);

  Serializable saveCustomer(Customer c);

  void saveOrUpdateCustomer(Customer c);

  Customer getCustomerById(long id);

  Customer getCustomerByName(String name);

  List<Customer> getAllCustomers();
  
}
