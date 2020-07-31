/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.common;

/**
 *
 * @author nhungtt
 */
public class SystemFunctionCode {

    public static final String APPROVAL_DEPARTMENT_INFO_CREATE_CODE = "approval.adminUserManagement.departmentManagement.departmentInfo.create";
    public static final String DEPARTMENT_INFO_CREATE_NAME = "Tạo thông tin phòng ban";
    public static final String APPROVAL_DEPARTMENT_INFO_UPDATE_CODE = "approval.adminUserManagement.departmentManagement.departmentInfo.update";
    public static final String DEPARTMENT_INFO_UPDATE_NAME = "Cập nhật thông tin phòng ban";
    public static final String APPROVAL_ADMIN_USER_CREATE_CODE = "approval.adminUserManagement.departmentManagement.loginAdminUserManagement.adminUserInfo.create";
    public static final String ADMIN_USER_CREATE_NAME = "Tạo thông tin người dùng đăng nhập admin";
    public static final String APPROVAL_ADMIN_USER_UPDATE_CODE = "approval.adminUserManagement.departmentManagement.loginAdminUserManagement.adminUserInfo.update";
    public static final String ADMIN_USER_UPDATE_NAME = "Cập nhật thông tin người dùng đăng nhập admin";
    public static final String APPROVAL_ADMIN_USER_ROLE_ASSIGN_CREATE_CODE = "approval.adminUserManagement.departmentManagement.loginAdminUserManagement.adminUserRoleAssign.create";
    public static final String ADMIN_USER_ROLE_ASSIGN_CREATE_NAME = "Gán nhóm quyền mặc định cho người dùng đăng nhập admin";
    public static final String APPROVAL_ADMIN_USER_FUNCTIONS_ASSIGN_CREATE_CODE = "approval.adminUserManagement.departmentManagement.loginAdminUserManagement.adminUserFunctionsAssign.create";
    public static final String ADMIN_USER_FUNCTIONS_ASSIGN_CREATE_NAME = "Gán quyền riêng cho người dùng đăng nhập admin";
    public static final String APPROVAL_ADMIN_USER_DEPT_CHANGE_UPDATE_CODE = "approval.adminUserManagement.departmentManagement.loginAdminUserManagement.adminUserDeptChange.update";
    public static final String ADMIN_USER_DEPT_CHANGE_UPDATE_NAME = "Chuyển phòng ban người dùng admin";
    
    public static final String APPROVAL_SYSTEM_ROLE_CREATE_CODE = "approval.adminUserManagement.roleManagement.roleInfo.create";
    public static final String SYSTEM_ROLE_CREATE_NAME = "Tạo thông tin nhóm quyền mặc định";
    public static final String APPROVAL_SYSTEM_ROLE_UPDATE_CODE = "approval.adminUserManagement.roleManagement.roleInfo.update";
    public static final String SYSTEM_ROLE_UPDATE_NAME = "Cập nhật thông tin nhóm quyền mặc định";
    public static final String APPROVAL_SYSTEM_ROLE_FUNCTIONS_ASSIGN_CREATE_CODE = "approval.adminUserManagement.roleManagement.roleFunctionsAssign.create";
    public static final String SYSTEM_ROLE_FUNCTIONS_ASSIGN_CREATE_NAME = "Gán quyền cho nhóm quyền mặc định";
    
