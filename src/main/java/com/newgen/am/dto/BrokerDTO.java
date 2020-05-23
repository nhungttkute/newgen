package com.newgen.am.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newgen.am.model.BrokerUser;
import com.newgen.am.model.Company;
import com.newgen.am.model.Contact;
import com.newgen.am.model.Individual;
import com.newgen.am.model.RoleFunction;
import com.newgen.am.model.UserRole;
import com.newgen.am.validation.FormatGroup;
import com.newgen.am.validation.LengthGroup;
import com.newgen.am.validation.ValidNumber;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class BrokerDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _id;
	@NotEmpty(message = "Required.")
    @ValidNumber(groups = FormatGroup.class)
    @Size(min = 3, max = 3, message = "Invalid format.", groups = LengthGroup.class)
    private String memberCode;
	@NotEmpty(message = "Required.")
    @Size(min = 1, max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String memberName;
    private String code;
    @NotEmpty(message = "Required.")
    @Size(min = 1, max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String name;
    @Size(min = 1, max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String note;
    @NotEmpty(message = "Required.")
    @Size(min = 1, max = 20, message = "Invalid format.", groups = LengthGroup.class)
    private String status;
    @NotEmpty(message = "Required.")
    @Size(min = 1, max = 20, message = "Invalid format.", groups = LengthGroup.class)
    private String type;
    private long createdDate;
    @Valid
    private Company company;
    @Valid
    private Individual individual;
    private Contact contact;
    private BrokerUser user;
    private UserRole role;
    private List<RoleFunction> functions;
    private long defaultCommodityFee;
    private List<BrokerCommodity> commodities;
    
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getMemberCode() {
		return memberCode;
	}
	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}
	public String getMemberName() {
		return memberName;
	}
	public void setMemberName(String memberName) {
		this.memberName = memberName;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}
	public Individual getIndividual() {
		return individual;
	}
	public void setIndividual(Individual individual) {
		this.individual = individual;
	}
	public Contact getContact() {
		return contact;
	}
	public void setContact(Contact contact) {
		this.contact = contact;
	}
	public BrokerUser getUser() {
		return user;
	}
	public void setUser(BrokerUser user) {
		this.user = user;
	}
	public UserRole getRole() {
		return role;
	}
	public void setRole(UserRole role) {
		this.role = role;
	}
	public List<RoleFunction> getFunctions() {
		return functions;
	}
	public void setFunctions(List<RoleFunction> functions) {
		this.functions = functions;
	}
	public List<BrokerCommodity> getCommodities() {
		return commodities;
	}
	public void setCommodities(List<BrokerCommodity> commodities) {
		this.commodities = commodities;
	}
	public long getDefaultCommodityFee() {
		return defaultCommodityFee;
	}
	public void setDefaultCommodityFee(long defaultCommodityFee) {
		this.defaultCommodityFee = defaultCommodityFee;
	}
	public long getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(long createdDate) {
		this.createdDate = createdDate;
	}
    
}
