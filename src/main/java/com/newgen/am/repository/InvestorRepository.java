/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.newgen.am.model.Investor;

/**
 *
 * @author nhungtt
 */
public interface InvestorRepository  extends MongoRepository<Investor, String>{
	Investor findByInvestorCode(String investorCode);
	boolean existsInvestorByInvestorCode(String investorCode);
}
