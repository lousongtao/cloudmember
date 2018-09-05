package com.shuishou.cloudmember.validatecheck.services;

import java.util.Date;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shuishou.cloudmember.validatecheck.models.CustomerLicense;
import com.shuishou.cloudmember.validatecheck.models.ILicenseDataAccessor;
import com.shuishou.cloudmember.views.ObjectResult;
import com.shuishou.cloudmember.views.Result;


@Service
@Transactional(readOnly = true)
public class LicenseService implements ILicenseService{

	/**
	 * the logger.
	 */
	private final static Logger logger = LogManager.getLogger(LicenseService.class);
	
	@Autowired
	private ILicenseDataAccessor licenseDA;

	@Override
	public ObjectResult queryAllLicense() {
		List<CustomerLicense> ls = licenseDA.queryAllLicense();
		return new ObjectResult(Result.OK, true, ls);
	}

	@Override
	public ObjectResult queryLicenseByCustomerName(String customerName, String key) {
		CustomerLicense l = licenseDA.queryLicense(customerName, key);
		if (l == null){
			return new ObjectResult("cannot find lisence by customerName : " + customerName + " key : " + key, false, null);
		}
		return new ObjectResult(Result.OK, true, l);
	}

	@Override
	public ObjectResult save(String customerName, Date expireDate) {
		CustomerLicense l = licenseDA.queryLicense(customerName);
		if (l == null){
			return new ObjectResult("cannot find lisence by customerName : " + customerName, false, null);
		}
		l.setExpireDate(expireDate);
		licenseDA.save(l);
		return new ObjectResult(Result.OK, true, l);
	}
	
	
}
