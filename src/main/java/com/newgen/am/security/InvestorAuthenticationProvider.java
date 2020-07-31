/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.newgen.am.common.Constant;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.Utility;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.LoginInvestorUser;

/**
 *
 * @author nhungtt
 */
@Component
public class InvestorAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    HybridUserDetailsService userService;
    
    @Override
    public Authentication authenticate(Authentication authentication) throws CustomException {
        LoginInvestorUser investorUser = userService.authenticateLoginInvestorUser(String.valueOf(authentication.getPrincipal()),
                String.valueOf(authentication.getCredentials()));
        if (Utility.isNull(investorUser)) {
        	throw new CustomException(ErrorMessage.AUTHENTICATION_FAILED, HttpStatus.UNAUTHORIZED);
        } else if (!Constant.STATUS_ACTIVE.equalsIgnoreCase(investorUser.getStatus())) {
        	throw new CustomException(ErrorMessage.INACTIVE_USER, HttpStatus.UNAUTHORIZED);
        } else {
            return new InvestorUsernamePasswordAuthenticationToken(
                    authentication.getPrincipal(), authentication.getCredentials(),
                    Utility.getInvestorAuthorities());
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return InvestorUsernamePasswordAuthenticationToken.class
        .isAssignableFrom(authentication);
    }
}
