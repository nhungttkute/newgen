/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.SerializationUtils;
import org.modelmapper.ModelMapper;
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
import com.newgen.am.dto.BasePagination;
import com.newgen.am.dto.ChangePasswordDTO;
import com.newgen.am.dto.LoginAdminUserOutputDTO;
import com.newgen.am.dto.LoginAdminUsersDTO;
import com.newgen.am.dto.LoginUserDataInputDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.LoginAdminUser;
import com.newgen.am.service.LoginAdminUserService;
import com.newgen.am.validation.ValidationSequence;

/**
 *
 * @author nhungtt
 */
@RestController
public class LoginAdminUserController {
	private String className = "LoginAdminUserController";

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private LoginAdminUserService loginAdmUserService;

	@PostMapping("/admin/users/login")
	public AdminResponseObj login(HttpServletRequest request, @RequestBody LoginUserDataInputDTO userDto) {
		String methodName = "login";
		long refId = System.currentTimeMillis();
		LoginUserDataInputDTO logUserDto = (LoginUserDataInputDTO) SerializationUtils.clone(userDto);
		logUserDto.setPassword("******");
		AMLogger.logMessage(className, methodName, refId,
				"REQUEST_API: [POST]/admin/users/login, INPUT:" + Utility.getGson().toJson(logUserDto));

		LoginAdminUserOutputDTO loginUserDto = loginAdmUserService.signin(request, userDto.getUsername(),
				userDto.getPassword(), refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
		response.setData(new AdminDataObj());
		response.getData().setUser(loginUserDto);

		AdminResponseObj logResponse = (AdminResponseObj) SerializationUtils.clone(response);
		if (logResponse != null && logResponse.getData() != null && logResponse.getData().getUser() != null) {
			logResponse.getData().setLayout("");
			logResponse.getData().getUser().setFunctions(null);
			logResponse.getData().getUser().setTableSetting("");
		}

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(logResponse));
		return response;
	}

