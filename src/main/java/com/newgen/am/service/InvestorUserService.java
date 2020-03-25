/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.service;

import com.newgen.am.model.Investor;
import com.newgen.am.model.InvestorUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author nhungtt
 */
@Service
public class InvestorUserService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public Investor backup(Long investorId, Long investorUserId, Long loginInvUserId, String oldPassword, String newPassword) {
        // Select investor_user
        Query checkPassQuery = new Query(Criteria.where("_id").is(investorId).andOperator(Criteria.where("users._id").is(investorUserId)).andOperator(Criteria.where("users.password").is(passwordEncoder.encode(oldPassword))));
        Investor existedInv = mongoTemplate.findOne(checkPassQuery, Investor.class);
        Query query = new Query(Criteria.where("_id").is(investorId).andOperator(Criteria.where("users._id").is(investorUserId)));
        Update update = new Update().set("users.$.password", newPassword);
        Investor newInvestor = mongoTemplate.findAndModify(query, update, Investor.class);
        return newInvestor;
    }
    
    public String getData (Long investorId, Long investorUserId) {
        MatchOperation matchStage = Aggregation.match(new Criteria("_id").is(investorId));
        UnwindOperation unwindStage = Aggregation.unwind("$users");
        MatchOperation matchStage2 = Aggregation.match(new Criteria("users._id").is(investorUserId));
        ProjectionOperation projectStage = Aggregation.project(InvestorUser.class);
        
        Aggregation aggregation = Aggregation.newAggregation(matchStage, unwindStage, matchStage2, projectStage);

        AggregationResults<InvestorUser> output
                = mongoTemplate.aggregate(aggregation, Investor.class, InvestorUser.class);
        InvestorUser result = output.getUniqueMappedResult();
        return result.getUsername();
    }
    
}
