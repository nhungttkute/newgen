/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newgen.am.model.RoleFunction;
import java.util.List;

import javax.validation.constraints.NotEmpty;

/**
 *
 * @author nhungtt
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SystemRoleDTO {
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String _id;
    @NotEmpty(message = "Required.")
    private String name;
    @NotEmpty(message = "Required.")
    private String description;
    @NotEmpty(message = "Required.")
    private String status;
    private String createdDate;
    private List<RoleFunction> functions;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public List<RoleFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(List<RoleFunction> functions) {
        this.functions = functions;
    }
    
}
