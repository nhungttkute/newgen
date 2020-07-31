/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

/**
 *
 * @author nhungtt
 */
public class EmailDTO {
	private String settingType;
	private String sendingObject;
    private String to;
    private String subject;
    private String bodyStr;

    public String getSettingType() {
		return settingType;
	}

	public void setSettingType(String settingType) {
		this.settingType = settingType;
	}

	public String getSendingObject() {
		return sendingObject;
	}

	public void setSendingObject(String sendingObject) {
		this.sendingObject = sendingObject;
	}

	public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBodyStr() {
        return bodyStr;
    }

    public void setBodyStr(String bodyStr) {
        this.bodyStr = bodyStr;
    }
    
}
