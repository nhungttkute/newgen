/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.service;

import com.newgen.am.model.Broker;
import com.newgen.am.model.Investor;
import com.newgen.am.model.InvestorUser;
import com.newgen.am.model.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

/**
 *
 * @author nhungtt
 */
@Service
public class InvestorUserService {

    @Autowired
    private MongoTemplate mongoTemplate;
    
    public InvestorUser changePassword(Long memberId, Long brokerId, Long investorId, Long investorUserId, Long loginInvUserId, String oldPassword, String newPassword) {
        MatchOperation matchStage = Aggregation.match(new Criteria("_id").is(memberId)
                .and("brokers").elemMatch(Criteria.where("_id").is(brokerId)
                .and("investors").elemMatch(Criteria.where("_id").is(investorId))
                .and("users").elemMatch(Criteria.where("_id").is(investorUserId))));
        ProjectionOperation projectStage = Aggregation.project(InvestorUser.class);
        
        Aggregation aggregation = Aggregation.newAggregation(matchStage, projectStage);
//        Aggregation aggregation = Aggregation.newAggregation(matchStage);
        AggregationResults<InvestorUser> output
                = mongoTemplate.aggregate(aggregation, Member.class, InvestorUser.class);
        InvestorUser user = output.getUniqueMappedResult();
        return user;
    }
    
    public String getData (Long memberId, Long brokerId) {
        MatchOperation matchStage = Aggregation.match(new Criteria("_id").is(memberId));
        UnwindOperation unwindStage = Aggregation.unwind("brokers");
        ProjectionOperation projectStage = Aggregation.project(Broker.class);
        
        Aggregation aggregation = Aggregation.newAggregation(matchStage, unwindStage, projectStage);

        MatchOperation matchStage2 = Aggregation.match(new Criteria("_id").is(brokerId));
        UnwindOperation unwindStage2 = Aggregation.unwind("investors");
        ProjectionOperation projectStage2 = Aggregation.project(Investor.class);
        Aggregation aggregation2 = Aggregation.newAggregation(matchStage2, unwindStage2, projectStage2);
        
        mongoTemplate.aggregateStream(aggregation, Member.class, Broker.class);
        Broker broker = output.getUniqueMappedResult();
        
    }
    
}
