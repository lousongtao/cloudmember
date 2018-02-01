package com.shuishou.cloudmember.account.services;

import com.shuishou.cloudmember.views.ObjectListResult;

public interface IPermissionService {
	public boolean checkPermission(long userId, String permission);
	
	public ObjectListResult queryAllPermissions();
}
