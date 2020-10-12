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
public class LocalServiceConnection extends BaseConnection {
    private String domain;
    
    public LocalServiceConnection() {
        super(ConfigLoader.getMainConfig().getInt(Constant.SERVICE_CONNECTION_TIMEOUT), ConfigLoader.getMainConfig().getInt(Constant.SERVICE_SOCKET_TIMEOUT));
        domain = ConfigLoader.getMainConfig().getString(Constant.DOMAIN);
    }
    
    public String getActivityLogServiceURL() {
        return domain + ConfigLoader.getMainConfig().getString(Constant.SERVICE_ACTIVITY_LOG);
    }
    
    public String getEmailNotificationServiceURL() {
        return domain + ConfigLoader.getMainConfig().getString(Constant.SERVICE_NOTIFICATION_EMAIL);
    }
    
    public String getSMSNotificationServiceURL() {
        return domain + ConfigLoader.getMainConfig().getString(Constant.SERVICE_NOTIFICATION_SMS);
    }
    
    public String getLogoutHandleServiceURL() {
        return domain + ConfigLoader.getMainConfig().getString(Constant.SERVICE_NOTIFICATION_LOGOUT);
    }
    
    public String getSessionDateServiceURL() {
        return domain + ConfigLoader.getMainConfig().getString(Constant.SERVICE_CONFIG_SESSION_DATE);
    }
    
    public String getWithdrawalAmountServiceURL(String investorCode) {
        return domain + String.format(ConfigLoader.getMainConfig().getString(Constant.SERVICE_MARGIN_WITHDRAWAL_AMOUNT), investorCode);
    }
    
    public String getExchangeRateServiceURL() {
        return domain + ConfigLoader.getMainConfig().getString(Constant.SERVICE_EXCHANGE_RATE);
    }
    
    public String getProcessMarginServiceURL(String investorCode) {
        return domain + String.format(ConfigLoader.getMainConfig().getString(Constant.SERVICE_PROCESS_MARGIN), investorCode);
    }
    
    public String getCMSServiceURL(String cmsUrl) {
        return domain + cmsUrl;
    }
}
