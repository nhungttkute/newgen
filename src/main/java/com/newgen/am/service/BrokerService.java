package com.newgen.am.service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.ApprovalConstant;
import com.newgen.am.common.ConfigLoader;
import com.newgen.am.common.Constant;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.ExcelHelper;
import com.newgen.am.common.FileUtility;
import com.newgen.am.common.LocalServiceConnection;
import com.newgen.am.common.MongoDBConnection;
import com.newgen.am.common.RequestParamsParser;
import com.newgen.am.common.SystemFunctionCode;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.ApprovalFunctionsDTO;
import com.newgen.am.dto.ApprovalUpdateBrokerDTO;
import com.newgen.am.dto.BasePagination;
import com.newgen.am.dto.BrokerCSV;
import com.newgen.am.dto.BrokerCommoditiesDTO;
import com.newgen.am.dto.BrokerCommodity;
import com.newgen.am.dto.BrokerDTO;
import com.newgen.am.dto.DefaultCommodityFeeDTO;
import com.newgen.am.dto.EmailDTO;
import com.newgen.am.dto.FunctionsDTO;
import com.newgen.am.dto.NotifyServiceDTO;
import com.newgen.am.dto.UpdateBrokerDTO;
import com.newgen.am.dto.UserDTO;
import com.newgen.am.dto.UserInfoDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.LoginAdminUser;
import com.newgen.am.model.NestedObjectInfo;
import com.newgen.am.model.PendingApproval;
import com.newgen.am.model.PendingData;
import com.newgen.am.model.RoleFunction;
import com.newgen.am.model.SystemRole;
import com.newgen.am.repository.BrokerRepository;
import com.newgen.am.repository.LoginAdminUserRepository;
import com.newgen.am.repository.PendingApprovalRepository;
import com.newgen.am.repository.SystemRoleRepository;

@Service
public class BrokerService {
	private String className = "BrokerService";

	// Nhung test 
	@Autowired
	ModelMapper modelMapper;
	
	@Autowired
	BrokerRepository brokerRepository;
	
	@Autowired
	PendingApprovalRepository pendingApprovalRepo;

	@Autowired
	LoginAdminUserRepository loginAdmUserRepo;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private RedisTemplate template;

	@Autowired
	private ActivityLogService activityLogService;

	@Autowired
	private RequestParamsParser rqParamsParser;
	
	@Autowired
	private DBSequenceService dbSeqService;
	
	@Autowired
	private SystemRoleRepository sysRoleRepository;

	@Autowired
	PasswordEncoder passwordEncoder;
	
	private Document getQueryDocument(RequestParamsParser.SearchCriteria searchCriteria, UserInfoDTO userInfo) {
		Document query = new Document();
		// get redis user info
		if (Utility.isDeptUser(userInfo)) {
			// do nothing
			query = searchCriteria.getQuery();
		} else if (Utility.isMemberUser(userInfo)) {
			// match code=memberCode
			query = searchCriteria.getQuery().append("memberCode", userInfo.getMemberCode());
		} else if (Utility.isBrokerUser(userInfo)) {
			query = searchCriteria.getQuery().append("memberCode", userInfo.getMemberCode()).append("code", userInfo.getBrokerCode());
		} else {
			throw new CustomException(ErrorMessage.ACCESS_DENIED, HttpStatus.FORBIDDEN);
		}
		return query;
	}
	
