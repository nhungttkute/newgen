package com.newgen.am.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newgen.am.model.Commodity;
import com.newgen.am.model.MemberUser;
import com.newgen.am.model.RoleFunction;
import com.newgen.am.model.UserRole;
import com.newgen.am.validation.LengthGroup;
import com.newgen.am.validation.ValidUpdateStringField;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateMemberDTO {
    @ValidUpdateStringField
    @Size(max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String name;
    @ValidUpdateStringField
    @Size(max = 20, message = "Invalid format.", groups = LengthGroup.class)
    private String status;
    @Size(max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String note;
    @Valid
    private UpdateCompanyDTO company;
    private List<UserRole> roles;
    private List<RoleFunction> functions;
    private int orderLimit;
    private int defaultPositionLimit;
    private long defaultCommodityFee;
    private List<Commodity> comodities;
    private List<MemberUser> memberUsers;

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
	public UpdateCompanyDTO getCompany() {
		return company;
	}
	public void setCompany(UpdateCompanyDTO company) {
		this.company = company;
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
	public List<Commodity> getComodities() {
		return comodities;
	}
	public void setComodities(List<Commodity> comodities) {
		this.comodities = comodities;
	}
	public List<MemberUser> getMemberUsers() {
		return memberUsers;
	}
	public void setMemberUsers(List<MemberUser> memberUsers) {
		this.memberUsers = memberUsers;
	}
    
}
