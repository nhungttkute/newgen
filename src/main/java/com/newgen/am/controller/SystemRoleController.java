/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.controller;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.newgen.am.dto.ApprovalFunctionsDTO;
import com.newgen.am.dto.ApprovalUpdateRoleDTO;
import com.newgen.am.dto.BasePagination;
import com.newgen.am.dto.RoleCSV;
import com.newgen.am.dto.RoleDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.service.SystemRoleService;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

/**
 *
 * @author nhungtt
 */
@RestController
public class SystemRoleController {
    private String className = "SystemRoleController";
    
    @Autowired
    SystemRoleService sysRoleService;
    
    @GetMapping("/admin/systemRoles")
    @PreAuthorize("hasAuthority('adminUserManagement.roleManagement.roleList.view') or hasAuthority('adminUserManagement.departmentManagement.loginAdminUserManagement.adminRoleList.view')")
    public AdminResponseObj listSystemRoles(HttpServletRequest request) {
        String methodName = "listSystemRoles";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/systemRoles");
        
        AdminResponseObj response = new AdminResponseObj();
        try {
        	BasePagination<RoleDTO> pagination = sysRoleService.list(request, refId);
            if (pagination != null && pagination.getData().size() > 0) {
                response.setStatus(Constant.RESPONSE_OK);
                response.setData(new AdminDataObj());
                response.getData().setSystemRoles(pagination.getData());
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
    
    @GetMapping("/admin/systemRoles/csv")
    @PreAuthorize("hasAuthority('adminUserManagement.roleManagement.roleList.view')")
    public void downloadSystemRolesCsv(HttpServletRequest request, HttpServletResponse response) {
        String methodName = "downloadSystemRolesCsv";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/systemRoles/csv");
        
        try {
        	//set file name and content type
            String filename = Constant.CSV_SYSTEM_ROLES;

            response.setContentType("text/csv");
            response.setCharacterEncoding("UTF-8");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + filename + "\"");

            //create a csv writer
            CustomMappingStrategy<RoleCSV> mappingStrategy = new CustomMappingStrategy<RoleCSV>();
            mappingStrategy.setType(RoleCSV.class);
            
            StatefulBeanToCsv<RoleCSV> writer = new StatefulBeanToCsvBuilder<RoleCSV>(response.getWriter())
            		.withMappingStrategy(mappingStrategy)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withOrderedResults(false)
                    .build();

            //write all users to csv file
            writer.write(sysRoleService.listCsv(request, refId));
        } catch (Exception e) {
        	AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/admin/systemRoles")
    @PreAuthorize("hasAuthority('adminUserManagement.roleManagement.roleInfo.create')")
    public AdminResponseObj createSystemRole(HttpServletRequest request, @Valid @RequestBody RoleDTO sysRoleDto) {
        String methodName = "createSystemRole";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/systemRoles");
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + Utility.getGson().toJson(sysRoleDto));
        
        sysRoleService.createSystemRolePA(request, sysRoleDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }
    
    @PutMapping("/admin/systemRoles/{sysRoleId}")
    @PreAuthorize("hasAuthority('adminUserManagement.roleManagement.roleInfo.update')")
    public AdminResponseObj updateSystemRole(HttpServletRequest request, @PathVariable String sysRoleId, @Valid @RequestBody ApprovalUpdateRoleDTO sysRoleDto) {
        String methodName = "updateSystemRole";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [PUT]/admin/systemRoles/" + sysRoleId);
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + Utility.getGson().toJson(sysRoleDto));
        
        sysRoleService.updateSystemRolePA(request, sysRoleId, sysRoleDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }
    
    @PostMapping("/admin/systemRoles/{sysRoleId}/functions")
    @PreAuthorize("hasAuthority('adminUserManagement.roleManagement.roleFunctionsAssign.create')")
    public AdminResponseObj createSystemRoleFunctions(HttpServletRequest request, @PathVariable String sysRoleId, @Valid @RequestBody ApprovalFunctionsDTO sysRoleDto) {
        String methodName = "createSystemRoleFunctions";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[POST]/admin/systemRoles/%s/functions", sysRoleId));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + Utility.getGson().toJson(sysRoleDto));
        
        sysRoleService.createSystemRoleFunctionsPA(request, sysRoleId, sysRoleDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }
}
