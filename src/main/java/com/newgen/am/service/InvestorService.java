/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.MongoDBConnection;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.InvestorAccountDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.Investor;
import com.newgen.am.model.LoginInvestorUser;
import com.newgen.am.repository.LoginInvestorUserRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 *
 * @author nhungtt
 */
@Service
public class InvestorService {

    private String className = "InvestorService";

    @Autowired
    private RedisTemplate redisTemplate;
    
    @Autowired
    private LoginInvestorUserRepository loginInvUserRepo;

    public InvestorAccountDTO getInvestorAccount(long refId) {
        String methodName = "getInvestorAccount";
        InvestorAccountDTO investorAccDto = new InvestorAccountDTO();
        try {
            MongoDatabase database = MongoDBConnection.getMongoDatabase();
            MongoCollection<Document> collection = database.getCollection("investors");
            
            LoginInvestorUser user = loginInvUserRepo.findByUsername(Utility.getUsername());
            BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.put("_id", user.getInvestorId());
            BasicDBObject projection = new BasicDBObject();
            projection.append("investorCode", 1);
            projection.append("investorName", 1);
            projection.append("account", 1);
            Document invDoc = collection.find(searchQuery).projection(projection).first();
            Investor investor = new Gson().fromJson(invDoc.toJson(Utility.getJsonWriterSettings()), Investor.class);

            if (investor != null) {
                // get info from redis
                String investorInfo = (String) redisTemplate.opsForValue().get(investor.getInvestorCode());
                JsonObject jobj = new Gson().fromJson(investorInfo, JsonObject.class);
                investorAccDto.setTransactionFee(jobj.get("transactionFee").getAsDouble());
                investorAccDto.setInitialRequiredMargin(jobj.get("initialRequiredMargin").getAsDouble());
                investorAccDto.setActualProfitVND(jobj.get("actualProfitVND").getAsDouble());
                investorAccDto.setEstimatedProfitVND(jobj.get("estimatedProfitVND").getAsDouble());

                // set infro from db
                investorAccDto.setInvestorName(investor.getInvestorName());
                investorAccDto.setInvestorCode(investor.getInvestorCode());
                investorAccDto.setSodBalance(investor.getAccount().getSodBalance());
                investorAccDto.setChangedAmount(investor.getAccount().getChangedAmount());
                investorAccDto.setGeneralFee(investor.getAccount().getGeneralFee());

                // caculate some fields
                calculateCurrentBalance(investorAccDto, investor.getAccount().getOtherFee());
                calculateNetMargin(investorAccDto);
                calculateAvailableMargin(investorAccDto);
                calculateAdditionalMargin(investorAccDto);
            }
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.UNPROCESSABLE_ENTITY);
        }

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
