package com.shuishou.cloudmember.validatecheck.models;

import java.util.List;

public interface ILicenseDataAccessor {

	void save(CustomerLicense license);
	
	CustomerLicense queryLicense(String customerName, String key);
	
	CustomerLicense queryLicense(String customerName);
	
	List<CustomerLicense> queryAllLicense();
}