	public BasePagination<BrokerDTO> list(HttpServletRequest request, long refId) {
		String methodName = "list";
		BasePagination<BrokerDTO> pagination = null;
		try {
			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "", refId);

			Document sortQuery = searchCriteria.getSort();
			sortQuery.remove(Constant.SORT_DETAUL_FIELD);
			if (!sortQuery.containsKey("code")) {
				sortQuery.append("code", 1);
			}
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			
			List<? extends Bson> pipeline = Arrays.asList(
                    new Document()
                            .append("$match", getQueryDocument(searchCriteria, userInfo)), 
                    new Document()
                            .append("$sort", sortQuery), 
                    new Document()
                            .append("$project", new Document()
                            		.append("_id", new Document().append("$toString", "$_id"))
                            		.append("memberCode", 1.0)
                            		.append("memberName", 1.0)
                                    .append("code", 1.0)
                                    .append("name", 1.0)
                                    .append("status", 1.0)
                                    .append("note", 1.0)
                                    .append("createdDate", 1.0)
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
			MongoCollection<Document> collection = database.getCollection("brokers");
			Document resultDoc = collection.aggregate(pipeline).allowDiskUse(true).first();
			pagination = mongoTemplate.getConverter().read(BasePagination.class, resultDoc);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return pagination;
	}
	
	public List<BrokerCSV> listCsv(HttpServletRequest request, long refId) {
		String methodName = "listCsv";
		List<BrokerCSV> brokerList = new ArrayList<>();
		try {
			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "", refId);

			Document sortQuery = searchCriteria.getSort();
			sortQuery.remove(Constant.SORT_DETAUL_FIELD);
			if (!sortQuery.containsKey("code")) {
				sortQuery.append("code", 1);
			}
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			
			List<? extends Bson> pipeline = Arrays.asList(
                    new Document()
                            .append("$match", getQueryDocument(searchCriteria, userInfo)), 
                    new Document()
                            .append("$sort", sortQuery), 
                    new Document()
                            .append("$project", new Document()
                            		.append("_id", 0.0)
                            		.append("memberCode", 1.0)
                            		.append("memberName", 1.0)
                                    .append("code", 1.0)
                                    .append("name", 1.0)
                                    .append("status", 1.0)
                                    .append("note", 1.0)
                                    .append("createdDate", new Document()
                                            .append("$dateToString", new Document()
                                                    .append("format", "%d/%m/%Y %H:%M:%S")
                                                    .append("date", new Document()
                                                            .append("$toDate", "$createdDate")
                                                    )
                                            )
                                    )
                            ));
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("brokers");
			MongoCursor<Document> cur = collection.aggregate(pipeline).allowDiskUse(true).iterator();
			while (cur.hasNext()) {
				BrokerCSV brokerCsv = mongoTemplate.getConverter().read(BrokerCSV.class, cur.next());
				if (brokerCsv != null) {
					brokerCsv.setStatus(Utility.getStatusVnStr(brokerCsv.getStatus()));
					brokerList.add(brokerCsv);
				}
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return brokerList;
	}
	
	public ByteArrayInputStream loadBrokersExcel(HttpServletRequest request, long refId) {
		String methodName = "listCsv";
		List<BrokerCSV> brokerList = new ArrayList<>();
		ByteArrayInputStream brokersExcel = null;
		
		try {
			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "", refId);

			Document sortQuery = searchCriteria.getSort();
			sortQuery.remove(Constant.SORT_DETAUL_FIELD);
			if (!sortQuery.containsKey("code")) {
				sortQuery.append("code", 1);
			}
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			
			List<? extends Bson> pipeline = Arrays.asList(
                    new Document()
                            .append("$match", getQueryDocument(searchCriteria, userInfo)), 
                    new Document()
                            .append("$sort", sortQuery), 
                    new Document()
                            .append("$project", new Document()
                            		.append("_id", 0.0)
                            		.append("memberCode", 1.0)
                            		.append("memberName", 1.0)
                                    .append("code", 1.0)
                                    .append("name", 1.0)
                                    .append("status", 1.0)
                                    .append("note", 1.0)
                                    .append("createdDate", new Document()
                                            .append("$dateToString", new Document()
                                                    .append("format", "%d/%m/%Y %H:%M:%S")
                                                    .append("date", new Document()
                                                            .append("$toDate", "$createdDate")
                                                    )
                                            )
                                    )
                            ));
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("brokers");
			MongoCursor<Document> cur = collection.aggregate(pipeline).allowDiskUse(true).iterator();
			while (cur.hasNext()) {
				BrokerCSV brokerCsv = mongoTemplate.getConverter().read(BrokerCSV.class, cur.next());
				if (brokerCsv != null) {
					brokerList.add(brokerCsv);
				}
			}
			brokersExcel = ExcelHelper.brokersToExcel(brokerList, refId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return brokersExcel;
	}
	
	public void createBroker(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "createBroker";
		try {
			BrokerDTO brokerDto = Utility.getGson().fromJson(pendingApproval.getPendingData().getPendingValue(), BrokerDTO.class);
			
			// generate broker code
//			String brokerCode = brokerDto.getMemberCode() + Utility.lpad5With0(dbSeqService.generateSequence(Constant.BROKER_SEQ + brokerDto.getMemberCode(), refId));
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_CREATE_BROKER,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_BROKER_DESC, brokerDto.getCode(), pendingApproval.getId());
			
			boolean existedBroker = brokerRepository.existsBrokerByCode(brokerDto.getCode());
			if (existedBroker) {
				AMLogger.logMessage(className, methodName, refId, "This broker code already exists");
				throw new CustomException(ErrorMessage.DOCUMENT_ALREADY_EXISTED, HttpStatus.OK);
			} else {
				Document company = null;
				Document individual = null;
				Document contact = null;
				
				if (Constant.BROKER_TYPE_COMPANY.equalsIgnoreCase(brokerDto.getType())) {
					// create Delegate document
					Document delegate = new Document();
					delegate.append("fullName", brokerDto.getCompany().getDelegate().getFullName());
					delegate.append("birthDay", brokerDto.getCompany().getDelegate().getBirthDay());
					delegate.append("identityCard", brokerDto.getCompany().getDelegate().getIdentityCard());
					delegate.append("idCreatedDate", brokerDto.getCompany().getDelegate().getIdCreatedDate());
					delegate.append("idCreatedLocation", brokerDto.getCompany().getDelegate().getIdCreatedLocation());
					delegate.append("email", brokerDto.getCompany().getDelegate().getEmail());
					delegate.append("phoneNumber", brokerDto.getCompany().getDelegate().getPhoneNumber());
					delegate.append("address", brokerDto.getCompany().getDelegate().getAddress());
					delegate.append("scannedFrontIdCard", brokerDto.getCompany().getDelegate().getScannedFrontIdCard());
					delegate.append("scannedBackIdCard", brokerDto.getCompany().getDelegate().getScannedBackIdCard());
					delegate.append("scannedSignature", brokerDto.getCompany().getDelegate().getScannedSignature());
					
					// create Company document
					company = new Document();
					company.append("name", brokerDto.getCompany().getName());
					company.append("taxCode", brokerDto.getCompany().getTaxCode());
					company.append("address", brokerDto.getCompany().getAddress());
					company.append("phoneNumber", brokerDto.getCompany().getPhoneNumber());
					company.append("fax", brokerDto.getCompany().getFax());
					company.append("email", brokerDto.getCompany().getEmail());
					company.append("delegate", delegate);
					
					// create Contact document
					contact = new Document();
					contact.append("fullName", brokerDto.getCompany().getDelegate().getFullName());
					contact.append("phoneNumber", brokerDto.getCompany().getDelegate().getPhoneNumber());
					contact.append("email", brokerDto.getCompany().getDelegate().getEmail());
				} else if (Constant.BROKER_TYPE_INDIVIDUAL.equalsIgnoreCase(brokerDto.getType())) {
					individual = new Document();
					individual.append("fullName", brokerDto.getIndividual().getFullName());
					individual.append("birthDay", brokerDto.getIndividual().getBirthDay());
					individual.append("identityCard", brokerDto.getIndividual().getIdentityCard());
					individual.append("idCreatedDate", brokerDto.getIndividual().getIdCreatedDate());
					individual.append("idCreatedLocation", brokerDto.getIndividual().getIdCreatedLocation());
					individual.append("email", brokerDto.getIndividual().getEmail());
					individual.append("phoneNumber", brokerDto.getIndividual().getPhoneNumber());
					individual.append("address", brokerDto.getIndividual().getAddress());
					individual.append("scannedFrontIdCard", brokerDto.getIndividual().getScannedFrontIdCard());
					individual.append("scannedBackIdCard", brokerDto.getIndividual().getScannedBackIdCard());
					individual.append("scannedSignature", brokerDto.getIndividual().getScannedSignature());
					
					contact = new Document();
					contact.append("fullName", brokerDto.getIndividual().getFullName());
					contact.append("phoneNumber", brokerDto.getIndividual().getPhoneNumber());
					contact.append("email", brokerDto.getIndividual().getEmail());
				}
				
				// create default broker role
				SystemRole defaultBrokerRole = sysRoleRepository.findByName(Constant.BROKER_DEFAULT_ROLE);
				if (Utility.isNull(defaultBrokerRole)) {
					throw new CustomException(ErrorMessage.DEFAULT_ROLE_DOESNT_EXIST, HttpStatus.OK);
				}
				
				Document brokerRole = new Document();
				brokerRole.append("name", defaultBrokerRole.getName());
				brokerRole.append("description", defaultBrokerRole.getDescription());
				
				Document newBroker = new Document();
				newBroker.append("createdUser", Utility.getCurrentUsername());
				newBroker.append("createdDate", System.currentTimeMillis());
				newBroker.append("_id", new ObjectId());
				newBroker.append("code", brokerDto.getCode());
				newBroker.append("name", brokerDto.getName());
				newBroker.append("status", Constant.STATUS_ACTIVE);
				newBroker.append("note", brokerDto.getNote());
				newBroker.append("memberCode", brokerDto.getMemberCode());
				newBroker.append("memberName", brokerDto.getMemberName());
				newBroker.append("type", brokerDto.getType());
				newBroker.append("company", company);
				newBroker.append("individual", individual);
				newBroker.append("contact", contact);
				newBroker.append("role", brokerRole);
				
				// insert new broker
				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("brokers");
				collection.insertOne(newBroker);
				
				// insert new broker's user to login_admin_users
				createBrokerUser(request, brokerDto, brokerDto.getCode(), refId);
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void createBrokerPA(HttpServletRequest request, BrokerDTO brokerDto, long refId) {
		String methodName = "createBrokerPA";
		boolean existedBroker = brokerRepository.existsBrokerByCode(brokerDto.getCode());
		if (existedBroker) {
			AMLogger.logMessage(className, methodName, refId, "This broker code already exists");
			throw new CustomException(ErrorMessage.DOCUMENT_ALREADY_EXISTED, HttpStatus.OK);
		} else {
			try {
				if (Constant.BROKER_TYPE_COMPANY.equalsIgnoreCase(brokerDto.getType())) {
					if (Utility.isNull(brokerDto.getCompany())) {
						throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
					}
				} else if (Constant.BROKER_TYPE_INDIVIDUAL.equalsIgnoreCase(brokerDto.getType())) {
					if (Utility.isNull(brokerDto.getIndividual())) {
						throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
					}
				} else {
					throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
				}
				
				// get redis user info
				UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
				// insert data to pending_approvals
				String approvalId = insertBrokerCreatePA(userInfo, brokerDto, refId);
				// send activity log
				activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_BROKER,
						ActivityLogService.ACTIVITY_CREATE_BROKER_DESC, brokerDto.getName(), approvalId);
			} catch (CustomException e) {
				throw e;
			} catch (Exception e) {
				AMLogger.logError(className, methodName, refId, e);
				throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	private void createBrokerUser(HttpServletRequest request, BrokerDTO brokerDto, String brokerCode, long refId) {
		String methodName = "createBrokerUser";
		boolean existedUser = false;
		String username = Constant.BROKER_USER_PREFIX + brokerCode;
		try {
			existedUser = loginAdmUserRepo.existsLoginAdminUserByUsername(username);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!existedUser) {
			try {
				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("brokers");

				String fullName = "";
				String email = "";
				String phoneNumber = "";
				if (Utility.isNotNull(brokerDto.getCompany()) && Utility.isNotNull(brokerDto.getCompany().getDelegate())) {
					fullName = brokerDto.getCompany().getDelegate().getFullName();
					email = brokerDto.getCompany().getDelegate().getEmail();
					phoneNumber = brokerDto.getCompany().getDelegate().getPhoneNumber();
				} else if (Utility.isNotNull(brokerDto.getIndividual())) {
					fullName = brokerDto.getIndividual().getFullName();
					email = brokerDto.getIndividual().getEmail();
					phoneNumber = brokerDto.getIndividual().getPhoneNumber();
				}
				
				Document brokerUser = new Document();
				brokerUser.append("_id", new ObjectId());
				brokerUser.append("username", username);
				brokerUser.append("fullName", fullName);
				brokerUser.append("email", email);
				brokerUser.append("phoneNumber", phoneNumber);
				brokerUser.append("status", Constant.STATUS_ACTIVE);
				brokerUser.append("note", brokerDto.getNote());
				brokerUser.append("isPasswordExpiryCheck", false);
				brokerUser.append("passwordExpiryDays", 0);
				brokerUser.append("expiryAlertDays", 0);
				brokerUser.append("createdUser", Utility.getCurrentUsername());
				brokerUser.append("createdDate", System.currentTimeMillis());
//				brokerUser.append("role", brokerRole);

				BasicDBObject query = new BasicDBObject();
				query.append("code", brokerCode);

				collection.updateOne(query, Updates.set("user", brokerUser));

				// insert loginAdminUser
				UserInfoDTO brokerUserDto = new UserInfoDTO();
				brokerUserDto.setMemberCode(brokerDto.getMemberCode());
				brokerUserDto.setMemberName(brokerDto.getMemberName());
				brokerUserDto.setBrokerCode(brokerCode);
				brokerUserDto.setBrokerName(brokerDto.getName());
				brokerUserDto.setUsername(username);
				brokerUserDto.setFullName(fullName);
				brokerUserDto.setEmail(email);
				brokerUserDto.setPhoneNumber(phoneNumber);
				
				String password = Utility.generateRandomPassword();
				String pin = Utility.generateRandomPin();
				createLoginAdminUser(brokerUserDto, password, pin, refId);

				// send email
				if (Utility.isNotifyOn()) {
					Utility.sendCreateNewUserEmail(Constant.BROKER_USER_PREFIX, brokerCode, username, password, pin, refId);
				}
			} catch (Exception e) {
				AMLogger.logError(className, methodName, refId, e);
				throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			AMLogger.logMessage(className, methodName, refId, "This username already exists");
			throw new CustomException(ErrorMessage.DOCUMENT_ALREADY_EXISTED, HttpStatus.OK);
		}
	}
	
	private LoginAdminUser createLoginAdminUser(UserInfoDTO brokerUserDto, String password, String pin,
			long refId) {
		String methodName = "createLoginAdminUser";
		try {
			LoginAdminUser loginAdmUser = modelMapper.map(brokerUserDto, LoginAdminUser.class);
			loginAdmUser.setPassword(passwordEncoder.encode(password));
			loginAdmUser.setPin(passwordEncoder.encode(pin));
			loginAdmUser.setStatus(Constant.STATUS_ACTIVE);
			loginAdmUser.setCreatedUser(Utility.getCurrentUsername());
			loginAdmUser.setCreatedDate(System.currentTimeMillis());
			LoginAdminUser newLoginAdmUser = loginAdmUserRepo.save(loginAdmUser);
			return newLoginAdmUser;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private String insertBrokerCreatePA(UserInfoDTO userInfo, BrokerDTO brokerDto, long refId) {
		String methodName = "insertBrokerCreatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());
			nestedObjInfo.setMemberCode(userInfo.getMemberCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.BROKER_CREATE);
			pendingData.setCollectionName("brokers");
			pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
			pendingData.setPendingValue(Utility.getGson().toJson(brokerDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_BROKER_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.BROKER_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), brokerDto.getName()));
			pendingApproval.setStatus(Constant.APPROVAL_STATUS_PENDING);
			pendingApproval.setNestedObjInfo(nestedObjInfo);
			pendingApproval.setPendingData(pendingData);
			pendingApproval.setSessionDate(Utility.getSessionDateRedis(template));
			approvalId = pendingApprovalRepo.save(pendingApproval).getId();
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return approvalId;
	}
	
	public void updateBroker(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "updateBroker";
		try {
			String brokerCode = pendingApproval.getPendingData().getQueryValue();
			UpdateBrokerDTO brokerDto = Utility.getGson().fromJson(pendingApproval.getPendingData().getPendingValue(), UpdateBrokerDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_UPDATE_BROKER,
					ActivityLogService.ACTIVITY_APPROVAL_UPDATE_BROKER_DESC, brokerCode, pendingApproval.getId());
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("brokers");
			
			BasicDBObject updateBroker = new BasicDBObject();
			BasicDBObject updateLoginAdmUser = new BasicDBObject();
			
			boolean isUserUpdated = false;
			boolean isStatusUpdated = false;
			boolean isNameUpdated = false;
			
			if (Utility.isNotNull(brokerDto.getName())) {
				updateBroker.append("name", brokerDto.getName());
				updateLoginAdmUser.append("brokerName", brokerDto.getName());
				isNameUpdated = true;
			}
			if (Utility.isNotNull(brokerDto.getStatus())) {
				updateBroker.append("status", brokerDto.getStatus().toUpperCase());
				updateBroker.append("user.status", brokerDto.getStatus().toUpperCase());
				updateLoginAdmUser.append("status", brokerDto.getStatus().toUpperCase());
				isUserUpdated = true;
				isStatusUpdated = true;
			}
			if (Utility.isNotNull(brokerDto.getNote())) {
				updateBroker.append("note", brokerDto.getNote());
				updateBroker.append("user.note", brokerDto.getNote());
				isUserUpdated = true;
			}
			
			
			if (Utility.isNotNull(brokerDto.getCompany())) {
				if (Utility.isNotNull(brokerDto.getCompany().getName())) updateBroker.append("company.name", brokerDto.getCompany().getName());
				if (Utility.isNotNull(brokerDto.getCompany().getTaxCode())) updateBroker.append("company.taxCode", brokerDto.getCompany().getTaxCode());
				if (Utility.isNotNull(brokerDto.getCompany().getAddress())) updateBroker.append("company.address", brokerDto.getCompany().getAddress());
				if (Utility.isNotNull(brokerDto.getCompany().getPhoneNumber())) updateBroker.append("company.phoneNumber", brokerDto.getCompany().getPhoneNumber());
				if (Utility.isNotNull(brokerDto.getCompany().getFax())) updateBroker.append("company.fax", brokerDto.getCompany().getFax());
				if (Utility.isNotNull(brokerDto.getCompany().getEmail())) updateBroker.append("company.email", brokerDto.getCompany().getEmail());
				
				if (Utility.isNotNull(brokerDto.getCompany().getDelegate())) {
					if (Utility.isNotNull(brokerDto.getCompany().getDelegate().getFullName())) {
						updateBroker.append("company.delegate.fullName", brokerDto.getCompany().getDelegate().getFullName());
						updateBroker.append("user.fullName", brokerDto.getCompany().getDelegate().getFullName());
						updateBroker.append("contact.fullName", brokerDto.getCompany().getDelegate().getFullName());
						updateLoginAdmUser.append("fullName", brokerDto.getCompany().getDelegate().getFullName());
						isUserUpdated = true;
					}
					if (Utility.isNotNull(brokerDto.getCompany().getDelegate().getBirthDay())) updateBroker.append("company.delegate.birthDay", brokerDto.getCompany().getDelegate().getBirthDay());
					if (Utility.isNotNull(brokerDto.getCompany().getDelegate().getIdentityCard())) updateBroker.append("company.delegate.identityCard", brokerDto.getCompany().getDelegate().getIdentityCard());
					if (Utility.isNotNull(brokerDto.getCompany().getDelegate().getIdCreatedDate()))  updateBroker.append("company.delegate.idCreatedDate", brokerDto.getCompany().getDelegate().getIdCreatedDate());
					if (Utility.isNotNull(brokerDto.getCompany().getDelegate().getIdCreatedLocation()))  updateBroker.append("company.delegate.idCreatedLocation", brokerDto.getCompany().getDelegate().getIdCreatedLocation());
					if (Utility.isNotNull(brokerDto.getCompany().getDelegate().getEmail()))  {
						updateBroker.append("company.delegate.email", brokerDto.getCompany().getDelegate().getEmail());
						updateBroker.append("user.email", brokerDto.getCompany().getDelegate().getEmail());
						updateBroker.append("contact.email", brokerDto.getCompany().getDelegate().getEmail());
						updateLoginAdmUser.append("email", brokerDto.getCompany().getDelegate().getEmail());
						isUserUpdated = true;
					}
					if (Utility.isNotNull(brokerDto.getCompany().getDelegate().getPhoneNumber()))  {
						updateBroker.append("company.delegate.phoneNumber", brokerDto.getCompany().getDelegate().getPhoneNumber());
						updateBroker.append("user.phoneNumber", brokerDto.getCompany().getDelegate().getPhoneNumber());
						updateBroker.append("contact.phoneNumber", brokerDto.getCompany().getDelegate().getPhoneNumber());
						updateLoginAdmUser.append("phoneNumber", brokerDto.getCompany().getDelegate().getPhoneNumber());
						isUserUpdated = true;
					}
					if (Utility.isNotNull(brokerDto.getCompany().getDelegate().getAddress()))  updateBroker.append("company.delegate.address", brokerDto.getCompany().getDelegate().getAddress());
					if (Utility.isNotNull(brokerDto.getCompany().getDelegate().getScannedFrontIdCard()))  updateBroker.append("company.delegate.scannedFrontIdCard", brokerDto.getCompany().getDelegate().getScannedFrontIdCard());
					if (Utility.isNotNull(brokerDto.getCompany().getDelegate().getScannedBackIdCard()))  updateBroker.append("company.delegate.scannedBackIdCard", brokerDto.getCompany().getDelegate().getScannedBackIdCard());
					if (Utility.isNotNull(brokerDto.getCompany().getDelegate().getScannedSignature()))  updateBroker.append("company.delegate.scannedSignature", brokerDto.getCompany().getDelegate().getScannedSignature());
				}
			}
			
			if (Utility.isNotNull(brokerDto.getIndividual())) {
				if (Utility.isNotNull(brokerDto.getIndividual().getFullName())) {
					updateBroker.append("individual.fullName", brokerDto.getIndividual().getFullName());
					updateBroker.append("user.fullName", brokerDto.getIndividual().getFullName());
					updateBroker.append("contact.fullName", brokerDto.getIndividual().getFullName());
					updateLoginAdmUser.append("fullName", brokerDto.getIndividual().getFullName());
					isUserUpdated = true;
				}
				if (Utility.isNotNull(brokerDto.getIndividual().getBirthDay())) updateBroker.append("individual.birthDay", brokerDto.getIndividual().getBirthDay());
				if (Utility.isNotNull(brokerDto.getIndividual().getIdentityCard())) updateBroker.append("individual.identityCard", brokerDto.getIndividual().getIdentityCard());
				if (Utility.isNotNull(brokerDto.getIndividual().getIdCreatedDate()))  updateBroker.append("individual.idCreatedDate", brokerDto.getIndividual().getIdCreatedDate());
				if (Utility.isNotNull(brokerDto.getIndividual().getIdCreatedLocation()))  updateBroker.append("individual.idCreatedLocation", brokerDto.getIndividual().getIdCreatedLocation());
				if (Utility.isNotNull(brokerDto.getIndividual().getEmail()))  {
					updateBroker.append("individual.email", brokerDto.getIndividual().getEmail());
					updateBroker.append("user.email", brokerDto.getIndividual().getEmail());
					updateBroker.append("contact.email", brokerDto.getIndividual().getEmail());
					updateLoginAdmUser.append("email", brokerDto.getIndividual().getEmail());
					isUserUpdated = true;
				}
				if (Utility.isNotNull(brokerDto.getIndividual().getPhoneNumber()))  {
					updateBroker.append("individual.phoneNumber", brokerDto.getIndividual().getPhoneNumber());
					updateBroker.append("user.phoneNumber", brokerDto.getIndividual().getPhoneNumber());
					updateBroker.append("contact.phoneNumber", brokerDto.getIndividual().getPhoneNumber());
					updateLoginAdmUser.append("phoneNumber", brokerDto.getIndividual().getPhoneNumber());
					isUserUpdated = true;
				}
				if (Utility.isNotNull(brokerDto.getIndividual().getAddress()))  updateBroker.append("individual.address", brokerDto.getIndividual().getAddress());
				if (Utility.isNotNull(brokerDto.getIndividual().getScannedFrontIdCard()))  updateBroker.append("individual.scannedFrontIdCard", brokerDto.getIndividual().getScannedFrontIdCard());
				if (Utility.isNotNull(brokerDto.getIndividual().getScannedBackIdCard()))  updateBroker.append("individual.scannedBackIdCard", brokerDto.getIndividual().getScannedBackIdCard());
				if (Utility.isNotNull(brokerDto.getIndividual().getScannedSignature()))  updateBroker.append("individual.scannedSignature", brokerDto.getIndividual().getScannedSignature());
			}
			
			if (updateBroker.isEmpty()) {
				throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
			} else {
				updateBroker.append("lastModifiedUser", Utility.getCurrentUsername());
				updateBroker.append("lastModifiedDate", System.currentTimeMillis());
				
				if (isUserUpdated) {
					updateBroker.append("user.lastModifiedUser", Utility.getCurrentUsername());
					updateBroker.append("user.lastModifiedDate", System.currentTimeMillis());
				}
				
				BasicDBObject query = new BasicDBObject();
				query.append("code", brokerCode);
				
				BasicDBObject update = new BasicDBObject();
				update.append("$set", updateBroker);
				
				collection.updateOne(query, update);
				
				// update login_admin_users if there's any change
				if (!updateLoginAdmUser.isEmpty()) {
					BasicDBObject logiAdmQuery = new BasicDBObject();
					logiAdmQuery.append("username", Constant.BROKER_USER_PREFIX + brokerCode);
					
					BasicDBObject loginAdmUpdate = new BasicDBObject();
					loginAdmUpdate.append("$set", updateLoginAdmUser);
					
					MongoCollection<Document> loginAdmCollection = database.getCollection("login_admin_users");
					loginAdmCollection.updateOne(logiAdmQuery, loginAdmUpdate);
				}
				
				if (isStatusUpdated) {
					String brokerUsername = Constant.BROKER_USER_PREFIX + brokerCode;
					// logout all users if status is inactive
					if (Constant.STATUS_INACTIVE.equalsIgnoreCase(brokerDto.getStatus())) {
						List<String> userList = new ArrayList<String>();
						userList.add(brokerUsername);
						Utility.sendHandleLogout(userList, refId);
					}
				}
				
				if (isNameUpdated) {
					// update collaborator's brokerName
					updateCollaboratorBrokerName(brokerCode, brokerDto.getName());
					// update investors's brokerName
					updateInvestorBrokerName(brokerCode, brokerDto.getName());
					// update login_admin_users' brokerName
					updateLoginAdminUserBrokerName(brokerCode, brokerDto.getName());
					// update login_investor_users' brokerName
					updateLoginInvestorUserBrokerName(brokerCode, brokerDto.getName());
					// update investor_margin_info's brokerName
					updateInvestorMarginInfoBrokerName(brokerCode, brokerDto.getName());
				}
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private void updateCollaboratorBrokerName(String brokerCode, String brokerName) {
		MongoDatabase database = MongoDBConnection.getMongoDatabase();
		MongoCollection<Document> collection = database.getCollection("collaborators");
		
		Document query = new Document();
		query.append("brokerCode", brokerCode);
		
		Document updateDoc = new Document();
		updateDoc.append("brokerName", brokerName);
		
		Document update = new Document();
		update.append("$set", updateDoc);
		
		collection.updateMany(query, update);
	}
	
	private void updateInvestorBrokerName(String brokerCode, String brokerName) {
		MongoDatabase database = MongoDBConnection.getMongoDatabase();
		MongoCollection<Document> collection = database.getCollection("investors");
		
		Document query = new Document();
		query.append("brokerCode", brokerCode);
		
		Document updateDoc = new Document();
		updateDoc.append("brokerName", brokerName);
		
		Document update = new Document();
		update.append("$set", updateDoc);
		
		collection.updateMany(query, update);
	}
	
	private void updateLoginAdminUserBrokerName(String brokerCode, String brokerName) {
		MongoDatabase database = MongoDBConnection.getMongoDatabase();
		MongoCollection<Document> collection = database.getCollection("login_admin_users");
		
		Document query = new Document();
		query.append("brokerCode", brokerCode);
		
		Document updateDoc = new Document();
		updateDoc.append("brokerName", brokerName);
		
		Document update = new Document();
		update.append("$set", updateDoc);
		
		collection.updateMany(query, update);
	}
	
	private void updateLoginInvestorUserBrokerName(String brokerCode, String brokerName) {
		MongoDatabase database = MongoDBConnection.getMongoDatabase();
		MongoCollection<Document> collection = database.getCollection("login_investor_users");
		
		Document query = new Document();
		query.append("brokerCode", brokerCode);
		
		Document updateDoc = new Document();
		updateDoc.append("brokerName", brokerName);
		
		Document update = new Document();
		update.append("$set", updateDoc);
		
		collection.updateMany(query, update);
	}
	
	private void updateInvestorMarginInfoBrokerName(String brokerCode, String brokerName) {
		MongoDatabase database = MongoDBConnection.getMongoDatabase();
		MongoCollection<Document> collection = database.getCollection("investor_margin_info");
		
		Document query = new Document();
		query.append("brokerCode", brokerCode);
		
		Document updateDoc = new Document();
		updateDoc.append("brokerName", brokerName);
		
		Document update = new Document();
		update.append("$set", updateDoc);
		
		collection.updateMany(query, update);
	}
	
	public void updateBrokerPA(HttpServletRequest request, String brokerCode, ApprovalUpdateBrokerDTO brokerDto, long refId) {
		String methodName = "updateBrokerPA";
		try {
			if (!brokerRepository.existsBrokerByCode(brokerCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertBrokerUpdatePA(userInfo, brokerCode, brokerDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_UPDATE_BROKER,
					ActivityLogService.ACTIVITY_UPDATE_BROKER_DESC, brokerCode, approvalId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String insertBrokerUpdatePA(UserInfoDTO userInfo, String brokerCode, ApprovalUpdateBrokerDTO brokerDto, long refId) {
		String methodName = "insertBrokerUpdatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());
			nestedObjInfo.setMemberCode(userInfo.getMemberCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.BROKER_UPDATE);
			pendingData.setCollectionName("brokers");
			pendingData.setQueryField("code");
			pendingData.setQueryValue(brokerCode);
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setOldValue(Utility.getGson().toJson(brokerDto.getOldData()));
			pendingData.setPendingValue(Utility.getGson().toJson(brokerDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_BROKER_UPDATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.BROKER_UPDATE_NAME);
			pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(),
					brokerCode));
			pendingApproval.setStatus(Constant.APPROVAL_STATUS_PENDING);
			pendingApproval.setNestedObjInfo(nestedObjInfo);
			pendingApproval.setPendingData(pendingData);
			pendingApproval.setSessionDate(Utility.getSessionDateRedis(template));
			approvalId = pendingApprovalRepo.save(pendingApproval).getId();
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return approvalId;
	}
	
	public BrokerDTO getBrokerDetail(HttpServletRequest request, String brokerCode, long refId) {
		String methodName = "getBrokerDetail";
		try {
			Document query = new Document();
            query.append("code", brokerCode);
            UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
            if (Utility.isNotNull(userInfo.getBrokerCode())) {
            	query = new Document();
                query.append("$and", Arrays.asList(
                        new Document()
                                .append("code", brokerCode),
                        new Document()
                                .append("code", userInfo.getBrokerCode())
                    )
                );
            }
            if (Utility.isNotNull(userInfo.getMemberCode())) {
            	query.append("memberCode", userInfo.getMemberCode());
            }
            
            
            MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("brokers");
			
			Document brokerDoc = collection.find(query).first();
			if (brokerDoc == null) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			BrokerDTO brokerDto = mongoTemplate.getConverter().read(BrokerDTO.class, brokerDoc);
			return brokerDto;
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public UserDTO getBrokerUserDetail(HttpServletRequest request, String brokerCode, long refId) {
		String methodName = "getBrokerUserDetail";
		try {
            MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("brokers");
			
			Document query = new Document().append("code", brokerCode);
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			if (Utility.isNotNull(userInfo.getBrokerCode())) {
				query = new Document();
			    query.append("$and", Arrays.asList(
			            new Document()
			                    .append("code", brokerCode),
			            new Document()
			                    .append("code", userInfo.getBrokerCode())
			        )
			    );
			}
			if (Utility.isNotNull(userInfo.getMemberCode())) {
				query.append("memberCode", userInfo.getMemberCode());
			}
			
			List<? extends Bson> pipeline = Arrays.asList(
                    new Document()
                            .append("$match", query), 
                    new Document()
                            .append("$project", new Document()
                                    .append("_id", 0.0)
                                    .append("user", 1.0)
                            ), 
                    new Document()
                            .append("$replaceRoot", new Document()
                                    .append("newRoot", "$user")
                            )
            );
			
			Document resultDoc = collection.aggregate(pipeline).first();
			if (resultDoc == null) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			UserDTO brokerUserDto = mongoTemplate.getConverter().read(UserDTO.class, resultDoc);
			return brokerUserDto;
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void createBrokerFunctions(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "createBrokerFunctions";
		try {
			String brokerCode = pendingApproval.getPendingData().getQueryValue();
			FunctionsDTO brokerDto = Utility.getGson().fromJson(pendingApproval.getPendingData().getPendingValue(), FunctionsDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_FUNCTIONS,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_FUNCTIONS_DESC, brokerCode,
					pendingApproval.getId());
			
			List<Document> functions = new ArrayList<Document>();
			for (RoleFunction function : brokerDto.getFunctions()) {
				Document func = new Document();
				func.append("code", function.getCode());
				func.append("name", function.getName());
				functions.add(func);
			}
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("brokers");
			
			BasicDBObject query = new BasicDBObject();
			query.append("code", brokerCode);
			
			BasicDBObject updateBroker = new BasicDBObject();
			updateBroker.append("functions", functions);
//			updateBroker.append("user.functions", functions);
			updateBroker.append("lastModifiedUser", Utility.getCurrentUsername());
			updateBroker.append("lastModifiedDate", System.currentTimeMillis());
//			updateBroker.append("user.lastModifiedUser", Utility.getCurrentUsername());
//			updateBroker.append("user.lastModifiedDate", System.currentTimeMillis());
			
			
			BasicDBObject update = new BasicDBObject();
			update.append("$set", updateBroker);
			
			collection.updateOne(query, update);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void createBrokerFunctionsPA(HttpServletRequest request, String brokerCode, ApprovalFunctionsDTO brokerDto,
			long refId) {
		String methodName = "createBrokerFunctionsPA";
		try {
			if (!brokerRepository.existsBrokerByCode(brokerCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertBrokerFunctionsAssignPA(userInfo, brokerCode, brokerDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_CREATE_MEMBER_FUNCTIONS,
					ActivityLogService.ACTIVITY_CREATE_MEMBER_FUNCTIONS_DESC, String.valueOf(brokerCode),
					approvalId);

		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private String insertBrokerFunctionsAssignPA(UserInfoDTO userInfo, String brokerCode, ApprovalFunctionsDTO brokerDto,
			long refId) {
		String methodName = "insertBrokerFunctionsAssignPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());
			nestedObjInfo.setMemberCode(userInfo.getMemberCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.BROKER_FUNCTIONS_CREATE);
			pendingData.setCollectionName("brokers");
			pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
			pendingData.setQueryField("code");
			pendingData.setQueryValue(brokerCode);
			pendingData.setOldValue(Utility.getGson().toJson(brokerDto.getOldData()));
			pendingData.setPendingValue(Utility.getGson().toJson(brokerDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_BROKER_FUNCTIONS_ASSIGN_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.BROKER_FUNCTIONS_ASSIGN_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), brokerCode));
			pendingApproval.setStatus(Constant.APPROVAL_STATUS_PENDING);
			pendingApproval.setNestedObjInfo(nestedObjInfo);
			pendingApproval.setPendingData(pendingData);
			pendingApproval.setSessionDate(Utility.getSessionDateRedis(template));
			approvalId = pendingApprovalRepo.save(pendingApproval).getId();
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return approvalId;
	}
	
	public void createDefaultSetting(HttpServletRequest request, String brokerCode, DefaultCommodityFeeDTO brokerDto, long refId) {
		String methodName = "createDefaultSetting";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_BROKER_DEFAULT_SETTING,
					ActivityLogService.ACTIVITY_CREATE_BROKER_DEFAULT_SETTING_DESC, brokerCode, "");

			if (!brokerRepository.existsBrokerByCode(brokerCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			Document updateDocument = new Document();
			updateDocument.append("defaultCommodityFee", brokerDto.getDefaultCommodityFee());
			
			if (updateDocument.isEmpty()) {
				throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
			}
			
			if (updateDocument.isEmpty()) {
				throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
			} else {
				updateDocument.append("lastModifiedUser", Utility.getCurrentUsername());
				updateDocument.append("lastModifiedDate", System.currentTimeMillis());
				
				Document query = new Document();
				query.append("code", brokerCode);
				
				Document update = new Document();
				update.append("$set", updateDocument);
				
				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("brokers");
				collection.updateOne(query, update);
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void createBrokerCommodities(HttpServletRequest request, String brokerCode, BrokerCommoditiesDTO brokerDto, long refId) {
		String methodName = "createBrokerCommodities";
		if (Utility.isNotNull(brokerDto.getCommodities()) && brokerDto.getCommodities().size() > 0) {
			try {
				// get redis user info
				UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
				// send activity log
				activityLogService.sendActivityLog(userInfo, request,
						ActivityLogService.ACTIVITY_CREATE_BROKER_COMMODITIES_ASSIGN,
						ActivityLogService.ACTIVITY_CREATE_BROKER_COMMODITIES_ASSIGN_DESC, brokerCode,
						"");

				if (!brokerRepository.existsBrokerByCode(brokerCode)) {
					throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
				}
				
				List<Document> commodities = new ArrayList<Document>();
				
				for (BrokerCommodity comm : brokerDto.getCommodities()) {
					Document commDoc = new Document();
					commDoc.append("commodityCode", comm.getCommodityCode());
					commDoc.append("commodityName", comm.getCommodityName());
					commDoc.append("commodityFee", comm.getCommodityFee());
					commDoc.append("currency", Constant.CURRENCY_VND);
					commodities.add(commDoc);
				}
				
				Document query = new Document();
				query.append("code", brokerCode);
				
				Document updateBroker = new Document();
				updateBroker.append("commodities", commodities);
				updateBroker.append("lastModifiedUser", Utility.getCurrentUsername());
				updateBroker.append("lastModifiedDate", System.currentTimeMillis());
				
				Document update = new Document();
				update.append("$set", updateBroker);
				
				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("brokers");
				collection.updateOne(query, update);
			} catch (CustomException e) {
				throw e;
			} catch (Exception e) {
				AMLogger.logError(className, methodName, refId, e);
				throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			Document query = new Document();
			query.append("code", brokerCode);
			
			Document updateBroker = new Document();
			updateBroker.append("commodities", null);
			updateBroker.append("lastModifiedUser", Utility.getCurrentUsername());
			updateBroker.append("lastModifiedDate", System.currentTimeMillis());
			
			Document update = new Document();
			update.append("$set", updateBroker);
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("brokers");
			collection.updateOne(query, update);
		}
	}
	
	public BrokerCommoditiesDTO getBrokerCommodities(HttpServletRequest request, String brokerCode, long refId) {
		String methodName = "getBrokerCommodities";
		try {
			if (!brokerRepository.existsBrokerByCode(brokerCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("brokers");
			
			Document query = new Document();
            query.append("code", brokerCode);
            UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
            if (Utility.isNotNull(userInfo.getBrokerCode())) {
            	query = new Document();
                query.append("$and", Arrays.asList(
                        new Document()
                                .append("code", brokerCode),
                        new Document()
                                .append("code", userInfo.getBrokerCode())
                    )
                );
            }
            if (Utility.isNotNull(userInfo.getMemberCode())) {
            	query.append("memberCode", userInfo.getMemberCode());
            }
            
            Document projection = new Document();
            projection.append("_id", 0.0);
            projection.append("commodities.commodityCode", 1.0);
            projection.append("commodities.commodityName", 1.0);
            
            Document result = collection.find(query).projection(projection).first();
            BrokerCommoditiesDTO brokerDto = mongoTemplate.getConverter().read(BrokerCommoditiesDTO.class, result);
			return brokerDto;
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}