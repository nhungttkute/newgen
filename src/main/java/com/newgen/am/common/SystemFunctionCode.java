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
    public static final String APPROVAL_ADMIN_USER_CREATE_CODE = "approval.adminUserManagement.loginAdminUserManagement.adminUserInfo.create";
    public static final String ADMIN_USER_CREATE_NAME = "Tạo thông tin người dùng đăng nhập admin";
    public static final String APPROVAL_ADMIN_USER_UPDATE_CODE = "approval.adminUserManagement.loginAdminUserManagement.adminUserInfo.update";
    public static final String ADMIN_USER_UPDATE_NAME = "Cập nhật thông tin người dùng đăng nhập admin";
    public static final String APPROVAL_ADMIN_USER_ROLE_ASSIGN_CREATE_CODE = "approval.adminUserManagement.loginAdminUserManagement.adminUserRoleAssign.create";
    public static final String ADMIN_USER_ROLE_ASSIGN_CREATE_NAME = "Gán nhóm quyền mặc định cho người dùng đăng nhập admin";
    public static final String APPROVAL_ADMIN_USER_FUNCTIONS_ASSIGN_CREATE_CODE = "approval.adminUserManagement.loginAdminUserManagement.adminUserFunctionsAssign.create";
    public static final String ADMIN_USER_FUNCTIONS_ASSIGN_CREATE_NAME = "Gán quyền riêng cho người dùng đăng nhập admin";
    public static final String APPROVAL_ADMIN_USER_DEPT_CHANGE_UPDATE_CODE = "approval.adminUserManagement.loginAdminUserManagement.adminUserDeptChange.update";
    public static final String ADMIN_USER_DEPT_CHANGE_UPDATE_NAME = "Chuyển phòng ban người dùng admin";
    public static final String APPROVAL_SYSTEM_ROLE_CREATE_CODE = "approval.adminUserManagement.roleManagement.roleInfo.create";
    public static final String SYSTEM_ROLE_CREATE_NAME = "Tạo thông tin nhóm quyền mặc định";
    public static final String APPROVAL_SYSTEM_ROLE_UPDATE_CODE = "approval.adminUserManagement.roleManagement.roleInfo.update";
    public static final String SYSTEM_ROLE_UPDATE_NAME = "Cập nhật thông tin nhóm quyền mặc định";
    public static final String APPROVAL_SYSTEM_ROLE_FUNCTIONS_ASSIGN_CREATE_CODE = "approval.adminUserManagement.roleManagement.roleFunctionsAssign.create";
    public static final String SYSTEM_ROLE_FUNCTIONS_ASSIGN_CREATE_NAME = "Gán quyền cho nhóm quyền mặc định";
    
    public static String getApprovalDescription(String name, String value) {
        return (name + " " + value);
    }
}