	@PostMapping("/admin/users/logout/{userId}")
	public AdminResponseObj logout(HttpServletRequest request, @PathVariable String userId) {
		String methodName = "logout";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/users/logout, INPUT:" + userId);

		loginAdmUserService.logout(request, userId, refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}

	@PostMapping("/admin/users/verifyPin/{userId}")
	public AdminResponseObj verifyPin(@PathVariable String userId, @RequestBody LoginUserDataInputDTO user) {
		String methodName = "verifyPin";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/users/verifyPin/" + userId);

		AdminResponseObj response = new AdminResponseObj();
		if (loginAdmUserService.verifyPin(userId, user.getPin(), refId)) {
			response.setStatus(Constant.RESPONSE_OK);
		} else {
			response.setStatus(Constant.RESPONSE_ERROR);
			response.setErrMsg(ErrorMessage.INCORRECT_PIN);
		}

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}

	@PostMapping("/admin/users/{userId}/password")
	public AdminResponseObj changePassword(HttpServletRequest request, @PathVariable String userId,
			@Validated(ValidationSequence.class) @RequestBody ChangePasswordDTO input) {
		String methodName = "changePassword";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				"REQUEST_API: " + String.format("[POST]/admin/users/%s/password", userId));

		loginAdmUserService.changePassword(request, userId, input.getOldPassword(), input.getNewPassword(), refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}

	@PostMapping("/admin/users/layout")
	public AdminResponseObj saveLayout(@PathVariable String userId, @RequestBody LoginUserDataInputDTO input) {
		String methodName = "saveLayout";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/users/layout");

		AdminResponseObj response = new AdminResponseObj();
		try {
			loginAdmUserService.saveAdmUserLayout(input.getLayout(), refId);
			response.setStatus(Constant.RESPONSE_OK);
		} catch (Exception ex) {
			AMLogger.logError(className, methodName, refId, ex);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}

	@PostMapping("/admin/users/tableSetting")
	public AdminResponseObj saveTableSetting(@RequestBody LoginUserDataInputDTO input) {
		String methodName = "saveLayout";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/users/tableSetting");

		AdminResponseObj response = new AdminResponseObj();
		try {
			loginAdmUserService.saveAdmUserTableSetting(input.getTableSetting(), refId);
			response.setStatus(Constant.RESPONSE_OK);
		} catch (Exception ex) {
			AMLogger.logError(className, methodName, refId, ex);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}

	@PostMapping("/admin/users/{userId}/watchlist")
	public AdminResponseObj saveWatchList(@PathVariable String userId, @RequestBody LoginUserDataInputDTO input) {
		String methodName = "saveWatchList";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				"REQUEST_API: " + String.format("[POST]/admin/users/%s/watchlist", userId));

		AdminResponseObj response = new AdminResponseObj();
		try {
			LoginAdminUser user = loginAdmUserService.search(userId, refId);
			if (user != null) {
				user.setWatchlists(input.getWatchlists());
				LoginAdminUser newUser = loginAdmUserService.save(user, refId);
				response.setStatus(Constant.RESPONSE_OK);
				response.setData(new AdminDataObj());
				response.getData().setWatchLists(newUser.getWatchlists());
			} else {
				response.setStatus(Constant.RESPONSE_ERROR);
				response.setErrMsg(ErrorMessage.USER_DOES_NOT_EXIST);
			}
		} catch (Exception ex) {
			AMLogger.logError(className, methodName, refId, ex);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}

	@PutMapping("/admin/users/resetPassword")
	public AdminResponseObj resetPassword(HttpServletRequest request,
			@Validated(ValidationSequence.class) @RequestBody LoginUserDataInputDTO userDto) {
		String methodName = "resetPassword";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				"REQUEST_API: [PUT]/admin/users/resetPassword, INPUT:" + Utility.getGson().toJson(userDto));

		loginAdmUserService.resetAdminUserPassword(request, userDto, refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}

	@PutMapping("/admin/users/resetPin")
	public AdminResponseObj resetPin(HttpServletRequest request,
			@Validated(ValidationSequence.class) @RequestBody LoginUserDataInputDTO userDto) {
		String methodName = "resetPin";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				"REQUEST_API: [PUT]/admin/users/resetPin, INPUT:" + Utility.getGson().toJson(userDto));

		loginAdmUserService.resetAdminUserPin(request, userDto, refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}

	@PutMapping("/admin/users/resetInvestorUserPassword")
	public AdminResponseObj resetInvestorUserPassword(HttpServletRequest request,
			@Validated(ValidationSequence.class) @RequestBody LoginUserDataInputDTO userDto) {
		String methodName = "resetInvestorUserPassword";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				"REQUEST_API: [PUT]/admin/users/resetInvestorUserPassword, INPUT:" + Utility.getGson().toJson(userDto));

		loginAdmUserService.resetInvestorUserPassword(request, userDto, refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}

	@PutMapping("/admin/users/resetInvestorUserPin")
	public AdminResponseObj resetInvestorUserPin(HttpServletRequest request,
			@Validated(ValidationSequence.class) @RequestBody LoginUserDataInputDTO userDto) {
		String methodName = "resetPin";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				"REQUEST_API: [PUT]/admin/users/resetInvestorUserPin, INPUT:" + Utility.getGson().toJson(userDto));

		loginAdmUserService.resetInvestorUserPin(request, userDto, refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}

	@GetMapping("/admin/users")
	@PreAuthorize("hasAuthority('systemManagement.loginUserStatusList')")
	public AdminResponseObj listAdminUsers(HttpServletRequest request) {
		String methodName = "listAdminUsers";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/users");
		AdminResponseObj response = new AdminResponseObj();

		try {
			BasePagination<LoginAdminUsersDTO> pagination = loginAdmUserService.listAdminUsers(request, refId);
			if (pagination != null && pagination.getData().size() > 0) {
				response.setStatus(Constant.RESPONSE_OK);
				response.setData(new AdminDataObj());
				response.getData().setAdminUsers(pagination.getData());
				response.setPagination(Utility.getPagination(request, pagination.getCount()));
				response.setFilterList(Arrays.asList(Constant.STATUS_ACTIVE, Constant.STATUS_INACTIVE));
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

	@GetMapping("/admin/users/excel")
	@PreAuthorize("hasAuthority('systemManagement.loginUserStatusList')")
	public ResponseEntity<Resource> downloadAdminUsersExcel(HttpServletRequest request) {
		String methodName = "downloadAdminUsersExcel";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/users/excel");

		try {
			InputStreamResource file = new InputStreamResource(loginAdmUserService.loadAdminUsersExcel(request, refId));

			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + Constant.EXCEL_ADMIN_USERS)
					.contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/admin/users/functions")
	public AdminResponseObj listUserFunctions(HttpServletRequest request) {
		String methodName = "listAdminUsers";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/users/functions");
		AdminResponseObj response = new AdminResponseObj();

		try {
			List<String> functions = loginAdmUserService.getFunctionsByUsername(Utility.getCurrentUsername());
			if (functions != null && functions.size() > 0) {
				response.setStatus(Constant.RESPONSE_OK);
				response.setData(new AdminDataObj());
				response.getData().setFunctions(functions);
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

}
