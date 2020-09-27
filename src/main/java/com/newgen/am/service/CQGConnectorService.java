package com.newgen.am.service;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.ConfigLoader;
import com.newgen.am.common.Constant;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.LocalServiceConnection;
import com.newgen.am.common.MongoDBConnection;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.CQGCMSAccountAuthDTO;
import com.newgen.am.dto.CQGCMSCommodityDTO;
import com.newgen.am.dto.CQGCMSRequestDTO;
import com.newgen.am.dto.CQGResponseObj;
import com.newgen.am.dto.InvestorDTO;
import com.newgen.am.dto.MemberDTO;
import com.newgen.am.exception.CustomException;

@Service
public class CQGConnectorService {
	private String className = "CQGConnectorService";
	
	public CQGResponseObj createCQGSaleSeries(MemberDTO memberDto, long refId) {
		String methodName = "createCQGSaleSeries";
		try {
			LocalServiceConnection serviceCon = new LocalServiceConnection();
			CQGCMSRequestDTO cqgRequest = new CQGCMSRequestDTO();
			cqgRequest.setName(memberDto.getName());
			cqgRequest.setNumber(memberDto.getCode());
			cqgRequest.setEmail(memberDto.getCompany().getEmail());
			cqgRequest.setPhone(memberDto.getCompany().getPhoneNumber());
			cqgRequest.setFullName(memberDto.getCompany().getDelegate().getFullName());
			cqgRequest.setAddress(memberDto.getCompany().getAddress());
			
			String input = new Gson().toJson(cqgRequest);
			AMLogger.logMessage(className, methodName, refId, "INPUT: " + input);
			
			String[] res = serviceCon.sendPostRequest(serviceCon.getCMSServiceURL(ConfigLoader.getMainConfig().getString(Constant.SERVICE_CMS_SALE_SERIES)), input, ConfigLoader.getMainConfig().getString(Constant.LOCAL_SECRET_KEY));
			AMLogger.logMessage(className, methodName, refId, "OUTPUT: " + res[0] + " => " + res[1]);
			if (res.length >=2 && "200".equals(res[0])) {
				CQGResponseObj cmsResponse = new Gson().fromJson(res[1], CQGResponseObj.class);
				if (cmsResponse != null && Constant.RESPONSE_OK.equalsIgnoreCase(cmsResponse.getStatus())) {
					return cmsResponse;
				}
			}
			return null;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public CQGResponseObj createCQGCustomer(InvestorDTO investorDto, String saleSeriesId, long refId) {
		String methodName = "createCQGCustomer";
		try {
			LocalServiceConnection serviceCon = new LocalServiceConnection();
			CQGCMSRequestDTO cqgRequest = new CQGCMSRequestDTO();
			cqgRequest.setName(investorDto.getInvestorName());
			cqgRequest.setSaleSeriesId(saleSeriesId);
			if (Utility.isNotNull(investorDto.getCompany())) {
				cqgRequest.setEmail(investorDto.getCompany().getEmail());
				cqgRequest.setPhone(investorDto.getCompany().getPhoneNumber());
				cqgRequest.setFullName(investorDto.getCompany().getDelegate().getFullName());
				cqgRequest.setAddress(investorDto.getCompany().getAddress());
			} else if (Utility.isNotNull(investorDto.getIndividual())) {
				cqgRequest.setEmail(investorDto.getIndividual().getEmail());
				cqgRequest.setPhone(investorDto.getIndividual().getPhoneNumber());
				cqgRequest.setFullName(investorDto.getIndividual().getFullName());
				cqgRequest.setAddress(investorDto.getIndividual().getAddress());
			}
			
			
			String input = new Gson().toJson(cqgRequest);
			AMLogger.logMessage(className, methodName, refId, "INPUT: " + input);
			
			String[] res = serviceCon.sendPostRequest(serviceCon.getCMSServiceURL(ConfigLoader.getMainConfig().getString(Constant.SERVICE_CMS_CUSTOMER)), input, ConfigLoader.getMainConfig().getString(Constant.LOCAL_SECRET_KEY));
			AMLogger.logMessage(className, methodName, refId, "OUTPUT: " + res[0] + " => " + res[1]);
			if (res.length >=2 && "200".equals(res[0])) {
				CQGResponseObj cmsResponse = new Gson().fromJson(res[1], CQGResponseObj.class);
				if (cmsResponse != null && Constant.RESPONSE_OK.equalsIgnoreCase(cmsResponse.getStatus())) {
					return cmsResponse;
				}
			}
			return null;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public CQGResponseObj createCQGTrader(String memberCode, String customerId, String profileId, long refId) {
		String methodName = "createCQGTrader";
		try {
			LocalServiceConnection serviceCon = new LocalServiceConnection();
			CQGCMSRequestDTO cqgRequest = new CQGCMSRequestDTO();
			cqgRequest.setUsername(Constant.MEMBER_MASTER_USER_PREFIX + memberCode);
			cqgRequest.setCustomerId(customerId);
			cqgRequest.setProfileId(profileId);
			
			String input = new Gson().toJson(cqgRequest);
			AMLogger.logMessage(className, methodName, refId, "INPUT: " + input);
			
			String[] res = serviceCon.sendPostRequest(serviceCon.getCMSServiceURL(ConfigLoader.getMainConfig().getString(Constant.SERVICE_CMS_TRADER)), input, ConfigLoader.getMainConfig().getString(Constant.LOCAL_SECRET_KEY));
			AMLogger.logMessage(className, methodName, refId, "OUTPUT: " + res[0] + " => " + res[1]);
			if (res.length >=2 && "200".equals(res[0])) {
				CQGResponseObj cmsResponse = new Gson().fromJson(res[1], CQGResponseObj.class);
				if (cmsResponse != null && Constant.RESPONSE_OK.equalsIgnoreCase(cmsResponse.getStatus())) {
					return cmsResponse;
				}
			}
			return null;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public CQGResponseObj createCQGAccount(String investorCode, String customerId, long refId) {
		String methodName = "createCQGAccount";
		CQGResponseObj cmsResponse = null;
		try {
			LocalServiceConnection serviceCon = new LocalServiceConnection();
			CQGCMSRequestDTO cqgRequest = new CQGCMSRequestDTO();
			cqgRequest.setName(Constant.CQG_ACCOUNT_NAME_PREFIX + investorCode);
			cqgRequest.setNumber(Constant.CQG_ACCOUNT_NUMBER_PREFIX + investorCode);
			cqgRequest.setCustomerId(customerId);
			
			String input = new Gson().toJson(cqgRequest);
			AMLogger.logMessage(className, methodName, refId, "INPUT: " + input);
			
			String[] res = serviceCon.sendPostRequest(serviceCon.getCMSServiceURL(ConfigLoader.getMainConfig().getString(Constant.SERVICE_CMS_ACCOUNT)), input, ConfigLoader.getMainConfig().getString(Constant.LOCAL_SECRET_KEY));
			AMLogger.logMessage(className, methodName, refId, "OUTPUT: " + res[0] + " => " + res[1]);
			if (res.length >=2 && "200".equals(res[0])) {
				cmsResponse = new Gson().fromJson(res[1], CQGResponseObj.class);
				if (cmsResponse != null && Constant.RESPONSE_OK.equalsIgnoreCase(cmsResponse.getStatus())) {
					updateInvestorCQGAccount(investorCode, customerId, cmsResponse.getData().getAccountId(), refId);
				}
			}
			return cmsResponse;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private void updateInvestorCQGAccount(String investorCode, String customerId, String accountId, long refId) {
		String methodName = "updateInvestorCQGAccount";
		try {
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investors");
			
			Document query = new Document();
			query.append("investorCode", investorCode);
			
			Document updateDoc = new Document();
			updateDoc.append("cqgInfo.customerId", customerId);
			updateDoc.append("cqgInfo.accountId", accountId);
			
			Document update = new Document();
			update.append("$set", updateDoc);
			
			collection.updateOne(query, update);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public boolean updateCQGAccountAuthList(long accountId, String userId, long refId) {
		String methodName = "updateCQGAccountAuthList";
		CQGResponseObj cmsResponse = null;
		try {
			LocalServiceConnection serviceCon = new LocalServiceConnection();
			CQGCMSRequestDTO cqgRequest = new CQGCMSRequestDTO();
			List<CQGCMSAccountAuthDTO> linksToSet = new ArrayList<CQGCMSAccountAuthDTO>();
			CQGCMSAccountAuthDTO accAuth = new CQGCMSAccountAuthDTO();
			accAuth.setAccountId(accountId);
			accAuth.setUserId(userId);
			accAuth.setViewOnly(false);
			
			linksToSet.add(accAuth);
			cqgRequest.setLinksToSet(linksToSet);
			
			String input = new Gson().toJson(cqgRequest);
			AMLogger.logMessage(className, methodName, refId, "INPUT: " + input);
			
			String[] res = serviceCon.sendPutRequest(serviceCon.getCMSServiceURL(ConfigLoader.getMainConfig().getString(Constant.SERVICE_CMS_ACCOUNT_USER_AUTH_LIST)), input, ConfigLoader.getMainConfig().getString(Constant.LOCAL_SECRET_KEY));
			AMLogger.logMessage(className, methodName, refId, "OUTPUT: " + res[0] + " => " + res[1]);
			if (res.length >=2 && "200".equals(res[0])) {
				cmsResponse = new Gson().fromJson(res[1], CQGResponseObj.class);
				if (cmsResponse != null && Constant.RESPONSE_OK.equalsIgnoreCase(cmsResponse.getStatus())) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private void updateInvestorCQGBalanceId(String investorCode, String balanceId, long refId) {
		String methodName = "updateInvestorCQGBalanceId";
		try {
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investors");
			
			Document query = new Document();
			query.append("investorCode", investorCode);
			
			Document updateDoc = new Document();
			updateDoc.append("cqgInfo.balanceId", balanceId);
			
			Document update = new Document();
			update.append("$set", updateDoc);
			
			collection.updateOne(query, update);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public boolean requestCQGAccBalanceRecord(String investorCode, String accountId, long refId) {
		String methodName = "createCQGAccountBalance";
		try {
			LocalServiceConnection serviceCon = new LocalServiceConnection();
			
			String requestURL = String.format(ConfigLoader.getMainConfig().getString(Constant.SERVICE_CMS_REQUEST_ACC_BALANCE), accountId);
			AMLogger.logMessage(className, methodName, refId, "Request URL: " + requestURL);
			
			String[] res = serviceCon.sendGetRequest(serviceCon.getCMSServiceURL(requestURL), ConfigLoader.getMainConfig().getString(Constant.LOCAL_SECRET_KEY));
			AMLogger.logMessage(className, methodName, refId, "OUTPUT: " + res[0] + " => " + res[1]);
			if (res.length >=2 && "200".equals(res[0])) {
				CQGResponseObj cmsResponse = new Gson().fromJson(res[1], CQGResponseObj.class);
				if (cmsResponse != null && Constant.RESPONSE_OK.equalsIgnoreCase(cmsResponse.getStatus())) {
					updateInvestorCQGBalanceId(investorCode, cmsResponse.getData().getBalanceId(), refId);
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	public boolean updateCQGAccountBalance(String investorCode, String accountId, double changedAmount, long refId) {
		String methodName = "updateCQGAccountBalance";
		try {
			LocalServiceConnection serviceCon = new LocalServiceConnection();
			CQGCMSRequestDTO cqgRequest = new CQGCMSRequestDTO();
			cqgRequest.setAccountId(accountId);
			cqgRequest.setChangedAmount(changedAmount);
			
			String input = new Gson().toJson(cqgRequest);
			AMLogger.logMessage(className, methodName, refId, "INPUT: " + input);
			
			String[] res = serviceCon.sendPutRequest(serviceCon.getCMSServiceURL(ConfigLoader.getMainConfig().getString(Constant.SERVICE_CMS_ACCOUNT_BALANCE)), input, ConfigLoader.getMainConfig().getString(Constant.LOCAL_SECRET_KEY));
			AMLogger.logMessage(className, methodName, refId, "OUTPUT: " + res[0] + " => " + res[1]);
			if (res.length >=2 && "200".equals(res[0])) {
				CQGResponseObj cmsResponse = new Gson().fromJson(res[1], CQGResponseObj.class);
				if (cmsResponse != null && Constant.RESPONSE_OK.equalsIgnoreCase(cmsResponse.getStatus())) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public boolean updateCQGAccountMarketLimits(String accountId, List<CQGCMSCommodityDTO> cqgCommodities, long refId) {
		String methodName = "updateCQGAccountMarketLimits";
		try {
			LocalServiceConnection serviceCon = new LocalServiceConnection();
			CQGCMSRequestDTO cqgRequest = new CQGCMSRequestDTO();
			cqgRequest.setAccountId(accountId);
			cqgRequest.setCommodities(cqgCommodities);
			
			String input = new Gson().toJson(cqgRequest);
			AMLogger.logMessage(className, methodName, refId, "INPUT: " + input);
			
			String[] res = serviceCon.sendPutRequest(serviceCon.getCMSServiceURL(ConfigLoader.getMainConfig().getString(Constant.SERVICE_CMS_ACCOUNT_USER_MARKET_LIMITS)), input, ConfigLoader.getMainConfig().getString(Constant.LOCAL_SECRET_KEY));
			AMLogger.logMessage(className, methodName, refId, "OUTPUT: " + res[0] + " => " + res[1]);
			if (res.length >=2 && "200".equals(res[0])) {
				CQGResponseObj cmsResponse = new Gson().fromJson(res[1], CQGResponseObj.class);
				if (cmsResponse != null && Constant.RESPONSE_OK.equalsIgnoreCase(cmsResponse.getStatus())) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public boolean updateCQGRiskParams(String accountId, double marginMultiplier, long tradeSizeLimit, long defaultPositionLimit, long refId) {
		String methodName = "updateCQGRiskParams";
		try {
			LocalServiceConnection serviceCon = new LocalServiceConnection();
			CQGCMSRequestDTO cqgRequest = new CQGCMSRequestDTO();
			cqgRequest.setAccountId(accountId);
			if (marginMultiplier > 0) {
				cqgRequest.setMarginMultiplier(marginMultiplier);
			}
			if (tradeSizeLimit > 0) {
				cqgRequest.setTradeSizeLimit(tradeSizeLimit);
			}
			if (defaultPositionLimit > 0) {
				cqgRequest.setDefaultPositionLimit(defaultPositionLimit);
			}
			
			String input = new Gson().toJson(cqgRequest);
			AMLogger.logMessage(className, methodName, refId, "INPUT: " + input);
			
			String[] res = serviceCon.sendPostRequest(serviceCon.getCMSServiceURL(ConfigLoader.getMainConfig().getString(Constant.SERVICE_CMS_ACCOUNT_RISK_PARAMS)), input, ConfigLoader.getMainConfig().getString(Constant.LOCAL_SECRET_KEY));
			AMLogger.logMessage(className, methodName, refId, "OUTPUT: " + res[0] + " => " + res[1]);
			if (res.length >=2 && "200".equals(res[0])) {
				CQGResponseObj cmsResponse = new Gson().fromJson(res[1], CQGResponseObj.class);
				if (cmsResponse != null && Constant.RESPONSE_OK.equalsIgnoreCase(cmsResponse.getStatus())) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
