package com.newgen.am.dto;

import lombok.Data;

@Data
public class LoginAdminUsersDTO {
	private String deptCode;
    private String memberCode;
    private String brokerCode;
    private String collaboratorCode;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String status;
    private Boolean logined = false;
    private int logonCounts;
    private long logonTime;
    private String logonTimeStr;
}
