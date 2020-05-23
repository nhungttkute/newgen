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
import com.newgen.am.dto.BasePagination;
import com.newgen.am.dto.BrokerCommodity;
import com.newgen.am.dto.BrokerDTO;
import com.newgen.am.dto.CommoditiesDTO;
import com.newgen.am.dto.CommodityFeesDTO;
import com.newgen.am.dto.EmailDTO;
import com.newgen.am.dto.FunctionsDTO;
import com.newgen.am.dto.GeneralFeeDTO;
import com.newgen.am.dto.InvestorDTO;
import com.newgen.am.dto.MarginMultiplierDTO;
import com.newgen.am.dto.MarginRatioAlertDTO;
import com.newgen.am.dto.MemberCSV;
import com.newgen.am.dto.MemberDTO;
import com.newgen.am.dto.OtherFeeDTO;
import com.newgen.am.dto.RiskParametersDTO;
import com.newgen.am.dto.UpdateMemberDTO;
import com.newgen.am.dto.UpdateUserDTO;
import com.newgen.am.dto.UserCSV;
import com.newgen.am.dto.UserDTO;
import com.newgen.am.dto.UserInfoDTO;
import com.newgen.am.dto.UserRolesDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.Commodity;
import com.newgen.am.model.CommodityFee;
import com.newgen.am.model.DBSequence;
import com.newgen.am.model.LoginAdminUser;
import com.newgen.am.model.NestedObjectInfo;
import com.newgen.am.model.PendingApproval;
import com.newgen.am.model.PendingData;
import com.newgen.am.model.RoleFunction;
import com.newgen.am.model.SystemRole;
import com.newgen.am.model.UserRole;
import com.newgen.am.repository.DBSequenceRepository;
import com.newgen.am.repository.LoginAdminUserRepository;
import com.newgen.am.repository.MemberRepository;
import com.newgen.am.repository.PendingApprovalRepository;
import com.newgen.am.repository.SystemRoleRepository;

@Service
public class MemberService {
	private String className = "MemberService";

	@Autowired
	ModelMapper modelMapper;
	
	@Autowired
	MemberRepository memberRepository;
	
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
	private SystemRoleRepository sysRoleRepository;
	
	@Autowired
	private DBSequenceRepository dbSeqRepository;

	@Autowired
	PasswordEncoder passwordEncoder;
	
