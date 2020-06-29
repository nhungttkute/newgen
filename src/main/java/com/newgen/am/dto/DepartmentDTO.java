/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newgen.am.validation.FormatGroup;
import com.newgen.am.validation.LengthGroup;
import com.newgen.am.validation.ValidCode;

/**
 *
 * @author nhungtt
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepartmentDTO {
    private String _id;
    @NotEmpty(message = "Required.")
    @ValidCode(groups = FormatGroup.class)
    private String code;
    @NotEmpty(message = "Required.")
    @Size(min = 1, max = 100, message = "Invalid format.", groups = LengthGroup.class)
    private String name;
    private String status;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private long createdDate;
    @Size(max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String note;
    private List<UserDTO> users;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

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

    public List<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UserDTO> users) {
        this.users = users;
    }

	public long getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(long createdDate) {
		this.createdDate = createdDate;
	}
}
