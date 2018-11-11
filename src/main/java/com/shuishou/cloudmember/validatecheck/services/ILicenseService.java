package com.shuishou.cloudmember.validatecheck.services;

import java.util.Date;

import com.shuishou.cloudmember.validatecheck.models.CustomerLicenseValidateHistory;
import com.shuishou.cloudmember.views.ObjectListResult;
import com.shuishou.cloudmember.views.ObjectResult;

import javax.servlet.http.HttpServletRequest;

public interface ILicenseService {

	ObjectResult queryAllLicense();
	
	ObjectResult queryLicenseByCustomerName(String customerName, String key, HttpServletRequest request);
	
	ObjectResult save(String customerName, Date expireDate);

	ObjectListResult<CustomerLicenseValidateHistory> queryLicenseValiateHistory(String customerName, Date startTime, Date endTime);
}
