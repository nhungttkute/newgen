/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.security;

import com.newgen.am.common.Constant;
import com.newgen.am.common.Utility;
import com.newgen.am.model.LoginInvestorUser;
import com.newgen.am.service.LoginInvestorUserService;
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
public class InvestorAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    HybridUserDetailsService userService;
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        LoginInvestorUser investorUser = userService.authenticateLoginInvestorUser(String.valueOf(authentication.getPrincipal()),
                String.valueOf(authentication.getCredentials()));
        if (investorUser != null && Constant.STATUS_ACTIVE.equalsIgnoreCase(investorUser.getStatus())) {
            return new InvestorUsernamePasswordAuthenticationToken(
                    authentication.getPrincipal(), authentication.getCredentials(),
                    Utility.getInvestorAuthorities());
        } else {
            throw new BadCredentialsException("Authentication failed.");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return InvestorUsernamePasswordAuthenticationToken.class
        .isAssignableFrom(authentication);
    }
}
