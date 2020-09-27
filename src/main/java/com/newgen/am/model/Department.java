/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.lang.NonNull;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
@Data
@Document(collection = "departments")
public class Department extends AuditModel implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
    private String id;
    @NonNull
    private String code;
    @NonNull
    private String name;
    private String status;
    private String note;
    private List<DeptUser> users;
}
