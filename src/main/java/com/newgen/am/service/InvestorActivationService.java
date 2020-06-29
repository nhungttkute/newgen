package com.newgen.am.service;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.Constant;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.MongoDBConnection;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.InterestRateDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.InvestorActivationApproval;
import com.newgen.am.repository.InvestorActivationApprovalRepository;

@Service
public class InvestorActivationService {
	private String className = "InvestorActivationService";
	
	@Autowired
    private InvestorActivationApprovalRepository invActivationApprovalRepo;
	
	public void activateInvestor(HttpServletRequest request, String approvalId, InterestRateDTO interestRateDto, long refId) {
    	String methodName = "activateInvestor";
    	try {
    		InvestorActivationApproval invActivationApproval = invActivationApprovalRepo.findById(approvalId).get();
    		
    		// check authorization
    		if (AuthorityUtils.authorityListToSet(SecurityContextHolder.getContext().getAuthentication().getAuthorities()).contains(invActivationApproval.getFunctionCode())) {
    			String investorCode = invActivationApproval.getPendingData().getQueryValue();
    			
    			// Call to api to activate at CQG
    			
    			// change status for investor
    			MongoDatabase database = MongoDBConnection.getMongoDatabase();
    			MongoCollection<Document> collection = database.getCollection("investors");
    			
    			Document query = new Document();
    			query.put("investorCode", investorCode);
    			query.put("users.username", Constant.INVESTOR_USER_PREFIX + investorCode);
    			
    			Document updateDoc = new Document();
    			updateDoc.append("status", Constant.STATUS_ACTIVE);
    			updateDoc.append("account.marginSurplusInterestRate", interestRateDto.getMarginSurplusInterestRate());
    			updateDoc.append("account.marginDeficitInterestRate", interestRateDto.getMarginDeficitInterestRate());
    			updateDoc.append("users.$.status", Constant.STATUS_ACTIVE);
    			
    			Document update = new Document();
    			update.append("$set", updateDoc);
    			
    			collection.updateOne(query, update);
    			
    			// change status for investor_login_users
    			MongoCollection<Document> loginCollection = database.getCollection("login_investor_users");
    			Document loginQuery = new Document();
    			loginQuery.put("investorCode", investorCode);
    			
    			Document loginUpdateDoc = new Document();
    			loginUpdateDoc.append("status", Constant.STATUS_ACTIVE);
    			
    			Document loginUpdate = new Document();
    			loginUpdate.append("$set", loginUpdateDoc);
    			
    			loginCollection.updateOne(loginQuery, loginUpdate);
    			
    			//update pending approval status
    			invActivationApproval.setStatus(Constant.APPROVAL_STATUS_ACTIVATED);
    			invActivationApproval.setApprovalUser(Utility.getCurrentUsername());
    			invActivationApproval.setApprovalDate(System.currentTimeMillis());
    			invActivationApprovalRepo.save(invActivationApproval);
    	    } else {
    	    	throw new CustomException(ErrorMessage.ACCESS_DENIED, HttpStatus.FORBIDDEN);
    	    }
    		
    	} catch (CustomException e) {
    		throw e;
    	} catch (Exception e) {
    		AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
}
