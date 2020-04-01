/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.security;

import com.newgen.am.common.Utility;
import com.newgen.am.model.LoginAdminUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 *
 * @author nhungtt
 */
@Component
public class AdminAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    HybridUserDetailsService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        LoginAdminUser adminUser = userService.authenticateLoginAdminUser(String.valueOf(authentication.getPrincipal()),
                String.valueOf(authentication.getCredentials()));
        if (adminUser != null) {
            return new AdminUsernamePasswordAuthenticationToken(
                    authentication.getPrincipal(), authentication.getCredentials(),
                    Utility.getAdminAuthorities());
        } else {
            throw new BadCredentialsException("Authentication failed.");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AdminUsernamePasswordAuthenticationToken.class
        .isAssignableFrom(authentication);
    }

}
