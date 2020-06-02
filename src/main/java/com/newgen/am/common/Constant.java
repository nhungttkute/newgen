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
public class Constant {
    public static final String CONFIG_DIR = "/opt/account-management/settings/properties/";
//    public static final String CONFIG_DIR = "C:/u01/apps/AM/settings/properties/";
    
    public static final String DATE_TIME_PATTERN = "dd/MM/yyyy HH:mm:ss";
    
    public static final String RESPONSE_OK = "ok";
    public static final String RESPONSE_ERROR = "error";
    
    public static final String MONGODB_HOST = "MONGODB_HOST";
    public static final String MONGODB_PORT = "MONGODB_PORT";
    public static final String MONGODB_USERNAME = "MONGODB_USERNAME";
    public static final String MONGODB_PASSWORD = "MONGODB_PASSWORD";
    public static final String MONGODB_DATABASE = "MONGODB_DATABASE";
    public static final String MONGODB_CONNECTIONS_PER_HOST = "MONGODB_CONNECTIONS_PER_HOST";
    public static final String MONGODB_CONNECTION_TIMEOUT = "MONGODB_CONNECTION_TIMEOUT";
    public static final String MONGODB_THREADS_ALLOWED_TO_BLOCK = "MONGODB_THREADS_ALLOWED_TO_BLOCK";
    public static final String MONGODB_MAX_TIME_WAIT = "MONGODB_MAX_TIME_WAIT";
    public static final String MONGODB_SOCKET_TIMEOUT = "MONGODB_SOCKET_TIMEOUT";
    public static final String MONGODB_HEARTBEAT_CONNECT_TIMEOUT = "MONGODB_HEARTBEAT_CONNECT_TIMEOUT";
    
    public static final String INVESTOR_JWT_SECRET = "INVESTOR_JWT_SECRET";
    public static final String INVESTOR_JWT_EXPIRATION = "INVESTOR_JWT_EXPIRATION";
    
    public static final String ADMIN_JWT_SECRET = "ADMIN_JWT_SECRET";
    public static final String ADMIN_JWT_EXPIRATION = "ADMIN_JWT_EXPIRATION";
    
    public static final String REDIS_HOST = "REDIS_HOST";
    public static final String REDIS_PORT = "REDIS_PORT";
    public static final String REDIS_PASSWORD = "REDIS_PASSWORD";
    public static final String REDIS_KEY_SECRET_KEY = "REDIS_KEY_SECRET_KEY";
    
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_PENDING = "PENDING";
    
    public static final String APPROVAL_STATUS_APPROVED = "APPROVED";
    public static final String APPROVAL_STATUS_REJECTED = "REJECTED";
    public static final String APPROVAL_STATUS_PENDING = "PENDING";
    
    public static final String APPROVAL_ACTION_CREATE = "create";
    public static final String APPROVAL_ACTION_UPDATE = "update";
    
    public static final String SERVICE_CONNECTION_TIMEOUT = "SERVICE_CONNECTION_TIMEOUT";
    public static final String SERVICE_SOCKET_TIMEOUT = "SERVICE_SOCKET_TIMEOUT";
    public static final String SERVICE_ACTIVITY_LOG = "SERVICE_ACTIVITY_LOG";
    public static final String SERVICE_NOTIFICATION_EMAIL = "SERVICE_NOTIFICATION_EMAIL";
    public static final String SERVICE_NOTIFICATION_SMS = "SERVICE_NOTIFICATION_SMS";

    public static final String OPT_CONTAINS = "$contains";
    public static final String OPT_EQUALS = "$eq";
    public static final String OPT_IN = "$in";
    public static final String OPT_NOT_IN = "$nin";
    public static final String SORT_DETAUL_FIELD = "lastModifiedDate";
    public static final int PAGINATION_DEFAULT_LIMIT = 20;
    public static final int PAGINATION_DEFAULT_OFFSET = 0;
    
    public static final String CSV_DEPARTMENTS = "departments.csv";
    public static final String CSV_DEPARTMENT_USERS = "department_users.csv";
    public static final String CSV_SYSTEM_ROLES = "system_roles.csv";
    public static final String CSV_MEMBERS = "members.csv";
    public static final String CSV_MEMBER_USERS = "member_users.csv";
    public static final String CSV_MEMBER_ROLES = "member_roles.csv";
    public static final String CSV_BROKERS = "brokers.csv";
    public static final String CSV_COLLABORATORS = "collaborators.csv";
    public static final String CSV_INVESTORS = "investors.csv";
    public static final String CSV_INVESTOR_USERS = "investor_users.csv";
    
    public static final String POSITION_LIMITED = "LIMITED";
    public static final String POSITION_INHERITED = "INHERITED";
    
    public static final String MEMBER_DEFAULT_ROLE = "TVKD";
    public static final String MEMBER_MASTER_USER_PREFIX = "TVKD_";
    
    public static final String BROKER_SEQ = "broker_seq_";
    public static final String COLLABORATOR_SEQ = "collaborator_seq_";
    
    public static final String BROKER_DEFAULT_ROLE = "Moi_Gioi";
    public static final String BROKER_USER_PREFIX = "MG_";
    
    public static final String COLLABORATOR_DEFAULT_ROLE = "CTV_Moi_Gioi";
    public static final String COLLABORATOR_USER_PREFIX = "CTVMG_";
    
    public static final String CURRENCY_VND = "VND";
    
    public static final String BROKER_TYPE_INDIVIDUAL = "INDIVIDUAL";
    public static final String BROKER_TYPE_COMPANY = "COMPANY";
    
    public static final String INVESTOR_DEFAULT_ROLE = "TKGD";
    public static final String INVESTOR_USER_PREFIX = "TKGD_";
    
    public static final String INVESTOR_TYPE_INNER_TRADING_COMPANY = "P";
    public static final String INVESTOR_TYPE_LOCAL_INDIVIDUAL = "C";
    public static final String INVESTOR_TYPE_LOCAL_COMPANY = "E";
    public static final String INVESTOR_TYPE_FOREIGN_INDIVIDUAL = "F";
    public static final String INVESTOR_TYPE_FOREIGN_COMPANY = "I";
    
    public static final String MARGIN_TRANS_TYPE_DEPOSIT = "DEPOSIT";
    public static final String MARGIN_TRANS_TYPE_WITHDRAW = "WITHDRAW";
}
