/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newgen.am.model.RoleFunction;
import com.newgen.am.model.UserRole;
import com.newgen.am.validation.FormatGroup;
import com.newgen.am.validation.LengthGroup;
import com.newgen.am.validation.ValidPhoneNumber;
import com.newgen.am.validation.ValidUpdateStringField;
import com.newgen.am.validation.ValidUsername;

/**
 *
 * @author nhungtt
 */
/**
 * @author E7470
 *
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class UserDTO {
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String _id;
    @ValidUpdateStringField
    private String memberCode;
    @ValidUpdateStringField
    private String brokerCode;
    @ValidUpdateStringField
    private String collaboratorCode;
    @NotEmpty(message = "Required.")
    @ValidUsername(groups = FormatGroup.class)
    private String username;
    @NotEmpty(message = "Required.")
    @Size(min = 1, max = 100, message = "Invalid format.", groups = LengthGroup.class)
    private String fullName;
    @NotEmpty(message = "Required.")
    @Email(message = "Invalid format.", groups = FormatGroup.class)
    @Size(min = 1, max = 50, message = "Invalid format.", groups = LengthGroup.class)
    private String email;
    @NotEmpty(message = "Required.")
    @ValidPhoneNumber(groups = FormatGroup.class)
    private String phoneNumber;
    @NotEmpty(message = "Required.")
    @Size(min = 1, max = 20, message = "Invalid format.", groups = LengthGroup.class)
    private String status; //pending, active, inactive
    @Size(min = 1, max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String note;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @NotNull(message = "Required.")
    private Boolean isPasswordExpiryCheck;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int passwordExpiryDays;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int expiryAlertDays;
    private long createdDate;
    private UserRole role;
    private List<UserRole> roles;
    private List<RoleFunction> functions;

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

	public String getBrokerCode() {
		return brokerCode;
	}

	public void setBrokerCode(String brokerCode) {
		this.brokerCode = brokerCode;
	}

	public String getCollaboratorCode() {
		return collaboratorCode;
	}

	public void setCollaboratorCode(String collaboratorCode) {
		this.collaboratorCode = collaboratorCode;
	}

	public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public Boolean getIsPasswordExpiryCheck() {
		return isPasswordExpiryCheck;
	}

	public void setIsPasswordExpiryCheck(Boolean isPasswordExpiryCheck) {
		this.isPasswordExpiryCheck = isPasswordExpiryCheck;
	}

	public int getPasswordExpiryDays() {
		return passwordExpiryDays;
	}

	public void setPasswordExpiryDays(int passwordExpiryDays) {
		this.passwordExpiryDays = passwordExpiryDays;
	}

	public int getExpiryAlertDays() {
        return expiryAlertDays;
    }

    public void setExpiryAlertDays(int expiryAlertDays) {
        this.expiryAlertDays = expiryAlertDays;
    }

	public long getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(long createdDate) {
		this.createdDate = createdDate;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
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
    
}
