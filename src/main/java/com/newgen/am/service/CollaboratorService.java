package com.newgen.am.service;

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
import com.newgen.am.common.FileUtility;
import com.newgen.am.common.LocalServiceConnection;
import com.newgen.am.common.MongoDBConnection;
import com.newgen.am.common.RequestParamsParser;
import com.newgen.am.common.SystemFunctionCode;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.ApprovalFunctionsDTO;
import com.newgen.am.dto.ApprovalUpdateCollaboratorDTO;
import com.newgen.am.dto.BasePagination;
import com.newgen.am.dto.CollaboratorCSV;
import com.newgen.am.dto.CollaboratorDTO;
import com.newgen.am.dto.EmailDTO;
import com.newgen.am.dto.FunctionsDTO;
import com.newgen.am.dto.UpdateCollaboratorDTO;
import com.newgen.am.dto.UserDTO;
import com.newgen.am.dto.UserInfoDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.LoginAdminUser;
import com.newgen.am.model.NestedObjectInfo;
import com.newgen.am.model.PendingApproval;
import com.newgen.am.model.PendingData;
import com.newgen.am.model.RoleFunction;
import com.newgen.am.model.SystemRole;
import com.newgen.am.repository.CollaboratorRepository;
import com.newgen.am.repository.LoginAdminUserRepository;
import com.newgen.am.repository.PendingApprovalRepository;
import com.newgen.am.repository.SystemRoleRepository;

@Service
public class CollaboratorService {
	private String className = "CollaboratorService";

	@Autowired
	ModelMapper modelMapper;
	
