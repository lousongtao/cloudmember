package com.shuishou.cloudmember.member.models;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.shuishou.cloudmember.InterceptorBuilder;
import com.shuishou.cloudmember.MemberInterceptor;
import com.shuishou.cloudmember.models.BaseDataAccessor;
import com.shuishou.cloudmember.models.InterceptorSession;

@Repository
public class MemberBalanceDataAccessor extends BaseDataAccessor implements IMemberBalanceDataAccessor {

	@Override
	public List<MemberBalance> getMemberBalanceByMemberId(String customerName, int memberId) {
		MemberInterceptor interceptor = InterceptorBuilder.build(InterceptorBuilder.CLASS_MEMBERBALANCE, customerName);
		if (getInterceptorThreadLocal().get() == null){
			InterceptorSession is = new InterceptorSession(interceptor);
			getInterceptorThreadLocal().set(is);
		} else {
			//重置interceptor数据
			InterceptorSession is = getInterceptorThreadLocal().get();
			is.setInterceptor(interceptor);
		}
		String hql = "from MemberBalance ms where ms.member.id = " + memberId;
		return getSession().createQuery(hql).list();
	}

	@Override
	public void save(String customerName, MemberBalance mc) {
		MemberInterceptor interceptor = InterceptorBuilder.build(InterceptorBuilder.CLASS_MEMBERBALANCE, customerName);
		if (getInterceptorThreadLocal().get() == null){
			InterceptorSession is = new InterceptorSession(interceptor);
			getInterceptorThreadLocal().set(is);
		} else {
			//重置interceptor数据
			InterceptorSession is = getInterceptorThreadLocal().get();
			is.setInterceptor(interceptor);
		}
		getSession().save(mc);
	}

	@Override
	public void delete(String customerName, MemberBalance mc) {
		MemberInterceptor interceptor = InterceptorBuilder.build(InterceptorBuilder.CLASS_MEMBERBALANCE, customerName);
		if (getInterceptorThreadLocal().get() == null){
			InterceptorSession is = new InterceptorSession(interceptor);
			getInterceptorThreadLocal().set(is);
		} else {
			//重置interceptor数据
			InterceptorSession is = getInterceptorThreadLocal().get();
			is.setInterceptor(interceptor);
		}
		getSession().delete(mc);
	}

	@Override
	public void deleteByMember(String customerName, int memberId) {
		MemberInterceptor interceptor = InterceptorBuilder.build(InterceptorBuilder.CLASS_MEMBERBALANCE, customerName);
		if (getInterceptorThreadLocal().get() == null){
			InterceptorSession is = new InterceptorSession(interceptor);
			getInterceptorThreadLocal().set(is);
		} else {
			//重置interceptor数据
			InterceptorSession is = getInterceptorThreadLocal().get();
			is.setInterceptor(interceptor);
		}
		String hql = "delete from MemberBalance mc where mc.member.id = "+ memberId;
		getSession().createQuery(hql).executeUpdate();
	}

	@Override
	public void setInterceptorSession(String customerName) {
		MemberInterceptor interceptor = InterceptorBuilder.build(InterceptorBuilder.CLASS_MEMBERBALANCE, customerName);
		InterceptorSession is = new InterceptorSession(interceptor);
		getInterceptorThreadLocal().set(is);
	}


}
