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
import com.newgen.am.dto.AMResponseObj;
import com.newgen.am.dto.DataObj;
import com.newgen.am.dto.LoginUserDataInputDTO;
import com.newgen.am.dto.LoginUserOutputDTO;
import com.newgen.am.model.LoginAdminUser;
import com.newgen.am.service.LoginAdminUserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public AMResponseObj login(@RequestBody LoginUserDataInputDTO userDto) {
        String methodName = "login";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: /admin/users/login");
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(userDto));
        AMResponseObj response = new AMResponseObj();
        try {
            LoginAdminUser loginAdmUser = loginAdmUserService.signin(userDto.getUsername(), userDto.getPassword(), refId);
            LoginUserOutputDTO loginUserDto = modelMapper.map(loginAdmUser, LoginUserOutputDTO.class);
            loginUserDto.setFunctions(loginAdmUserService.getFunctionsByUserId(loginAdmUser.getUserId()));
            response.setStatus(Constant.RESPONSE_OK);
            response.setData(new DataObj());
            response.getData().setUser(loginUserDto);
        } catch (Exception ex) {
            AMLogger.logError(className, methodName, refId, ex);
            response.setStatus(Constant.RESPONSE_ERROR);
            response.setErrMsg(ex.getMessage());
        }
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
    
    @PostMapping("/admin/users/logout/{userId}")
    public AMResponseObj logout(@PathVariable Long userId) {
        String methodName = "logout";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: /admin/users/logout");
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + userId);
        AMResponseObj response = new AMResponseObj();
        try {
            if (loginAdmUserService.logout(userId, refId)) {
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
    
    @PostMapping("/admin/users/verifyPin/{userId}")
    public AMResponseObj verifyPin(@PathVariable Long userId, @RequestBody LoginUserDataInputDTO user) {
        String methodName = "verifyPin";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: /admin/users/verifyPin/" + userId);
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(user));
        AMResponseObj response = new AMResponseObj();
        try {
            if (loginAdmUserService.verifyPin(userId, user.getPin(), refId)) {
                response.setStatus(Constant.RESPONSE_OK);
            } else {
                response.setStatus(Constant.RESPONSE_ERROR);
                response.setErrMsg("Cannot verify Pin.");
            }
        } catch (Exception ex) {
            AMLogger.logError(className, methodName, refId, ex);
            response.setStatus(Constant.RESPONSE_ERROR);
            response.setErrMsg(ex.getMessage());
        }
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
    
    @PostMapping("/admin/users/{userId}/password")
    public AMResponseObj changePassword(@PathVariable Long userId, @RequestBody LoginUserDataInputDTO input) {
        String methodName = "changePassword";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("/admin/users/%s/password", userId));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(input));
        AMResponseObj response = new AMResponseObj();
        try {
            if (loginAdmUserService.changePassword(userId, input.getOldPassword(), input.getNewPassword(), refId)) {
                response.setStatus(Constant.RESPONSE_OK);
            } else {
                response.setStatus(Constant.RESPONSE_ERROR);
                response.setErrMsg("Cannot change password.");
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
