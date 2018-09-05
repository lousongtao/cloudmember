package com.shuishou.cloudmember.validatecheck.models;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.shuishou.cloudmember.models.BaseDataAccessor;

@Repository
public class LicenseDataAccessor extends BaseDataAccessor implements ILicenseDataAccessor{

	@Override
	public void save(CustomerLicense license) {
		sessionFactory.getCurrentSession().save(license);
	}

	@Override
	public CustomerLicense queryLicense(String customerName, String key) {
		String hql = "from CustomerLicense where customerName = '"+customerName+"' and customerKey = '"+ key + "'";
		return (CustomerLicense) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
	}

	@Override
	public CustomerLicense queryLicense(String customerName) {
		String hql = "from CustomerLicense where customerName = '"+customerName+"'";
		return (CustomerLicense) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
	}
	
	@Override
	public List<CustomerLicense> queryAllLicense() {
		String hql = "from CustomerLicense";
		return (List<CustomerLicense>)sessionFactory.getCurrentSession().createQuery(hql).list();
	}

}
