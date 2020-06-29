package com.newgen.am.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.newgen.am.model.InvestorActivationApproval;

public interface InvestorActivationApprovalRepository extends MongoRepository<InvestorActivationApproval, String> {

}
