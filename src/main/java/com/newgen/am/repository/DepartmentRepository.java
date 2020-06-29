/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.newgen.am.model.Department;

/**
 *
 * @author nhungtt
 */
public interface DepartmentRepository extends MongoRepository<Department, String>{
    boolean existsDepartmentByCode(String code);
    Department findByCode(String code);
}
