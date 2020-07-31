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
    public static final String APPROVAL_PENDING_URL = "/am/admin/pendingApprovals/%s";
    public static final String APPROVAL_ACCOUNT_TRANS_URL = "/am/admin/accountTransApprovals/%s";
    public static final String APPROVAL_ACCOUNT_ACTIVATION_URL = "/am/admin/accActivationApprovals/";
    
    public static final String SERVICE_PACKAGE = "com.newgen.am.service.";
    public static final String DEPARTMENT_CREATE = SERVICE_PACKAGE + "DepartmentService_createDepartment";
    public static final String DEPARTMENT_UPDATE = SERVICE_PACKAGE + "DepartmentService_updateDepartment";
    public static final String DEPARTMENT_USER_CREATE = SERVICE_PACKAGE + "DepartmentService_createDepartmentUser";
    public static final String DEPARTMENT_USER_UPDATE = SERVICE_PACKAGE + "DepartmentService_updateDepartmentUser";
    public static final String DEPARTMENT_USER_ROLES_CREATE = SERVICE_PACKAGE + "DepartmentService_saveDepartmentUserRoles";
    public static final String DEPARTMENT_USER_FUNCTIONS_CREATE = SERVICE_PACKAGE + "DepartmentService_saveDepartmentUserFunctions";
    public static final String DEPARTMENT_CHANGE_USER_DEPT = SERVICE_PACKAGE + "DepartmentService_changeUserDepartment";
    
    public static final String SYSTEM_ROLE_CREATE = SERVICE_PACKAGE + "SystemRoleService_createSystemRole";
    public static final String SYSTEM_ROLE_UPDATE = SERVICE_PACKAGE + "SystemRoleService_updateSystemRole";
    public static final String SYSTEM_ROLE_FUNCTIONS_CREATE = SERVICE_PACKAGE + "SystemRoleService_createSystemRoleFunctions";
    
    public static final String MEMBER_CREATE = SERVICE_PACKAGE + "MemberService_createMember";
    public static final String MEMBER_UPDATE = SERVICE_PACKAGE + "MemberService_updateMember";
    public static final String MEMBER_FUNCTIONS_CREATE = SERVICE_PACKAGE + "MemberService_createMemberFunctions";
    public static final String MEMBER_USER_CREATE = SERVICE_PACKAGE + "MemberService_createMemberUser";
    public static final String MEMBER_USER_UPDATE = SERVICE_PACKAGE + "MemberService_updateMemberUser";
    public static final String MEMBER_USER_ROLES_CREATE = SERVICE_PACKAGE + "MemberService_saveMemberUserRoles";
    public static final String MEMBER_USER_FUNCTIONS_CREATE = SERVICE_PACKAGE + "MemberService_saveMemberUserFunctions";
    public static final String MEMBER_ORDER_LIMIT_CREATE = SERVICE_PACKAGE + "MemberService_createOrderLimit";
    public static final String MEMBER_DEFAULT_POSITION_LIMIT_CREATE = SERVICE_PACKAGE + "MemberService_createDefaultPositionLimit";
    public static final String MEMBER_COMMODITIES_ASSIGN = SERVICE_PACKAGE + "MemberService_assignCommodities";
    public static final String MEMBER_COMMODITIES_POSITION_LIMIT = SERVICE_PACKAGE + "MemberService_setCommoditiesPositionLimit";
    public static final String MEMBER_COMMODITIES_FEE = SERVICE_PACKAGE + "MemberService_setCommoditiesFee";
    public static final String MEMBER_RISK_NEW_POSITION_LOCK_SET = SERVICE_PACKAGE + "MemberService_setMemberNewPositionOrderLock";
    public static final String MEMBER_RISK_ORDER_LOCK_SET = SERVICE_PACKAGE + "MemberService_setMemberOrderLock";
    public static final String MEMBER_RISK_MARGIN_WITHDRAWAL_LOCK_SET = SERVICE_PACKAGE + "MemberService_setMemberMarginWithDrawalLock";
    public static final String MEMBER_MARGIN_MULTIPLIER_BULK_CREATE = SERVICE_PACKAGE + "MemberService_setMarginMultiplierBulk";
    public static final String MEMBER_MARGIN_RATIO_BULK_CREATE = SERVICE_PACKAGE + "MemberService_setMarginRatioAlertBulk";
    public static final String MEMBER_GENERAL_FEE_BULK_CREATE = SERVICE_PACKAGE + "MemberService_setGeneralFeeBulk";
    public static final String MEMBER_GENERAL_FEE_BULK_UPDATE = SERVICE_PACKAGE + "MemberService_updateGeneralFeeBulk";
    public static final String MEMBER_OTHER_FEE_BULK_CREATE = SERVICE_PACKAGE + "MemberService_setOtherFeeBulk";
    public static final String MEMBER_BROKER_COMMODITY_FEE_BULK_CREATE = SERVICE_PACKAGE + "MemberService_setBrokerCommoditiesFeeBulk";
    public static final String MEMBER_INVESTOR_COMMODITY_FEE_BULK_CREATE = SERVICE_PACKAGE + "MemberService_setInvestorCommoditiesFeeBulk";
    public static final String MEMBER_MOVE_ALL_INVESTORS = SERVICE_PACKAGE + "MemberService_moveAllInvestorsToNewMember";
    
    public static final String MEMBER_ROLE_CREATE = SERVICE_PACKAGE + "MemberRoleService_createMemberRole";
    public static final String MEMBER_ROLE_UPDATE = SERVICE_PACKAGE + "MemberRoleService_updateMemberRole";
    public static final String MEMBER_ROLE_FUNCTIONS_CREATE = SERVICE_PACKAGE + "MemberRoleService_createMemberRoleFunctions";
    
    public static final String BROKER_CREATE = SERVICE_PACKAGE + "BrokerService_createBroker";
    public static final String BROKER_UPDATE = SERVICE_PACKAGE + "BrokerService_updateBroker";
    public static final String BROKER_FUNCTIONS_CREATE = SERVICE_PACKAGE + "BrokerService_createBrokerFunctions";
    
    public static final String COLLABORATOR_CREATE = SERVICE_PACKAGE + "CollaboratorService_createCollaborator";
    public static final String COLLABORATOR_UPDATE = SERVICE_PACKAGE + "CollaboratorService_updateCollaborator";
    public static final String COLLABORATOR_FUNCTIONS_CREATE = SERVICE_PACKAGE + "CollaboratorService_createCollaboratorFunctions";
    
    public static final String INVESTOR_CREATE = SERVICE_PACKAGE + "InvestorService_createInvestor";
    public static final String INVESTOR_UPDATE = SERVICE_PACKAGE + "InvestorService_updateInvestor";
    public static final String INVESTOR_USER_CREATE = SERVICE_PACKAGE + "InvestorService_createInvestorUser";
    public static final String INVESTOR_RISK_NEW_POSITION_LOCK_SET = SERVICE_PACKAGE + "InvestorService_setInvestorNewPositionOrderLock";
    public static final String INVESTOR_RISK_ORDER_LOCK_SET = SERVICE_PACKAGE + "InvestorService_setInvestorOrderLock";
    public static final String INVESTOR_DEPOSIT_MONEY = SERVICE_PACKAGE + "InvestorService_depositMargin";
    public static final String INVESTOR_WITHDRAW_MONEY = SERVICE_PACKAGE + "InvestorService_withdrawMargin";
    public static final String INVESTOR_REFUND_DEPOSIT_MONEY = SERVICE_PACKAGE + "InvestorService_refundDepositMargin";
    public static final String INVESTOR_CHANGE_BROKER = SERVICE_PACKAGE + "InvestorService_changeBroker";
    public static final String INVESTOR_CHANGE_COLLABORATOR = SERVICE_PACKAGE + "InvestorService_changeCollaborator";
    
    public static final String INVESTOR_ACTIVATE = SERVICE_PACKAGE + "InvestorService_activateInvestor";
    
    public static final String USER_EXCHANGE_SETTING_CREATE = SERVICE_PACKAGE + "ExchangeSettingService_setExchangeSetting";
    public static final String USER_EXCHANGE_SETTING_UPDATE = SERVICE_PACKAGE + "ExchangeSettingService_updateExchangeSetting";
    
    public static final String APPLIED_OBJ_MEMBER = "TVKD %s";
    public static final String APPLIED_OBJ_MEMBER_INVESTORS = "Toàn bộ TKGD của TVKD %s";
    public static final String APPLIED_OBJ_MEMBER_BROKERS = "Toàn bộ MG của TVKD %s";
    public static final String APPLIED_OBJ_INVESTOR = "TKGD %s";
}
