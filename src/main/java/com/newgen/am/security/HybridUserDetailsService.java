/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.security;

import com.newgen.am.common.Utility;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.LoginAdminUser;
import com.newgen.am.model.LoginInvestorUser;
import com.newgen.am.repository.LoginAdminUserRepository;
import com.newgen.am.repository.LoginInvestorUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author nhungtt
 */
@Service
public class HybridUserDetailsService {
    @Autowired
    private LoginInvestorUserRepository loginInvUserRepository;
    
    @Autowired
    private LoginAdminUserRepository loginAdmUserRepository;
    
    private PasswordEncoder passwordEncoder;

    @Autowired
    public HybridUserDetailsService(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    public LoginInvestorUser authenticateLoginInvestorUser(String username, String password) {
        LoginInvestorUser user = loginInvUserRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        } else return null;
    }
    
    public UserDetails checkAccessTokenByInvestorUser(String username, String accessToken) {
        final LoginInvestorUser user = loginInvUserRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User '" + username + "' not found");
        }
        if (!accessToken.equalsIgnoreCase(user.getAccessToken())) {
            throw new CustomException("AccessToken is invalid", HttpStatus.UNAUTHORIZED);
        }

        return org.springframework.security.core.userdetails.User//
                .withUsername(username)//
                .password(user.getPassword())//
                .authorities(Utility.getInvestorAuthorities())//
                .accountExpired(false)//
                .accountLocked(false)//
                .credentialsExpired(false)//
                .disabled(false)//
                .build();
    }
    
    public LoginAdminUser authenticateLoginAdminUser(String username, String password) {
        LoginAdminUser user = loginAdmUserRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        } else return null;
    }
    
    public UserDetails checkAccessTokenByAdminUser(String username, String accessToken) {
        final LoginAdminUser user = loginAdmUserRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User '" + username + "' not found");
        }
        if (!accessToken.equalsIgnoreCase(user.getAccessToken())) {
            throw new CustomException("AccessToken is invalid", HttpStatus.UNAUTHORIZED);
        }

        return org.springframework.security.core.userdetails.User//
                .withUsername(username)//
                .password(user.getPassword())//
                .authorities(Utility.getAdminAuthorities())//
                .accountExpired(false)//
                .accountLocked(false)//
                .credentialsExpired(false)//
                .disabled(false)//
                .build();
    }
}
