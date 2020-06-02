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
import com.newgen.am.dto.NewsUserInfo;
import com.newgen.am.model.RoleFunction;
import com.newgen.am.service.CommonService;

@RestController
public class CommonController {
	private String className = "CommonController";
	
	@Autowired
	private CommonService commonSerivce;
	
	@GetMapping("/admin/memberList")
	public AdminResponseObj getMemberList(HttpServletRequest request) {
		String methodName = "getMemberList";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				String.format("REQUEST_API: [GET]/admin/memberList"));

		List<ListElementDTO> memberList = commonSerivce.getMemberList(request, refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setMemberList(memberList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/brokerList")
	public AdminResponseObj getBrokerList(HttpServletRequest request) {
		String methodName = "getBrokerList";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				String.format("REQUEST_API: [GET]/admin/brokerList"));

		List<ListElementDTO> brokerList = commonSerivce.getBrokerList(request, refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setBrokerList(brokerList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/collaboratorList")
	public AdminResponseObj getCollaboratorList(HttpServletRequest request) {
		String methodName = "getCollaboratorList";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				String.format("REQUEST_API: [GET]/admin/collaboratorList"));

		List<ListElementDTO> collaboratorList = commonSerivce.getCollaboratorList(request, refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setCollaboratorList(collaboratorList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/investorList")
	public AdminResponseObj getInvestorList(HttpServletRequest request) {
		String methodName = "getInvestorList";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				String.format("REQUEST_API: [GET]/admin/investorList"));

		List<ListElementDTO> investorList = commonSerivce.getInvestorList(request, refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setInvestorList(investorList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/adminUserList")
	public AdminResponseObj getAdminUserList(HttpServletRequest request) {
		String methodName = "getAdminUserList";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				String.format("REQUEST_API: [GET]/admin/adminUserList"));

		List<NewsUserInfo> userList = commonSerivce.getAdminUserList(refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setAdminUserList(userList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/memberUserList")
	public AdminResponseObj getMemberUserList(HttpServletRequest request) {
		String methodName = "getMemberUserList";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				String.format("REQUEST_API: [GET]/admin/memberUserList"));

		List<NewsUserInfo> userList = commonSerivce.getMemberUserList(refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setMemberUserList(userList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/brokerUserList")
	public AdminResponseObj getBrokerUserList(HttpServletRequest request) {
		String methodName = "getBrokerUserList";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				String.format("REQUEST_API: [GET]/admin/brokerUserList"));

		List<NewsUserInfo> userList = commonSerivce.getBrokerUserList(refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setBrokerUserList(userList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/collaboratorUserList")
	public AdminResponseObj getCollaboratorUserList(HttpServletRequest request) {
		String methodName = "getCollaboratorUserList";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				String.format("REQUEST_API: [GET]/admin/collaboratorUserList"));

		List<NewsUserInfo> userList = commonSerivce.getCollaboratorUserList(refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setCollaboratorUserList(userList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/investorUserList")
	public AdminResponseObj getInvestorUserList(HttpServletRequest request) {
		String methodName = "getInvestorUserList";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				String.format("REQUEST_API: [GET]/admin/investorUserList"));

		List<NewsUserInfo> userList = commonSerivce.getInvestorUserList(refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setInvestorUserList(userList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
}
