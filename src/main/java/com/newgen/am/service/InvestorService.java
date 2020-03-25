/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.InvestorAccountDTO;
import com.newgen.am.model.Investor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * @author nhungtt
 */
@Service
public class InvestorService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    public InvestorAccountDTO getInvestorAccount(Long investorId) {
        Query selectInvestorQuery = new Query(Criteria.where("_id").is(investorId));
        Investor investor = mongoTemplate.findOne(selectInvestorQuery, Investor.class);

        // get info from redis
        String investorInfo = (String) redisTemplate.opsForValue().get(investor.getCode());
        InvestorAccountDTO investorAccDto = new InvestorAccountDTO();
        JsonObject jobj = new Gson().fromJson(investorInfo, JsonObject.class);
        investorAccDto.setTransactionFee(jobj.get("transactionFee").getAsDouble());
        investorAccDto.setInitialRequiredMargin(jobj.get("initialRequiredMargin").getAsDouble());
        investorAccDto.setActualProfitVND(jobj.get("actualProfitVND").getAsDouble());
        investorAccDto.setEstimatedProfitVND(jobj.get("estimatedProfitVND").getAsDouble());

        // set infro from db
        investorAccDto.setInvestorName(investor.getName());
        investorAccDto.setInvestorCode(investor.getCode());
        investorAccDto.setSodBalance(investor.getAccount().getSodBalance());
        investorAccDto.setChangedAmount(investor.getAccount().getChangedAmount());
        investorAccDto.setGeneralFee(investor.getAccount().getGeneralFee());

        // caculate some fields
        calculateCurrentBalance(investorAccDto, investor.getAccount().getOtherFee());
        calculateNetMargin(investorAccDto);
        calculateAvailableMargin(investorAccDto);
        calculateAdditionalMargin(investorAccDto);
        return investorAccDto;
    }

    private void calculateAvailableMargin(InvestorAccountDTO investorAccDto) {
        investorAccDto.setAvailableMargin(Utility.getDouble(investorAccDto.getNetMargin()) - Utility.getDouble(investorAccDto.getInitialRequiredMargin()));
    }

    private void calculateNetMargin(InvestorAccountDTO investorAccDto) {
        investorAccDto.setNetMargin(Utility.getDouble(investorAccDto.getCurrentBalance()) + Utility.getDouble(investorAccDto.getEstimatedProfitVND()));
    }

    private void calculateCurrentBalance(InvestorAccountDTO investorAccDto, Double otherFee) {
        Double currentBalance = Utility.getDouble(investorAccDto.getSodBalance()) + Utility.getDouble(investorAccDto.getChangedAmount()) 
                - Utility.getDouble(investorAccDto.getTransactionFee()) - Utility.getDouble(otherFee) 
                - Utility.getDouble(investorAccDto.getGeneralFee()) + Utility.getDouble(investorAccDto.getActualProfitVND());
        investorAccDto.setCurrentBalance(Utility.getDouble(currentBalance));
    }

    private void calculateAdditionalMargin(InvestorAccountDTO investorAccDto) {
        if (investorAccDto.getAvailableMargin() >= 0) {
            investorAccDto.setAdditionalMargin(0d);
        } else {
            investorAccDto.setAdditionalMargin(Math.abs(Utility.getDouble(investorAccDto.getAvailableMargin())));
        }
    }
}
