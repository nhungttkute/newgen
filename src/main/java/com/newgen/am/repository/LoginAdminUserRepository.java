/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.repository;

import com.newgen.am.model.LoginAdminUser;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author nhungtt
 */
public interface LoginAdminUserRepository extends MongoRepository<LoginAdminUser, Long>{
    
}