	@Autowired
	CollaboratorRepository collaboratorRepo;
	
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
			query = searchCriteria.getQuery().append("memberCode", userInfo.getMemberCode()).append("brokerCode", userInfo.getBrokerCode());
		} else if (Utility.isCollaboratorUser(userInfo)) {
			query = searchCriteria.getQuery().append("memberCode", userInfo.getMemberCode()).append("brokerCode", userInfo.getBrokerCode()).append("code", userInfo.getCollaboratorCode());
		} else {
			throw new CustomException(ErrorMessage.ACCESS_DENIED, HttpStatus.FORBIDDEN);
		}
		return query;
	}
	
	public BasePagination<CollaboratorDTO> list(HttpServletRequest request, long refId) {
		String methodName = "list";
		BasePagination<CollaboratorDTO> pagination = null;
		try {
			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "", refId);

			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			
			List<? extends Bson> pipeline = Arrays.asList(
                    new Document()
                            .append("$match", getQueryDocument(searchCriteria, userInfo)), 
                    new Document()
                            .append("$sort", searchCriteria.getSort()), 
                    new Document()
                            .append("$project", new Document()
                            		.append("_id", new Document().append("$toString", "$_id"))
                            		.append("memberCode", 1.0)
                            		.append("memberName", 1.0)
                            		.append("brokerCode", 1.0)
                            		.append("brokerName", 1.0)
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
			MongoCollection<Document> collection = database.getCollection("collaborators");
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
	
	public List<CollaboratorCSV> listCsv(HttpServletRequest request, long refId) {
		String methodName = "listCsv";
		List<CollaboratorCSV> collaboratorList = new ArrayList<>();
		try {
			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "", refId);
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			
			List<? extends Bson> pipeline = Arrays.asList(
                    new Document()
                            .append("$match", getQueryDocument(searchCriteria, userInfo)), 
                    new Document()
                            .append("$sort", searchCriteria.getSort()), 
                    new Document()
                            .append("$project", new Document()
                            		.append("_id", new Document().append("$toString", "$_id"))
                            		.append("memberCode", 1.0)
                            		.append("memberName", 1.0)
                            		.append("brokerCode", 1.0)
                            		.append("brokerName", 1.0)
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
			MongoCollection<Document> collection = database.getCollection("collaborators");
			MongoCursor<Document> cur = collection.aggregate(pipeline).iterator();
			while (cur.hasNext()) {
				CollaboratorCSV brokerCsv = mongoTemplate.getConverter().read(CollaboratorCSV.class, cur.next());
				if (brokerCsv != null)
					collaboratorList.add(brokerCsv);
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return collaboratorList;
	}
	
	public void createCollaborator(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "createCollaborator";
		try {
			CollaboratorDTO collaboratorDto = new Gson().fromJson(pendingApproval.getPendingData().getPendingValue(), CollaboratorDTO.class);
			
			// generate collaborator code
			String collaboratorCode = collaboratorDto.getMemberCode() + Utility.lpad3With0(dbSeqService.generateSequence(Constant.COLLABORATOR_SEQ + collaboratorDto.getMemberCode(), refId));

			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_CREATE_COLLABORATOR,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_COLLABORATOR_DESC, collaboratorCode, pendingApproval.getId());
			
			// create Delegate document
			Document delegate = new Document();
			delegate.append("fullName", collaboratorDto.getDelegate().getFullName());
			delegate.append("birthDay", collaboratorDto.getDelegate().getBirthDay());
			delegate.append("identityCard", collaboratorDto.getDelegate().getIdentityCard());
			delegate.append("idCreatedDate", collaboratorDto.getDelegate().getIdCreatedDate());
			delegate.append("idCreatedLocation", collaboratorDto.getDelegate().getIdCreatedLocation());
			delegate.append("email", collaboratorDto.getDelegate().getEmail());
			delegate.append("phoneNumber", collaboratorDto.getDelegate().getPhoneNumber());
			delegate.append("address", collaboratorDto.getDelegate().getAddress());
			delegate.append("scannedFrontIdCard", collaboratorDto.getDelegate().getScannedFrontIdCard());
			delegate.append("scannedBackIdCard", collaboratorDto.getDelegate().getScannedBackIdCard());
			delegate.append("scannedSignature", collaboratorDto.getDelegate().getScannedSignature());
			
			// create Contact document
			Document contact = new Document();
			contact.append("fullName", collaboratorDto.getDelegate().getFullName());
			contact.append("phoneNumber", collaboratorDto.getDelegate().getPhoneNumber());
			contact.append("email", collaboratorDto.getDelegate().getEmail());
			
			// create default collaborator role
			SystemRole defaultCollaboratorRole = sysRoleRepository.findByName(Constant.COLLABORATOR_DEFAULT_ROLE);
			if (Utility.isNull(defaultCollaboratorRole)) {
				throw new CustomException(ErrorMessage.DEFAULT_ROLE_DOESNT_EXIST, HttpStatus.OK);
			}
			
			Document collaboratorRole = new Document();
			collaboratorRole.append("name", defaultCollaboratorRole.getName());
			collaboratorRole.append("description", defaultCollaboratorRole.getDescription());
			
			Document newCollaborator = new Document();
			newCollaborator.append("createdUser", Utility.getCurrentUsername());
			newCollaborator.append("createdDate", System.currentTimeMillis());
			newCollaborator.append("_id", new ObjectId());
			newCollaborator.append("code", collaboratorCode);
			newCollaborator.append("name", collaboratorDto.getName());
			newCollaborator.append("status", Constant.STATUS_ACTIVE);
			newCollaborator.append("note", collaboratorDto.getNote());
			newCollaborator.append("memberCode", collaboratorDto.getMemberCode());
			newCollaborator.append("memberName", collaboratorDto.getMemberName());
			newCollaborator.append("brokerCode", collaboratorDto.getBrokerCode());
			newCollaborator.append("brokerName", collaboratorDto.getBrokerName());
			newCollaborator.append("delegate", delegate);
			newCollaborator.append("contact", contact);
			newCollaborator.append("role", collaboratorRole);
			
			// insert new broker
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("collaborators");
			collection.insertOne(newCollaborator);
			
			// insert new broker's user to login_admin_users
			createCollaboratorUser(request, collaboratorDto, collaboratorCode, refId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void createCollaboratorPA(HttpServletRequest request, CollaboratorDTO collaboratorDto, long refId) {
		String methodName = "createCollaboratorPA";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertCollaboratorCreatePA(userInfo, collaboratorDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_COLLABORATOR,
					ActivityLogService.ACTIVITY_CREATE_COLLABORATOR_DESC, collaboratorDto.getName(), approvalId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private void createCollaboratorUser(HttpServletRequest request, CollaboratorDTO collaboratorDto, String collaboratorCode, long refId) {
		String methodName = "createCollaboratorUser";
		boolean existedUser = false;
		String username = Constant.COLLABORATOR_USER_PREFIX + collaboratorCode;
		try {
			existedUser = loginAdmUserRepo.existsLoginAdminUserByUsername(username);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!existedUser) {
			try {
				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("collaborators");

				Document collaboratorUser = new Document();
				collaboratorUser.append("_id", new ObjectId());
				collaboratorUser.append("username", username);
				collaboratorUser.append("fullName", collaboratorDto.getDelegate().getFullName());
				collaboratorUser.append("email", collaboratorDto.getDelegate().getEmail());
				collaboratorUser.append("phoneNumber", collaboratorDto.getDelegate().getPhoneNumber());
				collaboratorUser.append("status", Constant.STATUS_ACTIVE);
				collaboratorUser.append("note", collaboratorDto.getNote());
				collaboratorUser.append("isPasswordExpiryCheck", false);
				collaboratorUser.append("passwordExpiryDays", 0);
				collaboratorUser.append("expiryAlertDays", 0);
				collaboratorUser.append("createdUser", Utility.getCurrentUsername());
				collaboratorUser.append("createdDate", System.currentTimeMillis());

				BasicDBObject query = new BasicDBObject();
				query.append("code", collaboratorCode);

				collection.updateOne(query, Updates.set("user", collaboratorUser));

				// insert loginAdminUser
				UserInfoDTO userDto = new UserInfoDTO();
				userDto.setMemberCode(collaboratorDto.getMemberCode());
				userDto.setMemberName(collaboratorDto.getMemberName());
				userDto.setBrokerCode(collaboratorDto.getBrokerCode());
				userDto.setBrokerName(collaboratorDto.getBrokerName());
				userDto.setCollaboratorCode(collaboratorCode);
				userDto.setCollaboratorName(collaboratorDto.getName());
				userDto.setUsername(username);
				userDto.setFullName(collaboratorDto.getDelegate().getFullName());
				userDto.setEmail(collaboratorDto.getDelegate().getEmail());
				userDto.setPhoneNumber(collaboratorDto.getDelegate().getPhoneNumber());
				
				String password = Utility.generateRandomPassword();
				String pin = Utility.generateRandomPin();
				createLoginAdminUser(userDto, password, pin, refId);

				// send email
				sendCreateNewUserEmail(collaboratorDto.getDelegate().getEmail(), username, password, pin, refId);
			} catch (Exception e) {
				AMLogger.logError(className, methodName, refId, e);
				throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			AMLogger.logMessage(className, methodName, refId, "This username already exists");
			throw new CustomException(ErrorMessage.DOCUMENT_ALREADY_EXISTED, HttpStatus.OK);
		}
	}
	
	private LoginAdminUser createLoginAdminUser(UserInfoDTO userDto, String password, String pin,
			long refId) {
		String methodName = "createLoginAdminUser";
		try {
			LoginAdminUser loginAdmUser = modelMapper.map(userDto, LoginAdminUser.class);
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
	
	private void sendCreateNewUserEmail(String toEmail, String username, String password, String pin, long refId) {
		String methodName = "sendCreateNewUserEmail";
		try {
			LocalServiceConnection serviceCon = new LocalServiceConnection();
			EmailDTO email = new EmailDTO();
			email.setSettingType(Constant.SERVICE_NOTIFICATION_SETTING_TYPE_CREATE_USER);
			email.setSendingObject(Constant.SERVICE_NOTIFICATION_SENDING_OBJ);
			email.setTo(toEmail);
			email.setSubject(FileUtility.CREATE_NEW_USER_EMAIL_SUBJECT);

			String emailBody = String.format(
					FileUtility.loadFileContent(
							ConfigLoader.getMainConfig().getString(FileUtility.CREATE_NEW_USER_EMAIL_FILE), refId),
					username, password, pin);
			email.setBodyStr(emailBody);
			String emailJson = new Gson().toJson(email);
			AMLogger.logMessage(className, methodName, refId, "Email: " + emailJson);
			serviceCon.sendPostRequest(serviceCon.getEmailNotificationServiceURL(), emailJson);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private String insertCollaboratorCreatePA(UserInfoDTO userInfo, CollaboratorDTO collaboratorDto, long refId) {
		String methodName = "insertBrokerCreatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());
			nestedObjInfo.setMemberCode(userInfo.getMemberCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.COLLABORATOR_CREATE);
			pendingData.setCollectionName("collaborators");
			pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
			pendingData.setPendingValue(new Gson().toJson(collaboratorDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_COLLABORATOR_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.COLLABORATOR_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), collaboratorDto.getName()));
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
	
	public void updateCollaborator(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "updateCollaborator";
		try {
			String collaboratorCode = pendingApproval.getPendingData().getQueryValue();
			UpdateCollaboratorDTO collaboratorDto = new Gson().fromJson(pendingApproval.getPendingData().getPendingValue(), UpdateCollaboratorDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_UPDATE_COLLABORATOR,
					ActivityLogService.ACTIVITY_APPROVAL_UPDATE_COLLABORATOR_DESC, collaboratorCode, pendingApproval.getId());

			BasicDBObject collaboratorMember = new BasicDBObject();
			boolean isUserUpdated = false;
			boolean isStatusUpdated = false;
			
			if (Utility.isNotNull(collaboratorDto.getName())) collaboratorMember.append("name", collaboratorDto.getName());
			if (Utility.isNotNull(collaboratorDto.getStatus())) {
				collaboratorMember.append("status", collaboratorDto.getStatus().toUpperCase());
				isStatusUpdated = true;
			}
			if (Utility.isNotNull(collaboratorDto.getNote())) {
				collaboratorMember.append("note", collaboratorDto.getNote());
				collaboratorMember.append("user.note", collaboratorDto.getNote());
				isUserUpdated = true;
			}
			if (Utility.isNotNull(collaboratorDto.getDelegate())) {
				if (Utility.isNotNull(collaboratorDto.getDelegate().getFullName())) {
					collaboratorMember.append("delegate.fullName", collaboratorDto.getDelegate().getFullName());
					collaboratorMember.append("user.fullName", collaboratorDto.getDelegate().getFullName());
					collaboratorMember.append("contact.fullName", collaboratorDto.getDelegate().getFullName());
					isUserUpdated = true;
				}
				if (Utility.isNotNull(collaboratorDto.getDelegate().getBirthDay())) collaboratorMember.append("delegate.birthDay", collaboratorDto.getDelegate().getBirthDay());
				if (Utility.isNotNull(collaboratorDto.getDelegate().getIdentityCard())) collaboratorMember.append("delegate.identityCard", collaboratorDto.getDelegate().getIdentityCard());
				if (Utility.isNotNull(collaboratorDto.getDelegate().getIdCreatedDate()))  collaboratorMember.append("delegate.idCreatedDate", collaboratorDto.getDelegate().getIdCreatedDate());
				if (Utility.isNotNull(collaboratorDto.getDelegate().getIdCreatedLocation()))  collaboratorMember.append("delegate.idCreatedLocation", collaboratorDto.getDelegate().getIdCreatedLocation());
				if (Utility.isNotNull(collaboratorDto.getDelegate().getEmail()))  {
					collaboratorMember.append("delegate.email", collaboratorDto.getDelegate().getEmail());
					collaboratorMember.append("user.email", collaboratorDto.getDelegate().getEmail());
					collaboratorMember.append("contact.email", collaboratorDto.getDelegate().getEmail());
					isUserUpdated = true;
				}
				if (Utility.isNotNull(collaboratorDto.getDelegate().getPhoneNumber()))  {
					collaboratorMember.append("delegate.phoneNumber", collaboratorDto.getDelegate().getPhoneNumber());
					collaboratorMember.append("user.phoneNumber", collaboratorDto.getDelegate().getPhoneNumber());
					collaboratorMember.append("contact.phoneNumber", collaboratorDto.getDelegate().getPhoneNumber());
					isUserUpdated = true;
				}
				if (Utility.isNotNull(collaboratorDto.getDelegate().getAddress()))  collaboratorMember.append("delegate.address", collaboratorDto.getDelegate().getAddress());
				if (Utility.isNotNull(collaboratorDto.getDelegate().getScannedFrontIdCard()))  collaboratorMember.append("delegate.scannedFrontIdCard", collaboratorDto.getDelegate().getScannedFrontIdCard());
				if (Utility.isNotNull(collaboratorDto.getDelegate().getScannedBackIdCard()))  collaboratorMember.append("delegate.scannedBackIdCard", collaboratorDto.getDelegate().getScannedBackIdCard());
				if (Utility.isNotNull(collaboratorDto.getDelegate().getScannedSignature()))  collaboratorMember.append("delegate.scannedSignature", collaboratorDto.getDelegate().getScannedSignature());
			}
			
			if (collaboratorMember.isEmpty()) {
				throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
			} else {
				collaboratorMember.append("lastModifiedUser", Utility.getCurrentUsername());
				collaboratorMember.append("lastModifiedDate", System.currentTimeMillis());
				
				if (isUserUpdated) {
					collaboratorMember.append("user.lastModifiedUser", Utility.getCurrentUsername());
					collaboratorMember.append("user.lastModifiedDate", System.currentTimeMillis());
				}
				
				BasicDBObject query = new BasicDBObject();
				query.append("code", collaboratorCode);
				
				BasicDBObject update = new BasicDBObject();
				update.append("$set", collaboratorMember);
				
				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("collaborators");
				collection.updateOne(query, update);
				
				if (isStatusUpdated) {
					// update status of broker user
					Document collaboratorQuery = new Document();
					collaboratorQuery.append("code", collaboratorCode);
					
					Document updateDoc = new Document();
					updateDoc.append("user.status", collaboratorDto.getStatus().toUpperCase());
					
					Document collaboratorUpdate = new Document();
					collaboratorUpdate.append("$set", updateDoc);
					
					collection.updateMany(collaboratorQuery, collaboratorUpdate);
					
					// update status of all login broker user
					MongoCollection<Document> loginAdmCollection = database.getCollection("login_admin_users");
					
					String collaboratorUsername = Constant.COLLABORATOR_USER_PREFIX + collaboratorCode;
					Document loginAdmQuery = new Document();
					loginAdmQuery.append("username", collaboratorUsername);
					
					Document loginAdmUpdateDoc = new Document();
					loginAdmUpdateDoc.append("status", collaboratorDto.getStatus().toUpperCase());
					
					Document loginAdmUpdate = new Document();
					loginAdmUpdate.append("$set", loginAdmUpdateDoc);
					
					loginAdmCollection.updateMany(loginAdmQuery, loginAdmUpdate);
					
					// logout all users if status is invactive
					if (Constant.STATUS_INACTIVE.equalsIgnoreCase(collaboratorDto.getStatus())) {
						List<String> userList = new ArrayList<String>();
						userList.add(collaboratorUsername);
						Utility.sendHandleLogout(userList, refId);
					}
				}
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void updateCollaboratorPA(HttpServletRequest request, String collaboratorCode, ApprovalUpdateCollaboratorDTO collaboratorDto, long refId) {
		String methodName = "updateCollaboratorPA";
		try {
			if (!collaboratorRepo.existsCollaboratorByCode(collaboratorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertCollaboratorUpdatePA(userInfo, collaboratorCode, collaboratorDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_UPDATE_COLLABORATOR,
					ActivityLogService.ACTIVITY_UPDATE_COLLABORATOR_DESC, collaboratorCode, approvalId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String insertCollaboratorUpdatePA(UserInfoDTO userInfo, String collaboratorCode, ApprovalUpdateCollaboratorDTO collaboratorDto, long refId) {
		String methodName = "insertCollaboratorUpdatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());
			nestedObjInfo.setMemberCode(userInfo.getMemberCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.COLLABORATOR_UPDATE);
			pendingData.setCollectionName("collaborators");
			pendingData.setQueryField("code");
			pendingData.setQueryValue(collaboratorCode);
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setOldValue(new Gson().toJson(collaboratorDto.getOldData()));
			pendingData.setPendingValue(new Gson().toJson(collaboratorDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_COLLABORATOR_UPDATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.COLLABORATOR_UPDATE_NAME);
			pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(),
					collaboratorCode));
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
	
	public CollaboratorDTO getCollaboratorDetail(String collaboratorCode, long refId) {
		String methodName = "getCollaboratorDetail";
		try {
			Document query = new Document();
            query.append("code", collaboratorCode);
            
            MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("collaborators");
			
			Document collaboratorDoc = collection.find(query).first();
			if (collaboratorDoc == null) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			CollaboratorDTO collaboratorDto = mongoTemplate.getConverter().read(CollaboratorDTO.class, collaboratorDoc);
			return collaboratorDto;
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public UserDTO getCollaboratorUserDetail(String collaboratorCode, long refId) {
		String methodName = "getCollaboratorUserDetail";
		try {
            MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("collaborators");
			
			List<? extends Bson> pipeline = Arrays.asList(
                    new Document()
                            .append("$match", new Document()
                                    .append("code", collaboratorCode)
                            ), 
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
			
			UserDTO collaboratorUserDto = mongoTemplate.getConverter().read(UserDTO.class, resultDoc);
			return collaboratorUserDto;
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void createCollaboratorFunctions(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "createCollaboratorFunctions";
		try {
			String collaboratorCode = pendingApproval.getPendingData().getQueryValue();
			FunctionsDTO collaboratorDto = new Gson().fromJson(pendingApproval.getPendingData().getPendingValue(), FunctionsDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_COLLABORATOR_FUNCTIONS,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_COLLABORATOR_FUNCTIONS_DESC, String.valueOf(collaboratorCode),
					pendingApproval.getId());
			
			List<Document> functions = new ArrayList<Document>();
			for (RoleFunction function : collaboratorDto.getFunctions()) {
				Document func = new Document();
				func.append("code", function.getCode());
				func.append("name", function.getName());
				functions.add(func);
			}
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("collaborators");
			
			BasicDBObject query = new BasicDBObject();
			query.append("code", collaboratorCode);
			
			BasicDBObject updateBroker = new BasicDBObject();
			updateBroker.append("functions", functions);
			updateBroker.append("lastModifiedUser", Utility.getCurrentUsername());
			updateBroker.append("lastModifiedDate", System.currentTimeMillis());
			
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
	
	public void createCollaboratorFunctionsPA(HttpServletRequest request, String collaboratorCode, ApprovalFunctionsDTO collaboratorDto,
			long refId) {
		String methodName = "createCollaboratorFunctionsPA";
		try {
			if (!collaboratorRepo.existsCollaboratorByCode(collaboratorCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertCollaboratorFunctionsAssignPA(userInfo, collaboratorCode, collaboratorDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_CREATE_COLLABORATOR_FUNCTIONS,
					ActivityLogService.ACTIVITY_CREATE_COLLABORATOR_FUNCTIONS_DESC, String.valueOf(collaboratorCode),
					approvalId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private String insertCollaboratorFunctionsAssignPA(UserInfoDTO userInfo, String collaboratorCode, ApprovalFunctionsDTO collaboratorDto,
			long refId) {
		String methodName = "insertCollaboratorFunctionsAssignPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());
			nestedObjInfo.setMemberCode(userInfo.getMemberCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.COLLABORATOR_FUNCTIONS_CREATE);
			pendingData.setCollectionName("collaborators");
			pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
			pendingData.setQueryField("code");
			pendingData.setQueryValue(collaboratorCode);
			pendingData.setOldValue(new Gson().toJson(collaboratorDto.getOldData()));
			pendingData.setPendingValue(new Gson().toJson(collaboratorDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_COLLABORATOR_FUNCTIONS_ASSIGN_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.COLLABORATOR_FUNCTIONS_ASSIGN_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), collaboratorCode));
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
}