	public BasePagination<MemberDTO> list(HttpServletRequest request, long refId) {
		String methodName = "list";
		BasePagination<MemberDTO> pagination = null;
		try {
			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "", refId);

			List<? extends Bson> pipeline = Arrays.asList(
                    new Document()
                            .append("$match", searchCriteria.getQuery()), 
                    new Document()
                            .append("$sort", searchCriteria.getSort()), 
                    new Document()
                            .append("$project", new Document()
                            		.append("_id", new Document().append("$toString", "$_id"))
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
			MongoCollection<Document> collection = database.getCollection("members");
			Document resultDoc = collection.aggregate(pipeline).first();
			pagination = mongoTemplate.getConverter().read(BasePagination.class, resultDoc);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return pagination;
	}
	
	public List<MemberCSV> listCsv(HttpServletRequest request, long refId) {
		String methodName = "listCsv";
		List<MemberCSV> memberList = new ArrayList<>();
		try {
			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "", refId);

			List<? extends Bson> pipeline = Arrays.asList(
                    new Document()
                            .append("$match", searchCriteria.getQuery()), 
                    new Document()
                            .append("$sort", searchCriteria.getSort()), 
                    new Document()
                            .append("$project", new Document()
                            		.append("_id", new Document().append("$toString", "$_id"))
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
			MongoCollection<Document> collection = database.getCollection("members");
			MongoCursor<Document> cur = collection.aggregate(pipeline).iterator();
			while (cur.hasNext()) {
				MemberCSV memberCsv = mongoTemplate.getConverter().read(MemberCSV.class, cur.next());
				if (memberCsv != null)
					memberList.add(memberCsv);
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return memberList;
	}
	
	public void createMember(HttpServletRequest request, MemberDTO memberDto, long refId) {
		String methodName = "createMember";
		boolean existedMember= false;
		try {
			existedMember = memberRepository.existsMemberByCode(memberDto.getCode());
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!existedMember) {
			try {
				// get redis user info
				UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
				// insert data to pending_approvals
				String approvalId = insertMemberCreatePA(userInfo, memberDto, refId);
				// send activity log
				activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_MEMBER,
						ActivityLogService.ACTIVITY_CREATE_MEMBER_DESC, memberDto.getCode(), approvalId);

				// create Delegate document
				Document delegate = new Document();
				delegate.append("fullName", memberDto.getCompany().getDelegate().getFullName());
				delegate.append("birthDay", memberDto.getCompany().getDelegate().getBirthDay());
				delegate.append("identityCard", memberDto.getCompany().getDelegate().getIdentityCard());
				delegate.append("idCreatedDate", memberDto.getCompany().getDelegate().getIdCreatedDate());
				delegate.append("idCreatedLocation", memberDto.getCompany().getDelegate().getIdCreatedLocation());
				delegate.append("email", memberDto.getCompany().getDelegate().getEmail());
				delegate.append("phoneNumber", memberDto.getCompany().getDelegate().getPhoneNumber());
				delegate.append("address", memberDto.getCompany().getDelegate().getAddress());
				delegate.append("scannedFrontIdCard", memberDto.getCompany().getDelegate().getScannedFrontIdCard());
				delegate.append("scannedBackIdCard", memberDto.getCompany().getDelegate().getScannedBackIdCard());
				delegate.append("scannedSignature", memberDto.getCompany().getDelegate().getScannedSignature());
				
				// create Company document
				Document company = new Document();
				company.append("name", memberDto.getCompany().getName());
				company.append("taxCode", memberDto.getCompany().getTaxCode());
				company.append("address", memberDto.getCompany().getAddress());
				company.append("phoneNumber", memberDto.getCompany().getPhoneNumber());
				company.append("fax", memberDto.getCompany().getFax());
				company.append("email", memberDto.getCompany().getEmail());
				company.append("delegate", delegate);
				
				// create Contact document
				Document contact = new Document();
				contact.append("fullName", memberDto.getCompany().getDelegate().getFullName());
				contact.append("phoneNumber", memberDto.getCompany().getDelegate().getPhoneNumber());
				contact.append("email", memberDto.getCompany().getDelegate().getEmail());
				
				// create default member role
				SystemRole defaultMemberRole = sysRoleRepository.findByName(Constant.MEMBER_DEFAULT_ROLE);
				if (Utility.isNull(defaultMemberRole)) {
					throw new CustomException(ErrorMessage.DEFAULT_ROLE_DOESNT_EXIST, HttpStatus.OK);
				}
				Document memberRole = new Document();
				memberRole.append("name", defaultMemberRole.getName());
				memberRole.append("description", defaultMemberRole.getDescription());
				
				ObjectId memberId = new ObjectId();
				Document newMember = new Document();
				newMember.append("createdUser", Utility.getCurrentUsername());
				newMember.append("createdDate", System.currentTimeMillis());
				newMember.append("_id", memberId);
				newMember.append("code", memberDto.getCode());
				newMember.append("name", memberDto.getName());
				newMember.append("status", memberDto.getStatus());
				newMember.append("note", memberDto.getNote());
				newMember.append("company", company);
				newMember.append("contact", contact);
				newMember.append("role", memberRole);
				
				// insert new member
				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("members");
				collection.insertOne(newMember);
				
				// insert broker's seq, collaborator's seq
				DBSequence brokerSeq = new DBSequence(Constant.BROKER_SEQ + memberDto.getCode(), 0);
				DBSequence collaboratorSeq = new DBSequence(Constant.COLLABORATOR_SEQ + memberDto.getCode(), 0);
				
				dbSeqRepository.save(brokerSeq);
				dbSeqRepository.save(collaboratorSeq);
				
				// insert new member's master user to login_admin_users
				createMasterMemberUser(request, memberDto, memberDto.getCode(), memberRole, refId);
			} catch (CustomException e) {
				throw e;
			} catch (Exception e) {
				AMLogger.logError(className, methodName, refId, e);
				throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			AMLogger.logMessage(className, methodName, refId, "This member code already exists");
			throw new CustomException(ErrorMessage.DOCUMENT_ALREADY_EXISTED, HttpStatus.OK);
		}
	}
	
	public String insertMemberCreatePA(UserInfoDTO userInfo, MemberDTO memberDto, long refId) {
		String methodName = "insertMemberCreatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_CREATE);
			pendingData.setCollectionName("members");
			pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
			pendingData.setValue(new Gson().toJson(memberDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setId(approvalId);
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), memberDto.getCode()));
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
	
	public void createMasterMemberUser(HttpServletRequest request, MemberDTO memberDto, String memberCode, Document masterUserRole, long refId) {
		String methodName = "createMasterMemberUser";
		boolean existedUser = false;
		String username = Constant.MEMBER_MASTER_USER_PREFIX + memberDto.getCode();
		try {
			existedUser = loginAdmUserRepo.existsLoginAdminUserByUsername(username);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!existedUser) {
			try {
				// get redis user info
				UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
				// send activity log
				activityLogService.sendActivityLog(userInfo, request,
						ActivityLogService.ACTIVITY_CREATE_MEMBER_MASTER_USER,
						ActivityLogService.ACTIVITY_CREATE_MEMBER_MASTER_USER_DESC, username, "");

				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("members");

				Document masterUser = new Document();
				masterUser.append("_id", new ObjectId());
				masterUser.append("username", username);
				masterUser.append("fullName", memberDto.getCompany().getDelegate().getFullName());
				masterUser.append("email", memberDto.getCompany().getDelegate().getEmail());
				masterUser.append("phoneNumber", memberDto.getCompany().getDelegate().getPhoneNumber());
				masterUser.append("status", Constant.STATUS_ACTIVE);
				masterUser.append("note", memberDto.getNote());
				masterUser.append("isPasswordExpiryCheck", false);
				masterUser.append("passwordExpiryDays", 0);
				masterUser.append("expiryAlertDays", 0);
				masterUser.append("createdUser", Utility.getCurrentUsername());
				masterUser.append("createdDate", System.currentTimeMillis());
				masterUser.append("lastModifiedDate", System.currentTimeMillis());
				if (Utility.isNotNull(masterUserRole)) {
					masterUser.append("roles", Arrays.asList(masterUserRole));
				}

				BasicDBObject query = new BasicDBObject();
				query.append("code", memberCode);

				collection.updateOne(query, Updates.addToSet("users", masterUser));

				// insert loginAdminUser
				UserDTO memberUser = new UserDTO();
				memberUser.setUsername(username);
				memberUser.setFullName(memberDto.getCompany().getDelegate().getFullName());
				memberUser.setEmail(memberDto.getCompany().getDelegate().getEmail());
				memberUser.setPhoneNumber(memberDto.getCompany().getDelegate().getPhoneNumber());
				
				String password = Utility.generateRandomPassword();
				String pin = Utility.generateRandomPin();
				createLoginAdminUser(memberCode, memberUser, password, pin, refId);

				// send email
				sendCreateNewUserEmail(memberDto.getCompany().getDelegate().getEmail(), username, password, pin, refId);
			} catch (Exception e) {
				AMLogger.logError(className, methodName, refId, e);
				throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			AMLogger.logMessage(className, methodName, refId, "This username already exists");
			throw new CustomException(ErrorMessage.DOCUMENT_ALREADY_EXISTED, HttpStatus.OK);
		}
	}
	
	private LoginAdminUser createLoginAdminUser(String memberCode, UserDTO memberUserDto, String password, String pin,
			long refId) {
		String methodName = "createLoginAdminUser";
		try {
			LoginAdminUser loginAdmUser = modelMapper.map(memberUserDto, LoginAdminUser.class);
			loginAdmUser.setPassword(passwordEncoder.encode(password));
			loginAdmUser.setPin(passwordEncoder.encode(pin));
			loginAdmUser.setStatus(Constant.STATUS_ACTIVE);
			loginAdmUser.setMemberCode(memberCode);
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
	
	public void updateMember(HttpServletRequest request, String memberCode, UpdateMemberDTO memberDto, long refId) {
		String methodName = "updateMember";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertMemberUpdatePA(userInfo, memberCode, memberDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_UPDATE_MEMBER,
					ActivityLogService.ACTIVITY_UPDATE_MEMBER_DESC, memberCode, approvalId);

			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			BasicDBObject updateMember = new BasicDBObject();
			boolean isUserUpdated = false;
			
			if (Utility.isNotNull(memberDto.getName())) updateMember.append("name", memberDto.getName());
			if (Utility.isNotNull(memberDto.getStatus())) updateMember.append("status", memberDto.getName());
			if (Utility.isNotNull(memberDto.getNote())) {
				updateMember.append("note", memberDto.getNote());
				updateMember.append("users.$.note", memberDto.getNote());
				isUserUpdated = true;
			}
			if (Utility.isNotNull(memberDto.getCompany())) {
				if (Utility.isNotNull(memberDto.getCompany().getName())) updateMember.append("company.name", memberDto.getCompany().getName());
				if (Utility.isNotNull(memberDto.getCompany().getTaxCode())) updateMember.append("company.taxCode", memberDto.getCompany().getTaxCode());
				if (Utility.isNotNull(memberDto.getCompany().getAddress())) updateMember.append("company.address", memberDto.getCompany().getAddress());
				if (Utility.isNotNull(memberDto.getCompany().getPhoneNumber())) updateMember.append("company.phoneNumber", memberDto.getCompany().getPhoneNumber());
				if (Utility.isNotNull(memberDto.getCompany().getFax())) updateMember.append("company.fax", memberDto.getCompany().getFax());
				if (Utility.isNotNull(memberDto.getCompany().getEmail())) updateMember.append("company.email", memberDto.getCompany().getEmail());
				
				if (Utility.isNotNull(memberDto.getCompany().getDelegate())) {
					if (Utility.isNotNull(memberDto.getCompany().getDelegate().getFullName())) {
						updateMember.append("company.delegate.fullName", memberDto.getCompany().getDelegate().getFullName());
						updateMember.append("users.$.fullName", memberDto.getCompany().getDelegate().getFullName());
						updateMember.append("contact.fullName", memberDto.getCompany().getDelegate().getFullName());
						isUserUpdated = true;
					}
					if (Utility.isNotNull(memberDto.getCompany().getDelegate().getBirthDay())) updateMember.append("company.delegate.birthDay", memberDto.getCompany().getDelegate().getBirthDay());
					if (Utility.isNotNull(memberDto.getCompany().getDelegate().getIdentityCard())) updateMember.append("company.delegate.identityCard", memberDto.getCompany().getDelegate().getIdentityCard());
					if (Utility.isNotNull(memberDto.getCompany().getDelegate().getIdCreatedDate()))  updateMember.append("company.delegate.idCreatedDate", memberDto.getCompany().getDelegate().getIdCreatedDate());
					if (Utility.isNotNull(memberDto.getCompany().getDelegate().getIdCreatedLocation()))  updateMember.append("company.delegate.idCreatedLocation", memberDto.getCompany().getDelegate().getIdCreatedLocation());
					if (Utility.isNotNull(memberDto.getCompany().getDelegate().getEmail()))  {
						updateMember.append("company.delegate.email", memberDto.getCompany().getDelegate().getEmail());
						updateMember.append("users.$.email", memberDto.getCompany().getDelegate().getEmail());
						updateMember.append("contact.email", memberDto.getCompany().getDelegate().getEmail());
						isUserUpdated = true;
					}
					if (Utility.isNotNull(memberDto.getCompany().getDelegate().getPhoneNumber()))  {
						updateMember.append("company.delegate.phoneNumber", memberDto.getCompany().getDelegate().getPhoneNumber());
						updateMember.append("users.$.phoneNumber", memberDto.getCompany().getDelegate().getPhoneNumber());
						updateMember.append("contact.phoneNumber", memberDto.getCompany().getDelegate().getPhoneNumber());
						isUserUpdated = true;
					}
					if (Utility.isNotNull(memberDto.getCompany().getDelegate().getAddress()))  updateMember.append("company.delegate.address", memberDto.getCompany().getDelegate().getAddress());
					if (Utility.isNotNull(memberDto.getCompany().getDelegate().getScannedFrontIdCard()))  updateMember.append("company.delegate.scannedFrontIdCard", memberDto.getCompany().getDelegate().getScannedFrontIdCard());
					if (Utility.isNotNull(memberDto.getCompany().getDelegate().getScannedBackIdCard()))  updateMember.append("company.delegate.scannedBackIdCard", memberDto.getCompany().getDelegate().getScannedBackIdCard());
					if (Utility.isNotNull(memberDto.getCompany().getDelegate().getScannedSignature()))  updateMember.append("company.delegate.scannedSignature", memberDto.getCompany().getDelegate().getScannedSignature());
				}
			}
			
			if (updateMember.isEmpty()) {
				throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
			} else {
				updateMember.append("lastModifiedUser", Utility.getCurrentUsername());
				updateMember.append("lastModifiedDate", System.currentTimeMillis());
				
				if (isUserUpdated) {
					updateMember.append("users.$.lastModifiedUser", Utility.getCurrentUsername());
					updateMember.append("users.$.lastModifiedDate", System.currentTimeMillis());
				}
				
				BasicDBObject query = new BasicDBObject();
				query.append("code", memberCode);
				query.append("users.username", Constant.MEMBER_MASTER_USER_PREFIX + memberCode);
				
				BasicDBObject update = new BasicDBObject();
				update.append("$set", updateMember);
				
				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("members");
				collection.updateOne(query, update);
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public String insertMemberUpdatePA(UserInfoDTO userInfo, String memberCode, UpdateMemberDTO memberDto, long refId) {
		String methodName = "insertMemberUpdatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_UPDATE);
			pendingData.setCollectionName("members");
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setValue(new Gson().toJson(memberDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_UPDATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_UPDATE_NAME);
			pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(),
					memberCode));
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

	
	public MemberDTO getMemberDetail(String memberCode, long refId) {
		String methodName = "getMemberDetail";
		try {
			Document query = new Document();
            query.append("code", memberCode);
            
            MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("members");
			
			Document memberDoc = collection.find(query).first();
			if (memberDoc == null) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			MemberDTO memberDto = mongoTemplate.getConverter().read(MemberDTO.class, memberDoc);
			return memberDto;
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public UserDTO getMemberMasterUserDetail(String memberCode, long refId) {
		String methodName = "getMemberMasterUserDetail";
		try {
            MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("members");
			
			List<? extends Bson> pipeline = Arrays.asList(
                    new Document()
                            .append("$match", new Document()
                                    .append("code", memberCode)
                            ), 
                    new Document()
                            .append("$unwind", new Document()
                                    .append("path", "$users")
                            ), 
                    new Document()
                            .append("$match", new Document()
                                    .append("users.username", Constant.MEMBER_MASTER_USER_PREFIX + memberCode)
                            ), 
                    new Document()
                            .append("$project", new Document()
                                    .append("_id", 0.0)
                                    .append("users", 1.0)
                            ), 
                    new Document()
                            .append("$replaceRoot", new Document()
                                    .append("newRoot", "$users")
                            )
            );
			
			Document resultDoc = collection.aggregate(pipeline).first();
			if (resultDoc == null) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			UserDTO memberUserDto = mongoTemplate.getConverter().read(UserDTO.class, resultDoc);
			return memberUserDto;
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void createMemberFunctions(HttpServletRequest request, String memberCode, FunctionsDTO memberDto,
			long refId) {
		String methodName = "createMemberFunctions";
		if (Utility.isNotNull(memberDto.getFunctions()) && memberDto.getFunctions().size() > 0) {
			try {
				// get redis user info
				UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
				// insert data to pending_approvals
				String approvalId = insertMemberFunctionsAssignPA(userInfo, memberCode, memberDto, refId);
				// send activity log
				activityLogService.sendActivityLog(userInfo, request,
						ActivityLogService.ACTIVITY_CREATE_MEMBER_FUNCTIONS,
						ActivityLogService.ACTIVITY_CREATE_MEMBER_FUNCTIONS_DESC, String.valueOf(memberCode),
						approvalId);

				if (!memberRepository.existsMemberByCode(memberCode)) {
					throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
				}
				
				List<Document> functions = new ArrayList<Document>();
				for (RoleFunction function : memberDto.getFunctions()) {
					Document func = new Document();
					func.append("code", function.getCode());
					func.append("name", function.getName());
					functions.add(func);
				}
				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("members");
				
				BasicDBObject query = new BasicDBObject();
				query.append("code", memberCode);
				query.append("users.username", Constant.MEMBER_MASTER_USER_PREFIX + memberCode);
				
				BasicDBObject updateMember = new BasicDBObject();
				updateMember.append("functions", functions);
				updateMember.append("users.$.functions", functions);
				updateMember.append("lastModifiedUser", Utility.getCurrentUsername());
				updateMember.append("lastModifiedDate", System.currentTimeMillis());
				updateMember.append("users.$.lastModifiedUser", Utility.getCurrentUsername());
				updateMember.append("users.$.lastModifiedDate", System.currentTimeMillis());
				
				
				BasicDBObject update = new BasicDBObject();
				update.append("$set", updateMember);
				
				collection.updateOne(query, update);
			} catch (CustomException e) {
				throw e;
			} catch (Exception e) {
				AMLogger.logError(className, methodName, refId, e);
				throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			AMLogger.logMessage(className, methodName, refId, "Invalid input data");
			throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
		}
	}
	
	public String insertMemberFunctionsAssignPA(UserInfoDTO userInfo, String memberCode, FunctionsDTO memberDto,
			long refId) {
		String methodName = "insertMemberFunctionsAssignPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_FUNCTIONS_CREATE);
			pendingData.setCollectionName("members");
			pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setValue(new Gson().toJson(memberDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_FUNCTIONS_ASSIGN_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_FUNCTIONS_ASSIGN_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), memberCode));
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

	public void createMemberCommodities(HttpServletRequest request, String memberCode, CommoditiesDTO memberDto, long refId) {
		String methodName = "createMemberCommodities";
		if (Utility.isNotNull(memberDto.getCommodities()) && memberDto.getCommodities().size() > 0) {
			try {
				// get redis user info
				UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
				// insert data to pending_approvals
				String approvalId = insertMemberCommoditiesAssignPA(userInfo, memberCode, memberDto, refId);
				// send activity log
				activityLogService.sendActivityLog(userInfo, request,
						ActivityLogService.ACTIVITY_CREATE_MEMBER_COMMODITIES_ASSIGN,
						ActivityLogService.ACTIVITY_CREATE_MEMBER_COMMODITIES_ASSIGN_DESC, memberCode,
						approvalId);

				if (!memberRepository.existsMemberByCode(memberCode)) {
					throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
				}
				
				List<Document> commodities = new ArrayList<Document>();
				
				for (Commodity comm : memberDto.getCommodities()) {
					Document commDoc = new Document();
					commDoc.append("commodityCode", comm.getCommodityCode());
					commDoc.append("commodityName", comm.getCommodityName());
					commDoc.append("commodityFee", comm.getCommodityFee());
					commDoc.append("positionLimitType", comm.getPositionLimitType());
					commDoc.append("positionLimit", comm.getPositionLimit());
					commDoc.append("currency", Constant.CURRENCY_VND);
					commodities.add(commDoc);
				}
				
				Document query = new Document();
				query.append("code", memberCode);
				
				Document updateMember = new Document();
				updateMember.append("commodities", commodities);
				updateMember.append("lastModifiedUser", Utility.getCurrentUsername());
				updateMember.append("lastModifiedDate", System.currentTimeMillis());
				
				Document update = new Document();
				update.append("$set", updateMember);
				
				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("members");
				collection.updateOne(query, update);
			} catch (CustomException e) {
				throw e;
			} catch (Exception e) {
				AMLogger.logError(className, methodName, refId, e);
				throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			AMLogger.logMessage(className, methodName, refId, "Invalid input data");
			throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
		}
	}
	
	public String insertMemberCommoditiesAssignPA(UserInfoDTO userInfo, String memberCode, CommoditiesDTO memberDto,
			long refId) {
		String methodName = "insertMemberCommoditiesAssignPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_COMMODITIES_SETTING_CREATE);
			pendingData.setCollectionName("members");
			pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setValue(new Gson().toJson(memberDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_COMMODITIES_ASSIGN_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_FUNCTIONS_ASSIGN_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), memberCode));
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
	
	public void createDefaultSetting(HttpServletRequest request, String memberCode, UpdateMemberDTO memberDto, long refId) {
		String methodName = "createDefaultSetting";
		boolean needUpdateCommodities = false;
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertDefaultSettingPA(userInfo, memberCode, memberDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_MEMBER_DEFAULT_SETTING,
					ActivityLogService.ACTIVITY_CREATE_MEMBER_DEFAULT_SETTING_DESC, memberCode, approvalId);

			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			Document updateDocument = new Document();
			if (memberDto.getOrderLimit() > 0) {
				updateDocument.append("orderLimit", memberDto.getOrderLimit());
			}
			if (memberDto.getDefaultPositionLimit() > 0) {
				needUpdateCommodities = true;
				updateDocument.append("defaultPositionLimit", memberDto.getDefaultPositionLimit());
			}
			if (memberDto.getDefaultCommodityFee() > 0) {
				needUpdateCommodities = true;
				updateDocument.append("defaultCommodityFee", memberDto.getDefaultCommodityFee());
			}
			
			if (updateDocument.isEmpty()) {
				throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
			} else {
				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("members");
				
				Document query = new Document();
				query.append("code", memberCode);
				
				updateDocument.append("lastModifiedUser", Utility.getCurrentUsername());
				updateDocument.append("lastModifiedDate", System.currentTimeMillis());
				
				if (needUpdateCommodities) {
					// update default position litmit and fee for all commodities
					List<Document> newCommodities = new ArrayList<Document>();
					
					Document projection = new Document();
		            projection.append("_id", 0.0);
		            projection.append("commodities", 1.0);
		            
		            Document resultDoc = collection.find(query).projection(projection).first();
		            MemberDTO memberComm = mongoTemplate.getConverter().read(MemberDTO.class, resultDoc);
		            for (Commodity comm : memberComm.getCommodities()) {
		            	Document newComm = new Document();
		            	newComm.append("commodityCode", comm.getCommodityCode());
		            	newComm.append("commodityName", comm.getCommodityCode());
		            	newComm.append("currency", Constant.CURRENCY_VND);
		            	if (memberDto.getDefaultCommodityFee() > 0) {
		            		newComm.append("commodityFee", memberDto.getDefaultCommodityFee());
		            	} else {
		            		newComm.append("commodityFee", comm.getCommodityFee());
		            	}
		            	if (Constant.POSITION_INHERITED.equalsIgnoreCase(comm.getPositionLimitType())) {
		            		if (memberDto.getDefaultPositionLimit() > 0) {
		            			newComm.append("positionLimitType", Constant.POSITION_INHERITED);
		            			newComm.append("positionLimit", memberDto.getDefaultPositionLimit());
		            		} else {
		            			newComm.append("positionLimitType", Constant.POSITION_INHERITED);
		            			newComm.append("positionLimit", comm.getPositionLimit());
		            		}
		            	} else {
		            		newComm.append("positionLimitType", comm.getPositionLimitType());
	            			newComm.append("positionLimit", comm.getPositionLimit());
		            	}
		            	newCommodities.add(newComm);
		            }
		            
		            updateDocument.append("commodities", newCommodities);
				}
				
	            
	            Document update = new Document();
				update.append("$set", updateDocument);
				
				collection.updateOne(query, update);
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public String insertDefaultSettingPA(UserInfoDTO userInfo, String memberCode, UpdateMemberDTO memberDto,
			long refId) {
		String methodName = "insertDefaultSettingPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_DEFAULT_SETTING_CREATE);
			pendingData.setCollectionName("members");
			pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setValue(new Gson().toJson(memberDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_LIMIT_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_LIMIT_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), memberCode));
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
	
	public void setMemberNewPositionOrderLock(HttpServletRequest request, String memberCode, RiskParametersDTO memberDto, long refId) {
		String methodName = "setMemberNewPositionOrderLock";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertMemberNewPositionOrderLockPA(userInfo, memberCode, memberDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_MEMBER_RISK_NEW_POSITION_ORDER_LOCK,
					ActivityLogService.ACTIVITY_CREATE_MEMBER_RISK_NEW_POSITION_ORDER_LOCK_DESC, memberCode, approvalId);

			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			Document updateDocument = new Document();
			updateDocument.append("riskParameters.newPositionOrderLock", memberDto.getRiskParameters().getNewPositionOrderLock());
			updateDocument.append("lastModifiedUser", Utility.getCurrentUsername());
			updateDocument.append("lastModifiedDate", System.currentTimeMillis());
			
			Document query = new Document();
			query.append("code", memberCode);
			
			Document update = new Document();
			update.append("$set", updateDocument);
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("members");
			collection.updateOne(query, update);
			
			// delete redis info (if existed)
			Utility.deleteRedisInfo(template, memberCode, refId);
			
			// insert new redis info
			Document projection = new Document();
			projection.append("_id", 0.0);
			projection.append("riskParameters", 1.0);
			
			Document result = collection.find(query).projection(projection).first();
			RiskParametersDTO riskDto = mongoTemplate.getConverter().read(RiskParametersDTO.class, result);
			Utility.setRedisInfo(template, memberCode, riskDto, refId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public String insertMemberNewPositionOrderLockPA(UserInfoDTO userInfo, String memberCode, RiskParametersDTO memberDto,
			long refId) {
		String methodName = "insertMemberNewPositionOrderLockPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_RISK_NEW_POSITION_LOCK_SET);
			pendingData.setCollectionName("members");
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setValue(new Gson().toJson(memberDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_RISK_NEW_ORDER_LOCK_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_RISK_NEW_ORDER_LOCK_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), memberCode));
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
	
	public void setMemberOrderLock(HttpServletRequest request, String memberCode, RiskParametersDTO memberDto, long refId) {
		String methodName = "setMemberOrderLock";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertMemberNewPositionOrderLockPA(userInfo, memberCode, memberDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_MEMBER_RISK_ORDER_LOCK,
					ActivityLogService.ACTIVITY_CREATE_MEMBER_RISK_ORDER_LOCK_DESC, memberCode, approvalId);

			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			Document updateDocument = new Document();
			updateDocument.append("riskParameters.orderLock", memberDto.getRiskParameters().getOrderLock());
			updateDocument.append("lastModifiedUser", Utility.getCurrentUsername());
			updateDocument.append("lastModifiedDate", System.currentTimeMillis());
			
			Document query = new Document();
			query.append("code", memberCode);
			
			Document update = new Document();
			update.append("$set", updateDocument);
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("members");
			collection.updateOne(query, update);
			

			// delete redis info (if existed)
			Utility.deleteRedisInfo(template, memberCode, refId);
			
			// insert new redis info
			Document projection = new Document();
			projection.append("_id", 0.0);
			projection.append("riskParameters", 1.0);
			
			Document result = collection.find(query).projection(projection).first();
			RiskParametersDTO riskDto = mongoTemplate.getConverter().read(RiskParametersDTO.class, result);
			Utility.setRedisInfo(template, memberCode, riskDto, refId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public String insertMemberOrderLockPA(UserInfoDTO userInfo, String memberCode, RiskParametersDTO memberDto,
			long refId) {
		String methodName = "insertMemberOrderLockPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_RISK_ORDER_LOCK_SET);
			pendingData.setCollectionName("members");
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setValue(new Gson().toJson(memberDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_RISK_ORDER_LOCK_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_RISK_ORDER_LOCK_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), memberCode));
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
	
	public void setMemberMarginWithDrawalLock(HttpServletRequest request, String memberCode, RiskParametersDTO memberDto, long refId) {
		String methodName = "setMemberMarginWithDrawalLock";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertMemberNewPositionOrderLockPA(userInfo, memberCode, memberDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_MEMBER_RISK_MARGIN_WITHDRAWAL,
					ActivityLogService.ACTIVITY_CREATE_MEMBER_RISK_MARGIN_WITHDRAWAL_DESC, memberCode, approvalId);

			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			Document updateDocument = new Document();
			updateDocument.append("riskParameters.marginWithdrawalLock", memberDto.getRiskParameters().getMarginWithdrawalLock());
			updateDocument.append("lastModifiedUser", Utility.getCurrentUsername());
			updateDocument.append("lastModifiedDate", System.currentTimeMillis());
			
			Document query = new Document();
			query.append("code", memberCode);
			
			Document update = new Document();
			update.append("$set", updateDocument);
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("members");
			collection.updateOne(query, update);
			

			// delete redis info (if existed)
			Utility.deleteRedisInfo(template, memberCode, refId);
			
			// insert new redis info
			Document projection = new Document();
			projection.append("_id", 0.0);
			projection.append("riskParameters", 1.0);
			
			Document result = collection.find(query).projection(projection).first();
			RiskParametersDTO riskDto = mongoTemplate.getConverter().read(RiskParametersDTO.class, result);
			Utility.setRedisInfo(template, memberCode, riskDto, refId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public String insertMemberMarginWithDrawalLockPA(UserInfoDTO userInfo, String memberCode, RiskParametersDTO memberDto,
			long refId) {
		String methodName = "insertMemberMarginWithDrawalLockPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_RISK_MARGIN_WITHDRAWAL_LOCK_SET);
			pendingData.setCollectionName("members");
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setValue(new Gson().toJson(memberDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_RISK_MARGIN_WITHDRAWAL_LOCK_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_RISK_MARGIN_WITHDRAWAL_LOCK_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), memberCode));
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
	
	public void setMarginMultiplierBulk(HttpServletRequest request, String memberCode, MarginMultiplierDTO memberDto, long refId) {
		String methodName = "setMarginMultiplierBulk";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertMemberMarginMultiplierBulkPA(userInfo, memberCode, memberDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_MEMBER_MARGIN_MULTIPLIER_BULK,
					ActivityLogService.ACTIVITY_CREATE_MEMBER_MARGIN_MULTIPLIER_BULK_DESC, memberCode, approvalId);

			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			Document memberUpdateDoc = new Document();
			memberUpdateDoc.append("marginMultiplier", memberDto.getMarginMultiplier());
			memberUpdateDoc.append("lastModifiedUser", Utility.getCurrentUsername());
			memberUpdateDoc.append("lastModifiedDate", System.currentTimeMillis());
			
			Document memberQuery = new Document();
			memberQuery.append("code", memberCode);
			
			Document memberUpdate = new Document();
			memberUpdate.append("$set", memberUpdateDoc);
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> memberCollection = database.getCollection("members");
			memberCollection.updateOne(memberQuery, memberUpdate);
			
			BasicDBObject investorQuery = new BasicDBObject();
			investorQuery.append("memberCode", memberCode);
			
			BasicDBObject invUpdateDoc = new BasicDBObject();
			invUpdateDoc.append("marginMultiplier", memberDto.getMarginMultiplier());
			invUpdateDoc.append("lastModifiedUser", Utility.getCurrentUsername());
			invUpdateDoc.append("lastModifiedDate", System.currentTimeMillis());
			
			BasicDBObject invUpdate = new BasicDBObject();
			invUpdate.append("$set", invUpdateDoc);

			MongoCollection<Document> invCollection = database.getCollection("investors");
			invCollection.updateMany(investorQuery, invUpdate);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public String insertMemberMarginMultiplierBulkPA(UserInfoDTO userInfo, String memberCode, MarginMultiplierDTO memberDto,
			long refId) {
		String methodName = "insertMemberMarginWithDrawalLockPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_MARGIN_MULTIPLIER_BULK_CREATE);
			pendingData.setCollectionName("members");
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setValue(new Gson().toJson(memberDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_MARGIN_MULTIPLIER_BULK_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_MARGIN_MULTIPLIER_BULK_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), memberCode));
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
	
	public void setMarginRatioAlertBulk(HttpServletRequest request, String memberCode, MarginRatioAlertDTO memberDto, long refId) {
		String methodName = "setMarginRatioAlertBulk";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertMarginRatioAlertBulkPA(userInfo, memberCode, memberDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_MEMBER_MARGIN_RATIO_BULK,
					ActivityLogService.ACTIVITY_CREATE_MEMBER_MARGIN_RATIO_BULK_DESC, memberCode, approvalId);

			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			boolean isValidRatio = true;
			if (memberDto.getMarginRatioAlert().getFinalizationRatio() > memberDto.getMarginRatioAlert().getCancelOrderRatio() || (memberDto.getMarginRatioAlert().getFinalizationRatio() > memberDto.getMarginRatioAlert().getWarningRatio())) {
				isValidRatio = false;
			} else if (memberDto.getMarginRatioAlert().getCancelOrderRatio() > memberDto.getMarginRatioAlert().getWarningRatio()) {
				isValidRatio = false;
			}
			 
			if (!isValidRatio) {
				throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
			}
			
			Document marginRatioAlert = new Document();
			marginRatioAlert.append("warningRatio", memberDto.getMarginRatioAlert().getWarningRatio());
			marginRatioAlert.append("cancelOrderRatio", memberDto.getMarginRatioAlert().getCancelOrderRatio());
			marginRatioAlert.append("finalizationRatio", memberDto.getMarginRatioAlert().getFinalizationRatio());
			
			Document memberUpdateDoc = new Document();
			memberUpdateDoc.append("marginRatioAlert", marginRatioAlert);
			memberUpdateDoc.append("lastModifiedUser", Utility.getCurrentUsername());
			memberUpdateDoc.append("lastModifiedDate", System.currentTimeMillis());
			
			Document memberQuery = new Document();
			memberQuery.append("code", memberCode);
			
			Document memberUpdate = new Document();
			memberUpdate.append("$set", memberUpdateDoc);
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> memberCollection = database.getCollection("members");
			memberCollection.updateOne(memberQuery, memberUpdate);
			
			BasicDBObject investorQuery = new BasicDBObject();
			investorQuery.append("memberCode", memberCode);
			
			BasicDBObject invUpdateDoc = new BasicDBObject();
			invUpdateDoc.append("marginRatioAlert", marginRatioAlert);
			invUpdateDoc.append("lastModifiedUser", Utility.getCurrentUsername());
			invUpdateDoc.append("lastModifiedDate", System.currentTimeMillis());
			
			BasicDBObject invUpdate = new BasicDBObject();
			invUpdate.append("$set", invUpdateDoc);

			MongoCollection<Document> invCollection = database.getCollection("investors");
			invCollection.updateMany(investorQuery, invUpdate);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public String insertMarginRatioAlertBulkPA(UserInfoDTO userInfo, String memberCode, MarginRatioAlertDTO memberDto,
			long refId) {
		String methodName = "insertMarginRatioAlertBulkPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_MARGIN_RATIO_BULK_CREATE);
			pendingData.setCollectionName("members");
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setValue(new Gson().toJson(memberDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_MARGIN_RATIO_BULK_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_MARGIN_RATIO_BULK_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), memberCode));
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
	
	public void setGeneralFeeBulk(HttpServletRequest request, String memberCode, GeneralFeeDTO memberDto, long refId) {
		String methodName = "setGeneralFeeBulk";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertGeneralFeeBulkPA(userInfo, memberCode, memberDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_MEMBER_GENERAL_FEE_BULK,
					ActivityLogService.ACTIVITY_CREATE_MEMBER_GENERAL_FEE_BULK_DESC, memberCode, approvalId);

			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			Document memberUpdateDoc = new Document();
			memberUpdateDoc.append("generalFee", memberDto.getGeneralFee());
			memberUpdateDoc.append("lastModifiedUser", Utility.getCurrentUsername());
			memberUpdateDoc.append("lastModifiedDate", System.currentTimeMillis());
			
			Document memberQuery = new Document();
			memberQuery.append("code", memberCode);
			
			Document memberUpdate = new Document();
			memberUpdate.append("$set", memberUpdateDoc);
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> memberCollection = database.getCollection("members");
			memberCollection.updateOne(memberQuery, memberUpdate);
			
			BasicDBObject investorQuery = new BasicDBObject();
			investorQuery.append("memberCode", memberCode);
			
			BasicDBObject invUpdateDoc = new BasicDBObject();
			invUpdateDoc.append("generalFee", memberDto.getGeneralFee());
			invUpdateDoc.append("lastModifiedUser", Utility.getCurrentUsername());
			invUpdateDoc.append("lastModifiedDate", System.currentTimeMillis());
			
			BasicDBObject invUpdate = new BasicDBObject();
			invUpdate.append("$set", invUpdateDoc);

			MongoCollection<Document> invCollection = database.getCollection("investors");
			invCollection.updateMany(investorQuery, invUpdate);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public String insertGeneralFeeBulkPA(UserInfoDTO userInfo, String memberCode, GeneralFeeDTO memberDto,
			long refId) {
		String methodName = "insertGeneralFeeBulkPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_GENERAL_FEE_BULK_CREATE);
			pendingData.setCollectionName("members");
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setValue(new Gson().toJson(memberDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_GENERAL_FEE_BULK_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_GENERAL_FEE_BULK_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), memberCode));
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
	
	public void setOtherFeeBulk(HttpServletRequest request, String memberCode, OtherFeeDTO memberDto, long refId) {
		String methodName = "setOtherFeeBulk";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertOtherFeeBulkPA(userInfo, memberCode, memberDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_MEMBER_OTHER_FEE_BULK,
					ActivityLogService.ACTIVITY_CREATE_MEMBER_OTHER_FEE_BULK_DESC, memberCode, approvalId);

			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			Document memberUpdateDoc = new Document();
			memberUpdateDoc.append("otherFee", memberDto.getOtherFee());
			memberUpdateDoc.append("lastModifiedUser", Utility.getCurrentUsername());
			memberUpdateDoc.append("lastModifiedDate", System.currentTimeMillis());
			
			Document memberQuery = new Document();
			memberQuery.append("code", memberCode);
			
			Document memberUpdate = new Document();
			memberUpdate.append("$set", memberUpdateDoc);
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> memberCollection = database.getCollection("members");
			memberCollection.updateOne(memberQuery, memberUpdate);
			
			BasicDBObject investorQuery = new BasicDBObject();
			investorQuery.append("memberCode", memberCode);
			
			BasicDBObject invUpdateDoc = new BasicDBObject();
			invUpdateDoc.append("otherFee", memberDto.getOtherFee());
			invUpdateDoc.append("lastModifiedUser", Utility.getCurrentUsername());
			invUpdateDoc.append("lastModifiedDate", System.currentTimeMillis());
			
			BasicDBObject invUpdate = new BasicDBObject();
			invUpdate.append("$set", invUpdateDoc);

			MongoCollection<Document> invCollection = database.getCollection("investors");
			invCollection.updateMany(investorQuery, invUpdate);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public String insertOtherFeeBulkPA(UserInfoDTO userInfo, String memberCode, OtherFeeDTO memberDto,
			long refId) {
		String methodName = "insertOtherFeeBulkPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_OTHER_FEE_BULK_CREATE);
			pendingData.setCollectionName("members");
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setValue(new Gson().toJson(memberDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_OTHER_FEE_BULK_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_OTHER_FEE_BULK_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), memberCode));
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
	
	public void setBrokerCommoditiesFeeBulk(HttpServletRequest request, String memberCode, CommodityFeesDTO memberDto, long refId) {
		String methodName = "setBrokerCommoditiesFeeBulk";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertBrokerCommoditiesFeeBulkPA(userInfo, memberCode, memberDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_MEMBER_COMMODITIES_FEE_BULK,
					ActivityLogService.ACTIVITY_CREATE_MEMBER_COMMODITIES_FEE_BULK_DESC, memberCode, approvalId);

			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			// save commodityFees for member
			MongoCollection<Document> memberCollection = database.getCollection("members");
			Document memberQuery = new Document();
			memberQuery.append("code", memberCode);
			
			List<Document> commFees = new ArrayList<Document>();
			for (CommodityFee commFee : memberDto.getCommodityFees()) {
				Document commFeeDoc = new Document();
				commFeeDoc.append("commodityCode", commFee.getCommodityCode());
				commFeeDoc.append("commodityName", commFee.getCommodityName());
				commFeeDoc.append("brokerCommodityFee", commFee.getBrokerCommodityFee());
				commFeeDoc.append("investorCommodityFee", commFee.getInvestorCommodityFee());
				commFees.add(commFeeDoc);
			}
			memberCollection.updateOne(memberQuery, Updates.set("commodityFees", commFees));
			
			// save commodityFees for broker
			MongoCollection<Document> brokerCollection = database.getCollection("brokers");
			
			Document brokerQuery = new Document();
			brokerQuery.append("memberCode", memberCode);
			
			Document projection = new Document();
            projection.append("_id", 0.0);
            projection.append("code", 1.0);
            projection.append("commodities", 1.0);
			
			try (MongoCursor<Document> cur = brokerCollection.find(brokerQuery).projection(projection).iterator()) {

				while (cur.hasNext()) {
					BrokerDTO brokerDto = mongoTemplate.getConverter().read(BrokerDTO.class, cur.next());
					
					if (brokerDto != null && brokerDto.getCommodities() != null && brokerDto.getCommodities().size() > 0) {
						List<Document> newBrokerCommodities = new ArrayList<Document>();
						for (BrokerCommodity comm : brokerDto.getCommodities()) {
							Document newCommDoc = new Document();
							newCommDoc.append("commodityCode", comm.getCommodityCode());
							newCommDoc.append("commodityName", comm.getCommodityName());
							
							CommodityFee newComm = getCommodityInAListByCode(memberDto.getCommodityFees(), comm.getCommodityCode());
							if (Utility.isNotNull(newComm)) {
								newCommDoc.append("commodityFee", newComm.getBrokerCommodityFee());
							} else {
								newCommDoc.append("commodityFee", comm.getCommodityFee());
							}
							
							newBrokerCommodities.add(newCommDoc);
						}
						
						Document query = new Document();
						query.append("code", brokerDto.getCode());
						
						Document updateDocument = new Document();
						updateDocument.append("commodities", newBrokerCommodities);
						updateDocument.append("lastModifiedUser", Utility.getCurrentUsername());
						updateDocument.append("lastModifiedDate", System.currentTimeMillis());
						
						Document brokerUpdate = new Document();
						brokerUpdate.append("$set", updateDocument);
						
						brokerCollection.updateOne(query, brokerUpdate);
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
	
	private CommodityFee getCommodityInAListByCode(List<CommodityFee> commodities, String code) {
		for (CommodityFee comm : commodities) {
			if (code.equals(comm.getCommodityCode())) return comm;
		}
		return null;
	}
	
	public String insertBrokerCommoditiesFeeBulkPA(UserInfoDTO userInfo, String memberCode, CommodityFeesDTO memberDto,
			long refId) {
		String methodName = "insertBrokerCommoditiesFeeBulkPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_BROKER_COMMODITY_FEE_BULK_CREATE);
			pendingData.setCollectionName("members");
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setValue(new Gson().toJson(memberDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_COMMODITIES_FEE_BULK_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_COMMODITIES_FEE_BULK_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), memberCode));
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
	
	public void setInvestorCommoditiesFeeBulk(HttpServletRequest request, String memberCode, CommodityFeesDTO memberDto, long refId) {
		String methodName = "setInvestorCommoditiesFeeBulk";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertBrokerCommoditiesFeeBulkPA(userInfo, memberCode, memberDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_MEMBER_COMMODITIES_FEE_BULK,
					ActivityLogService.ACTIVITY_CREATE_MEMBER_COMMODITIES_FEE_BULK_DESC, memberCode, approvalId);

			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			
			// save commodityFees for member
			MongoCollection<Document> memberCollection = database.getCollection("members");
			Document memberQuery = new Document();
			memberQuery.append("code", memberCode);
			
			List<Document> commFees = new ArrayList<Document>();
			for (CommodityFee commFee : memberDto.getCommodityFees()) {
				Document commFeeDoc = new Document();
				commFeeDoc.append("commodityCode", commFee.getCommodityCode());
				commFeeDoc.append("commodityName", commFee.getCommodityName());
				commFeeDoc.append("brokerCommodityFee", commFee.getBrokerCommodityFee());
				commFeeDoc.append("investorCommodityFee", commFee.getInvestorCommodityFee());
				commFees.add(commFeeDoc);
			}
			memberCollection.updateOne(memberQuery, Updates.set("commodityFees", commFees));
			
			// save commodityFees for investors
			MongoCollection<Document> investorCollection = database.getCollection("investors");
			
			Document investorQuery = new Document();
			investorQuery.append("memberCode", memberCode);
			
			Document projection = new Document();
            projection.append("_id", 0.0);
            projection.append("investorCode", 1.0);
            projection.append("commodities", 1.0);
			
			try (MongoCursor<Document> cur = investorCollection.find(investorQuery).projection(projection).iterator()) {

				while (cur.hasNext()) {
					InvestorDTO investorDto = mongoTemplate.getConverter().read(InvestorDTO.class, cur.next());
					if (investorDto != null && investorDto.getCommodities() != null && investorDto.getCommodities().size() > 0) {
						List<Document> newInvestorCommodities = new ArrayList<Document>();
						for (Commodity comm : investorDto.getCommodities()) {
							Document newCommDoc = new Document();
							newCommDoc.append("commodityCode", comm.getCommodityCode());
							newCommDoc.append("commodityName", comm.getCommodityName());
							newCommDoc.append("positionLimitType", comm.getPositionLimitType());
							newCommDoc.append("positionLimit", comm.getPositionLimit());
							
							CommodityFee newComm = getCommodityInAListByCode(memberDto.getCommodityFees(), comm.getCommodityCode());
							if (Utility.isNotNull(newComm)) {
								newCommDoc.append("commodityFee", newComm.getInvestorCommodityFee());
							} else {
								newCommDoc.append("commodityFee", comm.getCommodityFee());
							}
							
							newInvestorCommodities.add(newCommDoc);
						}
						
						Document query = new Document();
						query.append("investorCode", investorDto.getInvestorCode());
						
						Document updateDocument = new Document();
						updateDocument.append("commodities", newInvestorCommodities);
						updateDocument.append("lastModifiedUser", Utility.getCurrentUsername());
						updateDocument.append("lastModifiedDate", System.currentTimeMillis());
						
						Document update = new Document();
						update.append("$set", updateDocument);
						
						investorCollection.updateOne(query, update);
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
	
	public String insertInvestorCommoditiesFeeBulkPA(UserInfoDTO userInfo, String memberCode, CommoditiesDTO memberDto,
			long refId) {
		String methodName = "insertInvestorCommoditiesFeeBulkPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_INVESTOR_COMMODITY_FEE_BULK_CREATE);
			pendingData.setCollectionName("members");
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setValue(new Gson().toJson(memberDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_COMMODITIES_FEE_BULK_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_COMMODITIES_FEE_BULK_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), memberCode));
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
	
	public BasePagination<UserDTO> listMemberUsers(HttpServletRequest request, String memberCode, long refId) {
		String methodName = "listMemberUsers";
		BasePagination<UserDTO> pagination = null;
		try {
			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			Document query1 = new Document();
			query1.append("code", memberCode);

			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "users.", refId);

			List<? extends Bson> pipeline = Arrays.asList(
                    new Document()
                            .append("$match", query1), 
                    new Document()
                            .append("$unwind", new Document()
                                    .append("path", "$users")
                            ), 
                    new Document()
                            .append("$match", searchCriteria.getQuery()), 
                    new Document()
                            .append("$sort", searchCriteria.getSort()), 
                    new Document()
                            .append("$project", new Document()
                                    .append("_id", new Document()
                                            .append("$toString", "$users._id")
                                    )
                                    .append("username", "$users.username")
                                    .append("fullName", "$users.fullName")
                                    .append("email", "$users.email")
                                    .append("phoneNumber", "$users.phoneNumber")
                                    .append("status", "$users.status")
                                    .append("isPasswordExpiryCheck", "$users.isPasswordExpiryCheck")
                                    .append("passwordExpiryDays", "$users.passwordExpiryDays")
                                    .append("expiryAlertDays", "$users.expiryAlertDays")
                                    .append("createdDate", "$users.createdDate")
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
			MongoCollection<Document> collection = database.getCollection("members");
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
	
	public List<UserCSV> listMemberUsersCsv(HttpServletRequest request, String memberCode, long refId) {
		String methodName = "listMemberUsersCsv";
		List<UserCSV> userList = new ArrayList<>();
		try {
			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			Document query1 = new Document();
			query1.append("code", memberCode);

			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "users.", refId);

			List<? extends Bson> pipeline = Arrays.asList(
                    new Document()
                            .append("$match", query1), 
                    new Document()
                            .append("$unwind", new Document()
                                    .append("path", "$users")
                            ), 
                    new Document()
                            .append("$match", searchCriteria.getQuery()), 
                    new Document()
                            .append("$sort", searchCriteria.getSort()), 
                    new Document()
                            .append("$project", new Document()
                                    .append("_id", new Document()
                                            .append("$toString", "$users._id")
                                    )
                                    .append("username", "$users.username")
                                    .append("fullName", "$users.fullName")
                                    .append("email", "$users.email")
                                    .append("phoneNumber", "$users.phoneNumber")
                                    .append("status", "$users.status")
                                    .append("isPasswordExpiryCheck", "$users.isPasswordExpiryCheck")
                                    .append("passwordExpiryDays", "$users.passwordExpiryDays")
                                    .append("expiryAlertDays", "$users.expiryAlertDays")
                                    .append("createdDate", new Document().append("$dateToString",
											new Document().append("format", "%d/%m/%Y %H:%M:%S").append("date",
													new Document().append("$toDate", "$users.createdDate"))))
                            )
            );

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("members");
			try (MongoCursor<Document> cur = collection.aggregate(pipeline).iterator()) {

				while (cur.hasNext()) {
					UserCSV userCSV = mongoTemplate.getConverter().read(UserCSV.class, cur.next());
					if (userCSV != null)
						userList.add(userCSV);
				}
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return userList;
	}
	
	public void createMemberUser(HttpServletRequest request, String memberCode, UserDTO memberUserDto, long refId) {
		String methodName = "createMemberUser";
		boolean existedUser = false;
		try {
			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			existedUser = loginAdmUserRepo.existsLoginAdminUserByUsername(memberUserDto.getUsername());
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!existedUser) {
			try {
				// get redis user info
				UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
				// insert data to pending_approvals
				String approvalId = insertMemberUserCreatePA(userInfo, memberCode, memberUserDto, refId);
				// send activity log
				activityLogService.sendActivityLog2(userInfo, request,
						ActivityLogService.ACTIVITY_CREATE_MEMBER_USER,
						ActivityLogService.ACTIVITY_CREATE_MEMBER_USER_DESC, memberUserDto.getUsername(), memberCode, approvalId);

				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("members");

				Document newDeptUser = new Document();
				newDeptUser.append("_id", new ObjectId());
				newDeptUser.append("username", memberUserDto.getUsername());
				newDeptUser.append("fullName", memberUserDto.getFullName());
				newDeptUser.append("email", memberUserDto.getEmail());
				newDeptUser.append("phoneNumber", memberUserDto.getPhoneNumber());
				newDeptUser.append("status", Constant.STATUS_ACTIVE);
				newDeptUser.append("note", memberUserDto.getNote());
				newDeptUser.append("isPasswordExpiryCheck", memberUserDto.getIsPasswordExpiryCheck());
				newDeptUser.append("passwordExpiryDays", memberUserDto.getPasswordExpiryDays());
				newDeptUser.append("expiryAlertDays", memberUserDto.getExpiryAlertDays());
				newDeptUser.append("createdUser", Utility.getCurrentUsername());
				newDeptUser.append("createdDate", System.currentTimeMillis());

				BasicDBObject query = new BasicDBObject();
				query.append("code", memberCode);

				collection.updateOne(query, Updates.addToSet("users", newDeptUser));

				// insert loginAdminUser
				String password = Utility.generateRandomPassword();
				String pin = Utility.generateRandomPin();
				LoginAdminUser newLoginAdmUser = createLoginAdminUser(memberCode, memberUserDto, password, pin, refId);

				// send email
				sendCreateNewUserEmail(memberUserDto.getEmail(), newLoginAdmUser.getUsername(), password, pin, refId);
			} catch (Exception e) {
				AMLogger.logError(className, methodName, refId, e);
				throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			AMLogger.logMessage(className, methodName, refId, "This username already exists");
			throw new CustomException(ErrorMessage.DOCUMENT_ALREADY_EXISTED, HttpStatus.OK);
		}
	}
	
	public String insertMemberUserCreatePA(UserInfoDTO userInfo, String memberCode, UserDTO memberUserDto,
			long refId) {
		String methodName = "insertMemberUserCreatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setMemberCode(memberCode);

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_USER_CREATE);
			pendingData.setCollectionName("members");
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
			pendingData.setValue(new Gson().toJson(memberUserDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_USER_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_USER_CREATE_NAME);
			pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription2(SystemFunctionCode.MEMBER_USER_CREATE_DESC, memberUserDto.getUsername(),
					memberCode));
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
	
	public void updateMemberUser(HttpServletRequest request, String memberCode, String username,
			UpdateUserDTO userDto, long refId) {
		String methodName = "updateMemberUser";
		try {
			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertMemberUserUpdatePA(userInfo, memberCode, username, userDto, refId);
			// send activity log
			activityLogService.sendActivityLog2(userInfo, request, ActivityLogService.ACTIVITY_UPDATE_MEMBER_USER,
					ActivityLogService.ACTIVITY_UPDATE_MEMBER_USER_DESC, username, memberCode, approvalId);

			BasicDBObject newDocument = new BasicDBObject();

			if (Utility.isNotNull(userDto.getFullName()))
				newDocument.append("users.$.fullName", userDto.getFullName());
			if (Utility.isNotNull(userDto.getPhoneNumber()))
				newDocument.append("users.$.phoneNumber", userDto.getPhoneNumber());
			if (Utility.isNotNull(userDto.getEmail()))
				newDocument.append("users.$.email", userDto.getEmail());
			if (Utility.isNotNull(userDto.getStatus()))
				newDocument.append("users.$.status", userDto.getStatus());
			if (Utility.isNotNull(userDto.getNote()))
				newDocument.append("users.$.note", userDto.getNote());
			if (Utility.isNotNull(userDto.getIsPasswordExpiryCheck()))
				newDocument.append("users.$.isPasswordExpiryCheck", userDto.getIsPasswordExpiryCheck());
			if (Utility.isNotNull(userDto.getPasswordExpiryDays()) && userDto.getPasswordExpiryDays() > 0)
				newDocument.append("users.$.passwordExpiryDays", userDto.getPasswordExpiryDays());
			if (Utility.isNotNull(userDto.getExpiryAlertDays()) && userDto.getExpiryAlertDays() > 0)
				newDocument.append("users.$.expiryAlertDays", userDto.getExpiryAlertDays());

			if (newDocument.isEmpty()) {
				throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
			} else {
				newDocument.append("users.$.lastModifiedUser", Utility.getCurrentUsername());
				newDocument.append("users.$.lastModifiedDate", System.currentTimeMillis());

				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("members");

				BasicDBObject query = new BasicDBObject();
				query.append("code", memberCode);
				query.append("users.username", username);
				
				BasicDBObject update = new BasicDBObject();
				update.append("$set", newDocument);

				collection.updateOne(query, update);
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public String insertMemberUserUpdatePA(UserInfoDTO userInfo, String memberCode, String username,
			UpdateUserDTO userDto, long refId) {
		String methodName = "insertMemberUserUpdatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setMemberCode(memberCode);

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.DEPARTMENT_USER_UPDATE);
			pendingData.setCollectionName("members");
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setQueryField2("users.username");
			pendingData.setQueryValue2(username);
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setValue(new Gson().toJson(userDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_USER_UPDATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_USER_UPDATE_NAME);
			pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription2(SystemFunctionCode.MEMBER_USER_CREATE_DESC, username,
					memberCode));
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
	
	public UserDTO getMemberUser(String memberCode, String username, long refId) {
		String methodName = "getMemberUser";
		try {
			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("members");

			List<? extends Bson> pipeline = Arrays.asList(
					new Document().append("$match", new Document().append("code", memberCode)),
					new Document().append("$unwind", new Document().append("path", "$users")),
					new Document().append("$match", new Document().append("users.username", username)),
					new Document().append("$project", new Document().append("_id", 0.0).append("users", 1.0)),
					new Document().append("$replaceRoot", new Document().append("newRoot", "$users")));

			Document resultDoc = collection.aggregate(pipeline).first();
//			System.out.println("resultDoc: " + resultDoc);
			UserDTO memberUserDto = mongoTemplate.getConverter().read(UserDTO.class, resultDoc);
			return memberUserDto;
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void saveMemberUserRoles(HttpServletRequest request, String memberCode, String username,
			UserRolesDTO userDto, long refId) {
		String methodName = "saveMemberUserRoles";
		if (Utility.isNotNull(userDto.getRoles()) && userDto.getRoles().size() > 0) {
			try {
				if (!memberRepository.existsMemberByCode(memberCode)) {
					throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
				}
				
				// get redis user info
				UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
				// insert data to pending_approvals
				String approvalId = insertMemberUserRoleCreatePA(userInfo, memberCode, username, userDto, refId);
				// send activity log
				activityLogService.sendActivityLog2(userInfo, request,
						ActivityLogService.ACTIVITY_CREATE_MEMBER_USER_ROLES,
						ActivityLogService.ACTIVITY_CREATE_MEMBER_USER_ROLES_DESC, username, memberCode,
						approvalId);

				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("members");
				
				List<Document> roles = new ArrayList<Document>();
				for (UserRole role : userDto.getRoles()) {
					Document roleDoc = new Document();
					roleDoc.append("name", role.getName());
					roleDoc.append("description", role.getDescription());
					roleDoc.append("status", role.getStatus());
					roles.add(roleDoc);
				}
				BasicDBObject query = new BasicDBObject();
				query.append("code", memberCode);
				query.append("users", new BasicDBObject("$elemMatch", new BasicDBObject("username", username)));
				
				BasicDBObject newDocument = new BasicDBObject();
				newDocument.append("users.$.roles", roles);
				newDocument.append("users.$.lastModifiedUser", Utility.getCurrentUsername());
				newDocument.append("users.$.lastModifiedDate", System.currentTimeMillis());
				
				BasicDBObject update = new BasicDBObject();
				update.append("$set", newDocument);
				
				collection.updateOne(query, update);
			} catch (CustomException e) {
				throw e;
			} catch (Exception e) {
				AMLogger.logError(className, methodName, refId, e);
				throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			AMLogger.logMessage(className, methodName, refId, "Invalid input data");
			throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
		}
	}

	public String insertMemberUserRoleCreatePA(UserInfoDTO userInfo, String memberCode, String username,
			UserRolesDTO userDto, long refId) {
		String methodName = "insertMemberUserRoleCreatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setMemberCode(memberCode);

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_USER_ROLES_CREATE);
			pendingData.setCollectionName("members");
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setQueryField2("users.username");
			pendingData.setQueryValue2(username);
			pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
			pendingData.setValue(new Gson().toJson(userDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_USER_ROLES_ASSIGN_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_USER_ROLES_ASSIGN_CREATE_NAME);
			pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription2(SystemFunctionCode.MEMBER_USER_ROLES_ASSIGN_CREATE_DESC,
					username, memberCode));
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

	public void saveMemberUserFunctions(HttpServletRequest request, String memberCode, String username,
			FunctionsDTO userDto, long refId) {
		String methodName = "saveMemberUserFunctions";
		if (Utility.isNotNull(userDto.getFunctions()) && userDto.getFunctions().size() > 0) {
			try {
				if (!memberRepository.existsMemberByCode(memberCode)) {
					throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
				}
				
				// get redis user info
				UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
				// insert data to pending_approvals
				String approvalId = insertMemberUserFunctionsCreatePA(userInfo, memberCode, username, userDto, refId);
				// send activity log
				activityLogService.sendActivityLog2(userInfo, request,
						ActivityLogService.ACTIVITY_CREATE_MEMBER_USER_FUNCTIONS,
						ActivityLogService.ACTIVITY_CREATE_MEMBER_USER_FUNCTIONS_DESC, username, memberCode,
						approvalId);

				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("members");
				
				List<Document> functions = new ArrayList<Document>();
				for (RoleFunction function : userDto.getFunctions()) {
					Document funcDoc = new Document();
					funcDoc.append("code", function.getCode());
					funcDoc.append("name", function.getName());
					functions.add(funcDoc);
				}
				BasicDBObject query = new BasicDBObject();
				query.append("code", memberCode);
				query.append("users", new BasicDBObject("$elemMatch", new BasicDBObject("username", username)));
				
				BasicDBObject newDocument = new BasicDBObject();
				newDocument.append("users.$.functions", functions);
				newDocument.append("users.$.lastModifiedUser", Utility.getCurrentUsername());
				newDocument.append("users.$.lastModifiedDate", System.currentTimeMillis());
				
				BasicDBObject update = new BasicDBObject();
				update.append("$set", newDocument);
				
				collection.updateOne(query, update);
				
			} catch (CustomException e) {
				throw e;
			} catch (Exception e) {
				AMLogger.logError(className, methodName, refId, e);
				throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			AMLogger.logMessage(className, methodName, refId, "Invalid input data");
			throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
		}
	}

	public String insertMemberUserFunctionsCreatePA(UserInfoDTO userInfo, String memberCode, String username,
			FunctionsDTO userDto, long refId) {
		String methodName = "insertMemberUserFunctionsCreatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setMemberCode(memberCode);

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_USER_FUNCTIONS_CREATE);
			pendingData.setCollectionName("members");
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setQueryField2("users.username");
			pendingData.setQueryValue2(username);
			pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
			pendingData.setValue(new Gson().toJson(userDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_USER_FUNCTIONS_ASSIGN_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_USER_FUNCTIONS_ASSIGN_CREATE_NAME);
			pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription2(SystemFunctionCode.MEMBER_USER_FUNCTIONS_ASSIGN_CREATE_DESC,
					username, memberCode));
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
	
	public List<RoleFunction> getMemberFunctions(String memberCode, long refId) {
		String methodName = "getMemberFunctions";
		try {
			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("members");
			
			List<? extends Bson> pipeline = Arrays.asList(
                    new Document()
                            .append("$match", new Document()
                                    .append("code", memberCode)
                            ), 
                    new Document()
                            .append("$lookup", new Document()
                                    .append("from", "system_roles")
                                    .append("localField", "role.name")
                                    .append("foreignField", "name")
                                    .append("as", "roleObj")
                            ), 
                    new Document()
                            .append("$project", new Document()
                                    .append("_id", 0.0)
                                    .append("roleFunctions", "$roleObj.functions")
                                    .append("specificFunctions", "$functions")
                            ), 
                    new Document()
                            .append("$unwind", new Document()
                                    .append("path", "$roleFunctions")
                            ), 
                    new Document()
                            .append("$project", new Document()
                                    .append("functions", new Document()
                                            .append("$concatArrays", Arrays.asList(
                                                    "$roleFunctions",
                                                    "$specificFunctions"
                                                )
                                            )
                                    )
                            )
            );
			
			Document result = collection.aggregate(pipeline).first();
			MemberDTO memberDto = mongoTemplate.getConverter().read(MemberDTO.class, result);
			if (Utility.isNull(memberDto)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			return memberDto.getFunctions();
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public MemberDTO getMemberCommodities(String memberCode, long refId) {
		String methodName = "getMemberCommodities";
		try {
			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("members");
			
			Document query = new Document();
            query.append("code", memberCode);
            
            Document projection = new Document();
            projection.append("_id", 0.0);
            projection.append("commodities.commodityCode", 1.0);
            projection.append("commodities.commodityName", 1.0);
            
            Document result = collection.find(query).projection(projection).first();
			MemberDTO memberDto = mongoTemplate.getConverter().read(MemberDTO.class, result);
			return memberDto;
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
