/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.newgen.am.common.Constant;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.Utility;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.LoginAdminUser;
import com.newgen.am.service.LoginAdminUserService;

/**
 *
 * @author nhungtt
 */
@Component
public class AdminAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    HybridUserDetailsService userService;
    
    @Autowired
    LoginAdminUserService loginAdmUserService;

    @Override
    public Authentication authenticate(Authentication authentication) throws CustomException {
        LoginAdminUser adminUser = userService.authenticateLoginAdminUser(String.valueOf(authentication.getPrincipal()),
                String.valueOf(authentication.getCredentials()));
        if (Utility.isNull(adminUser)) {
        	throw new CustomException(ErrorMessage.AUTHENTICATION_FAILED, HttpStatus.UNAUTHORIZED);
        } else if (!Constant.STATUS_ACTIVE.equalsIgnoreCase(adminUser.getStatus())) {
        	throw new CustomException(ErrorMessage.INACTIVE_USER, HttpStatus.UNAUTHORIZED);
        } else {
        	List<String> admUserFunctions = loginAdmUserService.getFunctionsByUser(adminUser);
            List<SimpleGrantedAuthority> admUserRoles = admUserFunctions.stream().map(s -> new SimpleGrantedAuthority(s)).collect(Collectors.toList());
            
            return new AdminUsernamePasswordAuthenticationToken(
                    authentication.getPrincipal(), authentication.getCredentials(),
                    admUserRoles);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AdminUsernamePasswordAuthenticationToken.class
        .isAssignableFrom(authentication);
    }

}
