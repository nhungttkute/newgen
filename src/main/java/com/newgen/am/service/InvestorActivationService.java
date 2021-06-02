package com.newgen.am.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.ConfigLoader;
import com.newgen.am.common.Constant;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.MongoDBConnection;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.CQGCMSCommodityDTO;
import com.newgen.am.dto.CQGResponseObj;
import com.newgen.am.dto.InterestRateDTO;
import com.newgen.am.dto.InvestorDTO;
import com.newgen.am.dto.MemberDTO;
import com.newgen.am.dto.UserInfoDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.Commodity;
import com.newgen.am.model.CqgInfo;
import com.newgen.am.model.InvestorActivationApproval;
import com.newgen.am.model.LoginInvestorUser;
import com.newgen.am.repository.InvestorActivationApprovalRepository;
import com.newgen.am.repository.LoginInvestorUserRepository;

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
	
	@Autowired
    private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	private LoginInvestorUserRepository loginInvUserRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public void activateInvestor(HttpServletRequest request, String approvalId, InterestRateDTO interestRateDto, long refId) {
    	String methodName = "activateInvestor";
    	try {
    		if (Utility.setApprovalIDonRedis(redisTemplate, approvalId, refId)) {
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
                				// check if exist investor's customerId
                				String customerId = "";
                				if (Utility.isNotNull(investorDto.getCqgInfo()) && Utility.isNotNull(investorDto.getCqgInfo().getCustomerId())) {
                					customerId = investorDto.getCqgInfo().getCustomerId();
                				} else {
                					// create cqg customer
                    				CQGResponseObj customerRes = cqgService.createCQGCustomer(investorDto, cqgInfo.getProfileId(), refId);
                    				if (customerRes != null) {
                    					customerId = customerRes.getData().getCustomerId();
                    				} else {
                    					AMLogger.logMessage(className, methodName, refId, String.format("Activating investor %s. Error: timeout creating customer", investorDto.getInvestorCode()));
                        				throw new CustomException(ErrorMessage.CQG_INFO_CREATED_UNSUCCESSFULLY, HttpStatus.OK);
                        			}
                				}
                				
                				// check if exist investor's customerId
                				String accountId = "";
                				if (Utility.isNotNull(investorDto.getCqgInfo()) && Utility.isNotNull(investorDto.getCqgInfo().getAccountId())) {
                					accountId = investorDto.getCqgInfo().getAccountId();
                				} else {
                					if (Utility.isNotNull(customerId)) {
                						// create cqg account
                        				CQGResponseObj accountRes = cqgService.createCQGAccount(cqgInfo.getProfileId(), Utility.getCQGAccountName(investorDto), investorCode, customerId, refId);
                            			if (accountRes != null) {
                            				accountId = accountRes.getData().getAccountId();
                            			} else {
                            				AMLogger.logMessage(className, methodName, refId, String.format("Activating investor %s. Error: timeout creating account", investorDto.getInvestorCode()));
                            				throw new CustomException(ErrorMessage.CQG_INFO_CREATED_UNSUCCESSFULLY, HttpStatus.OK);
                            			}
                					}
                				}
                				
                				if (Utility.isNotNull(accountId)) {
                					// add cqg account auth list
                    				String traderId = ConfigLoader.getMainConfig().getString(Constant.CQG_CMS_TRADER_ID);
                    				boolean isAccAuthListSuccessful = cqgService.updateCQGAccountAuthList(Utility.convertStringToLong(accountId), traderId, refId);
                    				if (!isAccAuthListSuccessful) {
                    					AMLogger.logMessage(className, methodName, refId, String.format("Activating investor %s. Error: timeout creating account authorization list", investorDto.getInvestorCode()));
                    					throw new CustomException(ErrorMessage.CQG_INFO_CREATED_UNSUCCESSFULLY, HttpStatus.OK);
                    				}
                    				
                    				// update risk params (marginMultiplier, orderLimit, defaultPositionLimit)
                    				boolean isUpdateRiskParamsSuccessful = cqgService.updateCQGRiskParams(accountId, investorDto.getMarginMultiplier(), investorDto.getOrderLimit(), investorDto.getDefaultPositionLimit(), refId);
                    				if (!isUpdateRiskParamsSuccessful) {
                    					AMLogger.logMessage(className, methodName, refId, String.format("Activating investor %s. Error: timeout setting account risk params", investorDto.getInvestorCode()));
                    					throw new CustomException(ErrorMessage.CQG_INFO_CREATED_UNSUCCESSFULLY, HttpStatus.OK);
                    				}
                    				
                    				// update commodities
                    				if (investorDto.getCommodities() != null && investorDto.getCommodities().size() > 0) {
                    					List<CQGCMSCommodityDTO> cqgCommodities = new ArrayList<CQGCMSCommodityDTO>();
                    					for (Commodity comm : investorDto.getCommodities()) {
                    						CQGCMSCommodityDTO cqgComm = new CQGCMSCommodityDTO();
                    						cqgComm.setSymbol(comm.getCommodityCode());
                    						cqgComm.setPositionLimit(comm.getPositionLimit());
                    						cqgCommodities.add(cqgComm);
                    					}
                    					
                    					boolean result = cqgService.updateCQGAccountMarketLimits(accountId,
                								cqgCommodities, refId);
                						if (!result) {
                							throw new CustomException(ErrorMessage.CQG_INFO_CREATED_UNSUCCESSFULLY, HttpStatus.OK);
                						}
                    				}
                				}
                			} else {
                				AMLogger.logMessage(className, methodName, refId, String.format("Activating investor %s. Error: Member %s doesnt have cqgInfo", investorDto.getInvestorCode(), investorDto.getMemberCode()));
                				throw new CustomException(ErrorMessage.CQG_INFO_CREATED_UNSUCCESSFULLY, HttpStatus.OK);
                			}
            			}
            			
            			// update investor status to ACTIVE
            			updateInvestor(database, investorCode, interestRateDto, refId);
            			
            			// update pending approval status
            			invActivationApproval.setStatus(Constant.APPROVAL_STATUS_ACTIVATED);
            			invActivationApproval.setMarginSurplusInterestRate(interestRateDto.getMarginSurplusInterestRate());
            			invActivationApproval.setMarginDeficitInterestRate(interestRateDto.getMarginDeficitInterestRate());
            			invActivationApproval.setApprovalUser(Utility.getCurrentUsername());
            			invActivationApproval.setApprovalDate(System.currentTimeMillis());
            			invActivationApproval.setSessionDate(Utility.getSessionDateRedis(redisTemplate));
            			invActivationApprovalRepo.save(invActivationApproval);
            			
            			// insert to investor_margin_info
            			marginInfoService.insertNewInvestorMarginInfo(investorDto, interestRateDto, refId);
            			
            			// insert loginAdminUser
            			String password = Utility.generateRandomPassword();
        				String pin = Utility.generateRandomPin();
        				
        				LoginInvestorUser investorUser = new LoginInvestorUser();
        				investorUser.setMemberCode(investorDto.getMemberCode());
        				investorUser.setMemberName(investorDto.getMemberName());
        				investorUser.setBrokerCode(investorDto.getBrokerCode());
        				investorUser.setBrokerName(investorDto.getBrokerName());
        				investorUser.setCollaboratorCode(investorDto.getCollaboratorCode());
        				investorUser.setCollaboratorName(investorDto.getCollaboratorName());
        				investorUser.setInvestorCode(investorDto.getInvestorCode());
        				investorUser.setInvestorName(investorDto.getInvestorName());
        				investorUser.setUsername(investorDto.getInvestorCode());
        				investorUser.setPassword(passwordEncoder.encode(password));
        				investorUser.setPin(passwordEncoder.encode(pin));
        				investorUser.setStatus(Constant.STATUS_ACTIVE);
        				investorUser.setCreatedUser(Utility.getCurrentUsername());
        				investorUser.setCreatedDate(System.currentTimeMillis());
        				loginInvUserRepo.save(investorUser);
        				
        				// send email
        				if (Utility.isNotifyOn()) {
        					Utility.sendCreateNewUserEmail(Constant.INVESTOR_USER_PREFIX, "", investorDto.getInvestorCode(), password, pin, refId);
        				}
            	    } else {
            	    	throw new CustomException(ErrorMessage.ACCESS_DENIED, HttpStatus.FORBIDDEN);
            	    }
        		} else {
        			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        		}
    		} else {
    			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
    		}
    	} catch (CustomException e) {
    		throw e;
    	} catch (Exception e) {
    		AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
    	} finally {
			// delete approvalId on Redis for the next try
    		Utility.deleteApprovalIDonRedis(redisTemplate, approvalId, refId);
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
			projection.append("contact", 1.0);
			projection.append("cqgInfo", 1.0);
			projection.append("orderLimit", 1.0);
			projection.append("marginMultiplier", 1.0);
			projection.append("defaultPositionLimit", 1.0);
			projection.append("commodities", 1.0);
			
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
