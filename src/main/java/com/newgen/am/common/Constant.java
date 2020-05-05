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
//    public static final String CONFIG_DIR = "/opt/account-management/settings/properties/";
    public static final String CONFIG_DIR = "C:/u01/apps/AM/settings/properties/";
    
    public static final String DATE_TIME_PATTERN = "dd/MM/yyyy HH:mm:ss";
    
    public static final String RESPONSE_OK = "ok";
    public static final String RESPONSE_ERROR = "error";
    
    public static final String MONGODB_HOST = "MONGODB_HOST";
    public static final String MONGODB_PORT = "MONGODB_PORT";
    public static final String MONGODB_USERNAME = "MONGODB_USERNAME";
    public static final String MONGODB_PASSWORD = "MONGODB_PASSWORD";
    public static final String MONGODB_DATABASE = "MONGODB_DATABASE";
    
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
    
    public static final String POSITION_LIMITED = "LIMITED";
    public static final String POSITION_INHERITED = "INHERITED";
    
    public static final String MEMBER_DEFAULT_ROLE = "TVKD";
    public static final String MEMBER_MASTER_USER_PREFIX = "TVKD_";
}
