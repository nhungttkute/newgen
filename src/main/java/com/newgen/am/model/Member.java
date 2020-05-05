/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@Document(collection = "members")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Member extends AuditModel implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
    private String id;
    @NonNull
    private String code;
    @NonNull
    private String name;
    private String note;
    private String status;
    private Company company;
    private Contact contact;
    private List<UserRole> roles;
    private List<RoleFunction> functions;
    private int orderLimit;
    private int defaultPositionLimit;
    private long defaultCommodityFee;
    private List<Commodity> comodities;
    private List<MemberUser> users;

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public List<MemberUser> getUsers() {
        return users;
    }

    public void setUsers(List<MemberUser> users) {
        this.users = users;
    }

    public List<Commodity> getComodities() {
        return comodities;
    }

    public void setComodities(List<Commodity> comodities) {
        this.comodities = comodities;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }

    public List<RoleFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(List<RoleFunction> functions) {
        this.functions = functions;
    }

    public int getOrderLimit() {
        return orderLimit;
    }

    public void setOrderLimit(int orderLimit) {
        this.orderLimit = orderLimit;
    }

	public int getDefaultPositionLimit() {
		return defaultPositionLimit;
	}

	public void setDefaultPositionLimit(int defaultPositionLimit) {
		this.defaultPositionLimit = defaultPositionLimit;
	}

	public long getDefaultCommodityFee() {
		return defaultCommodityFee;
	}

	public void setDefaultCommodityFee(long defaultCommodityFee) {
		this.defaultCommodityFee = defaultCommodityFee;
	}

}
