package com.newgen.am.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.ApprovalConstant;
import com.newgen.am.common.Constant;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.MongoDBConnection;
import com.newgen.am.common.RequestParamsParser;
import com.newgen.am.common.SystemFunctionCode;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.ApprovalExchangeSettingDTO;
import com.newgen.am.dto.BasePagination;
import com.newgen.am.dto.BrokerCommodity;
import com.newgen.am.dto.BrokerDTO;
import com.newgen.am.dto.ExchangeSettingDTO;
import com.newgen.am.dto.MemberCSV;
import com.newgen.am.dto.UserBaseInfo;
import com.newgen.am.dto.UserInfoDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.CommodityFee;
import com.newgen.am.model.Exchange;
import com.newgen.am.model.NestedObjectInfo;
import com.newgen.am.model.PendingApproval;
import com.newgen.am.model.PendingData;
import com.newgen.am.repository.LoginAdminUserRepository;
import com.newgen.am.repository.LoginInvestorUserRepository;
import com.newgen.am.repository.PendingApprovalRepository;

@Service
public class ExchangeSettingService {
	private String className = "ExchangeSettingService";
	
	@Autowired
	private RedisTemplate template;
	
	@Autowired
	private PendingApprovalRepository pendingApprovalRepo;
	
	@Autowired
	private ActivityLogService activityLogService;
	
	@Autowired
	private LoginAdminUserRepository loginAdmUserRepo;
	
	@Autowired
	private RequestParamsParser rqParamsParser;
	
