package com.shuishou.cloudmember.member.views;

import java.util.ArrayList;

import com.shuishou.cloudmember.views.Result;

/**
 * 这个工程由于需要动态变化member表, 所以跟member相关的实体没有用hibernate映射,
 * 奇怪的是, MappingJackson2HttpMessageConverter对于实体的json解析也失灵
 * 导致Date对象无法正常转换.
 * 对于所有的member对象, 通过该类, 全部转换为String格式, 
 * @author Administrator
 *
 */
public class MemberListResult extends Result {

	public final boolean success;
	
	public ArrayList<MemberInfo> data;
	public MemberListResult(String result, boolean success, ArrayList<MemberInfo> memberInfo) {
		super(result);
		this.success = success;
		this.data = memberInfo;
	}
	
	public MemberListResult(String result, boolean success) {
		super(result);
		this.success = success;
	}
}
