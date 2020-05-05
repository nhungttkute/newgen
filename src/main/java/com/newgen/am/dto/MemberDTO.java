package com.newgen.am.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newgen.am.model.Commodity;
import com.newgen.am.model.Company;
import com.newgen.am.model.Contact;
import com.newgen.am.model.MemberUser;
import com.newgen.am.model.RoleFunction;
import com.newgen.am.model.UserRole;
import com.newgen.am.validation.FormatGroup;
import com.newgen.am.validation.LengthGroup;
import com.newgen.am.validation.ValidNumber;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberDTO {
	private String _id;
    @NotEmpty(message = "Required.")
    @ValidNumber(groups = FormatGroup.class)
    @Size(min = 3, max = 3, message = "Invalid format.", groups = LengthGroup.class)
    private String code;
    @NotEmpty(message = "Required.")
    @Size(min = 1, max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String name;
    @NotEmpty(message = "Required.")
    @Size(min = 1, max = 20, message = "Invalid format.", groups = LengthGroup.class)
    private String status;
    private String note;
    private String createdDate;
    @NotNull(message = "Required.")
    @Valid
    private Company company;
    private Contact contact;
    private List<UserRole> roles;
    private List<RoleFunction> functions;
    private int orderLimit;
    private int defaultPositionLimit;
    private long defaultCommodityFee;
    private List<Commodity> comodities;
    private List<MemberUser> memberUsers;
    
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
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
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
