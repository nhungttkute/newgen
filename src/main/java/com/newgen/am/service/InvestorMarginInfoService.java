package com.newgen.am.service;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.MongoDBConnection;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.InterestRateDTO;
import com.newgen.am.dto.InvestorDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.InvestorMarginInfo;
import com.newgen.am.repository.InvestorMarginInfoRepository;

@Service
public class InvestorMarginInfoService {
	private String className = "InvestorMarginInfoService";
	
	@Autowired
    private MongoTemplate mongoTemplate;
	
	@Autowired
	private InvestorMarginInfoRepository marginInfoRepo;
	
	public InvestorMarginInfo getInvestorMarginInfo(String investorCode, long refId) {
		String methodName = "getInvestorMarginInfo";
		try {
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investor_margin_info");
			
			Document query = new Document();
            query.append("investorCode", investorCode);
            
            Document projection = new Document();
            projection.append("sodBalance", 1.0);
            projection.append("changedAmount", 1.0);
            projection.append("pendingWithdrawalAmount", 1.0);
            
            Document result = collection.find(query).projection(projection).first();
            InvestorMarginInfo marginInfo = mongoTemplate.getConverter().read(InvestorMarginInfo.class, result);
            
            return marginInfo;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void updateChangedAmount(String investorCode, double changedAmount, long refId) {
		String methodName = "updateChangedAmount";
		try {
			AMLogger.logMessage(className, methodName, refId, "Updating margin info for: " + investorCode + ": start");
			Document query = new Document();
			query.append("investorCode", investorCode);
			
			Document updateDoc = new Document();
			updateDoc.append("changedAmount", changedAmount);
			updateDoc.append("lastModifiedUser", Utility.getCurrentUsername());
			updateDoc.append("lastModifiedDate", System.currentTimeMillis());
			
			Document update = new Document();
			update.append("$set", updateDoc);
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investor_margin_info");
			collection.updateOne(query, update);
			AMLogger.logMessage(className, methodName, refId, "Updating margin info for: " + investorCode + ": finish");
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void updateChangedAmountAndPendingWithdrawalAmount(String investorCode, double changedAmount, double pendingWithdrawalAmount, long refId) {
		String methodName = "updateChangedAmountAndPendingWithdrawalAmount";
		try {
			AMLogger.logMessage(className, methodName, refId, "Updating margin info for: " + investorCode + ": start");
			Document query = new Document();
			query.append("investorCode", investorCode);
			
			Document updateDoc = new Document();
			updateDoc.append("changedAmount", changedAmount);
			updateDoc.append("pendingWithdrawalAmount", pendingWithdrawalAmount);
			
			Document update = new Document();
			update.append("$set", updateDoc);
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investor_margin_info");
			collection.updateOne(query, update);
			AMLogger.logMessage(className, methodName, refId, "Updating margin info for: " + investorCode + ": finish");
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void updatePendingWithdrawalAmount(String investorCode, double pendingWithdrawalAmount, long refId) {
		String methodName = "updatePendingWithdrawalAmount";
		
		try {
			AMLogger.logMessage(className, methodName, refId, "Updating margin info for: " + investorCode + ": start");
			Document query = new Document();
			query.append("investorCode", investorCode);
			
			Document updateDoc = new Document();
			updateDoc.append("pendingWithdrawalAmount", pendingWithdrawalAmount);
			
			Document update = new Document();
			update.append("$set", updateDoc);
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investor_margin_info");
			collection.updateOne(query, update);
			AMLogger.logMessage(className, methodName, refId, "Updating margin info for: " + investorCode + ": finish");
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void insertNewInvestorMarginInfo(InvestorDTO investorDto, InterestRateDTO interestRateDto, long refId) {
		String methodName = "insertNewInvestorMarginInfo";
		
		try {
			if (!marginInfoRepo.existsInvestorMarginInfoByInvestorCode(investorDto.getInvestorCode())) {
				AMLogger.logMessage(className, methodName, refId, "Creating margin info for: " + investorDto.getInvestorCode() + ": start");
				Document marginInfo = new Document();
				marginInfo.append("memberCode", investorDto.getMemberCode());
				marginInfo.append("memberName", investorDto.getMemberName());
				marginInfo.append("brokerCode", investorDto.getBrokerCode());
				marginInfo.append("brokerName", investorDto.getBrokerName());
				marginInfo.append("collaboratorCode", investorDto.getCollaboratorCode());
				marginInfo.append("collaboratorName", investorDto.getCollaboratorName());
				marginInfo.append("investorCode", investorDto.getInvestorCode());
				marginInfo.append("investorName", investorDto.getInvestorName());
				marginInfo.append("sodBalance", 0);
				marginInfo.append("changedAmount", 0);
				marginInfo.append("pendingWithdrawalAmount", 0);
				marginInfo.append("createdUser", Utility.getCurrentUsername());
				marginInfo.append("createdDate", System.currentTimeMillis());
				
				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("investor_margin_info");
				
				collection.insertOne(marginInfo);
				AMLogger.logMessage(className, methodName, refId, "Creating margin info for: " + investorDto.getInvestorCode() + ": finish");
			} else {
				AMLogger.logMessage(className, methodName, refId, "Margin info for: " + investorDto.getInvestorCode() + ": has already existed");
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
