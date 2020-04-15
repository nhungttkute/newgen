/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import com.newgen.am.common.Utility;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;

/**
 *
 * @author nhungtt
 */
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        if (Utility.isNotNull(Utility.getCurrentUsername())) {
            return Optional.of(Utility.getCurrentUsername());
        } else {
            return  Optional.of("anonymous");
        }
        
    }
    
}
