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
    public static final String ACTIVITY_APPROVAL_CREATE_DEPARTMENT = "Phê duyệt tạo mới phòng ban";
    public static final String ACTIVITY_APPROVAL_CREATE_DEPARTMENT_DESC = "Phê duyệt tạo mới phòng ban %s - mã phê duyệt %s";
    public static final String ACTIVITY_UPDATE_DEPARTMENT = "Cập nhật phòng ban";
    public static final String ACTIVITY_UPDATE_DEPARTMENT_DESC = "Cập nhật phòng ban Id=%s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_UPDATE_DEPARTMENT = "Phê duyệt cập nhật phòng ban";
    public static final String ACTIVITY_APPROVAL_UPDATE_DEPARTMENT_DESC = "Phê duyệt cập nhật phòng ban %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_DEPARTMENT_USER = "Tạo mới người dùng quản trị";
    public static final String ACTIVITY_CREATE_DEPARTMENT_USER_DESC = "Tạo mới người dùng quản trị %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_DEPARTMENT_USER = "Phê duyệt tạo mới người dùng quản trị";
    public static final String ACTIVITY_APPROVAL_CREATE_DEPARTMENT_USER_DESC = "Phê duyệt tạo mới người dùng quản trị %s - mã phê duyệt %s";
    public static final String ACTIVITY_UPDATE_DEPARTMENT_USER = "Cập nhật người dùng quản trị";
    public static final String ACTIVITY_UPDATE_DEPARTMENT_USER_DESC = "Cập nhật người dùng quản trị Id=%s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_UPDATE_DEPARTMENT_USER = "Phê duyệt cập nhật người dùng quản trị";
    public static final String ACTIVITY_APPROVAL_UPDATE_DEPARTMENT_USER_DESC = "Phê duyệt cập nhật người dùng quản trị Id=%s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_DEPARTMENT_USER_ROLE = "Gán nhóm quyền mặc định cho người dùng quản trị";
    public static final String ACTIVITY_CREATE_DEPARTMENT_USER_ROLE_DESC = "Gán nhóm quyền mặc định cho người dùng quản trị Id=%s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_DEPARTMENT_USER_ROLE = "Phê duyệt gán nhóm quyền mặc định cho người dùng quản trị";
    public static final String ACTIVITY_APPROVAL_CREATE_DEPARTMENT_USER_ROLE_DESC = "Phê duyệt gán nhóm quyền mặc định cho người dùng quản trị Id=%s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_DEPARTMENT_USER_FUNCTIONS = "Gán quyền riêng cho người dùng quản trị";
    public static final String ACTIVITY_CREATE_DEPARTMENT_USER_FUNCTIONS_DESC = "Gán quyền riêng cho người dùng quản trị Id=%s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_DEPARTMENT_USER_FUNCTIONS = "Phê duyệt gán quyền riêng cho người dùng quản trị";
    public static final String ACTIVITY_APPROVAL_CREATE_DEPARTMENT_USER_FUNCTIONS_DESC = "Phê duyệt gán quyền riêng cho người dùng quản trị Id=%s - mã phê duyệt %s";
    
    public static final String ACTIVITY_RESET_PASSWORD = "Reset mật khẩu người dùng";
    public static final String ACTIVITY_RESET_PASSWORD_DESC = "Reset mật khẩu người dùng %s";
    public static final String ACTIVITY_RESET_PIN = "Reset số PIN người dùng";
    public static final String ACTIVITY_RESET_PIN_DESC = "Reset số PIN người dùng %s";
    
    public static final String ACTIVITY_CREATE_SYS_ROLE = "Tạo mới nhóm quyền";
    public static final String ACTIVITY_CREATE_SYS_ROLE_DESC = "Tạo mới nhóm quyền %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_SYS_ROLE = "Phê duyệt tạo mới nhóm quyền";
    public static final String ACTIVITY_APPROVAL_CREATE_SYS_ROLE_DESC = "Phê duyệt tạo mới nhóm quyền %s - mã phê duyệt %s";
    public static final String ACTIVITY_UPDATE_SYS_ROLE = "Cập nhật nhóm quyền";
    public static final String ACTIVITY_UPDATE_SYS_ROLE_DESC = "Cập nhật nhóm quyền Id=%s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_UPDATE_SYS_ROLE = "Phê duyệt cập nhật nhóm quyền";
    public static final String ACTIVITY_APPROVAL_UPDATE_SYS_ROLE_DESC = "Phê duyệt cập nhật nhóm quyền Id=%s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_SYS_ROLE_FUNCTIONS = "Gán quyền cho nhóm quyền";
    public static final String ACTIVITY_CREATE_SYS_ROLE_FUNCTIONS_DESC = "Gán quyền cho nhóm quyền Id=%s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_SYS_ROLE_FUNCTIONS = "Gán quyền cho nhóm quyền";
    public static final String ACTIVITY_APPROVAL_CREATE_SYS_ROLE_FUNCTIONS_DESC = "Gán quyền cho nhóm quyền Id=%s - mã phê duyệt %s";
    public static final String ACTIVITY_UPDATE_USER_DEPARTMENT = "Chuyển phòng ban người dùng admin";
    public static final String ACTIVITY_UPDATE_USER_DEPARTMENT_DESC = "Chuyển phòng ban người dùng admin %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_UPDATE_USER_DEPARTMENT = "Phê duyệt chuyển phòng ban người dùng admin";
    public static final String ACTIVITY_APPROVAL_UPDATE_USER_DEPARTMENT_DESC = "Phê duyệt chuyển phòng ban người dùng admin %s - mã phê duyệt %s";
    
    public static final String ACTIVITY_CREATE_MEMBER = "Tạo thông tin TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_DESC = "Tạo thông tin TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER = "Phê duyệt tạo thông tin TVKD";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_DESC = "Phê duyệt tạo thông tin TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_UPDATE_MEMBER = "Cập nhật thông tin TVKD";
    public static final String ACTIVITY_UPDATE_MEMBER_DESC = "Cập nhật thông tin TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_UPDATE_MEMBER = "Phê duyệt cập nhật thông tin TVKD";
    public static final String ACTIVITY_APPROVAL_UPDATE_MEMBER_DESC = "Phê duyệt cập nhật thông tin TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_FUNCTIONS = "Gán quyền riêng cho TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_FUNCTIONS_DESC = "Gán quyền riêng cho TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_FUNCTIONS = "Phê duyệt gán quyền riêng cho TVKD";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_FUNCTIONS_DESC = "Phê duyệt gán quyền riêng cho TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_DEFAULT_SETTING = "Thiết lập hạn mức cho TKVD";
    public static final String ACTIVITY_CREATE_MEMBER_DEFAULT_SETTING_DESC = "Thiết lập hạn mức cho TKVD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_ORDER_LIMIT = "Phê duyệt thiết lập hạn mức một lần đặt lệnh cho TKVD";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_ORDER_LIMIT_DESC = "Phê duyệt thiết lập hạn mức một lần đặt lệnh cho TKVD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_DEFAULT_POSITION_LIMIT = "Phê duyệt thiết lập hạn mức chung cho TKVD";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_DEFAULT_POSITION_LIMIT_DESC = "Phê duyệt thiết lập hạn mức chung cho TKVD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_COMMODITIES_ASSIGN = "Gán hàng hóa, phí HH, hạn mức HH cho TKVD";
    public static final String ACTIVITY_CREATE_MEMBER_COMMODITIES_ASSIGN_DESC = "Gán hàng hóa, phí HH, hạn mức HH cho TKVD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_MEMBER_COMMODITIES_ASSIGN = "Phê duyệt gán hàng hóa cho TKVD";
    public static final String ACTIVITY_APPROVAL_MEMBER_COMMODITIES_ASSIGN_DESC = "Phê duyệt gán hàng hóa cho TKVD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_COMMODITIES_POSITION_LIMIT = "Thiết lập hạn mức hàng hóa cho TKVD";
    public static final String ACTIVITY_CREATE_MEMBER_COMMODITIES_POSITION_LIMIT_DESC = "Thiết lập hạn mức hàng hóa cho TKVD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_MEMBER_COMMODITIES_POSITION_LIMIT = "Phê duyệt thiết lập hạn mức hàng hóa cho TKVD";
    public static final String ACTIVITY_APPROVAL_MEMBER_COMMODITIES_POSITION_LIMIT_DESC = "Phê duyệt thiết lập hạn mức hàng hóa cho TKVD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_COMMODITIES_FEE = "Thiết lập phí hàng hóa cho TKVD";
    public static final String ACTIVITY_CREATE_MEMBER_COMMODITIES_FEE_DESC = "Thiết lập phí hàng hóa cho TKVD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_MEMBER_COMMODITIES_FEE = "Phê duyệt thiết lập phí hàng hóa cho TKVD";
    public static final String ACTIVITY_APPROVAL_MEMBER_COMMODITIES_FEE_DESC = "Phê duyệt thiết lập phí hàng hóa cho TKVD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_RISK_ORDER_LOCK = "Thiết lập chức năng khoá giao dịch cho TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_RISK_ORDER_LOCK_DESC = "Thiết lập chức năng khoá giao dịch cho TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_RISK_ORDER_LOCK = "Phê duyệt thiết lập chức năng khoá giao dịch cho TVKD";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_RISK_ORDER_LOCK_DESC = "Phê duyệt thiết lập chức năng khoá giao dịch cho TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_RISK_NEW_POSITION_ORDER_LOCK = "Thiết lập chức năng chặn giao dịch một chiều cho TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_RISK_NEW_POSITION_ORDER_LOCK_DESC = "Thiết lập chức năng chặn giao dịch một chiều cho TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_RISK_NEW_POSITION_ORDER_LOCK = "Phê duyệt thiết lập chức năng chặn giao dịch một chiều cho TVKD";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_RISK_NEW_POSITION_ORDER_LOCK_DESC = "Phê duyệt thiết lập chức năng chặn giao dịch một chiều cho TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_RISK_MARGIN_WITHDRAWAL = "Thiết lập chức năng khoá nộp/rút của TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_RISK_MARGIN_WITHDRAWAL_DESC = "Thiết lập chức năng khoá nộp/rút của TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_RISK_MARGIN_WITHDRAWAL = "Phê duyệt thiết lập chức năng khoá nộp/rút của TVKD";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_RISK_MARGIN_WITHDRAWAL_DESC = "Phê duyệt thiết lập chức năng khoá nộp/rút của TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_MARGIN_MULTIPLIER_BULK = "Thiết lập hệ số ký quỹ đồng loạt cho TKGD theo TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_MARGIN_MULTIPLIER_BULK_DESC = "Thiết lập hệ số ký quỹ đồng loạt cho TKGD theo TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_MARGIN_MULTIPLIER_BULK = "Phê duyệt thiết lập hệ số ký quỹ đồng loạt cho TKGD theo TVKD";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_MARGIN_MULTIPLIER_BULK_DESC = "Phê duyệt thiết lập hệ số ký quỹ đồng loạt cho TKGD theo TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_MARGIN_RATIO_BULK = "Thiết lập tỷ lệ ký quỹ đồng loạt cho TKGD theo TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_MARGIN_RATIO_BULK_DESC = "Thiết lập tỷ lệ ký quỹ đồng loạt cho TKGD theo TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_MARGIN_RATIO_BULK = "Phê duyệt thiết lập tỷ lệ ký quỹ đồng loạt cho TKGD theo TVKD";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_MARGIN_RATIO_BULK_DESC = "Phê duyệt thiết lập tỷ lệ ký quỹ đồng loạt cho TKGD theo TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_GENERAL_FEE_BULK = "Thiết lập thuế/phí đồng loạt cho TKGD theo TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_GENERAL_FEE_BULK_DESC = "Thiết lập thuế/phí đồng loạt cho TKGD theo TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_GENERAL_FEE_BULK = "Phê duyệt thiết lập thuế/phí đồng loạt cho TKGD theo TVKD";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_GENERAL_FEE_BULK_DESC = "Phê duyệt thiết lập thuế/phí đồng loạt cho TKGD theo TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_OTHER_FEE_BULK = "Thiết lập phí khác cho đồng loạt các TKGD theo TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_OTHER_FEE_BULK_DESC = "Thiết lập phí khác cho đồng loạt các TKGD theo TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_OTHER_FEE_BULK = "Phê duyệt thiết lập phí khác cho đồng loạt các TKGD theo TVKD";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_OTHER_FEE_BULK_DESC = "Phê duyệt thiết lập phí khác cho đồng loạt các TKGD theo TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_COMMODITIES_FEE_BULK = "Thiết lập phí hàng hóa đồng loạt cho MG/TKGD theo TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_COMMODITIES_FEE_BULK_DESC = "Thiết lập phí hàng hóa đồng loạt cho MG/TKGD theo TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_INVESTOR_COMMODITIES_FEE_BULK = "Phê duyệt thiết lập phí hàng hóa đồng loạt cho TKGD theo TVKD";
    public static final String ACTIVITY_APPROVAL_CREATE_INVESTOR_COMMODITIES_FEE_BULK_DESC = "Phê duyệt thiết lập phí hàng hóa đồng loạt cho TKGD theo TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_BROKER_COMMODITIES_FEE_BULK = "Phê duyệt thiết lập phí hàng hóa đồng loạt cho MG theo TVKD";
    public static final String ACTIVITY_APPROVAL_CREATE_BROKER_COMMODITIES_FEE_BULK_DESC = "Phê duyệt thiết lập phí hàng hóa đồng loạt cho MG theo TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_USER = "Tạo thông tin người dùng đăng nhập của TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_USER_DESC = "Tạo thông tin người dùng đăng nhập %s của TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_USER = "Phê duyệt tạo thông tin người dùng đăng nhập của TVKD";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_USER_DESC = "Phê duyệt tạo thông tin người dùng đăng nhập %s của TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_UPDATE_MEMBER_USER = "Cập nhật thông tin người dùng đăng nhập của TVKD";
    public static final String ACTIVITY_UPDATE_MEMBER_USER_DESC = "Cập nhật thông tin người dùng đăng nhập %s của TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_UPDATE_MEMBER_USER = "Phê duyệt cập nhật thông tin người dùng đăng nhập của TVKD";
    public static final String ACTIVITY_APPROVAL_UPDATE_MEMBER_USER_DESC = "Phê duyệt cập nhật thông tin người dùng đăng nhập %s của TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_USER_ROLES = "Gán nhóm quyền cho người dùng của TKVD";
    public static final String ACTIVITY_CREATE_MEMBER_USER_ROLES_DESC = "Gán nhóm quyền cho người dùng %s của TKVD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_USER_ROLES = "Phê duyệt gán nhóm quyền cho người dùng của TKVD";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_USER_ROLES_DESC = "Phê duyệt gán nhóm quyền cho người dùng %s của TKVD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_USER_FUNCTIONS = "Gán quyền riêng cho người dùng của TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_USER_FUNCTIONS_DESC = "Gán quyền riêng cho người dùng %s của TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_USER_FUNCTIONS = "Phê duyệt gán quyền riêng cho người dùng của TVKD";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_USER_FUNCTIONS_DESC = "Phê duyệt gán quyền riêng cho người dùng %s của TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_ROLE = "Tạo thông tin nhóm quyền của TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_ROLE_DESC = "Tạo thông tin nhóm quyền %s của TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_ROLE = "Tạo thông tin nhóm quyền của TVKD";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_ROLE_DESC = "Tạo thông tin nhóm quyền %s của TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_UPDATE_MEMBER_ROLE = "Cập nhật thông tin nhóm quyền của TVKD";
    public static final String ACTIVITY_UPDATE_MEMBER_ROLE_DESC = "Cập nhật thông tin nhóm quyền %s của TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_UPDATE_MEMBER_ROLE = "Phê duyệt cập nhật thông tin nhóm quyền của TVKD";
    public static final String ACTIVITY_APPROVAL_UPDATE_MEMBER_ROLE_DESC = "Phê duyệt cập nhật thông tin nhóm quyền %s của TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_MEMBER_ROLE_FUNCTIONS = "Gán quyền cho nhóm quyền của TVKD";
    public static final String ACTIVITY_CREATE_MEMBER_ROLE_FUNCTIONS_DESC = "Gán quyền cho nhóm quyền %s của TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_ROLE_FUNCTIONS = "Phê duyệt gán quyền cho nhóm quyền của TVKD";
    public static final String ACTIVITY_APPROVAL_CREATE_MEMBER_ROLE_FUNCTIONS_DESC = "Phê duyệt gán quyền cho nhóm quyền %s của TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_MEMBER_MOVE_ALL_INVESTORS = "Chuyển toàn bộ TKGD sang TVKD mới";
    public static final String ACTIVITY_MEMBER_MOVE_ALL_INVESTORS_DESC = "Chuyển toàn bộ TKGD từ TVKD %s sang TVKD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_MEMBER_MOVE_ALL_INVESTORS = "Phê duyệt huyển toàn bộ TKGD sang TVKD mới";
    public static final String ACTIVITY_APPROVAL_MEMBER_MOVE_ALL_INVESTORS_DESC = "Phê duyệt chuyển toàn bộ TKGD từ TVKD %s sang TVKD %s - mã phê duyệt %s";
    
    public static final String ACTIVITY_CREATE_BROKER = "Tạo thông tin MG";
    public static final String ACTIVITY_CREATE_BROKER_DESC = "Tạo thông tin MG %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_BROKER = "Phê duyệt tạo thông tin MG";
    public static final String ACTIVITY_APPROVAL_CREATE_BROKER_DESC = "Phê duyệt tạo thông tin MG %s - mã phê duyệt %s";
    public static final String ACTIVITY_UPDATE_BROKER = "Cập nhật thông tin MG";
    public static final String ACTIVITY_UPDATE_BROKER_DESC = "Cập nhật thông tin MG %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_UPDATE_BROKER = "Phê duyệt cập nhật thông tin MG";
    public static final String ACTIVITY_APPROVAL_UPDATE_BROKER_DESC = "Phê duyệt cập nhật thông tin MG %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_BROKER_FUNCTIONS = "Gán quyền riêng cho MG";
    public static final String ACTIVITY_CREATE_BROKER_FUNCTIONS_DESC = "Gán quyền riêng cho MG %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_BROKER_FUNCTIONS = "Phê duyệt gán quyền riêng cho MG";
    public static final String ACTIVITY_APPROVAL_CREATE_BROKER_FUNCTIONS_DESC = "Phê duyệt gán quyền riêng cho MG %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_BROKER_DEFAULT_SETTING = "Thiết lập phí HH mặc định cho MG";
    public static final String ACTIVITY_CREATE_BROKER_DEFAULT_SETTING_DESC = "Thiết lập phí HH mặc định cho MG %s";
    public static final String ACTIVITY_APPROVAL_CREATE_BROKER_DEFAULT_SETTING = "Phê duyệt thiết lập phí HH mặc định cho MG";
    public static final String ACTIVITY_APPROVAL_CREATE_BROKER_DEFAULT_SETTING_DESC = "Phê duyệt thiết lập phí HH mặc định cho MG %s";
    public static final String ACTIVITY_CREATE_BROKER_COMMODITIES_ASSIGN = "Gán hàng hóa, phí HH cho MG";
    public static final String ACTIVITY_CREATE_BROKER_COMMODITIES_ASSIGN_DESC = "Gán hàng hóa, phí HH cho MG %s";
    public static final String ACTIVITY_APPROVAL_CREATE_BROKER_COMMODITIES_ASSIGN = "Phê duyệt gán hàng hóa, phí HH cho MG";
    public static final String ACTIVITY_APPROVAL_CREATE_BROKER_COMMODITIES_ASSIGN_DESC = "Phê duyệt gán hàng hóa, phí HH cho MG %s";
    
    public static final String ACTIVITY_CREATE_COLLABORATOR = "Tạo thông tin CTV MG";
    public static final String ACTIVITY_CREATE_COLLABORATOR_DESC = "Tạo thông tin CTV MG %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_COLLABORATOR = "Phê duyệt tạo thông tin CTV MG";
    public static final String ACTIVITY_APPROVAL_CREATE_COLLABORATOR_DESC = "Phê duyệt tạo thông tin CTV MG %s - mã phê duyệt %s";
    public static final String ACTIVITY_UPDATE_COLLABORATOR = "Cập nhật thông tin CTV MG";
    public static final String ACTIVITY_UPDATE_COLLABORATOR_DESC = "Cập nhật thông tin CTV MG %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_UPDATE_COLLABORATOR = "Phê duyệt cập nhật thông tin CTV MG";
    public static final String ACTIVITY_APPROVAL_UPDATE_COLLABORATOR_DESC = "Phê duyệt cập nhật thông tin CTV MG %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_COLLABORATOR_FUNCTIONS = "Gán quyền riêng cho MG";
    public static final String ACTIVITY_CREATE_COLLABORATOR_FUNCTIONS_DESC = "Gán quyền riêng cho CTV MG %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_COLLABORATOR_FUNCTIONS = "Phê duyệt gán quyền riêng cho MG";
    public static final String ACTIVITY_APPROVAL_CREATE_COLLABORATOR_FUNCTIONS_DESC = "Phê duyệt gán quyền riêng cho CTV MG %s - mã phê duyệt %s";
    
    public static final String ACTIVITY_CREATE_INVESTOR = "Tạo thông tin TKGD";
    public static final String ACTIVITY_CREATE_INVESTOR_DESC = "Tạo thông tin TKGD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_INVESTOR = "Phê duyệt tạo thông tin TKGD";
    public static final String ACTIVITY_APPROVAL_CREATE_INVESTOR_DESC = "Phê duyệt tạo thông tin TKGD %s - mã phê duyệt %s";
    public static final String ACTIVITY_UPDATE_INVESTOR = "Cập nhật thông tin TKGD";
    public static final String ACTIVITY_UPDATE_INVESTOR_DESC = "Cập nhật thông tin TKGD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_UPDATE_INVESTOR = "Phê duyệt cập nhật thông tin TKGD";
    public static final String ACTIVITY_APPROVAL_UPDATE_INVESTOR_DESC = "Phê duyệt cập nhật thông tin TKGD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_INVESTOR_USER2 = "Tạo đăng nhập thứ 2 trở lên cho TKGD";
    public static final String ACTIVITY_CREATE_INVESTOR_USER2_DESC = "Tạo đăng nhập %s cho TKGD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_INVESTOR_USER2 = "Phê duyệt tạo đăng nhập thứ 2 trở lên cho TKGD";
    public static final String ACTIVITY_APPROVAL_CREATE_INVESTOR_USER2_DESC = "Phê duyệt tạo đăng nhập %s cho TKGD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_INVESTOR_DEFAULT_SETTING = "Thiết lập/cập nhật hạn mức chung cho TKGD";
    public static final String ACTIVITY_CREATE_INVESTOR_DEFAULT_SETTING_DESC = "Thiết lập/cập nhật hạn mức chung cho TKGD %s";
    public static final String ACTIVITY_APPROVAL_CREATE_INVESTOR_DEFAULT_SETTING = "Phê duyệt thiết lập hạn mức, phí HH mặc định TKGD";
    public static final String ACTIVITY_APPROVAL_CREATE_INVESTOR_DEFAULT_SETTING_DESC = "Phê duyệt thiết lập hạn mức, phí HH mặc định TKGD %s";
    public static final String ACTIVITY_CREATE_INVESTOR_COMMODITIES_ASSIGN = "Gán hàng hóa, phí HH, hạn mức HH cho TKGD";
    public static final String ACTIVITY_CREATE_INVESTOR_COMMODITIES_ASSIGN_DESC = "Gán hàng hóa, phí HH, hạn mức HH cho TKGD %s";
    public static final String ACTIVITY_APPROVAL_CREATE_INVESTOR_COMMODITIES_ASSIGN = "Phê duyệt gán hàng hóa, phí HH, hạn mức HH cho TKGD";
    public static final String ACTIVITY_APPROVAL_CREATE_INVESTOR_COMMODITIES_ASSIGN_DESC = "Phê duyệt gán hàng hóa, phí HH, hạn mức HH cho TKGD %s";
    public static final String ACTIVITY_CREATE_INVESTOR_RISK_ORDER_LOCK = "Thiết lập chức năng khoá giao dịch cho TKGD";
    public static final String ACTIVITY_CREATE_INVESTOR_RISK_ORDER_LOCK_DESC = "Thiết lập chức năng khoá giao dịch cho TKGD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_INVESTOR_RISK_ORDER_LOCK = "Phê duyệt thiết lập chức năng khoá giao dịch cho TKGD";
    public static final String ACTIVITY_APPROVAL_CREATE_INVESTOR_RISK_ORDER_LOCK_DESC = "Phê duyệt thiết lập chức năng khoá giao dịch cho TKGD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_INVESTOR_RISK_NEW_POSITION_ORDER_LOCK = "Thiết lập chức năng chặn giao dịch một chiều cho TKGD";
    public static final String ACTIVITY_CREATE_INVESTOR_RISK_NEW_POSITION_ORDER_LOCK_DESC = "Thiết lập chức năng chặn giao dịch một chiều cho TKGD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_INVESTOR_RISK_NEW_POSITION_ORDER_LOCK = "Phê duyệt thiết lập chức năng chặn giao dịch một chiều cho TKGD";
    public static final String ACTIVITY_APPROVAL_CREATE_INVESTOR_RISK_NEW_POSITION_ORDER_LOCK_DESC = "Phê duyệt thiết lập chức năng chặn giao dịch một chiều cho TKGD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_INVESTOR_MARGIN_MULTIPLIER = "Thiết lập hệ số ký quỹ cho TKGD";
    public static final String ACTIVITY_CREATE_INVESTOR_MARGIN_MULTIPLIER_DESC = "Thiết lập hệ số ký quỹ cho TKGD %s";
    public static final String ACTIVITY_APPROVAL_CREATE_INVESTOR_MARGIN_MULTIPLIER = "Phê duyệt thiết lập hệ số ký quỹ cho TKGD";
    public static final String ACTIVITY_APPROVAL_CREATE_INVESTOR_MARGIN_MULTIPLIER_DESC = "Phê duyệt thiết lập hệ số ký quỹ cho TKGD %s";
    public static final String ACTIVITY_CREATE_INVESTOR_MARGIN_RATIO = "Thiết lập tỷ lệ ký quỹ cho TKGD";
    public static final String ACTIVITY_CREATE_INVESTOR_MARGIN_RATIO_DESC = "Thiết lập tỷ lệ ký quỹ cho TKGD %s";
    public static final String ACTIVITY_APPROVAL_CREATE_INVESTOR_MARGIN_RATIO = "Phê duyệt thiết lập tỷ lệ ký quỹ cho TKGD";
    public static final String ACTIVITY_APPROVAL_CREATE_INVESTOR_MARGIN_RATIO_DESC = "Phê duyệt thiết lập tỷ lệ ký quỹ cho TKGD %s";
    public static final String ACTIVITY_CREATE_INVESTOR_GENERAL_FEE = "Thiết lập thuế/phí cho TKGD";
    public static final String ACTIVITY_CREATE_INVESTOR_GENERAL_FEE_DESC = "Thiết lập thuế/phí cho TKGD %s";
    public static final String ACTIVITY_APPROVAL_CREATE_INVESTOR_GENERAL_FEE = "Phê duyệt thiết lập thuế/phí cho TKGD";
    public static final String ACTIVITY_APPROVAL_CREATE_INVESTOR_GENERAL_FEE_DESC = "Phê duyệt thiết lập thuế/phí cho TKGD %s";
    public static final String ACTIVITY_CREATE_INVESTOR_OTHER_FEE = "Thiết lập phí khác cho TKGD";
    public static final String ACTIVITY_CREATE_INVESTOR_OTHER_FEE_DESC = "Thiết lập phí khác cho TKGD %s";
    public static final String ACTIVITY_APPROVAL_CREATE_INVESTOR_OTHER_FEE = "Phê duyệt thiết lập phí khác cho TKGD";
    public static final String ACTIVITY_APPROVAL_CREATE_INVESTOR_OTHER_FEE_DESC = "Phê duyệt thiết lập phí khác cho TKGD %s";
    public static final String ACTIVITY_INVESTOR_BROKER_CHANGE = "Chuyển TKGD sang MG mới";
    public static final String ACTIVITY_INVESTOR_BROKER_CHANGE_DESC = "Chuyển TKGD %s sang MG mới %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_INVESTOR_BROKER_CHANGE = "Phê duyệt chuyển TKGD sang MG mới";
    public static final String ACTIVITY_APPROVAL_INVESTOR_BROKER_CHANGE_DESC = "Phê duyệt chuyển TKGD %s sang MG mới %s - mã phê duyệt %s";
    public static final String ACTIVITY_INVESTOR_COLLABORATOR_CHANGE = "Chuyển TKGD sang CTV MG mới";
    public static final String ACTIVITY_INVESTOR_COLLABORATOR_CHANGE_DESC = "Chuyển TKGD %s sang CTV MG mới %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_INVESTOR_COLLABORATOR_CHANGE = "Phê duyệt chuyển TKGD sang CTV MG mới";
    public static final String ACTIVITY_APPROVAL_INVESTOR_COLLABORATOR_CHANGE_DESC = "Phê duyệt chuyển TKGD %s sang CTV MG mới %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_INVESTOR_DEPOSIT_MONEY = "Nộp ký quỹ TKGD";
    public static final String ACTIVITY_CREATE_INVESTOR_DEPOSIT_MONEY_DESC = "Nộp ký quỹ TKGD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_INVESTOR_DEPOSIT_MONEY = "Phê duyệt nộp ký quỹ TKGD";
    public static final String ACTIVITY_APPROVAL_CREATE_INVESTOR_DEPOSIT_MONEY_DESC = "Phê duyệt nộp ký quỹ TKGD %s - mã phê duyệt %s";
    public static final String ACTIVITY_CREATE_INVESTOR_WITHDRAWAL_MONEY = "Rút ký quỹ TKGD";
    public static final String ACTIVITY_CREATE_INVESTOR_WITHDRAWAL_MONEY_DESC = "Rút ký quỹ TKGD %s - mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_CREATE_INVESTOR_WITHDRAWAL_MONEY = "Phê duyệt rút ký quỹ TKGD";
    public static final String ACTIVITY_APPROVAL_CREATE_INVESTOR_WITHDRAWAL_MONEY_DESC = "Phê duyệt rút ký quỹ TKGD %s - mã phê duyệt %s";
    public static final String ACTIVITY_UPDATE_MARGIN_INFO = "Cập nhật Lãi suất dư thừa/thiếu hụt ký quỹ";
    public static final String ACTIVITY_UPDATE_MARGIN_INFO_DESC = "Cập nhật Lãi suất dư thừa/thiếu hụt ký quỹ cho TKGD %s";
    public static final String ACTIVITY_APPROVAL_UPDATE_MARGIN_INFO = "Phê duyệt cập nhật Lãi suất dư thừa/thiếu hụt ký quỹ";
    public static final String ACTIVITY_APPROVAL_UPDATE_MARGIN_INFO_DESC = "Phê duyệt cập nhật Lãi suất dư thừa/thiếu hụt ký quỹ cho TKGD %s";
    public static final String ACTIVITY_REFUND_INVESTOR_DEPOSIT_MONEY = "Hoàn trả lệnh nộp ký quỹ TKGD";
    public static final String ACTIVITY_REFUND_INVESTOR_DEPOSIT_MONEY_DESC = "Hoàn trả lệnh nộp ký quỹ TKGD %s- mã phê duyệt %s";
    public static final String ACTIVITY_APPROVAL_REFUND_INVESTOR_DEPOSIT_MONEY = "Phê duyệt hoàn trả nộp ký quỹ TKGD";
    public static final String ACTIVITY_APPROVAL_REFUND_INVESTOR_DEPOSIT_MONEY_DESC = "Phê duyệt hoàn trả nộp ký quỹ TKGD %s- mã phê duyệt %s";
    
    public static final String ACTIVITY_USER_EXCHANGE_SETTING_CREATE = "Kích hoạt giá cho đăng nhập";
    public static final String ACTIVITY_APPROVAL_USER_EXCHANGE_SETTING_CREATE = "Phê duyệt kích hoạt giá cho đăng nhập";
    public static final String ACTIVITY_USER_EXCHANGE_SETTING_UPDATE = "Cập nhật kích hoạt giá cho đăng nhập";
    public static final String ACTIVITY_APPROVAL_USER_EXCHANGE_SETTING_UPDATE = "Phê duyệt cập nhật kích hoạt giá cho đăng nhập";
    
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
    
    public void sendActivityLog2(UserInfoDTO userInfo, HttpServletRequest request, String action, String desc, String objectStr, String orgCode, String approvalId) {
        ActivityLogDTO activityLog = modelMapper.map(userInfo, ActivityLogDTO.class);
        activityLog.setIpAddress(Utility.getClientIp(request));
        activityLog.setUserAgent(Utility.getHeadersInfo(request).get("User-Agent"));
        activityLog.setAction(action);
        activityLog.setDescription(String.format(desc, objectStr, orgCode, approvalId));
        activityLog.setDatetime(System.currentTimeMillis());
        redisMessagePublisher.publish(new Gson().toJson(activityLog));
    }
}
