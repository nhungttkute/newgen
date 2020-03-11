/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import com.mongodb.lang.NonNull;
import java.io.Serializable;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author nhungtt
 */
@Document(collection = "departments")
public class Department extends AuditModel implements Serializable {
    @Transient
    public static final String SEQUENCE_NAME = "department_seq";
    @Id
    private Long id;
    @NonNull
    private String code;
    @NonNull
    private String name;
    private String status;
    private String note;
    private List<DeptUser> users;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<DeptUser> getUsers() {
        return users;
    }

    public void setUsers(List<DeptUser> users) {
        this.users = users;
    }
    
}
