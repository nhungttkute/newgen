package com.newgen.am.service;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.ConfigLoader;
import com.newgen.am.common.Constant;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.MongoDBConnection;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.CQGResponseObj;
import com.newgen.am.dto.InterestRateDTO;
import com.newgen.am.dto.InvestorDTO;
import com.newgen.am.dto.MemberDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.CqgInfo;
import com.newgen.am.model.InvestorActivationApproval;
import com.newgen.am.repository.InvestorActivationApprovalRepository;

@Service
public class InvestorActivationService {
	private String className = "InvestorActivationService";
	
	@Autowired
    private InvestorActivationApprovalRepository invActivationApprovalRepo;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired 
	private InvestorMarginInfoService marginInfoService;
	
	@Autowired
	private CQGConnectorService cqgService;
	
	public void activateInvestor(HttpServletRequest request, String approvalId, InterestRateDTO interestRateDto, long refId) {
    	String methodName = "activateInvestor";
    	try {
    		InvestorActivationApproval invActivationApproval = invActivationApprovalRepo.findById(approvalId).get();
    		if (Constant.STATUS_PENDING.equals(invActivationApproval.getStatus())) {
    			// check authorization
        		if (AuthorityUtils.authorityListToSet(SecurityContextHolder.getContext().getAuthentication().getAuthorities()).contains(invActivationApproval.getFunctionCode())) {
        			String investorCode = invActivationApproval.getPendingData().getQueryValue();
        			
        			MongoDatabase database = MongoDBConnection.getMongoDatabase();
        			InvestorDTO investorDto = getInvestorInfo(database, investorCode, refId);
        			
        			if (Utility.isCQGSyncOn()) {
        				// create cqg account
            			CqgInfo cqgInfo = getMemberCQGInfo(database, investorDto.getMemberCode(), refId);
            			if (cqgInfo != null) {
            				// create cqg customer
            				CQGResponseObj customerRes = cqgService.createCQGCustomer(investorDto, cqgInfo.getProfileId(), refId);
            				if (customerRes != null) {
            					// create cqg account
                				CQGResponseObj accountRes = cqgService.createCQGAccount(investorCode, customerRes.getData().getCustomerId(), refId);
                    			if (accountRes != null) {
                    				String accountId = accountRes.getData().getAccountId();
                    				
            	        			// add cqg account auth list
                    				String traderId = ConfigLoader.getMainConfig().getString(Constant.CQG_CMS_TRADER_ID);
                    				boolean result = cqgService.updateCQGAccountAuthList(Utility.convertStringToLong(accountId), traderId, refId);
                    				
                    				if (!result) {
                    					throw new CustomException(ErrorMessage.CQG_INFO_CREATED_UNSUCCESSFULLY, HttpStatus.OK);
                    				}
                    			} else {
                    				throw new CustomException(ErrorMessage.CQG_INFO_CREATED_UNSUCCESSFULLY, HttpStatus.OK);
                    			}
            				} else {
                				throw new CustomException(ErrorMessage.CQG_INFO_CREATED_UNSUCCESSFULLY, HttpStatus.OK);
                			}
            			} else {
            				throw new CustomException(ErrorMessage.CQG_INFO_CREATED_UNSUCCESSFULLY, HttpStatus.OK);
            			}
        			}
        			
        			// update investor status to ACTIVE
        			updateInvestor(database, investorCode, interestRateDto, refId);
        			
        			// update investor_login_users status to ACTIVE
        			updateLoginInvestorUser(database, investorCode, refId);
        			
        			// update pending approval status
        			invActivationApproval.setStatus(Constant.APPROVAL_STATUS_ACTIVATED);
        			invActivationApproval.setMarginSurplusInterestRate(interestRateDto.getMarginSurplusInterestRate());
        			invActivationApproval.setMarginDeficitInterestRate(interestRateDto.getMarginDeficitInterestRate());
        			invActivationApproval.setApprovalUser(Utility.getCurrentUsername());
        			invActivationApproval.setApprovalDate(System.currentTimeMillis());
        			invActivationApprovalRepo.save(invActivationApproval);
        			
        			// update investor_margin_info
        			marginInfoService.insertNewInvestorMarginInfo(investorDto, interestRateDto, refId);
        	    } else {
        	    	throw new CustomException(ErrorMessage.ACCESS_DENIED, HttpStatus.FORBIDDEN);
        	    }
    		}
    	} catch (CustomException e) {
    		throw e;
    	} catch (Exception e) {
    		AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
	
	private InvestorDTO getInvestorInfo(MongoDatabase database, String investorCode, long refId) {
		String methodName = "getInvestorInfo";
		try {
			MongoCollection<Document> collection = database.getCollection("investors");
			
			Document query = new Document();
			query.append("investorCode", investorCode);
			
			Document projection = new Document();
			projection.append("memberCode", 1.0);
			projection.append("memberName", 1.0);
			projection.append("brokerCode", 1.0);
			projection.append("brokerName", 1.0);
			projection.append("collaboratorCode", 1.0);
			projection.append("collaboratorName", 1.0);
			projection.append("investorCode", 1.0);
			projection.append("investorName", 1.0);
			projection.append("company", 1.0);
			projection.append("individual", 1.0);
			
			Document result = collection.find(query).projection(projection).first();
			InvestorDTO investorDto = mongoTemplate.getConverter().read(InvestorDTO.class, result);
			return investorDto;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private void updateInvestor(MongoDatabase database, String investorCode, InterestRateDTO interestRateDto, long refId) {
		String methodName = "updateInvestor";
		try {
			MongoCollection<Document> collection = database.getCollection("investors");
			
			Document query = new Document();
			query.put("investorCode", investorCode);
			query.put("users.username", investorCode);
			
			Document updateDoc = new Document();
			updateDoc.append("status", Constant.STATUS_ACTIVE);
			updateDoc.append("account.marginSurplusInterestRate", interestRateDto.getMarginSurplusInterestRate());
			updateDoc.append("account.marginDeficitInterestRate", interestRateDto.getMarginDeficitInterestRate());
			updateDoc.append("users.$.status", Constant.STATUS_ACTIVE);
			
			Document update = new Document();
			update.append("$set", updateDoc);
			
			collection.updateOne(query, update);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private void updateLoginInvestorUser(MongoDatabase database, String investorCode, long refId) {
		String methodName = "updateLoginInvestorUser";
		try {
			MongoCollection<Document> loginCollection = database.getCollection("login_investor_users");
			Document loginQuery = new Document();
			loginQuery.put("investorCode", investorCode);
			
			Document loginUpdateDoc = new Document();
			loginUpdateDoc.append("status", Constant.STATUS_ACTIVE);
			
			Document loginUpdate = new Document();
			loginUpdate.append("$set", loginUpdateDoc);
			
			loginCollection.updateOne(loginQuery, loginUpdate);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private CqgInfo getMemberCQGInfo(MongoDatabase database, String memberCode, long refId) {
		String methodName = "getMemberCQGInfo";
		try {
			MongoCollection<Document> collection = database.getCollection("members");
			
			Document query = new Document();
			query.append("code", memberCode);
			
			Document projection = new Document();
			projection.append("_id", 0.0);
			projection.append("cqgInfo", 1.0);
			
			Document result = collection.find(query).projection(projection).first();
			MemberDTO memberDto = mongoTemplate.getConverter().read(MemberDTO.class, result);
			return memberDto.getCqgInfo();
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
