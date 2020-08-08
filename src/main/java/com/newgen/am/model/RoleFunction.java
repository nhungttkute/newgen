/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.constraints.NotEmpty;

/**
 *
 * @author nhungtt
 */
public class RoleFunction implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@NotEmpty(message = "Required.")
    private String code;
	@NotEmpty(message = "Required.")
    private String name;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleFunction function = (RoleFunction) o;
        return code.equals(function.code) && name.equals(function.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(code, name);
    }
}