    public static final String APPROVAL_MEMBER_CREATE_CODE = "approval.clientManagement.memberManagement.memberInfo.create";
    public static final String MEMBER_CREATE_NAME = "Tạo thông tin TVKD";
    public static final String APPROVAL_MEMBER_UPDATE_CODE = "approval.clientManagement.memberManagement.memberInfo.update";
    public static final String MEMBER_UPDATE_NAME = "Cập nhật thông tin TVKD";
    public static final String APPROVAL_MEMBER_FUNCTIONS_ASSIGN_CREATE_CODE = "approval.clientManagement.memberManagement.memberFunctionsAssign.create";
    public static final String MEMBER_FUNCTIONS_ASSIGN_CREATE_NAME = "Gán quyền riêng cho TVKD";
    public static final String APPROVAL_MEMBER_MASTER_USER_CREATE_CODE = "approval.clientManagement.memberManagement.memberMasterUserInfo.create";
    public static final String MEMBER_MASTER_USER_CREATE_NAME = "Tạo đăng nhập tổng TVKD";
    public static final String APPROVAL_MEMBER_LIMIT_CREATE_CODE = "approval.clientManagement.memberManagement.memberOrderLimitConfig.create";
    public static final String MEMBER_LIMIT_CREATE_NAME = "Thiết lập/cập nhật điều chỉnh hạn mức TVKD";
    public static final String APPROVAL_MEMBER_ORDER_LIMIT_CREATE_CODE = "approval.clientManagement.memberManagement.memberOrderLimitConfig.create";
    public static final String MEMBER_ORDER_LIMIT_CREATE_NAME = "Thiết lập/cập nhật điều chỉnh hạn mức 1 lần đặt lệnh cho TVKD";
    public static final String APPROVAL_MEMBER_DEFAULT_POSITION_LIMIT_CREATE_CODE = "approval.clientManagement.memberManagement.defaultPositionLimitConfig.create";
    public static final String MEMBER_DEFAULT_POSITION_LIMIT_CREATE_NAME = "Thiết lập/cập nhật hạn mức chung cho TVKD";
    public static final String APPROVAL_MEMBER_COMMODITIES_ASSIGN_CREATE_CODE = "approval.clientManagement.memberManagement.memberCommoditiesAssign.create";
    public static final String MEMBER_COMMODITIES_ASSIGN_CREATE_NAME = "Gán hàng hóa cho TKVD";
    public static final String APPROVAL_MEMBER_COMMODITIES_FEE_CREATE_CODE = "approval.clientManagement.memberManagement.memberCommoditiesFeeConfig.create";
    public static final String MEMBER_COMMODITIES_FEE_CREATE_NAME = "Thiết lập phí hàng hóa cho TKVD";
    public static final String APPROVAL_MEMBER_RISK_ORDER_LOCK_CREATE_CODE = "approval.clientManagement.memberManagement.memberRiskManagement.orderLockConfig.create";
    public static final String MEMBER_RISK_ORDER_LOCK_CREATE_NAME = "Thiết lập chức năng khoá giao dịch cho TVKD";
    public static final String APPROVAL_MEMBER_RISK_NEW_ORDER_LOCK_CREATE_CODE = "approval.clientManagement.memberManagement.memberRiskManagement.newOrderLockConfig.create";
    public static final String MEMBER_RISK_NEW_ORDER_LOCK_CREATE_NAME = "Thiết lập chức năng chặn giao dịch một chiều cho TVKD";
    public static final String APPROVAL_MEMBER_RISK_MARGIN_WITHDRAWAL_LOCK_CREATE_CODE = "approval.clientManagement.memberManagement.memberRiskManagement.marginWithdrawalLockConfig.create";
    public static final String MEMBER_RISK_MARGIN_WITHDRAWAL_LOCK_CREATE_NAME = "Thiết lập chức năng khoá nộp/rút của TVKD";
    public static final String APPROVAL_MEMBER_MARGIN_MULTIPLIER_BULK_CREATE_CODE = "approval.clientManagement.memberManagement.investorMarginMultiplierBulkConfig.create";
    public static final String MEMBER_MARGIN_MULTIPLIER_BULK_CREATE_NAME = "Thiết lập hệ số ký quỹ đồng loạt cho TKGD theo TVKD";
    public static final String APPROVAL_MEMBER_MARGIN_RATIO_BULK_CREATE_CODE = "approval.clientManagement.memberManagement.investorMarginRatioBulkConfig.create";
    public static final String MEMBER_MARGIN_RATIO_BULK_CREATE_NAME = "Thiết lập tỷ lệ ký quỹ đồng loạt cho TKGD theo TVKD";
    public static final String APPROVAL_MEMBER_GENERAL_FEE_BULK_CREATE_CODE = "approval.clientManagement.memberManagement.investorGeneralFeeBulkConfig.create";
    public static final String MEMBER_GENERAL_FEE_BULK_CREATE_NAME = "Thiết lập thuế/phí đồng loạt cho TKGD theo TVKD";
    public static final String APPROVAL_MEMBER_OTHER_FEE_BULK_CREATE_CODE = "approval.clientManagement.memberManagement.investorOtherFeeBulkConfig.create";
    public static final String MEMBER_OTHER_FEE_BULK_CREATE_NAME = "Thiết lập phí khác cho đồng loạt các TKGD theo TVKD";
    public static final String APPROVAL_MEMBER_INVESTOR_COMMODITIES_FEE_BULK_CREATE_CODE = "approval.clientManagement.memberManagement.investorCommoditiesFeeBulkConfig.create";
    public static final String MEMBER_INVESTOR_COMMODITIES_FEE_BULK_CREATE_NAME = "Thiết lập phí hàng hóa đồng loạt cho TKGD theo TVKD";
    public static final String APPROVAL_MEMBER_BROKER_COMMODITIES_FEE_BULK_CREATE_CODE = "approval.clientManagement.memberManagement.brokerCommoditiesFeeBulkConfig.create";
    public static final String MEMBER_BROKER_COMMODITIES_FEE_BULK_CREATE_NAME = "Thiết lập phí hàng hóa đồng loạt cho MG theo TVKD";
    public static final String APPROVAL_MEMBER_USER_CREATE_CODE = "approval.clientManagement.memberManagement.memberUserManagement.memberUserInfo.create";
    public static final String MEMBER_USER_CREATE_NAME = "Tạo thông tin người dùng đăng nhập của TVKD";
    public static final String MEMBER_USER_CREATE_DESC = "Tạo thông tin người dùng đăng nhập %s của TVKD %s";
    public static final String APPROVAL_MEMBER_USER_UPDATE_CODE = "approval.clientManagement.memberManagement.memberUserManagement.memberUserInfo.update";
    public static final String MEMBER_USER_UPDATE_NAME = "Cập nhật thông tin người dùng đăng nhập của TVKD";
    public static final String MEMBER_USER_UPDATE_DESC = "Cập nhật thông tin người dùng đăng nhập %s của TVKD %s";
    public static final String APPROVAL_MEMBER_USER_ROLES_ASSIGN_CREATE_CODE = "approval.clientManagement.memberManagement.memberUserManagement.memberUserRoleAssign.create";
    public static final String MEMBER_USER_ROLES_ASSIGN_CREATE_NAME = "Gán nhóm quyền cho người dùng của TKVD";
    public static final String MEMBER_USER_ROLES_ASSIGN_CREATE_DESC = "Gán nhóm quyền cho người dùng %s của TKVD %s";
    public static final String APPROVAL_MEMBER_USER_FUNCTIONS_ASSIGN_CREATE_CODE = "approval.clientManagement.memberManagement.memberUserManagement.memberUserFunctionsAssign.create";
    public static final String MEMBER_USER_FUNCTIONS_ASSIGN_CREATE_NAME = "Gán quyền riêng cho người dùng của TVKD";
    public static final String MEMBER_USER_FUNCTIONS_ASSIGN_CREATE_DESC = "Gán quyền riêng cho người dùng %s của TVKD %s";
    public static final String APPROVAL_MEMBER_ROLE_CREATE_CODE = "approval.clientManagement.memberManagement.memberRoleManagement.memberRole.create";
    public static final String MEMBER_ROLE_CREATE_NAME = "Tạo thông tin nhóm quyền của TVKD";
    public static final String MEMBER_ROLE_CREATE_DESC = "Tạo thông tin nhóm quyền %s của TVKD %s";
    public static final String APPROVAL_MEMBER_ROLE_UPDATE_CODE = "approval.clientManagement.memberManagement.memberRoleManagement.memberRole.update";
    public static final String MEMBER_ROLE_UPDATE_NAME = "Cập nhật thông tin nhóm quyền của TVKD";
    public static final String MEMBER_ROLE_UPDATE_DESC = "Cập nhật thông tin nhóm quyền %s của TVKD %s";
    public static final String APPROVAL_MEMBER_ROLE_FUNCTIONS_ASSIGN_CREATE_CODE = "approval.clientManagement.memberManagement.memberRoleManagement.memberRoleFunctionsAssign.create";
    public static final String MEMBER_ROLE_FUNCTIONS_ASSIGN_CREATE_NAME = "Gán quyền cho nhóm quyền của TVKD";
    public static final String MEMBER_ROLE_FUNCTIONS_ASSIGN_CREATE_DESC = "Gán quyền cho nhóm quyền %s của TVKD %s";
    public static final String APPROVAL_MEMBER_MOVE_ALL_INVESTORS_CODE = "approval.clientManagement.memberManagement.allInvestorsNewMember.transfer";
    public static final String MEMBER_MOVE_ALL_INVESTORS_NAME = "Chuyển toàn bộ TKGD sang TVKD mới";
    public static final String MEMBER_MOVE_ALL_INVESTORS_DESC = "Chuyển toàn bộ TKGD từ TVKD %s sang TVKD %s";
    
