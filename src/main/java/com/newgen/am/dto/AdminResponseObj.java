/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author nhungtt
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AdminResponseObj implements Serializable {
	private static final long serialVersionUID = 1L;
	private String status;
    private String errMsg;
    private AdminDataObj data;
    private Pagination pagination;
    private List<String> filterList;

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

    public AdminDataObj getData() {
        return data;
    }

    public void setData(AdminDataObj data) {
        this.data = data;
    }

	public Pagination getPagination() {
		return pagination;
	}

	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}

	public List<String> getFilterList() {
		return filterList;
	}

	public void setFilterList(List<String> filterList) {
		this.filterList = filterList;
	}
    
}
