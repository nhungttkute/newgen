/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.controller;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import com.newgen.am.common.CustomMappingStrategy;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.AdminDataObj;
import com.newgen.am.dto.AdminResponseObj;
import com.newgen.am.dto.AdminUserDTO;
import com.newgen.am.dto.ApprovalFunctionsDTO;
import com.newgen.am.dto.ApprovalUpdateAdminUserDTO;
import com.newgen.am.dto.ApprovalUpdateDepartmentDTO;
import com.newgen.am.dto.ApprovalUpdateUserDTO;
import com.newgen.am.dto.ApprovalUserRolesDTO;
import com.newgen.am.dto.BasePagination;
import com.newgen.am.dto.DepartmentCSV;
import com.newgen.am.dto.DepartmentDTO;
import com.newgen.am.dto.UserCSV;
import com.newgen.am.dto.UserDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.service.DepartmentService;
import com.newgen.am.validation.ValidationSequence;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

/**
 *
 * @author nhungtt
 */
@RestController
public class DepartmentController {
	private String className = "DepartmentController";

	@Autowired
	private DepartmentService deptService;

	@GetMapping("/admin/departments")
	@PreAuthorize("hasAuthority('adminUserManagement.departmentManagement.departmentList.view')")
	public AdminResponseObj listDeparments(HttpServletRequest request) {
		String methodName = "listDeparments";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/departments");
		AdminResponseObj response = new AdminResponseObj();
		
		try {
			BasePagination<DepartmentDTO> pagination = deptService.list(request, refId);
			if (pagination != null && pagination.getData().size() > 0) {
				response.setStatus(Constant.RESPONSE_OK);
				response.setData(new AdminDataObj());
				response.getData().setDepartments(pagination.getData());
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
		
		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}

	@GetMapping("/admin/departments/csv")
	@PreAuthorize("hasAuthority('adminUserManagement.departmentManagement.departmentList.view')")
	public void downloadDeparmentsCsv(HttpServletRequest request, HttpServletResponse response) {
		String methodName = "downloadDeparmentsCsv";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/departments/csv");

		try {
			// set file name and content type
			String filename = Constant.CSV_DEPARTMENTS;

			response.setContentType("text/csv");
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
			response.setCharacterEncoding("UTF-8");

			// create a csv writer
			CustomMappingStrategy<DepartmentCSV> mappingStrategy = new CustomMappingStrategy<DepartmentCSV>();
			mappingStrategy.setType(DepartmentCSV.class);

			StatefulBeanToCsv<DepartmentCSV> writer = new StatefulBeanToCsvBuilder<DepartmentCSV>(response.getWriter())
					.withMappingStrategy(mappingStrategy).withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
					.withSeparator(CSVWriter.DEFAULT_SEPARATOR).withOrderedResults(false).build();

			// write all users to csv file
			writer.write(deptService.listCsv(request, refId));
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/admin/departments")
	@PreAuthorize("hasAuthority('adminUserManagement.departmentManagement.departmentInfo.create')")
	public AdminResponseObj createDepartment(HttpServletRequest request, @Validated(ValidationSequence.class) @RequestBody DepartmentDTO deptDto) {
		String methodName = "createDepartment";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/deparments");
		AMLogger.logMessage(className, methodName, refId, "INPUT:" + Utility.getGson().toJson(deptDto));

		if (Utility.isNotNull(deptDto)) {
			deptService.createDepartmentPA(request, deptDto, refId);
		} else {
			throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
		}
		
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}

	@PutMapping("/admin/departments/{deptId}")
	@PreAuthorize("hasAuthority('adminUserManagement.departmentManagement.departmentInfo.update')")
	public AdminResponseObj updateDepartment(HttpServletRequest request, @PathVariable String deptId,
			@Validated(ValidationSequence.class) @RequestBody ApprovalUpdateDepartmentDTO deptDto) {
		String methodName = "updateDepartment";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [PUT]/admin/deparments/" + deptId);
		AMLogger.logMessage(className, methodName, refId, "INPUT:" + Utility.getGson().toJson(deptDto));

		if (Utility.isNotNull(deptDto)) {
			deptService.updateDepartmentPA(request, deptId, deptDto, refId);
		} else {
			throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
		}

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}

	@GetMapping("/admin/departments/{deptId}")
	@PreAuthorize("hasAuthority('adminUserManagement.departmentManagement.departmentList.view')")
	public AdminResponseObj getDepartmentDetail(@PathVariable String deptId) {
		String methodName = "getDepartmentDetail";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/deparments/" + deptId);
		AdminResponseObj response = new AdminResponseObj();

		DepartmentDTO deptDto = deptService.getDepartmentDetail(deptId, refId);
		if (deptDto != null) {
			response.setStatus(Constant.RESPONSE_OK);
			response.setData(new AdminDataObj());
			response.getData().setDepartment(deptDto);
		} else {
			response.setStatus(Constant.RESPONSE_ERROR);
			response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
		}

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}

	@GetMapping("/admin/departments/{deptId}/users")
	@PreAuthorize("hasAuthority('adminUserManagement.departmentManagement.loginAdminUserManagement.adminUserList.view')")
	public AdminResponseObj listDeparmentUsers(HttpServletRequest request, @PathVariable String deptId) {
		String methodName = "listDeparmentUsers";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				String.format("REQUEST_API: [GET]/admin/departments/%s/users", deptId));

		AdminResponseObj response = new AdminResponseObj();
		try {
			BasePagination<UserDTO> pagination = deptService.listDeptUsers(request, deptId, refId);
			if (pagination != null && pagination.getData().size() > 0) {
				response.setStatus(Constant.RESPONSE_OK);
				response.setData(new AdminDataObj());
				response.getData().setDeptUsers(pagination.getData());
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

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}

	@GetMapping("/admin/departments/{deptId}/users/csv")
    @PreAuthorize("hasAuthority('adminUserManagement.departmentManagement.loginAdminUserManagement.adminUserList.view')")
    public void downloadDeparmentUsersCsv(HttpServletRequest request, HttpServletResponse response, @PathVariable String deptId) {
        String methodName = "downloadDeparmentUsersCsv";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, String.format("REQUEST_API: [GET]/admin/departments/%s/users/csv", deptId));
        
        try {
        	//set file name and content type
            String filename = Constant.CSV_DEPARTMENT_USERS;

            response.setContentType("text/csv");
            response.setCharacterEncoding("UTF-8");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + filename + "\"");

            //create a csv writer
            CustomMappingStrategy<UserCSV> mappingStrategy = new CustomMappingStrategy<UserCSV>();
            mappingStrategy.setType(UserCSV.class);
            StatefulBeanToCsv<UserCSV> writer = new StatefulBeanToCsvBuilder<UserCSV>(response.getWriter())
            		.withMappingStrategy(mappingStrategy)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withOrderedResults(false)
                    .build();

            //write all users to csv file
            writer.write(deptService.listDeptUsersCsv(request, deptId, refId));
    	} catch (Exception e) {
    		AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }

	@PostMapping("/admin/departments/{deptId}/users")
	@PreAuthorize("hasAuthority('adminUserManagement.departmentManagement.loginAdminUserManagement.adminUserInfo.create')")
	public AdminResponseObj createDepartmentUser(HttpServletRequest request, @PathVariable String deptId,
			@Validated(ValidationSequence.class) @RequestBody AdminUserDTO deptUserDto) {
		String methodName = "createDepartmentUser";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/departments/users");
		AMLogger.logMessage(className, methodName, refId, "INPUT:" + Utility.getGson().toJson(deptUserDto));

		deptService.createDepartmentUserPA(request, deptId, deptUserDto, refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}

	@PutMapping("/admin/departments/{deptId}/users/{deptUserId}")
	@PreAuthorize("hasAuthority('adminUserManagement.departmentManagement.loginAdminUserManagement.adminUserInfo.update')")
	public AdminResponseObj updateDepartmentUser(HttpServletRequest request, @PathVariable String deptId,
			@PathVariable String deptUserId, @Validated(ValidationSequence.class) @RequestBody ApprovalUpdateAdminUserDTO deptUserDto) {
		String methodName = "updateDepartmentUser";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				String.format("REQUEST_API: [PUT]/admin/departments/%s/users/%s", deptId, deptUserId));
		AMLogger.logMessage(className, methodName, refId, "INPUT:" + Utility.getGson().toJson(deptUserDto));

		deptService.updateDepartmentUserPA(request, deptId, deptUserId, deptUserDto, refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}

	@GetMapping("/admin/departments/{deptId}/users/{deptUserId}")
	@PreAuthorize("hasAuthority('adminUserManagement.departmentManagement.loginAdminUserManagement.adminUserInfo.view')")
	public AdminResponseObj getDepartmentUser(@PathVariable String deptId, @PathVariable String deptUserId) {
		String methodName = "getDepartmentUser";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				String.format("REQUEST_API: [GET]/admin/departments/%s/users/%s", deptId, deptUserId));
		AdminResponseObj response = new AdminResponseObj();

		UserDTO deptUserDto = deptService.getDepartmentUser(deptId, deptUserId, refId);
		if (deptUserDto != null) {
			response.setStatus(Constant.RESPONSE_OK);
			response.setData(new AdminDataObj());
			response.getData().setDeptUser(deptUserDto);
		} else {
			response.setStatus(Constant.RESPONSE_ERROR);
			response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
		}

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}

	@PostMapping("/admin/departments/{deptId}/users/{deptUserId}/roles")
	@PreAuthorize("hasAuthority('adminUserManagement.departmentManagement.loginAdminUserManagement.adminUserRoleAssign.create')")
	public AdminResponseObj saveDepartmentUserRoles(HttpServletRequest request, @PathVariable String deptId,
			@PathVariable String deptUserId, @Validated(ValidationSequence.class) @RequestBody ApprovalUserRolesDTO deptUserDto) {
		String methodName = "saveDepartmentUserRoles";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				String.format("REQUEST_API: [POST]/admin/departments/%s/users/%s/roles", deptId, deptUserId));

		deptService.saveDepartmentUserRolesPA(request, deptId, deptUserId, deptUserDto, refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}

	@PostMapping("/admin/departments/{deptId}/users/{deptUserId}/functions")
	@PreAuthorize("hasAuthority('adminUserManagement.departmentManagement.loginAdminUserManagement.adminUserFunctionsAssign.create')")
	public AdminResponseObj saveDepartmentUserFunctions(HttpServletRequest request, @PathVariable String deptId,
			@PathVariable String deptUserId, @Validated(ValidationSequence.class) @RequestBody ApprovalFunctionsDTO deptUserDto) {
		String methodName = "saveDepartmentUserFunctions";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				String.format("REQUEST_API: [POST]/admin/departments/%s/users/%s/functions", deptId, deptUserId));

		deptService.saveDepartmentUserFunctionsPA(request, deptId, deptUserId, deptUserDto, refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}

	@PutMapping("/admin/departments/users/changeDepartment/{fromDeptId}/{toDeptId}/{username}")
	@PreAuthorize("hasAuthority('adminUserManagement.departmentManagement.loginAdminUserManagement.adminUserDeptChange.update')")
	public AdminResponseObj changeUserDepartment(HttpServletRequest request, @PathVariable String fromDeptId,
			@PathVariable String toDeptId, @PathVariable String username) {
		String methodName = "changeUserDepartment";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				String.format("REQUEST_API: [PUT]/admin/departments/users/changeDepartment/%s/%s/%s", fromDeptId,
						toDeptId, username));

		deptService.changeUserDepartmentPA(request, fromDeptId, toDeptId, username, refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
}
