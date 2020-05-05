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
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.LoginAdminUser;
import com.newgen.am.service.LoginAdminUserService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public AdminResponseObj login(HttpServletRequest request, @RequestBody LoginUserDataInputDTO userDto) {
        String methodName = "login";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/users/login");
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(userDto));
        
        LoginAdminUserOutputDTO loginUserDto = loginAdmUserService.signin(request, userDto.getUsername(), userDto.getPassword(), refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setUser(loginUserDto);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
    
    @PostMapping("/admin/users/logout/{userId}")
    public AdminResponseObj logout(HttpServletRequest request, @PathVariable String userId) {
        String methodName = "logout";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/users/logout");
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + userId);
        
        loginAdmUserService.logout(request, userId, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
    
    @PostMapping("/admin/users/verifyPin/{userId}")
    public AdminResponseObj verifyPin(@PathVariable String userId, @RequestBody LoginUserDataInputDTO user) {
        String methodName = "verifyPin";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/users/verifyPin/" + userId);
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(user));
        
        AdminResponseObj response = new AdminResponseObj();
        if (loginAdmUserService.verifyPin(userId, user.getPin(), refId)) {
            response.setStatus(Constant.RESPONSE_OK);
        } else {
            response.setStatus(Constant.RESPONSE_ERROR);
            response.setErrMsg(ErrorMessage.INCORRECT_PIN);
        }
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
    
    @PostMapping("/admin/users/{userId}/password")
    public AdminResponseObj changePassword(HttpServletRequest request, @PathVariable String userId, @RequestBody LoginUserDataInputDTO input) {
        String methodName = "changePassword";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[POST]/admin/users/%s/password", userId));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(input));
        
        loginAdmUserService.changePassword(request, userId, input.getOldPassword(), input.getNewPassword(), refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
    
    @PostMapping("/admin/users/{userId}/layout")
    public AdminResponseObj saveLayout(@PathVariable String userId, @RequestBody LoginUserDataInputDTO input) {
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
            throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
    
    @PostMapping("/admin/users/{userId}/watchlist")
    public AdminResponseObj saveWatchList(@PathVariable String userId, @RequestBody LoginUserDataInputDTO input) {
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
            throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
    
    @PutMapping("/admin/users/resetPassword")
    public AdminResponseObj resetPassword(HttpServletRequest request, @Valid @RequestBody LoginUserDataInputDTO userDto) {
        String methodName = "resetPassword";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [PUT]/admin/users/resetPassword");
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(userDto));
        
        loginAdmUserService.resetAdminUserPassword(request, userDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
    
    @PutMapping("/admin/users/resetPin")
    public AdminResponseObj resetPin(HttpServletRequest request, @Valid @RequestBody LoginUserDataInputDTO userDto) {
        String methodName = "resetPin";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [PUT]/admin/users/resetPin");
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(userDto));
        
        loginAdmUserService.resetAdminUserPin(request, userDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
}
