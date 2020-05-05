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
    
    public static final String ACTIVITY_RESET_PASSWORD = "Reset mật khẩu người dùng";
    public static final String ACTIVITY_RESET_PASSWORD_DESC = "Reset mật khẩu người dùng %s";
    public static final String ACTIVITY_RESET_PIN = "Reset số PIN người dùng";
    public static final String ACTIVITY_RESET_PIN_DESC = "Reset số PIN người dùng %s";
    
    public static final String ACTIVITY_CREATE_SYS_ROLE = "Tạo mới nhóm quyền";
    public static final String ACTIVITY_CREATE_SYS_ROLE_DESC = "Tạo mới nhóm quyền %s - mã phê duyệt %s";
    public static final String ACTIVITY_UPDATE_SYS_ROLE = "Cập nhật nhóm quyền";
    public static final String ACTIVITY_UPDATE_SYS_ROLE_DESC = "Cập nhật nhóm quyền Id=%s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_SYS_ROLE_FUNCTIONS = "Gán quyền cho nhóm quyền";
    public static final String ACTIVITY_CREATE_SYS_ROLE_FUNCTIONS_DESC = "Gán quyền cho nhóm quyền Id=%s - mã phê duyệt %s";
    public static final String ACTIVITY_UPDATE_USER_DEPARTMENT = "Chuyển phòng ban người dùng admin";
    public static final String ACTIVITY_UPDATE_USER_DEPARTMENT_DESC = "Chuyển phòng ban người dùng admin %s - mã phê duyệt %s";
    
    public static final String ACTIVITY_CREATE_MEMBER = "Tạo thông tin TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_DESC = "Tạo thông tin TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_UPDATE_MEMBER = "Cập nhật thông tin TVKD";
    public static final String ACTIVITY_UPDATE_MEMBER_DESC = "Cập nhật thông tin TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_FUNCTIONS = "Gán quyền riêng cho TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_FUNCTIONS_DESC = "Gán quyền riêng cho TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_MASTER_USER = "Tạo đăng nhập tổng TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_MASTER_USER_DESC = "Tạo đăng nhập tổng TVKD %s";
    public static final String ACTIVITY_CREATE_MEMBER_LIMIT = "Thiết lập hạn mức TKVD";
    public static final String ACTIVITY_CREATE_MEMBER_LIMIT_DESC = "Thiết lập hạn mức TKVD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_COMMODITIES_ASSIGN = "Gán hàng hóa cho TKVD";
    public static final String ACTIVITY_CREATE_MEMBER_COMMODITIES_ASSIGN_DESC = "Gán hàng hóa cho TKVD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_COMMODITIES_FEE = "Thiết lập phí hàng hóa cho TKVD";
    public static final String ACTIVITY_CREATE_MEMBER_COMMODITIES_FEE_DESC = "Thiết lập phí hàng hóa cho TKVD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_RISK_ORDER_LOCK = "Thiết lập chức năng khoá giao dịch cho TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_RISK_ORDER_LOCK_DESC = "Thiết lập chức năng khoá giao dịch cho TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_RISK_NEW_ORDER_LOCK = "Thiết lập chức năng chặn giao dịch một chiều cho TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_RISK_NEW_ORDER_LOCK_DESC = "Thiết lập chức năng chặn giao dịch một chiều cho TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_RISK_MARGIN_WITHDRAWAL = "Thiết lập chức năng khoá nộp/rút của TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_RISK_MARGIN_WITHDRAWAL_DESC = "Thiết lập chức năng khoá nộp/rút của TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_MARGIN_MULTIPLIER_BULK = "Thiết lập hệ số ký quỹ đồng loạt cho TKGD theo TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_MARGIN_MULTIPLIER_BULK_DESC = "Thiết lập hệ số ký quỹ đồng loạt cho TKGD theo TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_MARGIN_RATIO_BULK = "Thiết lập tỷ lệ ký quỹ đồng loạt cho TKGD theo TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_MARGIN_RATIO_BULK_DESC = "Thiết lập tỷ lệ ký quỹ đồng loạt cho TKGD theo TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_GENERAL_FEE_BULK = "Thiết lập thuế/phí đồng loạt cho TKGD theo TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_GENERAL_FEE_BULK_DESC = "Thiết lập thuế/phí đồng loạt cho TKGD theo TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_OTHER_FEE_BULK = "Thiết lập phí khác cho đồng loạt các TKGD theo TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_OTHER_FEE_BULK_DESC = "Thiết lập phí khác cho đồng loạt các TKGD theo TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_COMMODITIES_FEE_BULK = "Thiết lập phí hàng hóa đồng loạt cho MG/TKGD theo TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_COMMODITIES_FEE_BULK_DESC = "Thiết lập phí hàng hóa đồng loạt cho MG/TKGD theo TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_USER = "Tạo thông tin người dùng đăng nhập của TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_USER_DESC = "Tạo thông tin người dùng đăng nhập của TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_UPDATE_MEMBER_USER = "Cập nhật thông tin người dùng đăng nhập của TVKD";
    public static final String ACTIVITY_UPDATE_MEMBER_USER_DESC = "Cập nhật thông tin người dùng đăng nhập của TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_USER_ROLES = "Gán nhóm quyền cho người dùng của TKVD";
    public static final String ACTIVITY_CREATE_MEMBER_USER_ROLES_DESC = "Gán nhóm quyền cho người dùng của TKVD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_USER_FUNCTIONS = "Gán quyền riêng cho người dùng của TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_USER_FUNCTIONS_DESC = "Gán quyền riêng cho người dùng của TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_ROLE = "Tạo thông tin nhóm quyền của TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_ROLE_DESC = "Tạo thông tin nhóm quyền của TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_UPDATE_MEMBER_ROLE = "Cập nhật thông tin nhóm quyền của TVKD";
    public static final String ACTIVITY_UPDATE_MEMBER_ROLE_DESC = "Cập nhật thông tin nhóm quyền của TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_ROLE_FUNCTIONS = "Gán quyền cho nhóm quyền của TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_ROLE_FUNCTIONS_DESC = "Gán quyền cho nhóm quyền của TVKD %s - mã phê duyệt %s";
    
    @Autowired
    private RedisMessagePublisher redisMessagePublisher;
    
    private ModelMapper modelMapper;

    @Autowired
    public ActivityLogService(@Lazy ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
    
    public void sendActivityLog(UserInfoDTO userInfo, HttpServletRequest request, String action, String desc, String objectStr, String approvalId) {
        ActivityLogDTO activityLog = modelMapper.map(userInfo, ActivityLogDTO.class);
        activityLog.setIpAddress(Utility.getClientIp(request));
        activityLog.setUserAgent(Utility.getHeadersInfo(request).get("User-Agent"));
        activityLog.setAction(action);
        activityLog.setDescription(String.format(desc, objectStr, approvalId));
        activityLog.setDatetime(System.currentTimeMillis());
        redisMessagePublisher.publish(new Gson().toJson(activityLog));
    }
}
