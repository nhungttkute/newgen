/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 *
 * @author nhungtt
 */
@Document(collection = "pending_approvals")
public class PendingApproval extends AuditModel implements Serializable {
    @Transient
    public static final String SEQUENCE_NAME = "pending_approval_seq";
    @Id
    private long id;
    @Field(name = "created_event_date")
    private Date createdEventDate;
    @Field(name = "approval_date")
    private Date approvalDate;
    @Field(name = "created_event_user")
    private String createdEventUser;
    @Field(name = "approval_user")
    private String approvalUser;
    @Field(name = "function_code")
    private String functionCode;
    @Field(name = "function_name")
    private String functionName;
    private String description;
    private String status;
    @Field(name = "reject_reason")
    private String rejectReason;
    @Field(name = "nested_obj_info")
    private NestedObjectInfo nestedObjInfo;
    @Field(name = "pending_fields")
    private List<PendingField> pendingFields;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreatedEventDate() {
        return createdEventDate;
    }

    public void setCreatedEventDate(Date createdEventDate) {
        this.createdEventDate = createdEventDate;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
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

    public List<PendingField> getPendingFields() {
        return pendingFields;
    }

    public void setPendingFields(List<PendingField> pendingFields) {
        this.pendingFields = pendingFields;
    }

    public String getCreatedEventUser() {
        return createdEventUser;
    }

    public void setCreatedEventUser(String createdEventUser) {
        this.createdEventUser = createdEventUser;
    }
    
}
