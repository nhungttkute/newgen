package com.newgen.am.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.newgen.am.model.InvestorMarginInfo;

public interface InvestorMarginInfoRepository extends MongoRepository<InvestorMarginInfo, String> {
	InvestorMarginInfo findByInvestorCode(String investorCode);
	boolean existsInvestorMarginInfoByInvestorCode(String investorCode);
}
