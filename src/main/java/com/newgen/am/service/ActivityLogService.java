/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.service;

import com.google.gson.Gson;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.ActivityLogDTO;
import com.newgen.am.dto.UserInfoDTO;
import javax.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author nhungtt
 */
@Service
public class ActivityLogService {
    public static final String ACTIVITY_TP_LOGIN = "Đăng nhập trading platform";
    public static final String ACTIVITY_ADMIN_LOGIN = "Đăng nhập M-system admin";
    public static final String ACTIVITY_LOGIN_DESC = "Tài khoản %s đăng nhập";
    public static final String ACTIVITY_TP_LOGOUT = "Đăng xuất trading platform";
    public static final String ACTIVITY_ADMIN_LOGOUT = "Đăng xuất M-system admin";
    public static final String ACTIVITY_LOGOUT_DESC = "Tài khoản %s đăng xuất";
    public static final String ACTIVITY_CHANGE_PASSWORD = "Thay đổi mật khẩu";
    public static final String ACTIVITY_CHANGE_PASSWORD_DESC = "Tài khoản %s thay đổi mật khẩu";
    public static final String ACTIVITY_CREATE_DEPARTMENT = "Tạo mới phòng ban";
    public static final String ACTIVITY_CREATE_DEPARTMENT_DESC = "Tạo mới phòng ban %s - mã phê duyệt %s";
    public static final String ACTIVITY_UPDATE_DEPARTMENT = "Cập nhật phòng ban";
    public static final String ACTIVITY_UPDATE_DEPARTMENT_DESC = "Cập nhật phòng ban Id=%s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_DEPARTMENT_USER = "Tạo mới người dùng quản trị";
    public static final String ACTIVITY_CREATE_DEPARTMENT_USER_DESC = "Tạo mới người dùng quản trị %s - mã phê duyệt %s";
    public static final String ACTIVITY_UPDATE_DEPARTMENT_USER = "Cập nhật người dùng quản trị";
    public static final String ACTIVITY_UPDATE_DEPARTMENT_USER_DESC = "Cập nhật người dùng quản trị Id=%s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_DEPARTMENT_USER_ROLE = "Gán nhóm quyền mặc định cho người dùng quản trị";
    public static final String ACTIVITY_CREATE_DEPARTMENT_USER_ROLE_DESC = "Gán nhóm quyền mặc định cho người dùng quản trị Id=%s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_DEPARTMENT_USER_FUNCTIONS = "Gán quyền riêng cho người dùng quản trị";
    public static final String ACTIVITY_CREATE_DEPARTMENT_USER_FUNCTIONS_DESC = "Gán quyền riêng cho người dùng quản trị Id=%s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_SYS_ROLE = "Tạo mới nhóm quyền";
    public static final String ACTIVITY_CREATE_SYS_ROLE_DESC = "Tạo mới nhóm quyền %s - mã phê duyệt %s";
    public static final String ACTIVITY_UPDATE_SYS_ROLE = "Cập nhật nhóm quyền";
    public static final String ACTIVITY_UPDATE_SYS_ROLE_DESC = "Cập nhật nhóm quyền Id=%s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_SYS_ROLE_FUNCTIONS = "Gán quyền cho nhóm quyền";
    public static final String ACTIVITY_CREATE_SYS_ROLE_FUNCTIONS_DESC = "Gán quyền cho nhóm quyền Id=%s - mã phê duyệt %s";
    
    @Autowired
    private RedisMessagePublisher redisMessagePublisher;
    
    private ModelMapper modelMapper;

    @Autowired
    public ActivityLogService(@Lazy ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
    
    public void sendActivityLog(UserInfoDTO userInfo, HttpServletRequest request, String action, String desc, String objectStr, long approvalId) {
        ActivityLogDTO activityLog = modelMapper.map(userInfo, ActivityLogDTO.class);
        activityLog.setIpAddress(Utility.getClientIp(request));
        activityLog.setUserAgent(Utility.getHeadersInfo(request).get("User-Agent"));
        activityLog.setAction(action);
        activityLog.setDescription(String.format(desc, objectStr, approvalId));
        activityLog.setDatetime(System.currentTimeMillis());
        redisMessagePublisher.publish(new Gson().toJson(activityLog));
    }
}
