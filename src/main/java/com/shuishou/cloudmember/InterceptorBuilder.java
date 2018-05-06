package com.shuishou.cloudmember;

import org.hibernate.EmptyInterceptor;

public class InterceptorBuilder {
	public static final String CLASS_MEMBER = "member";
	public static final String CLASS_MEMBERSCORE = "member_score";
	public static final String CLASS_MEMBERBALANCE = "member_balance";
	private static MemberInterceptor memberInterceptor = new MemberInterceptor();
	private static MemberInterceptor memberScoreInterceptor = new MemberInterceptor();
	private static MemberInterceptor memberBalanceInterceptor = new MemberInterceptor();
	public static MemberInterceptor build(String className, String customerName){
		if (CLASS_MEMBER.equals(className)){
			memberInterceptor.clearReplacePair();
			memberInterceptor.addReplacePair(new String[]{"member", "member_"+customerName});
			return memberInterceptor;
		} else if (CLASS_MEMBERSCORE.equals(className)){
			memberScoreInterceptor.clearReplacePair();
			memberScoreInterceptor.addReplacePair(new String[]{"member", "member_"+customerName});
			memberScoreInterceptor.addReplacePair(new String[]{"member_balance", "member_balance_"+customerName});
			memberScoreInterceptor.addReplacePair(new String[]{"member_score", "member_score_"+ customerName});
			return memberScoreInterceptor;
		} else if (CLASS_MEMBERBALANCE.equals(className)){
			memberBalanceInterceptor.clearReplacePair();
			memberBalanceInterceptor.addReplacePair(new String[]{"member", "member_"+customerName});
			memberBalanceInterceptor.addReplacePair(new String[]{"member_score", "member_score_"+customerName});
			memberBalanceInterceptor.addReplacePair(new String[]{"member_balance", "member_balance_"+ customerName});
			return memberBalanceInterceptor;
		}
		return null;
	}

}
