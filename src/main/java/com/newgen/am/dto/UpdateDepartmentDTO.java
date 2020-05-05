/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import com.newgen.am.validation.FormatGroup;
import com.newgen.am.validation.ValidCode;
import com.newgen.am.validation.ValidUpdateStringField;

/**
 *
 * @author nhungtt
 */
public class UpdateDepartmentDTO {
    @ValidUpdateStringField
    @ValidCode(groups = FormatGroup.class)
    private String code;
    @ValidUpdateStringField
    private String name;
    @ValidUpdateStringField
    private String status;
    private String note;

	public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
    
}
