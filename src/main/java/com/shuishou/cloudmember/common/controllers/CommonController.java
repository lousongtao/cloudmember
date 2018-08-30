package com.shuishou.cloudmember.common.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shuishou.cloudmember.common.services.ICommonService;
import com.shuishou.cloudmember.views.ObjectResult;

@Controller
public class CommonController {

	@Autowired
	private ICommonService commonService;
	
	@RequestMapping(value = "/common/getlastfile", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody ObjectResult getLastFileName(){
		return commonService.getLastBackupFileName();
	}
	
	@RequestMapping(value = "/common/getlastwholefile", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody ObjectResult getLastWholeFileName(){
		return commonService.getLastWholeBackupFileName();
	}
}
