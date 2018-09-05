package com.shuishou.cloudmember.validatecheck.controllers;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shuishou.cloudmember.BaseController;
import com.shuishou.cloudmember.validatecheck.services.ILicenseService;
import com.shuishou.cloudmember.views.ObjectResult;

@Controller
public class LicenseController extends BaseController {

	private final static Logger logger = LoggerFactory.getLogger(LicenseController.class);
	
	@Autowired
	private ILicenseService licenseService;
	
	@RequestMapping(value = "/lisence/querylisence", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ObjectResult queryLisence(
			@RequestParam(value = "customerName", required = true) String customerName,
			@RequestParam(value = "key", required = true) String key) throws Exception {
		return licenseService.queryLicenseByCustomerName(customerName, key);
	}
	
	@RequestMapping(value = "/lisence/queryalllisence", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ObjectResult queryAllLisence() throws Exception {
		return licenseService.queryAllLicense();
	}
	
	@RequestMapping(value = "/lisence/updatelisence", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ObjectResult updateLisence(
			@RequestParam(value = "customerName", required = true) String customerName,
			@RequestParam(value = "expireDate", required = true) Date expireDate) throws Exception {
		return licenseService.save(customerName, expireDate);
	}
	
}
