package com.newgen.am.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.Constant;
import com.newgen.am.dto.AdminDataObj;
import com.newgen.am.dto.AdminResponseObj;
import com.newgen.am.dto.ApprovalExchangeSettingDTO;
import com.newgen.am.dto.ExchangeSettingDTO;
import com.newgen.am.service.ExchangeSettingService;
import com.newgen.am.validation.ValidationSequence;

@RestController
public class ExchangeSettingController {
	private String className = "ExchangeSettingController";
	
	@Autowired
	private ExchangeSettingService exchangeSettingService;
	
	@PostMapping("/admin/exchangeSettings")
	@PreAuthorize("hasAuthority('exchangeSetting.create')")
	public AdminResponseObj setUsersExchanges(HttpServletRequest request, @Validated(ValidationSequence.class) @RequestBody ExchangeSettingDTO exchangeDto) {
		String methodName = "setUsersExchanges";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/exchangeSetting");
		AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(exchangeDto));

		exchangeSettingService.setExchangeSettingPA(request, exchangeDto, refId);
		
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
	
	@PutMapping("/admin/exchangeSetting")
	@PreAuthorize("hasAuthority('exchangeSetting.update')")
	public AdminResponseObj updateUsersExchanges(HttpServletRequest request, @Validated(ValidationSequence.class) @RequestBody ApprovalExchangeSettingDTO exchangeDto) {
		String methodName = "updateUsersExchanges";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/exchangeSetting");
		AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(exchangeDto));

		exchangeSettingService.updateExchangeSettingPA(request, exchangeDto, refId);
		
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/exchangeSetting")
	@PreAuthorize("hasAuthority('exchangeSetting.list')")
	public AdminResponseObj getExchangeSettings() {
		String methodName = "getExchangeSettings";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/exchangeSetting");

		List<ExchangeSettingDTO> exchangeSettings = exchangeSettingService.listExchangeSettings(refId);
		
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
		response.setData(new AdminDataObj());
		response.getData().setExchangeSettings(exchangeSettings);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/exchangeSetting/{username}/{investorCode}")
	@PreAuthorize("hasAuthority('exchangeSetting.view')")
	public AdminResponseObj getExchangeSetting(@PathVariable String username, @PathVariable String investorCode) {
		String methodName = "getExchangeSettings";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/exchangeSetting/" + username);

		ExchangeSettingDTO exchangeSetting = exchangeSettingService.getExchangeSetting(username, investorCode, refId);
		
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
		response.setData(new AdminDataObj());
		response.getData().setExchangeSetting(exchangeSetting);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
}