    public static final String APPROVAL_BROKER_CREATE_CODE = "approval.clientManagement.brokerManagement.brokerInfo.create";
    public static final String BROKER_CREATE_NAME = "Tạo thông tin MG";
    public static final String APPROVAL_BROKER_UPDATE_CODE = "approval.clientManagement.brokerManagement.brokerInfo.update";
    public static final String BROKER_UPDATE_NAME = "Cập nhật thông tin MG";
    public static final String APPROVAL_BROKER_FUNCTIONS_ASSIGN_CREATE_CODE = "approval.clientManagement.brokerManagement.brokerFunctionsAssign.create";
    public static final String BROKER_FUNCTIONS_ASSIGN_CREATE_NAME = "Gán quyền riêng cho MG";
    
    public static final String APPROVAL_COLLABORATOR_CREATE_CODE = "approval.clientManagement.brokerCollaboratorManagement.collaboratorInfo.create";
    public static final String COLLABORATOR_CREATE_NAME = "Tạo thông tin CTV MG";
    public static final String APPROVAL_COLLABORATOR_UPDATE_CODE = "approval.clientManagement.brokerCollaboratorManagement.collaboratorInfo.update";
    public static final String COLLABORATOR_UPDATE_NAME = "Cập nhật thông tin CTV MG";
    public static final String APPROVAL_COLLABORATOR_FUNCTIONS_ASSIGN_CREATE_CODE = "approval.clientManagement.brokerCollaboratorManagement.collaboratorFunctionsAssign.create";
    public static final String COLLABORATOR_FUNCTIONS_ASSIGN_CREATE_NAME = "Gán quyền riêng cho CTV MG";
    
