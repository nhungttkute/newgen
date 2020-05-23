package com.newgen.am.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.Constant;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.dto.AdminDataObj;
import com.newgen.am.dto.AdminResponseObj;
import com.newgen.am.dto.ListElementDTO;
import com.newgen.am.model.RoleFunction;
import com.newgen.am.service.CommonService;

@RestController
public class CommonController {
	private String className = "CommonController";
	
	@Autowired
	private CommonService commonSerivce;
	
	@GetMapping("/admin/memberList")
//	@PreAuthorize("hasAuthority('clientManagement.memberManagement.memberRoleManagement.memberRoleFunctionsAssign.create')")
	public AdminResponseObj getMemberList(HttpServletRequest request) {
		String methodName = "getMemberList";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				String.format("REQUEST_API: [GET]/admin/memberList"));

		List<ListElementDTO> memberList = commonSerivce.getMemberList(request, refId);
		AdminResponseObj response = new AdminResponseObj();
		if (memberList != null && memberList.size() > 0) {
            response.setStatus(Constant.RESPONSE_OK);
            response.setData(new AdminDataObj());
            response.getData().setMemberList(memberList);
        } else {
            response.setStatus(Constant.RESPONSE_ERROR);
            response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
        }

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/brokerList")
//	@PreAuthorize("hasAuthority('clientManagement.memberManagement.memberRoleManagement.memberRoleFunctionsAssign.create')")
	public AdminResponseObj getBrokerList(HttpServletRequest request) {
		String methodName = "getBrokerList";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				String.format("REQUEST_API: [GET]/admin/brokerList"));

		List<ListElementDTO> brokerList = commonSerivce.getBrokerList(request, refId);
		AdminResponseObj response = new AdminResponseObj();
		if (brokerList != null && brokerList.size() > 0) {
            response.setStatus(Constant.RESPONSE_OK);
            response.setData(new AdminDataObj());
            response.getData().setBrokerList(brokerList);
        } else {
            response.setStatus(Constant.RESPONSE_ERROR);
            response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
        }

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
}
