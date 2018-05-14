/**
 * Copyright 2012 - 2013 Fglinxun Studios, Inc.
 * All rights reserved.
 */
package com.shuishou.cloudmember.customer.models;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.shuishou.cloudmember.models.BaseDataAccessor;

@Repository
public class CustomerDataAccessor extends BaseDataAccessor implements ICustomerDataAccessor {

	@Override
	public void persistCustomer(Customer c) {
		sessionFactory.getCurrentSession().persist(c);
	}

	@Override
	public void updateCustomer(Customer c) {
		sessionFactory.getCurrentSession().update(c);
	}

	@Override
	public void deleteCustomer(Customer c) {
		sessionFactory.getCurrentSession().delete(c);
	}

	@Override
	public Serializable saveCustomer(Customer c) {
		return sessionFactory.getCurrentSession().save(c);
	}

	@Override
	public void saveOrUpdateCustomer(Customer c) {
		sessionFactory.getCurrentSession().saveOrUpdate(c);
	}

	@Override
	public Customer getCustomerById(long id) {
		return (Customer) sessionFactory.getCurrentSession().get(Customer.class, id);
	}

	@Override
	public Customer getCustomerByName(String name) {
		String hql = "from Customer where name='" + name+"'";
		Customer c = (Customer) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
		return c;
	}

	@Override
//	@org.springframework.transaction.annotation.Transactional
	public List<Customer> getAllCustomers() {
		List<Customer> list = (List<Customer>) sessionFactory.getCurrentSession()
				.createQuery("select c from Customer c").list();
		return list;
	}

}
