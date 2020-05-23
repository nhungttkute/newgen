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
    
    public static final String MEMBER_CREATE = "MemberService.createMember";
    public static final String MEMBER_UPDATE = "MemberService.updateMember";
    public static final String MEMBER_FUNCTIONS_CREATE = "MemberService.createMemberFunctions";
    public static final String MEMBER_USER_CREATE = "MemberService.createMemberUser";
    public static final String MEMBER_USER_UPDATE = "MemberService.updateMemberUser";
    public static final String MEMBER_USER_ROLES_CREATE = "MemberService.saveMemberUserRoles";
    public static final String MEMBER_USER_FUNCTIONS_CREATE = "MemberService.saveMemberUserFunctions";
    public static final String MEMBER_DEFAULT_SETTING_CREATE = "MemberService.createDefaultSetting";
    public static final String MEMBER_COMMODITIES_SETTING_CREATE = "MemberService.createMemberCommodities";
    public static final String MEMBER_RISK_NEW_POSITION_LOCK_SET = "MemberService.setMemberNewPositionOrderLock";
    public static final String MEMBER_RISK_ORDER_LOCK_SET = "MemberService.setMemberOrderLock";
    public static final String MEMBER_RISK_MARGIN_WITHDRAWAL_LOCK_SET = "MemberService.setMemberMarginWithDrawalLock";
    public static final String MEMBER_MARGIN_MULTIPLIER_BULK_CREATE = "MemberService.setMarginMultiplierBulk";
    public static final String MEMBER_MARGIN_RATIO_BULK_CREATE = "MemberService.setMarginRatioAlertBulk";
    public static final String MEMBER_GENERAL_FEE_BULK_CREATE = "MemberService.setGeneralFeeBulk";
    public static final String MEMBER_OTHER_FEE_BULK_CREATE = "MemberService.setOtherFeeBulk";
    public static final String MEMBER_BROKER_COMMODITY_FEE_BULK_CREATE = "MemberService.setBrokerCommoditiesFeeBulk";
    public static final String MEMBER_INVESTOR_COMMODITY_FEE_BULK_CREATE = "MemberService.setInvestorCommoditiesFeeBulk";
    public static final String MEMBER_ROLE_CREATE = "MemberRoleService.createMemberRole";
    public static final String MEMBER_ROLE_UPDATE = "MemberRoleService.updateMemberRole";
    public static final String MEMBER_ROLE_FUNCTIONS_CREATE = "MemberRoleService.createMemberRoleFunctions";
    
    public static final String BROKER_CREATE = "BrokerService.createBroker";
    public static final String BROKER_UPDATE = "BrokerService.updateBroker";
    public static final String BROKER_FUNCTIONS_CREATE = "BrokerService.";
    public static final String BROKER_DEFAULT_SETTING_CREATE = "BrokerService.";
    public static final String BROKER_COMMODITIES_SETTING_CREATE = "BrokerService.";
    
    public static final String COLLABORATOR_CREATE = "CollaboratorService.createCollaborator";
    public static final String COLLABORATOR_UPDATE = "CollaboratorService.createCollaborator";
    public static final String COLLABORATOR_FUNCTIONS_CREATE = "CollaboratorService.createCollaborator";
}
