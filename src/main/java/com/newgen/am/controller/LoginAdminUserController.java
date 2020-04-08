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
import com.newgen.am.common.Utility;
import com.newgen.am.dto.AdminDataObj;
import com.newgen.am.dto.AdminResponseObj;
import com.newgen.am.dto.LoginAdminUserOutputDTO;
import com.newgen.am.dto.LoginUserDataInputDTO;
import com.newgen.am.model.LoginAdminUser;
import com.newgen.am.service.LoginAdminUserService;
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
public class LoginAdminUserController {
    private String className = "LoginAdminUserController";
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private LoginAdminUserService loginAdmUserService;
    
    @PostMapping("/admin/users/login")
    public AdminResponseObj login(@RequestBody LoginUserDataInputDTO userDto) {
        String methodName = "login";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/users/login");
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(userDto));
        AdminResponseObj response = new AdminResponseObj();
        try {
            LoginAdminUser loginAdmUser = loginAdmUserService.signin(userDto.getUsername(), userDto.getPassword(), refId);
            LoginAdminUserOutputDTO loginUserDto = modelMapper.map(loginAdmUser, LoginAdminUserOutputDTO.class);
            loginUserDto.setFunctions(loginAdmUserService.getFunctionsByUserId(loginAdmUser.getUserId()));
            response.setStatus(Constant.RESPONSE_OK);
            response.setData(new AdminDataObj());
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
    public AdminResponseObj logout(@PathVariable Long userId) {
        String methodName = "logout";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/users/logout");
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + userId);
        AdminResponseObj response = new AdminResponseObj();
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
    public AdminResponseObj verifyPin(@PathVariable Long userId, @RequestBody LoginUserDataInputDTO user) {
        String methodName = "verifyPin";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/users/verifyPin/" + userId);
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(user));
        AdminResponseObj response = new AdminResponseObj();
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
    public AdminResponseObj changePassword(@PathVariable Long userId, @RequestBody LoginUserDataInputDTO input) {
        String methodName = "changePassword";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[POST]/admin/users/%s/password", userId));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(input));
        AdminResponseObj response = new AdminResponseObj();
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
    
    @PostMapping("/admin/users/{userId}/layout")
    public AdminResponseObj saveLayout(@PathVariable Long userId, @RequestBody LoginUserDataInputDTO input) {
        String methodName = "saveLayout";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[POST]/admin/users/%s/layout", userId));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(input));
        AdminResponseObj response = new AdminResponseObj();
        try {
            LoginAdminUser user = loginAdmUserService.search(userId, refId);
            if (user != null) {
                if (Utility.isNotNull(input.getLayout())) {
                    user.setLayout(input.getLayout());
                }
                if (Utility.isNotNull(input.getLanguage())) {
                    user.setLanguage(input.getLanguage());
                }
                if (Utility.isNotNull(input.getTheme())) {
                    user.setTheme(input.getTheme());
                }
                if (Utility.isNotNull(input.getFontSize()) && input.getFontSize() > 0) {
                    user.setFontSize(input.getFontSize());
                }
                LoginAdminUser newUser = loginAdmUserService.save(user, refId);
                response.setStatus(Constant.RESPONSE_OK);
                response.setData(new AdminDataObj());
                response.getData().setLayout(newUser.getLayout());
                response.getData().setLanguage(newUser.getLanguage());
                response.getData().setTheme(newUser.getTheme());
                response.getData().setFontSize(newUser.getFontSize());
            } else {
                response.setStatus(Constant.RESPONSE_ERROR);
                response.setErrMsg(ErrorMessage.USER_DOES_NOT_EXIST);
            }
        } catch (Exception ex) {
            AMLogger.logError(className, methodName, refId, ex);
            response.setStatus(Constant.RESPONSE_ERROR);
            response.setErrMsg(ex.getMessage());
        }
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
    
    @PostMapping("/admin/users/{userId}/watchlist")
    public AdminResponseObj saveWatchList(@PathVariable Long userId, @RequestBody LoginUserDataInputDTO input) {
        String methodName = "saveWatchList";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[POST]/admin/users/%s/watchlist", userId));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(input));
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
            response.setStatus(Constant.RESPONSE_ERROR);
            response.setErrMsg(ex.getMessage());
        }
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
    
    @PutMapping("/admin/users/resetPassword")
    public AdminResponseObj resetPassword(@RequestBody LoginUserDataInputDTO userDto) {
        String methodName = "resetPassword";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [PUT]/admin/users/resetPassword");
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(userDto));
        AdminResponseObj response = new AdminResponseObj();
        try {
            boolean result = loginAdmUserService.resetAdminUserPassword(userDto, refId);
            if (result) {
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
