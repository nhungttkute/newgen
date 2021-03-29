package com.newgen.am.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.AdminDataObj;
import com.newgen.am.dto.AdminResponseObj;
import com.newgen.am.dto.ApprovalExchangeSettingDTO;
import com.newgen.am.dto.BasePagination;
import com.newgen.am.dto.ExchangeSettingDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.service.ExchangeSettingService;
import com.newgen.am.validation.ValidationSequence;

@RestController
public class ExchangeSettingController {
	private String className = "ExchangeSettingController";
	
	@Autowired
	private ExchangeSettingService exchangeSettingService;
	
	@PostMapping("/admin/exchangeSetting")
	@PreAuthorize("hasAuthority('exchangeSetting.create')")
	public AdminResponseObj setUsersExchanges(HttpServletRequest request, @Validated(ValidationSequence.class) @RequestBody ExchangeSettingDTO exchangeDto) {
		String methodName = "setUsersExchanges";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/exchangeSetting, INPUT:" + Utility.getGson().toJson(exchangeDto));

		exchangeSettingService.setExchangeSettingPA(request, exchangeDto, refId);
		
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@PutMapping("/admin/exchangeSetting")
	@PreAuthorize("hasAuthority('exchangeSetting.update')")
	public AdminResponseObj updateUsersExchanges(HttpServletRequest request, @Validated(ValidationSequence.class) @RequestBody ApprovalExchangeSettingDTO exchangeDto) {
		String methodName = "updateUsersExchanges";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/exchangeSetting, INPUT:" + Utility.getGson().toJson(exchangeDto));

		exchangeSettingService.updateExchangeSettingPA(request, exchangeDto, refId);
		
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/adminUserExchanges")
	@PreAuthorize("hasAuthority('exchangeSetting.list')")
	public AdminResponseObj listLoginAdmUserExchanges(HttpServletRequest request) {
		String methodName = "listLoginAdmUserExchanges";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/adminUserExchanges");
		AdminResponseObj response = new AdminResponseObj();
		
		try {
			BasePagination<ExchangeSettingDTO> pagination = exchangeSettingService.listLoginAdmUserExchanges(request, refId);
			if (pagination != null && pagination.getData().size() > 0) {
				response.setStatus(Constant.RESPONSE_OK);
				response.setData(new AdminDataObj());
				response.getData().setAdminUserExchanges(pagination.getData());
				response.setPagination(Utility.getPagination(request, pagination.getCount()));
			} else {
				response.setStatus(Constant.RESPONSE_ERROR);
				response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		AMLogger.logMessage(className, methodName, refId, "OUTPUT: OK");
		return response;
	}
	
	@GetMapping("/admin/adminUserExchanges/excel")
	@PreAuthorize("hasAuthority('exchangeSetting.list')")
	public ResponseEntity<Resource> downloadLoginAdmUserExchangesExcel(HttpServletRequest request) {
		String methodName = "downloadLoginAdmUserExchangesExcel";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/adminUserExchanges/excel");
		
		try {
			InputStreamResource file = new InputStreamResource(exchangeSettingService.loadLoginAdmUserExchangesExcel(request, refId));

			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + Constant.EXCEL_ADM_USER_EXCHANGES)
					.contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/admin/investorUserExchanges")
	@PreAuthorize("hasAuthority('exchangeSetting.list')")
	public AdminResponseObj listLoginInvUserExchanges(HttpServletRequest request) {
		String methodName = "listLoginInvUserExchanges";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/investorUserExchanges");
		AdminResponseObj response = new AdminResponseObj();
		
		try {
			BasePagination<ExchangeSettingDTO> pagination = exchangeSettingService.listLoginInvUserExchanges(request, refId);
			if (pagination != null && pagination.getData().size() > 0) {
				response.setStatus(Constant.RESPONSE_OK);
				response.setData(new AdminDataObj());
				response.getData().setInvestorUserExchanges(pagination.getData());
				response.setPagination(Utility.getPagination(request, pagination.getCount()));
			} else {
				response.setStatus(Constant.RESPONSE_ERROR);
				response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		AMLogger.logMessage(className, methodName, refId, "OUTPUT: OK");
		return response;
	}
	
	@GetMapping("/admin/investorUserExchanges/excel")
	@PreAuthorize("hasAuthority('exchangeSetting.list')")
	public ResponseEntity<Resource> downloadLoginInvUserExchangesExcel(HttpServletRequest request) {
		String methodName = "downloadLoginInvUserExchangesExcel";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/investorUserExchanges/excel");
		
		try {
			InputStreamResource file = new InputStreamResource(exchangeSettingService.loadLoginInvUserExchangesExcel(request, refId));

			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + Constant.EXCEL_INV_USER_EXCHANGES)
					.contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
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

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
}
