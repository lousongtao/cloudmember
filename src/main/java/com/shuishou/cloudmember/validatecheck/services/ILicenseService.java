package com.shuishou.cloudmember.validatecheck.services;

import java.util.Date;

import com.shuishou.cloudmember.views.ObjectResult;

public interface ILicenseService {

	ObjectResult queryAllLicense();
	
	ObjectResult queryLicenseByCustomerName(String customerName, String key);
	
	ObjectResult save(String customerName, Date expireDate);
}
