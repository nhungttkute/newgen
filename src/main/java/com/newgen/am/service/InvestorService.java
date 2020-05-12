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
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.MongoDBConnection;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.BasePagination;
import com.newgen.am.dto.InvestorAccountDTO;
import com.newgen.am.dto.InvestorDTO;
import com.newgen.am.dto.RoleCSV;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.Investor;
import com.newgen.am.model.LoginAdminUser;
import com.newgen.am.model.LoginInvestorUser;
import com.newgen.am.repository.LoginAdminUserRepository;
import com.newgen.am.repository.LoginInvestorUserRepository;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
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
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private LoginInvestorUserRepository loginInvUserRepo;
    
    @Autowired
    private LoginAdminUserRepository loginAdmUserRepo;

    public InvestorAccountDTO getInvestorAccount(long refId) {
        String methodName = "getInvestorAccount";
        InvestorAccountDTO investorAccDto = new InvestorAccountDTO();
        try {
            MongoDatabase database = MongoDBConnection.getMongoDatabase();
            MongoCollection<Document> collection = database.getCollection("investors");
            
            LoginInvestorUser user = loginInvUserRepo.findByUsername(Utility.getCurrentUsername());
            BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.put("_id", new ObjectId(user.getInvestorId()));
            BasicDBObject projection = new BasicDBObject();
            projection.append("investorCode", 1);
            projection.append("investorName", 1);
            projection.append("account", 1);
            Document invDoc = collection.find(searchQuery).projection(projection).first();
            Investor investor = mongoTemplate.getConverter().read(Investor.class, invDoc);

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
            throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
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
    
    public List<String> getInvestorCodesByUser(long refId) {
    	String methodName = "getInvestorCodesByUser";
    	List<String> investorCodes = new ArrayList<String>();
    	try {
    		LoginAdminUser user = loginAdmUserRepo.findByUsername(Utility.getCurrentUsername());
    		Document query = new Document();
    		if (user == null ) {
    			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
    		}
    		
    		if (Utility.isNotNull(user.getCollaboratorCode())) {
    			query.append("collaboratorCode", user.getCollaboratorCode());
    		} else if (Utility.isNotNull(user.getBrokerCode())) {
    			query.append("brokerCode", user.getBrokerCode());
    		} else if (Utility.isNotNull(user.getMemberCode())) {
    			query.append("memberCode", user.getMemberCode());
    		}
    		
    		MongoDatabase database = MongoDBConnection.getMongoDatabase();
            MongoCollection<Document> collection = database.getCollection("investors");
            
            Document projection = new Document();
            projection.append("_id", 0.0);
            projection.append("investorCode", 1.0);
            
            MongoCursor<Document> cur = collection.find(query).projection(projection).iterator();

			while (cur.hasNext()) {
				InvestorDTO investor = mongoTemplate.getConverter().read(InvestorDTO.class, cur.next());
				if (investor != null)
					investorCodes.add(investor.getInvestorCode());
			}
			return investorCodes;
    	} catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
