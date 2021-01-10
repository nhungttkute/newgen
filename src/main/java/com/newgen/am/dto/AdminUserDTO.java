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
import com.newgen.am.validation.UniqueAdminEmail;
import com.newgen.am.validation.UniqueGroup;
import com.newgen.am.validation.ValidPhoneNumber;
import com.newgen.am.validation.ValidUpdateStringField;
import com.newgen.am.validation.ValidUsername;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
/**
 * @author E7470
 *
 */
@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class AdminUserDTO {
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
    @UniqueAdminEmail(groups = UniqueGroup.class)
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
}
