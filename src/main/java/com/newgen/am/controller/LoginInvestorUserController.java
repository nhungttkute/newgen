package com.newgen.am.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.SerializationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

//import com.google.gson.Gson;
import com.google.gson.Gson;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.Constant;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.AdminResponseObj;
import com.newgen.am.dto.ChangePasswordDTO;
import com.newgen.am.dto.DataObj;
import com.newgen.am.dto.ListUserDTO;
import com.newgen.am.dto.LoginInvestorUserOutputDTO;
import com.newgen.am.dto.LoginUserDataInputDTO;
import com.newgen.am.dto.ResponseObj;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.LoginInvestorUser;
import com.newgen.am.service.LoginInvestorUserService;
import com.newgen.am.validation.ValidationSequence;

@RestController
public class LoginInvestorUserController {

    private String className = "LoginInvestorUserController";

    @Autowired
    private LoginInvestorUserService loginInvUserService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/users/login")
    public ResponseObj login(HttpServletRequest request, @RequestBody LoginUserDataInputDTO userDto) {
        String methodName = "login";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/users/login");
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + Utility.getGson().toJson(userDto));
        
        LoginInvestorUserOutputDTO loginUserDto = loginInvUserService.signin(request, userDto.getUsername(), userDto.getPassword(), refId);
        
        ResponseObj response = new ResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        response.setData(new DataObj());
        response.getData().setUser(loginUserDto);
        
        ResponseObj logResponse = (ResponseObj) SerializationUtils.clone(response);
        if (logResponse != null && logResponse.getData() != null) {
        	logResponse.getData().setLayout("");
        }
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(logResponse));
        return response;
    }
    
    @GetMapping("/users/refresh")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseObj refresh(HttpServletRequest req) {
        String methodName = "refresh";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/users/refresh");
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + req.getRemoteUser());
        
        String accessToken = loginInvUserService.refresh(req.getRemoteUser(), refId);
        ResponseObj response = new ResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        response.setData(new DataObj());
        response.getData().setAccessToken(accessToken);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }

    @PostMapping("/users/logout/{userId}")
    public ResponseObj logout(HttpServletRequest request, @PathVariable String userId) {
        String methodName = "logout";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/users/logout");
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + userId);
        
        loginInvUserService.logout(request, userId, refId);
        
        ResponseObj response = new ResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }

    @PostMapping("/users/verifyPin/{userId}")
    public ResponseObj verifyPin(@PathVariable String userId, @RequestBody LoginUserDataInputDTO user) {
        String methodName = "verifyPin";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/users/verifyPin/" + userId);
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + Utility.getGson().toJson(user));
        
        ResponseObj response = new ResponseObj();
        if (loginInvUserService.verifyPin(userId, user.getPin(), refId)) {
            response.setStatus(Constant.RESPONSE_OK);
        } else {
            response.setStatus(Constant.RESPONSE_ERROR);
            response.setErrMsg("Cannot verify Pin.");
        }
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }

    @PostMapping("/users/{userId}/watchlist")
    public ResponseObj saveWatchList(@PathVariable String userId, @RequestBody LoginUserDataInputDTO input) {
        String methodName = "saveWatchList";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[POST]/users/%s/watchlist", userId));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + Utility.getGson().toJson(input));
        
        ResponseObj response = new ResponseObj();
        try {
            LoginInvestorUser user = loginInvUserService.search(userId, refId);
            if (user != null) {
                user.setWatchlists(input.getWatchlists());
                LoginInvestorUser newUser = loginInvUserService.save(user, refId);
                response.setStatus(Constant.RESPONSE_OK);
                response.setData(new DataObj());
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

    @PostMapping("/users/{userId}/layout")
    public ResponseObj saveLayout(@PathVariable String userId, @RequestBody LoginUserDataInputDTO input) {
        String methodName = "saveLayout";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[POST]/users/%s/layout", userId));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + Utility.getGson().toJson(input));
        
        ResponseObj response = new ResponseObj();
        try {
            LoginInvestorUser user = loginInvUserService.search(userId, refId);
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
                LoginInvestorUser newUser = loginInvUserService.save(user, refId);
                response.setStatus(Constant.RESPONSE_OK);
                response.setData(new DataObj());
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
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }

    @PostMapping("/users/{userId}/password")
    public ResponseObj changePassword(HttpServletRequest request, @PathVariable String userId, @Validated(ValidationSequence.class) @RequestBody ChangePasswordDTO input) {
        String methodName = "changePassword";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[POST]/users/%s/password", userId));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + Utility.getGson().toJson(input));
        
        loginInvUserService.changePassword(request, userId, input.getOldPassword(), input.getNewPassword(), refId);
        
        ResponseObj response = new ResponseObj();
        response.setStatus(Constant.RESPONSE_OK);

        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }

    @GetMapping("/users/{userId}")
    public ResponseObj getUserDetail(@PathVariable String userId) {
        String methodName = "getUserDetail";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[GET]/users/%s", userId));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + userId);
        
        ResponseObj response = new ResponseObj();
        LoginInvestorUser user = loginInvUserService.search(userId, refId);
        if (user != null) {
            user.setAccessToken(null);
            LoginInvestorUserOutputDTO userDto = modelMapper.map(user, LoginInvestorUserOutputDTO.class);
            response.setStatus(Constant.RESPONSE_OK);
            response.setData(new DataObj());
            response.getData().setUser(userDto);
        } else {
            response.setStatus(Constant.RESPONSE_ERROR);
            response.setErrMsg(ErrorMessage.USER_DOES_NOT_EXIST);
        }
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }

    @GetMapping("/users")
    public ResponseObj listUsers() {
        String methodName = "listUsers";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/users");
        
        ResponseObj response = new ResponseObj();
        List<ListUserDTO> userDtos = new ArrayList<>();
        List<LoginInvestorUser> users = loginInvUserService.list(refId);

        if (users != null && users.size() > 0) {
            for (LoginInvestorUser user : users) {
                ListUserDTO userDto = new ListUserDTO(user.getId(), user.getUsername());
                userDtos.add(userDto);
            }
            response.setStatus(Constant.RESPONSE_OK);
            response.setData(new DataObj());
            response.getData().setUsers(userDtos);
        } else {
            response.setStatus(Constant.RESPONSE_ERROR);
            response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
        }
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }

}
