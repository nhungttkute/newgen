/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.io.Serializable;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author nhungtt
 */
@Document(collection = "pending_approvals")
public class PendingApproval extends AuditModel implements Serializable {
    @Transient
    public static final String SEQUENCE_NAME = "pending_approval_seq";
    @Id
    private String id;
    private String apiUrl;
    private long creatorDate;
    private String creatorUser;
    private long approvalDate;
    private String approvalUser;
    private String functionCode;
    private String functionName;
    private String description;
    private String status;
    private String rejectReason;
    private NestedObjectInfo nestedObjInfo;
    private PendingData pendingData;

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public long getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(long approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getApprovalUser() {
        return approvalUser;
    }

    public void setApprovalUser(String approvalUser) {
        this.approvalUser = approvalUser;
    }

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public NestedObjectInfo getNestedObjInfo() {
        return nestedObjInfo;
    }

    public void setNestedObjInfo(NestedObjectInfo nestedObjInfo) {
        this.nestedObjInfo = nestedObjInfo;
    }

    public long getCreatorDate() {
        return creatorDate;
    }

    public void setCreatorDate(long creatorDate) {
        this.creatorDate = creatorDate;
    }

    public String getCreatorUser() {
        return creatorUser;
    }

    public void setCreatorUser(String creatorUser) {
        this.creatorUser = creatorUser;
    }

    public PendingData getPendingData() {
        return pendingData;
    }

    public void setPendingData(PendingData pendingData) {
        this.pendingData = pendingData;
    }

}
