/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import javax.validation.constraints.Size;

import com.newgen.am.validation.FormatGroup;
import com.newgen.am.validation.LengthGroup;
import com.newgen.am.validation.ValidCode;
import com.newgen.am.validation.ValidUpdateStringField;

/**
 *
 * @author nhungtt
 */public class UpdateDepartmentDTO {
    @ValidUpdateStringField
    @Size(max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String name;
    @ValidUpdateStringField
    @Size(max = 20, message = "Invalid format.", groups = LengthGroup.class)
    private String status;
    @Size(max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String note;

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
