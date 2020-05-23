package com.newgen.am.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.newgen.am.model.CollaboratorUser;
import com.newgen.am.model.Contact;
import com.newgen.am.model.Delegate;
import com.newgen.am.model.RoleFunction;
import com.newgen.am.model.UserRole;
import com.newgen.am.validation.FormatGroup;
import com.newgen.am.validation.LengthGroup;
import com.newgen.am.validation.ValidNumber;

public class CollaboratorDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	@NotEmpty(message = "Required.")
    @ValidNumber(groups = FormatGroup.class)
    @Size(min = 3, max = 3, message = "Invalid format.", groups = LengthGroup.class)
	private String memberCode;
	@NotEmpty(message = "Required.")
    @Size(min = 1, max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String memberName;
	@NotEmpty(message = "Required.")
    @ValidNumber(groups = FormatGroup.class)
    @Size(min = 8, max = 8, message = "Invalid format.", groups = LengthGroup.class)
    private String brokerCode;
	@NotEmpty(message = "Required.")
    @Size(min = 1, max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String brokerName;
    private String code;
    @NotEmpty(message = "Required.")
    @Size(min = 1, max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String name;
    @Size(min = 1, max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String note;
    @NotEmpty(message = "Required.")
    @Size(min = 1, max = 20, message = "Invalid format.", groups = LengthGroup.class)
    private String status;
    @NotNull(message = "Required.")
    @Valid
    private Delegate delegate;
    private Contact contact;
    private CollaboratorUser user;
    private UserRole role;
    private List<RoleFunction> functions;
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
	public String getBrokerCode() {
		return brokerCode;
	}
	public void setBrokerCode(String brokerCode) {
		this.brokerCode = brokerCode;
	}
	public String getBrokerName() {
		return brokerName;
	}
	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
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
	public Delegate getDelegate() {
		return delegate;
	}
	public void setDelegate(Delegate delegate) {
		this.delegate = delegate;
	}
	public Contact getContact() {
		return contact;
	}
	public void setContact(Contact contact) {
		this.contact = contact;
	}
	public CollaboratorUser getUser() {
		return user;
	}
	public void setUser(CollaboratorUser user) {
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
    
}
