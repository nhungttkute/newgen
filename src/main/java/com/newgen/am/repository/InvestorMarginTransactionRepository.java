package com.newgen.am.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.newgen.am.model.InvestorMarginTransaction;

public interface InvestorMarginTransactionRepository extends MongoRepository<InvestorMarginTransaction, String> {

}
