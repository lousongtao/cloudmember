package com.shuishou.cloudmember.member.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.shuishou.cloudmember.ConstantValue;
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

	@Override
	public List<MemberBalance> getMemberBalance(String customerName, Date startTime, Date endTime, String type) {
		MemberInterceptor interceptor = InterceptorBuilder.build(InterceptorBuilder.CLASS_MEMBERBALANCE, customerName);
		if (getInterceptorThreadLocal().get() == null){
			InterceptorSession is = new InterceptorSession(interceptor);
			getInterceptorThreadLocal().set(is);
		} else {
			//重置interceptor数据
			InterceptorSession is = getInterceptorThreadLocal().get();
			is.setInterceptor(interceptor);
		}
		ArrayList<Integer> types = new ArrayList<>(); 
		if (type.indexOf(ConstantValue.MEMBERBALANCE_QUERYTYPE_ADJUST) >= 0)
			types.add(ConstantValue.MEMBERDEPOSIT_ADJUST);
		if (type.indexOf(ConstantValue.MEMBERBALANCE_QUERYTYPE_RECHARGE) >= 0)
			types.add(ConstantValue.MEMBERDEPOSIT_RECHARGE);
		if (type.indexOf(ConstantValue.MEMBERBALANCE_QUERYTYPE_CONSUME) >= 0)
			types.add(ConstantValue.MEMBERDEPOSIT_CONSUM);
		String hql = "from MemberBalance mb where mb.type in (";
		for (int i = 0; i < types.size(); i++) {
			if (i != 0)
				hql += ",";
			hql += types.get(i);
		}
		hql+=")";
		if (startTime != null)
			hql += " and mb.date >= :startTime";
		if (endTime != null)
			hql += " and mb.date <= :endTime";
		Query query = getSession().createQuery(hql);
		if (startTime != null)
			query.setTimestamp("startTime", startTime);
		if (endTime != null)
			query.setTimestamp("endTime", endTime);
		return query.list();
	}


}
