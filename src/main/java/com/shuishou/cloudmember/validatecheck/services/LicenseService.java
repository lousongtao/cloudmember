package com.shuishou.cloudmember.validatecheck.services;

import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import com.shuishou.cloudmember.ConstantValue;
import com.shuishou.cloudmember.validatecheck.models.CustomerLicenseValidateHistory;
import com.shuishou.cloudmember.validatecheck.models.ILicenseValidateHistoryDataAccessor;
import com.shuishou.cloudmember.views.ObjectListResult;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shuishou.cloudmember.validatecheck.models.CustomerLicense;
import com.shuishou.cloudmember.validatecheck.models.ILicenseDataAccessor;
import com.shuishou.cloudmember.views.ObjectResult;
import com.shuishou.cloudmember.views.Result;

import javax.servlet.http.HttpServletRequest;


@Service
@Transactional
public class LicenseService implements ILicenseService{

	/**
	 * the logger.
	 */
	private final static Logger logger = Logger.getLogger(LicenseService.class);
	
	@Autowired
	private ILicenseDataAccessor licenseDA;

	@Autowired
    private ILicenseValidateHistoryDataAccessor historyDA;

	@Override
	public ObjectResult queryAllLicense() {
		List<CustomerLicense> ls = licenseDA.queryAllLicense();
		return new ObjectResult(Result.OK, true, ls);
	}

	@Override
	public ObjectResult queryLicenseByCustomerName(String customerName, String key, HttpServletRequest request) {
	    String ip = getIpAdrress(request);
	    CustomerLicenseValidateHistory history = new CustomerLicenseValidateHistory();
	    history.setCustomerName(customerName);
	    history.setCustomerKey(key);
	    history.setIp(ip);
	    history.setValidateDate(new Date());
		CustomerLicense l = licenseDA.queryLicense(customerName, key);
		if (l == null){
		    history.setStatus(ConstantValue.CUSTOMERLICENSE_VALIDATEHISTORY_WRONGKEY);
		    historyDA.save(history);
			return new ObjectResult("cannot find lisence by customerName : " + customerName + " key : " + key, false, null);
		}
		history.setStatus(ConstantValue.CUSTOMERLICENSE_VALIDATEHISTORY_SUCCESS);
		historyDA.save(history);
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

    @Override
    public ObjectListResult<CustomerLicenseValidateHistory> queryLicenseValiateHistory(String customerName, Date startTime, Date endTime) {
        return null;
    }


    /**
     * 获取Ip地址
     * @param request
     * @return
     */
    private static String getIpAdrress(HttpServletRequest request) {
        logger.debug("----------------------");
        Enumeration enu = request.getHeaderNames();
        while(enu.hasMoreElements()){
            String s = (String)enu.nextElement();
            logger.debug(s + " = " + request.getHeader(s));
        }
        logger.debug("----------------------");
        String ip = request.getHeader("x-forwarded-for");
        logger.debug("x-forwarded-for = " + request.getHeader("x-forwarded-for"));
        logger.debug("X-Forwarded-For = " + request.getHeader("X-Forwarded-For"));
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            logger.debug("Proxy-Client-IP = " + request.getHeader("Proxy-Client-IP"));
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            logger.debug("WL-Proxy-Client-IP = " + request.getHeader("WL-Proxy-Client-IP"));
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            logger.debug("HTTP_CLIENT_IP = " + request.getHeader("HTTP_CLIENT_IP"));
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            logger.debug("HTTP_X_FORWARDED_FOR = " + request.getHeader("HTTP_X_FORWARDED_FOR"));
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            logger.debug("remote address = " + request.getRemoteAddr());
        }
        return ip;
    }
}
