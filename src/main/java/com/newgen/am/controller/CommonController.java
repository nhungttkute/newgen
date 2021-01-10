package com.newgen.am.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.newgen.am.common.AMLogger;
import com.newgen.am.common.Constant;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.AdminDataObj;
import com.newgen.am.dto.AdminResponseObj;
import com.newgen.am.dto.ListElementDTO;
import com.newgen.am.dto.UserBaseInfo;
import com.newgen.am.exception.CustomException;
import com.newgen.am.service.CommonService;

@RestController
public class CommonController {
	private String className = "CommonController";
	
	@Autowired
	private CommonService commonSerivce;
	
	@GetMapping("/admin/info")
	public AdminResponseObj getInfo(HttpServletRequest request) {
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
		return response;
	}
	
	@GetMapping("/admin/departmentList")
	public AdminResponseObj getDepartmentList(HttpServletRequest request) {
		String methodName = "getDepartmentList";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/departmentList");

		List<ListElementDTO> departmentList = commonSerivce.getDeptList(refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setDepartmentList(departmentList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/memberList")
	public AdminResponseObj getMemberList(HttpServletRequest request) {
		String methodName = "getMemberList";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/memberList");

		List<ListElementDTO> memberList = commonSerivce.getMemberList(request, refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setMemberList(memberList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/brokerList")
	public AdminResponseObj getBrokerList(HttpServletRequest request) {
		String methodName = "getBrokerList";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/brokerList");

		List<ListElementDTO> brokerList = commonSerivce.getBrokerList(request, refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setBrokerList(brokerList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/collaboratorList")
	public AdminResponseObj getCollaboratorList(HttpServletRequest request) {
		String methodName = "getCollaboratorList";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/collaboratorList");

		List<ListElementDTO> collaboratorList = commonSerivce.getCollaboratorList(request, refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setCollaboratorList(collaboratorList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/investorList")
	public AdminResponseObj getInvestorList(HttpServletRequest request) {
		String methodName = "getInvestorList";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/investorList");

		List<ListElementDTO> investorList = commonSerivce.getInvestorList(request, refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setInvestorList(investorList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/adminUserList")
	public AdminResponseObj getAdminUserList(HttpServletRequest request) {
		String methodName = "getAdminUserList";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/adminUserList");

		List<UserBaseInfo> userList = commonSerivce.getAdminUserList(request, refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setAdminUserList(userList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/memberUserList")
	public AdminResponseObj getMemberUserList(HttpServletRequest request) {
		String methodName = "getMemberUserList";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/memberUserList");

		List<UserBaseInfo> userList = commonSerivce.getMemberUserList(request, refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setMemberUserList(userList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/brokerUserList")
	public AdminResponseObj getBrokerUserList(HttpServletRequest request) {
		String methodName = "getBrokerUserList";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/brokerUserList");

		List<UserBaseInfo> userList = commonSerivce.getBrokerUserList(request, refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setBrokerUserList(userList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/collaboratorUserList")
	public AdminResponseObj getCollaboratorUserList(HttpServletRequest request) {
		String methodName = "getCollaboratorUserList";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/collaboratorUserList");

		List<UserBaseInfo> userList = commonSerivce.getCollaboratorUserList(request, refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setCollaboratorUserList(userList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/investorUserList")
	public AdminResponseObj getInvestorUserList(HttpServletRequest request) {
		String methodName = "getInvestorUserList";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/investorUserList");

		List<UserBaseInfo> userList = commonSerivce.getInvestorUserList(request, refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setInvestorUserList(userList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/adminUserListLocal")
	public AdminResponseObj getAdminUserListLocal(HttpServletRequest request) {
		String methodName = "getAdminUserListLocal";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/adminUserListLocal");

		if (!Utility.isLocalRequest(request)) throw new CustomException(ErrorMessage.ACCESS_DENIED, HttpStatus.FORBIDDEN);
		
		List<UserBaseInfo> userList = commonSerivce.getAdminUserList(request, refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setAdminUserList(userList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/memberUserListLocal")
	public AdminResponseObj getMemberUserListLocal(HttpServletRequest request) {
		String methodName = "getMemberUserListLocal";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/memberUserListLocal");

		if (!Utility.isLocalRequest(request)) throw new CustomException(ErrorMessage.ACCESS_DENIED, HttpStatus.FORBIDDEN);
		
		List<UserBaseInfo> userList = commonSerivce.getMemberUserList(request, refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setMemberUserList(userList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/brokerUserListLocal")
	public AdminResponseObj getBrokerUserListLocal(HttpServletRequest request) {
		String methodName = "getBrokerUserListLocal";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/brokerUserListLocal");

		if (!Utility.isLocalRequest(request)) throw new CustomException(ErrorMessage.ACCESS_DENIED, HttpStatus.FORBIDDEN);
		
		List<UserBaseInfo> userList = commonSerivce.getBrokerUserList(request, refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setBrokerUserList(userList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/collaboratorUserListLocal")
	public AdminResponseObj getCollaboratorUserListLocal(HttpServletRequest request) {
		String methodName = "getCollaboratorUserListLocal";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/collaboratorUserListLocal");

		if (!Utility.isLocalRequest(request)) throw new CustomException(ErrorMessage.ACCESS_DENIED, HttpStatus.FORBIDDEN);
		
		List<UserBaseInfo> userList = commonSerivce.getCollaboratorUserList(request, refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setCollaboratorUserList(userList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/investorUserListLocal")
	public AdminResponseObj getInvestorUserListLocal(HttpServletRequest request) {
		String methodName = "getInvestorUserListLocal";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/investorUserListLocal");

		if (!Utility.isLocalRequest(request)) throw new CustomException(ErrorMessage.ACCESS_DENIED, HttpStatus.FORBIDDEN);
		
		List<UserBaseInfo> userList = commonSerivce.getInvestorUserList(request, refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setInvestorUserList(userList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/usersByDeptCode/{deptCode:.+}")
	public AdminResponseObj getUsersByDeptCode(HttpServletRequest request, @PathVariable String deptCode) {
		String methodName = "getUsersByDeptCode";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/usersByDeptCode/" + deptCode);

		List<UserBaseInfo> userList = commonSerivce.getUsersByDeptCode(deptCode, refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setAdminUserList(userList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/usersByMemberCode/{memberCode}/")
	public AdminResponseObj getUsersByMemberCode(@PathVariable String memberCode) {
		String methodName = "getUsersByMemberCode";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/usersByMemberCode/" + memberCode);

		List<UserBaseInfo> userList = commonSerivce.getUsersByMemberCode(memberCode, refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setMemberUserList(userList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/usersByBrokerCode/{brokerCode}/")
	public AdminResponseObj getUsersByBrokerCode(@PathVariable String brokerCode) {
		String methodName = "getUsersByBrokerCode";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,"REQUEST_API: [GET]/admin/usersByBrokerCode/" + brokerCode);

		List<UserBaseInfo> userList = commonSerivce.getUsersByBrokerCode(brokerCode, refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setBrokerUserList(userList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/usersByCollaboratorCode/{collaboratorCode}/")
	public AdminResponseObj getUsersByCollaboratorCode(@PathVariable String collaboratorCode) {
		String methodName = "getUsersByCollaboratorCode";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/usersByCollaboratorCode/" + collaboratorCode);

		List<UserBaseInfo> userList = commonSerivce.getUsersByCollaboratorCode(collaboratorCode, refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setCollaboratorUserList(userList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/usersByInvestorCode/{investorCode}/")
	public AdminResponseObj getUsersByInvestorCode(@PathVariable String investorCode) {
		String methodName = "getUsersByInvestorCode";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/usersByInvestorCode/" + investorCode);

		List<UserBaseInfo> userList = commonSerivce.getUsersByInvestorCode(investorCode, refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setInvestorUserList(userList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
}
