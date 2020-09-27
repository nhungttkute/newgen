/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityLogDTO {
    private String deptCode;
    private String deptName;
    private String memberCode;
    private String memberName;
    private String brokerCode;
    private String brokerName;
    private String collaboratorCode;
    private String collaboratorName;
    private String investorCode;
    private String investorName;
    private String username;
    private String accessToken;
    private String ipAddress;
    private String userAgent;
    private String action;
    private long datetime;
    private String description;
}
