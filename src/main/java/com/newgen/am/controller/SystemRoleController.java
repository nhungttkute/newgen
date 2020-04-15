/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.controller;

import com.google.gson.Gson;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.Constant;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.dto.AdminDataObj;
import com.newgen.am.dto.AdminResponseObj;
import com.newgen.am.dto.SystemRoleDTO;
import com.newgen.am.service.SystemRoleService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public AdminResponseObj listSystemRoles() {
        String methodName = "listSystemRoles";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/systemRoles");
        AdminResponseObj response = new AdminResponseObj();
        try {
            List<SystemRoleDTO> sysRoleDtos = sysRoleService.list(refId);
            if (sysRoleDtos != null && sysRoleDtos.size() > 0) {
                response.setStatus(Constant.RESPONSE_OK);
                response.setData(new AdminDataObj());
                response.getData().setSystemRoles(sysRoleDtos);
            } else {
                response.setStatus(Constant.RESPONSE_ERROR);
                response.setErrMsg(ErrorMessage.NO_RESULT_FOUND);
            }
        } catch (Exception ex) {
            AMLogger.logError(className, methodName, refId, ex);
            response.setStatus(Constant.RESPONSE_ERROR);
            response.setErrMsg(ex.getMessage());
        }
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
    
    @PostMapping("/admin/systemRoles")
    public AdminResponseObj createSystemRole(HttpServletRequest request, @RequestBody SystemRoleDTO sysRoleDto) {
        String methodName = "createSystemRole";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/systemRoles");
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(sysRoleDto));
        AdminResponseObj response = new AdminResponseObj();
        try {
            boolean result = sysRoleService.createSystemRole(request, sysRoleDto, refId);
            if (result) {
                response.setStatus(Constant.RESPONSE_OK);
            } else {
                response.setStatus(Constant.RESPONSE_ERROR);
                response.setErrMsg(ErrorMessage.ERROR_OCCURRED);
            }
        } catch (Exception ex) {
            AMLogger.logError(className, methodName, refId, ex);
            response.setStatus(Constant.RESPONSE_ERROR);
            response.setErrMsg(ex.getMessage());
        }
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
    
    @PutMapping("/admin/systemRoles/{sysRoleId}")
    public AdminResponseObj updateSystemRole(HttpServletRequest request, @PathVariable Long sysRoleId, @RequestBody SystemRoleDTO sysRoleDto) {
        String methodName = "updateSystemRole";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [PUT]/admin/systemRoles/" + sysRoleId);
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(sysRoleDto));
        AdminResponseObj response = new AdminResponseObj();
        try {
            boolean result = sysRoleService.updateSystemRole(request, sysRoleId, sysRoleDto, refId);
            if (result) {
                response.setStatus(Constant.RESPONSE_OK);
            } else {
                response.setStatus(Constant.RESPONSE_ERROR);
                response.setErrMsg(ErrorMessage.ERROR_OCCURRED);
            }
        } catch (Exception ex) {
            AMLogger.logError(className, methodName, refId, ex);
            response.setStatus(Constant.RESPONSE_ERROR);
            response.setErrMsg(ex.getMessage());
        }
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
    
    @PostMapping("/admin/systemRoles/{sysRoleId}/functions")
    public AdminResponseObj createSystemRoleFunctions(HttpServletRequest request, @PathVariable Long sysRoleId, @RequestBody SystemRoleDTO sysRoleDto) {
        String methodName = "createSystemRoleFunctions";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[POST]/admin/systemRoles/%s/functions", sysRoleId));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(sysRoleDto));
        AdminResponseObj response = new AdminResponseObj();
        try {
            boolean result = sysRoleService.createSystemRoleFunctions(request, sysRoleId, sysRoleDto, refId);
            if (result) {
                response.setStatus(Constant.RESPONSE_OK);
            } else {
                response.setStatus(Constant.RESPONSE_ERROR);
                response.setErrMsg(ErrorMessage.ERROR_OCCURRED);
            }
        } catch (Exception ex) {
            AMLogger.logError(className, methodName, refId, ex);
            response.setStatus(Constant.RESPONSE_ERROR);
            response.setErrMsg(ex.getMessage());
        }
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
}
