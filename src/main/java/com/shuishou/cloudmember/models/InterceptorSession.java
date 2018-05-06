package com.shuishou.cloudmember.models;

import org.hibernate.Session;

import com.shuishou.cloudmember.MemberInterceptor;

/**
 * 对每个线程绑定一个session, 避免一个线程中创建多个session.
 * 创建多个线程会发生 org.hibernate.HibernateException: Illegal attempt to associate a collection with two open sessions
 * @author Administrator
 *
 */
public class InterceptorSession {

	private MemberInterceptor interceptor;
	
	private Session session;
	
	public InterceptorSession(MemberInterceptor interceptor){
		this.interceptor = interceptor;
	}

	public MemberInterceptor getInterceptor() {
		return interceptor;
	}

	public void setInterceptor(MemberInterceptor interceptor) {
		this.interceptor = interceptor;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	
	
}
