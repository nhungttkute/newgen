package com.newgen.am.model;

import lombok.Data;

@Data
public class BaseApproval extends AuditModel {
	private static final long serialVersionUID = 1L;
	private String apiUrl;
    private long approvalDate;
    private String approvalUser;
    private String functionCode;
    private String functionName;
    private String description;
    private String status;
    private String rejectReason;
    private NestedObjectInfo nestedObjInfo;
    private PendingData pendingData;
}
