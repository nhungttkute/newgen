package com.newgen.am.model;

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
	public PendingData getPendingData() {
		return pendingData;
	}
	public void setPendingData(PendingData pendingData) {
		this.pendingData = pendingData;
	}
}
