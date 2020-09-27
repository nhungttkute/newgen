package com.newgen.am.dto;

import java.util.List;

import javax.validation.Valid;

import com.newgen.am.model.UserRole;

import lombok.Data;

@Data
public class UserRolesDTO {
    @Valid
    private List<UserRole> roles;
}
