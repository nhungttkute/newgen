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
public class ApprovalConstant {
    public static final String APPROVAL_PENDING_URL = "/admin/pendingApprovals/%s";
    public static final String APPROVAL_ACCOUNT_TRANS_URL = "/admin/accountTransApprovals/%s";
    public static final String APPROVAL_ACCOUNT_ACTIVATION_URL = "/admin/accActivationApprovals/%s";
    
    public static final String DEPARTMENT_CREATE = "DepartmentService.createDepartment";
    public static final String DEPARTMENT_UPDATE = "DepartmentService.updateDepartment";
    public static final String DEPARTMENT_USER_CREATE = "DepartmentService.createDepartmentUser";
    public static final String DEPARTMENT_USER_UPDATE = "DepartmentService.updateDepartmentUser";
    public static final String DEPARTMENT_USER_ROLES_CREATE = "DepartmentService.saveDepartmentUserRoles";
    public static final String DEPARTMENT_USER_FUNCTIONS_CREATE = "DepartmentService.saveDepartmentUserFunctions";
    
    public static final String SYSTEM_ROLE_CREATE = "SystemRoleService.createSystemRole";
    public static final String SYSTEM_ROLE_UPDATE = "SystemRoleService.updateSystemRole";
    public static final String SYSTEM_ROLE_FUNCTIONS_CREATE = "SystemRoleService.createSystemRoleFunctions";
}
