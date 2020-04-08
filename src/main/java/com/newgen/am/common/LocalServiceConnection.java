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
    
    public LocalServiceConnection() {
        super(ConfigLoader.getMainConfig().getInt(Constant.SERVICE_CONNECTION_TIMEOUT), ConfigLoader.getMainConfig().getInt(Constant.SERVICE_SOCKET_TIMEOUT));
    }
    
    public String getActivityLogServiceURL() {
        return ConfigLoader.getMainConfig().getString(Constant.SERVICE_ACTIVITY_LOG);
    }
    
    public String getEmailNotificationServiceURL() {
        return ConfigLoader.getMainConfig().getString(Constant.SERVICE_NOTIFICATION_EMAIL);
    }
    
    public String getSMSNotificationServiceURL() {
        return ConfigLoader.getMainConfig().getString(Constant.SERVICE_NOTIFICATION_SMS);
    }
}
