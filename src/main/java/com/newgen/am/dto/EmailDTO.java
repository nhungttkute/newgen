/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
@Data
public class EmailDTO {
	private String settingType;
	private String sendingObject;
    private String to;
    private String subject;
    private String bodyStr;
}
