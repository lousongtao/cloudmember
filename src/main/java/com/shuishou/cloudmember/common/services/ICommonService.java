package com.shuishou.cloudmember.common.services;

import com.shuishou.cloudmember.views.ObjectResult;

public interface ICommonService {

	ObjectResult getLastBackupFileName();
	
	ObjectResult getLastWholeBackupFileName();
}
