/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.security;

import com.newgen.am.common.Constant;
import com.newgen.am.common.Utility;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.LoginAdminUser;
import com.newgen.am.service.LoginAdminUserService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

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
        if (adminUser != null && Constant.STATUS_ACTIVE.equalsIgnoreCase(adminUser.getStatus())) {
        	List<String> admUserFunctions = loginAdmUserService.getFunctionsByUsername(adminUser.getUsername());
            List<SimpleGrantedAuthority> admUserRoles = admUserFunctions.stream().map(s -> new SimpleGrantedAuthority(s)).collect(Collectors.toList());
            
            return new AdminUsernamePasswordAuthenticationToken(
                    authentication.getPrincipal(), authentication.getCredentials(),
                    admUserRoles);
        } else {
            throw new CustomException("Authentication failed.", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AdminUsernamePasswordAuthenticationToken.class
        .isAssignableFrom(authentication);
    }

}