	@Autowired
	private LoginInvestorUserRepository loginInvUserRepo;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public void setExchangeSetting(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "setExchangeSetting";
		try {
			ExchangeSettingDTO exchangeDto = new Gson().fromJson(pendingApproval.getPendingData().getPendingValue(), ExchangeSettingDTO.class);
			
			if (exchangeDto != null & exchangeDto.getUsers() != null && exchangeDto.getUsers().size() > 0) {
				// get redis user info
				UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
				// send activity log
				activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_USER_EXCHANGE_SETTING_CREATE,
						ActivityLogService.ACTIVITY_APPROVAL_USER_EXCHANGE_SETTING_CREATE, "", pendingApproval.getId());
				
				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = null;
				
				if (Utility.isNotNull(exchangeDto.getInvestorCode())) {
					// update users in login_investor_users
					collection = database.getCollection("login_investor_users");
				} else {
					// update users in login_admin_users
					collection = database.getCollection("login_admin_users");
				}

				for (UserBaseInfo user : exchangeDto.getUsers()) {
					List<Document> exchanges = new ArrayList<Document>();
					if (exchangeDto.getExchanges() != null && exchangeDto.getExchanges().size() > 0) {
						for (Exchange ex : exchangeDto.getExchanges()) {
							Document exDoc = new Document();
							exDoc.append("exchangeCode", ex.getExchangeCode());
							exDoc.append("priceType", ex.getPriceType());
							exDoc.append("processMethod", ex.getProcessMethod());
							exDoc.append("appliedDate", ex.getAppliedDate());
							
							exchanges.add(exDoc);
						}
					}
					
					Document query = new Document();
					query.append("username", user.getUsername());
					
					Document updateDoc = new Document();
					updateDoc.append("exchanges", exchanges);
					
					Document update = new Document();
					update.append("$set", updateDoc);
					
					collection.updateOne(query, update);
					
					// update redis user info
					Document projection = new Document();
					projection.append("_id", 0.0);
					projection.append("accessToken", 1.0);
					
					Document userDoc = collection.find(query).projection(projection).first();
					UserInfoDTO userDto = mongoTemplate.getConverter().read(UserInfoDTO.class, userDoc);
					
					if (userDto != null) {
						// delete old redis info
						Utility.deleteOldRedisUserInfo(template, userDto.getAccessToken(), refId);
						
						// set exchanges to userInfo
						UserInfoDTO currentUserInfo = Utility.getRedisUserInfo(template, userDto.getAccessToken(), refId);
						if (currentUserInfo != null) {
							currentUserInfo.setExchanges(exchangeDto.getExchanges());
							
							// insert new redis info
							Utility.setRedisInfo(template, Utility.genRedisKey(userDto.getAccessToken()), currentUserInfo, refId);
						}
					}
					
				}
				
			} else {
				throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
			}
			
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
	
	public void setExchangeSettingPA(HttpServletRequest request, ExchangeSettingDTO exchangeDto, long refId) {
		String methodName = "setExchangeSettingPA";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertExchangeSettingPA(userInfo, exchangeDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_USER_EXCHANGE_SETTING_CREATE,
					ActivityLogService.ACTIVITY_USER_EXCHANGE_SETTING_CREATE, "", approvalId);
		} catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
	
	private String insertExchangeSettingPA(UserInfoDTO userInfo, ExchangeSettingDTO exchangeDto, long refId) {
		String methodName = "insertExchangeSettingPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.USER_EXCHANGE_SETTING_CREATE);
			pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
			pendingData.setPendingValue(new Gson().toJson(exchangeDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_USER_EXCHANGE_SETTING_CREATE);
			pendingApproval.setFunctionName(SystemFunctionCode.USER_EXCHANGE_SETTING_CREATE_NAME);
			pendingApproval.setDescription(SystemFunctionCode.USER_EXCHANGE_SETTING_CREATE_NAME);
			pendingApproval.setStatus(Constant.APPROVAL_STATUS_PENDING);
			pendingApproval.setNestedObjInfo(nestedObjInfo);
			pendingApproval.setPendingData(pendingData);
			approvalId = pendingApprovalRepo.save(pendingApproval).getId();
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return approvalId;
	}
	
	public void updateExchangeSetting(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "updateExchangeSetting";
		try {
			ExchangeSettingDTO exchangeDto = new Gson().fromJson(pendingApproval.getPendingData().getPendingValue(), ExchangeSettingDTO.class);
			
			if (exchangeDto != null & exchangeDto.getUsername() != null) {
				// get redis user info
				UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
				// send activity log
				activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_USER_EXCHANGE_SETTING_UPDATE,
						ActivityLogService.ACTIVITY_APPROVAL_USER_EXCHANGE_SETTING_UPDATE, "", pendingApproval.getId());
				
				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = null;
				if (Utility.isNotNull(exchangeDto.getInvestorCode())) {
					// update users in login_investor_users
					collection = database.getCollection("login_investor_users");
				} else {
					// update users in login_admin_users
					collection = database.getCollection("login_admin_users");
				}

				List<Document> exchanges = new ArrayList<Document>();
				
				for (Exchange ex : exchangeDto.getExchanges()) {
					Document exDoc = new Document();
					exDoc.append("exchangeCode", ex.getExchangeCode());
					exDoc.append("priceType", ex.getPriceType());
					exDoc.append("processMethod", ex.getProcessMethod());
					exDoc.append("appliedDate", ex.getAppliedDate());
					
					exchanges.add(exDoc);
				}
				
				Document query = new Document();
				query.append("username", exchangeDto.getUsername());
				
				Document updateDoc = new Document();
				updateDoc.append("exchanges", exchanges);
				
				Document update = new Document();
				update.append("$set", updateDoc);
				
				collection.updateOne(query, update);
				
				// update redis user info
				Document projection = new Document();
				projection.append("_id", 0.0);
				projection.append("accessToken", 1.0);
				
				Document userDoc = collection.find(query).projection(projection).first();
				UserInfoDTO userDto = mongoTemplate.getConverter().read(UserInfoDTO.class, userDoc);
				
				// delete old redis info
				Utility.deleteOldRedisUserInfo(template, userDto.getAccessToken(), refId);
				
				// set exchanges to userInfo
				UserInfoDTO currentUserInfo = Utility.getRedisUserInfo(template, userDto.getAccessToken(), refId);
				if (currentUserInfo != null) {
					currentUserInfo.setExchanges(exchangeDto.getExchanges());
					
					// insert new redis info
					Utility.setRedisInfo(template, Utility.genRedisKey(userDto.getAccessToken()), currentUserInfo, refId);
				}
				
			} else {
				throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
			}
			
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}

	public void updateExchangeSettingPA(HttpServletRequest request, ApprovalExchangeSettingDTO exchangeDto, long refId) {
		String methodName = "updateExchangeSettingPA";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertUpdateExchangeSettingPA(userInfo, exchangeDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_USER_EXCHANGE_SETTING_UPDATE,
					ActivityLogService.ACTIVITY_USER_EXCHANGE_SETTING_UPDATE, "", approvalId);
		} catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
	
	private String insertUpdateExchangeSettingPA(UserInfoDTO userInfo, ApprovalExchangeSettingDTO exchangeDto, long refId) {
		String methodName = "insertExchangeSettingPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.USER_EXCHANGE_SETTING_UPDATE);
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setOldValue(new Gson().toJson(exchangeDto.getOldData()));
			pendingData.setPendingValue(new Gson().toJson(exchangeDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROAL_USER_EXCHANGE_SETTING_UPDATE);
			pendingApproval.setFunctionName(SystemFunctionCode.USER_EXCHANGE_SETTING_UPDATE_NAME);
			pendingApproval.setDescription(SystemFunctionCode.USER_EXCHANGE_SETTING_UPDATE_NAME);
			pendingApproval.setStatus(Constant.APPROVAL_STATUS_PENDING);
			pendingApproval.setNestedObjInfo(nestedObjInfo);
			pendingApproval.setPendingData(pendingData);
			approvalId = pendingApprovalRepo.save(pendingApproval).getId();
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return approvalId;
	}
	
	public BasePagination<ExchangeSettingDTO> listLoginAdmUserExchanges(HttpServletRequest request, long refId) {
		String methodName = "list";
		BasePagination<ExchangeSettingDTO> pagination = null;
		try {
			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "", refId);

			List<? extends Bson> pipeline = Arrays.asList(
                    new Document()
                            .append("$match", searchCriteria.getQuery()), 
                    new Document()
                            .append("$match", new Document()
                                    .append("exchanges", new Document()
                                            .append("$exists", true)
                                    )
                            ), 
                    new Document()
                            .append("$sort", searchCriteria.getSort()), 
                    new Document()
                            .append("$project", new Document()
                                    .append("_id", new Document().append("$toString", "$_id"))
                                    .append("deptCode", 1.0)
                                    .append("deptName", 1.0)
                                    .append("memberCode", 1.0)
                                    .append("memberName", 1.0)
                                    .append("brokerCode", 1.0)
                                    .append("brokerName", 1.0)
                                    .append("collaboratorCode", 1.0)
                                    .append("collaboratorName", 1.0)
                                    .append("investorCode", 1.0)
                                    .append("investorName", 1.0)
                                    .append("username", 1.0)
                                    .append("fullName", 1.0)
                                    .append("exchanges", 1.0)
                            ), 
                    new Document()
                            .append("$facet", new Document()
                                    .append("stage1", Arrays.asList(
                                            new Document()
                                                    .append("$count", "total")
                                        )
                                    )
                                    .append("stage2", Arrays.asList(
                                            new Document()
                                                    .append("$skip", searchCriteria.getSkip()),
                                            new Document()
                                                    .append("$limit", searchCriteria.getLimit())
                                        )
                                    )
                            ), 
                    new Document()
                            .append("$unwind", new Document()
                                    .append("path", "$stage1")
                            ), 
                    new Document()
                            .append("$project", new Document()
                                    .append("count", "$stage1.total")
                                    .append("data", "$stage2")
                            )
            );
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("login_admin_users");
			Document resultDoc = collection.aggregate(pipeline).first();
			pagination = mongoTemplate.getConverter().read(BasePagination.class, resultDoc);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return pagination;
	}
	
	public BasePagination<ExchangeSettingDTO> listLoginInvUserExchanges(HttpServletRequest request, long refId) {
		String methodName = "list";
		BasePagination<ExchangeSettingDTO> pagination = null;
		try {
			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "", refId);

			List<? extends Bson> pipeline = Arrays.asList(
                    new Document()
                            .append("$match", searchCriteria.getQuery()), 
                    new Document()
                            .append("$match", new Document()
                                    .append("exchanges", new Document()
                                            .append("$exists", true)
                                    )
                            ), 
                    new Document()
                            .append("$sort", searchCriteria.getSort()), 
                    new Document()
                            .append("$project", new Document()
                                    .append("_id", new Document().append("$toString", "$_id"))
                                    .append("deptCode", 1.0)
                                    .append("deptName", 1.0)
                                    .append("memberCode", 1.0)
                                    .append("memberName", 1.0)
                                    .append("brokerCode", 1.0)
                                    .append("brokerName", 1.0)
                                    .append("collaboratorCode", 1.0)
                                    .append("collaboratorName", 1.0)
                                    .append("investorCode", 1.0)
                                    .append("investorName", 1.0)
                                    .append("username", 1.0)
                                    .append("fullName", 1.0)
                                    .append("exchanges", 1.0)
                            ), 
                    new Document()
                            .append("$facet", new Document()
                                    .append("stage1", Arrays.asList(
                                            new Document()
                                                    .append("$count", "total")
                                        )
                                    )
                                    .append("stage2", Arrays.asList(
                                            new Document()
                                                    .append("$skip", searchCriteria.getSkip()),
                                            new Document()
                                                    .append("$limit", searchCriteria.getLimit())
                                        )
                                    )
                            ), 
                    new Document()
                            .append("$unwind", new Document()
                                    .append("path", "$stage1")
                            ), 
                    new Document()
                            .append("$project", new Document()
                                    .append("count", "$stage1.total")
                                    .append("data", "$stage2")
                            )
            );
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("login_investor_users");
			Document resultDoc = collection.aggregate(pipeline).first();
			pagination = mongoTemplate.getConverter().read(BasePagination.class, resultDoc);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return pagination;
	}
	
	public ExchangeSettingDTO getExchangeSetting(String username, String investorCode, long refId) {
		String methodName = "getExchangeSetting";
		try {
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection =  null;
			if (Utility.isNotNull(investorCode) && !"0".equals(investorCode)) {
				// update users in login_investor_users
				collection = database.getCollection("login_investor_users");
			} else {
				// update users in login_admin_users
				collection = database.getCollection("login_admin_users");
			}
			
			Document query = new Document();
			query.append("username", username);
			
			Document projection = new Document();
			projection.append("_id", 0.0);
			projection.append("deptCode", 1.0);
			projection.append("deptName", 1.0);
			projection.append("memberCode", 1.0);
			projection.append("memberName", 1.0);
			projection.append("brokerCode", 1.0);
			projection.append("brokerName", 1.0);
			projection.append("collaboratorCode", 1.0);
			projection.append("collaboratorName", 1.0);
			projection.append("investorCode", 1.0);
			projection.append("investorName", 1.0);
			projection.append("username", 1.0);
			projection.append("fullName", 1.0);
			projection.append("exchanges", 1.0);
			
			Document result = collection.find(query).projection(projection).first();
			ExchangeSettingDTO exchangeSettingDto = mongoTemplate.getConverter().read(ExchangeSettingDTO.class, result);
			return exchangeSettingDto;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
