package com.newgen.am.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CQGResponseObj {
	private String status;
    private String errMsg;
    private CQGDataObj data;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	public CQGDataObj getData() {
		return data;
	}
	public void setData(CQGDataObj data) {
		this.data = data;
	}
}
