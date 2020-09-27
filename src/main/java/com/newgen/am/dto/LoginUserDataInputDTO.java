package com.newgen.am.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newgen.am.model.WatchList;
import com.newgen.am.validation.FormatGroup;

import lombok.Data;

import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LoginUserDataInputDTO {
	@NotEmpty(message = "Required.")
    private String username;
    private String password;
    private String oldPassword;
    private String newPassword;
    private String pin;
    private List<WatchList> watchlists;
    private String layout;
    private String language;
    private String theme;
    private int fontSize;
    @NotEmpty(message = "Required.")
    @Email(message = "Invalid format.", groups = FormatGroup.class)
    private String email;
    private String phoneNumber;
    private String tableSetting;
}
