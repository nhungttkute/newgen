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
import com.newgen.am.dto.DepartmentDTO;
import com.newgen.am.dto.DeptUserDTO;
import com.newgen.am.model.Department;
import com.newgen.am.service.DepartmentService;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
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
public class DepartmentController {
    private String className = "DepartmentController";
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private DepartmentService deptService;
    
    @GetMapping("/admin/departments")
    public AdminResponseObj listDeparments() {
        String methodName = "listDeparments";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/departments");
        AdminResponseObj response = new AdminResponseObj();
        try {
            List<DepartmentDTO> deptDtos = new ArrayList<>();
            List<Department> depts = deptService.list(refId);

            if (depts != null && depts.size() > 0) {
                for (Department dept : depts) {
                    DepartmentDTO deptDto = modelMapper.map(dept, DepartmentDTO.class);
                    deptDto.setUsers(null);
                    deptDtos.add(deptDto);
                }
                response.setStatus(Constant.RESPONSE_OK);
                response.setData(new AdminDataObj());
                response.getData().setDepartments(deptDtos);
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
    
    @PostMapping("/admin/departments")
    public AdminResponseObj createDepartment(HttpServletRequest request, @RequestBody DepartmentDTO deptDto) {
        String methodName = "createDepartment";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/deparments");
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(deptDto));
        AdminResponseObj response = new AdminResponseObj();
        try {
            boolean result = deptService.createDepartment(request, deptDto, refId);
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
    
    @PutMapping("/admin/departments/{deptId}")
    public AdminResponseObj updateDepartment(HttpServletRequest request, @PathVariable Long deptId, @RequestBody DepartmentDTO deptDto) {
        String methodName = "updateDepartment";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [PUT]/admin/deparments/" + deptId);
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(deptDto));
        AdminResponseObj response = new AdminResponseObj();
        try {
            boolean result = deptService.updateDepartment(request, deptId, deptDto, refId);
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
    
    @GetMapping("/admin/departments/{deptId}")
    public AdminResponseObj getDepartmentDetail(@PathVariable Long deptId) {
        String methodName = "getDepartmentDetail";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/deparments/" + deptId);
        AdminResponseObj response = new AdminResponseObj();
        try {
            DepartmentDTO deptDto = deptService.getDepartmentDetail(deptId, refId);
            if (deptDto != null) {
                response.setStatus(Constant.RESPONSE_OK);
                response.setData(new AdminDataObj());
                response.getData().setDepartment(deptDto);
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
    
    @PostMapping("/admin/departments/{deptId}/users")
    public AdminResponseObj createDepartmentUser(HttpServletRequest request, @PathVariable Long deptId, @RequestBody DeptUserDTO deptUserDto) {
        String methodName = "createDepartmentUser";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/departments/users");
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(deptUserDto));
        AdminResponseObj response = new AdminResponseObj();
        try {
            boolean result = deptService.createDepartmentUser(request, deptId, deptUserDto, refId);
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
    
    @PutMapping("/admin/departments/{deptId}/users/{deptUserId}")
    public AdminResponseObj updateDepartmentUser(HttpServletRequest request, @PathVariable Long deptId, @PathVariable Long deptUserId, @RequestBody DeptUserDTO deptUserDto) {
        String methodName = "updateDepartmentUser";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, String.format("REQUEST_API: [PUT]/admin/departments/%s/users/%s", deptId, deptUserId));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(deptUserDto));
        AdminResponseObj response = new AdminResponseObj();
        try {
            boolean result = deptService.updateDepartmentUser(request, deptId, deptUserId, deptUserDto, refId);
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
    
    @GetMapping("/admin/departments/{deptId}/users/{deptUserId}")
    public AdminResponseObj getDepartmentUser(@PathVariable Long deptId, @PathVariable Long deptUserId) {
        String methodName = "updateDepartmentUser";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, String.format("REQUEST_API: [GET]/admin/departments/%s/users/%s", deptId, deptUserId));
        AdminResponseObj response = new AdminResponseObj();
        try {
            DeptUserDTO deptUserDto = deptService.getDepartmentUser(deptId, deptUserId, refId);
            if (deptUserDto != null) {
                response.setStatus(Constant.RESPONSE_OK);
                response.setData(new AdminDataObj());
                response.getData().setDeptUser(deptUserDto);
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
    
    @PostMapping("/admin/departments/{deptId}/users/{deptUserId}/roles")
    public AdminResponseObj saveDepartmentUserRoles(HttpServletRequest request, @PathVariable Long deptId, @PathVariable Long deptUserId, @RequestBody DeptUserDTO deptUserDto) {
        String methodName = "saveDepartmentUserRoles";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, String.format("REQUEST_API: [POST]/admin/departments/%s/users/%s/roles", deptId, deptUserId));
        AdminResponseObj response = new AdminResponseObj();
        try {
            boolean result = deptService.saveDepartmentUserRoles(request, deptId, deptUserId, deptUserDto, refId);
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
    
    @PostMapping("/admin/departments/{deptId}/users/{deptUserId}/functions")
    public AdminResponseObj saveDepartmentUserFunctions(HttpServletRequest request, @PathVariable Long deptId, @PathVariable Long deptUserId, @RequestBody DeptUserDTO deptUserDto) {
        String methodName = "saveDepartmentUserFunctions";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, String.format("REQUEST_API: [POST]/admin/departments/%s/users/%s/functions", deptId, deptUserId));
        AdminResponseObj response = new AdminResponseObj();
        try {
            boolean result = deptService.saveDepartmentUserFunctions(request, deptId, deptUserId, deptUserDto, refId);
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
