/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author nhungtt
 */
public class UserAudtiting implements AuditorAware<String>{

    @Override
    public Optional<String> getCurrentAuditor() {

        String uname = SecurityContextHolder.getContext().getAuthentication().getName();
        return Optional.of(uname);
    }
    
}