    public static final String APPROVAL_INVESTOR_CREATE_CODE = "approval.clientManagement.investorManagement.investorInfo.create";
    public static final String INVESTOR_CREATE_NAME = "Tạo thông tin TKGD";
    public static final String APPROVAL_INVESTOR_UPDATE_CODE = "approval.clientManagement.investorManagement.investorInfo.update";
    public static final String INVESTOR_UPDATE_NAME = "Cập nhật thông tin TKGD";
    public static final String APPROVAL_INVESTOR_USER_CREATE_CODE = "approval.clientManagement.investorManagement.investorUser.create";
    public static final String INVESTOR_USER_CREATE_NAME = "Tạo đăng nhập thứ hai trở lên của TKGD";
    public static final String INVESTOR_USER_CREATE_DESC = "Tạo đăng nhập %s của TKGD %s";
    public static final String APPROVAL_INVESTOR_RISK_ORDER_LOCK_CREATE_CODE = "approval.clientManagement.investorManagement.investorRiskManagement.orderLockConfig.create";
    public static final String INVESTOR_RISK_ORDER_LOCK_CREATE_NAME = "Thiết lập chức năng khoá giao dịch của TKGD";
    public static final String APPROVAL_INVESTOR_RISK_NEW_ORDER_LOCK_CREATE_CODE = "approval.clientManagement.investorManagement.investorRiskManagement.newOrderLockConfig.create";
    public static final String INVESTOR_RISK_NEW_ORDER_LOCK_CREATE_NAME = "Thiết lập chức năng chặn giao dịch một chiều của TKGD";
    public static final String APPROVAL_INVESTOR_BROKER_CHANGE_CODE = "approval.clientManagement.investorManagement.investorNewBroker.transfer";
    public static final String INVESTOR_BROKER_CHANGE_NAME = "Chuyển TKGD sang MG mới";
    public static final String INVESTOR_BROKER_CHANGE_DESC = "Chuyển TKGD %s sang MG %s";
    public static final String APPROVAL_INVESTOR_COLLABORATOR_CHANGE_CODE = "approval.clientManagement.investorManagement.investorNewCollaborator.transfer";
    public static final String INVESTOR_COLLABORATOR_CHANGE_NAME = "Chuyển TKGD sang CTV MG mới";
    public static final String INVESTOR_COLLABORATOR_CHANGE_DESC = "Chuyển TKGD %s sang CTV MG %s";
    public static final String APPROVAL_INVESTOR_MARGIN_DEPOSIT_CODE = "approval.clientManagement.marginDepositWithdrawalManagement.investorMarginDeposit.create";
    public static final String INVESTOR_MARGIN_DEPOSIT_NAME = "Nộp ký quỹ TKGD";
    public static final String APPROVAL_INVESTOR_MARGIN_WITHDRAWAL_CODE = "approval.clientManagement.marginDepositWithdrawalManagement.investorMarginWithdrawal.create";
    public static final String INVESTOR_MARGIN_WITHDRAWAL_NAME = "Rút ký quỹ TKGD";
    public static final String APPROVAL_REFUND_INVESTOR_MARGIN_DEPOSIT_CODE = "approval.clientManagement.marginDepositWithdrawalManagement.investorMarginDeposit.refund";
    public static final String REFUND_INVESTOR_MARGIN_DEPOSIT_NAME = "Hoàn trả nộp ký quỹ TKGD";
    
    public static final String INVESTOR_ACTIVATE_CODE = "clientManagement.investorManagement.investorAccount.activate";
    public static final String INVESTOR_ACTIVATE_NAME = "Kích hoạt TKGD";
    
    public static final String APPROVAL_USER_EXCHANGE_SETTING_CREATE = "approval.exchangeSetting.create";
    public static final String USER_EXCHANGE_SETTING_CREATE_NAME = "Kích hoạt giá cho đăng nhập";
    public static final String APPROAL_USER_EXCHANGE_SETTING_UPDATE = "approval.exchangeSetting.update";
    public static final String USER_EXCHANGE_SETTING_UPDATE_NAME = "Cập nhật kích hoạt giá cho đăng nhập";
    
    public static String getApprovalDescription(String name, String value) {
        return (name + " " + value);
    }
    
    public static String getApprovalDescription2(String name, String value1, String value2) {
        return String.format(name, value1, value2);
    }
}
