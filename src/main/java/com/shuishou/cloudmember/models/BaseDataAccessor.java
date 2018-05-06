/**
 * Copyright 2012 - 2013 Fglinxun Studios, Inc.
 * All rights reserved.
 */
package com.shuishou.cloudmember.models;

import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.shuishou.cloudmember.MemberInterceptor;

public class BaseDataAccessor {
	
	/**
	 * the hibernate session factory.
	 */
	@Autowired
	protected SessionFactory sessionFactory;
	
	private static final ThreadLocal<InterceptorSession> interceptorThreadLocal = new ThreadLocal<>();

	/**
	 * @return the sessionFactory
	 */
	public Session getSession() {
		if (interceptorThreadLocal.get() == null)
			return sessionFactory.getCurrentSession();
		else {
			InterceptorSession is = interceptorThreadLocal.get();
			if (is.getSession() == null || !is.getSession().isOpen()){
				SessionBuilder builder = sessionFactory.withOptions().interceptor(is.getInterceptor());
				Session session = builder.openSession();
				is.setSession(session);
			}
			return is.getSession();
		}
	}
	
	public void closeSession(){
		InterceptorSession is = interceptorThreadLocal.get();
		if (is != null && is.getSession() != null){
			if (is.getSession().isOpen()){
				is.getSession().close();
			}
		}
	}

	public ThreadLocal<InterceptorSession> getInterceptorThreadLocal() {
		return interceptorThreadLocal;
	}

//	public void setInterceptorThreadLocal(ThreadLocal<InterceptorSession> interceptorThreadLocal) {
//		this.interceptorThreadLocal = interceptorThreadLocal;
//	}
	
	
}
