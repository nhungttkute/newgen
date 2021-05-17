package com.newgen.am.service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.ApprovalConstant;
import com.newgen.am.common.Constant;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.ExcelHelper;
import com.newgen.am.common.MongoDBConnection;
import com.newgen.am.common.RequestParamsParser;
import com.newgen.am.common.SystemFunctionCode;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.ApprovalChangeGroupDTO;
import com.newgen.am.dto.ApprovalCommoditiesDTO;
import com.newgen.am.dto.ApprovalCommodityFeesDTO;
import com.newgen.am.dto.ApprovalDefaultPositionLimitDTO;
import com.newgen.am.dto.ApprovalFunctionsDTO;
import com.newgen.am.dto.ApprovalGeneralFeeDTO;
import com.newgen.am.dto.ApprovalMarginMultiplierDTO;
import com.newgen.am.dto.ApprovalMarginRatioAlertDTO;
import com.newgen.am.dto.ApprovalOrderLimitDTO;
import com.newgen.am.dto.ApprovalRiskParametersDTO;
import com.newgen.am.dto.ApprovalUpdateMemberDTO;
import com.newgen.am.dto.ApprovalUpdateUserDTO;
import com.newgen.am.dto.ApprovalUserRolesDTO;
import com.newgen.am.dto.BasePagination;
import com.newgen.am.dto.BrokerCommodity;
import com.newgen.am.dto.BrokerDTO;
import com.newgen.am.dto.CQGResponseObj;
import com.newgen.am.dto.ChangeGroupDTO;
import com.newgen.am.dto.CommoditiesDTO;
import com.newgen.am.dto.CommodityFeesDTO;
import com.newgen.am.dto.DefaultPositionLimitDTO;
import com.newgen.am.dto.FunctionsDTO;
import com.newgen.am.dto.GeneralFeeDTO;
import com.newgen.am.dto.InvestorDTO;
import com.newgen.am.dto.ListElementDTO;
import com.newgen.am.dto.MarginMultiplierDTO;
import com.newgen.am.dto.MarginRatioAlertDTO;
import com.newgen.am.dto.MemberCSV;
import com.newgen.am.dto.MemberCommoditiesDTO;
import com.newgen.am.dto.MemberDTO;
import com.newgen.am.dto.OrderLimitDTO;
import com.newgen.am.dto.RiskParametersDTO;
import com.newgen.am.dto.RoleFunctionsDTO;
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
import com.newgen.am.model.Investor;
import com.newgen.am.model.LoginAdminUser;
import com.newgen.am.model.Member;
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
	private ModelMapper modelMapper;
	
	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private PendingApprovalRepository pendingApprovalRepo;

	@Autowired
	private LoginAdminUserRepository loginAdmUserRepo;

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
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private CQGConnectorService cqgService;
	
	private Document getQueryDocument(RequestParamsParser.SearchCriteria searchCriteria, UserInfoDTO userInfo) {
		Document query = new Document();
		// get redis user info
		if (Utility.isDeptUser(userInfo)) {
			// do nothing
			query = searchCriteria.getQuery();
		} else if (Utility.isMemberUser(userInfo)) {
			// match code=memberCode
			query = searchCriteria.getQuery().append("code", userInfo.getMemberCode());
		} else {
			throw new CustomException(ErrorMessage.ACCESS_DENIED, HttpStatus.FORBIDDEN);
		}
		return query;
	}
	
	public BasePagination<MemberDTO> list(HttpServletRequest request, long refId) {
		String methodName = "list";
		BasePagination<MemberDTO> pagination = null;
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
	
	public List<MemberCSV> listCsv(HttpServletRequest request, long refId) {
		String methodName = "listCsv";
		List<MemberCSV> memberList = new ArrayList<>();
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
			MongoCursor<Document> cur = collection.aggregate(pipeline).allowDiskUse(true).iterator();
			while (cur.hasNext()) {
				MemberCSV memberCsv = mongoTemplate.getConverter().read(MemberCSV.class, cur.next());
				if (memberCsv != null) {
					memberCsv.setStatus(Utility.getStatusVnStr(memberCsv.getStatus()));
					memberList.add(memberCsv);
				}
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return memberList;
	}
	
	public ByteArrayInputStream loadMembersExcel(HttpServletRequest request, long refId) {
		String methodName = "loadMembersExcel";
		List<MemberCSV> memberList = new ArrayList<>();
		ByteArrayInputStream membersExcel = null;
		
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
			MongoCursor<Document> cur = collection.aggregate(pipeline).allowDiskUse(true).iterator();
			while (cur.hasNext()) {
				MemberCSV memberCsv = mongoTemplate.getConverter().read(MemberCSV.class, cur.next());
				if (memberCsv != null)
					memberList.add(memberCsv);
			}
			
			membersExcel = ExcelHelper.membersToExcel(memberList, refId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return membersExcel;
	}
	
	public void createMember(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "createMember";
		try {
			MemberDTO memberDto = Utility.getGson().fromJson(pendingApproval.getPendingData().getPendingValue(), MemberDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_DESC, memberDto.getCode(), pendingApproval.getId());

			boolean existedMember = memberRepository.existsMemberByCode(memberDto.getCode());
			if (existedMember) {
				AMLogger.logMessage(className, methodName, refId, "This member code already exists");
				throw new CustomException(ErrorMessage.DOCUMENT_ALREADY_EXISTED, HttpStatus.OK);
			} else {
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
				
				// create riskParameters
				Document riskParameters = new Document();
				riskParameters.append("newPositionOrderLock", Constant.RISK_OPTION_NO);
				riskParameters.append("orderLock", Constant.RISK_OPTION_NO);
				riskParameters.append("marginWithdrawalLock", Constant.RISK_OPTION_NO);
				
				ObjectId memberId = new ObjectId();
				Document newMember = new Document();
				newMember.append("createdUser", Utility.getCurrentUsername());
				newMember.append("createdDate", System.currentTimeMillis());
				newMember.append("_id", memberId);
				newMember.append("code", memberDto.getCode());
				newMember.append("name", memberDto.getName());
				newMember.append("status", Constant.STATUS_ACTIVE);
				newMember.append("note", memberDto.getNote());
				newMember.append("company", company);
				newMember.append("contact", contact);
				newMember.append("role", memberRole);
				newMember.append("riskParameters", riskParameters);
				
				if (Utility.isCQGSyncOn()) {
					// create a new cqg customer
					CQGResponseObj saleSeriesResult = cqgService.createCQGSaleSeries(memberDto, refId);
					if (saleSeriesResult != null) {
						String profileId = saleSeriesResult.getData().getProfileId();
						if (Utility.isNotNull(profileId)) {
							Document cqgInfo = new Document();
							cqgInfo.append("profileId", profileId.substring(1, profileId.length()));
							newMember.append("cqgInfo", cqgInfo);
						}
					} else {
						throw new CustomException(ErrorMessage.CQG_INFO_CREATED_UNSUCCESSFULLY, HttpStatus.OK);
					}
				}
							
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
				
				// set member to redis
				setMemberInfoRedis(collection, memberDto.getCode(), refId);
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void createMemberPA(HttpServletRequest request, MemberDTO memberDto, long refId) {
		String methodName = "createMemberPA";
		boolean existedMember = memberRepository.existsMemberByCode(memberDto.getCode());
		if (existedMember) {
			AMLogger.logMessage(className, methodName, refId, "This member code already exists");
			throw new CustomException(ErrorMessage.DOCUMENT_ALREADY_EXISTED, HttpStatus.OK);
		} else {
			try {
				// get redis user info
				UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
				// insert data to pending_approvals
				String approvalId = insertMemberCreatePA(userInfo, memberDto, refId);
				// send activity log
				activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_MEMBER,
						ActivityLogService.ACTIVITY_CREATE_MEMBER_DESC, memberDto.getCode(), approvalId);
			} catch (CustomException e) {
				throw e;
			} catch (Exception e) {
				AMLogger.logError(className, methodName, refId, e);
				throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	private String insertMemberCreatePA(UserInfoDTO userInfo, MemberDTO memberDto, long refId) {
		String methodName = "insertMemberCreatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_CREATE);
			pendingData.setCollectionName("members");
			pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
			pendingData.setPendingValue(Utility.getGson().toJson(memberDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), memberDto.getCode()));
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
	
	private void createMasterMemberUser(HttpServletRequest request, MemberDTO memberDto, String memberCode, Document masterUserRole, long refId) {
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
				createLoginAdminUser(memberCode, memberDto.getName(), memberUser, password, pin, refId);

				// send email
				if (Utility.isNotifyOn()) {
					Utility.sendCreateNewUserEmail(Constant.MEMBER_MASTER_USER_PREFIX, "", username, password, pin, refId);
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
	
	private LoginAdminUser createLoginAdminUser(String memberCode, String memberName, UserDTO memberUserDto, String password, String pin,
			long refId) {
		String methodName = "createLoginAdminUser";
		try {
			LoginAdminUser loginAdmUser = modelMapper.map(memberUserDto, LoginAdminUser.class);
			loginAdmUser.setPassword(passwordEncoder.encode(password));
			loginAdmUser.setPin(passwordEncoder.encode(pin));
			loginAdmUser.setStatus(Constant.STATUS_ACTIVE);
			loginAdmUser.setMemberCode(memberCode);
			loginAdmUser.setMemberName(memberName);
			loginAdmUser.setCreatedUser(Utility.getCurrentUsername());
			loginAdmUser.setCreatedDate(System.currentTimeMillis());
			LoginAdminUser newLoginAdmUser = loginAdmUserRepo.save(loginAdmUser);
			return newLoginAdmUser;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void updateMember(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "updateMember";
		try {
			String memberCode = pendingApproval.getPendingData().getQueryValue();
			UpdateMemberDTO memberDto = Utility.getGson().fromJson(pendingApproval.getPendingData().getPendingValue(), UpdateMemberDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_UPDATE_MEMBER,
					ActivityLogService.ACTIVITY_APPROVAL_UPDATE_MEMBER_DESC, memberCode, pendingApproval.getId());
			
			BasicDBObject updateMember = new BasicDBObject();
			BasicDBObject updateLoginAdmUser = new BasicDBObject();
			
			boolean isUserUpdated = false;
			boolean isStatusUpdated = false;
			boolean isNameUpdated = false;
			
			if (Utility.isNotNull(memberDto.getName())) {
				updateMember.append("name", memberDto.getName());
				updateLoginAdmUser.append("memberName", memberDto.getName());
				isNameUpdated = true;
			}
			if (Utility.isNotNull(memberDto.getStatus())) {
				isStatusUpdated = true;
				updateMember.append("status", memberDto.getStatus().toUpperCase());
				updateLoginAdmUser.append("status", memberDto.getStatus().toUpperCase());
			}
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
						updateLoginAdmUser.append("fullName", memberDto.getCompany().getDelegate().getFullName());
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
						updateLoginAdmUser.append("email", memberDto.getCompany().getDelegate().getEmail());
						isUserUpdated = true;
					}
					if (Utility.isNotNull(memberDto.getCompany().getDelegate().getPhoneNumber()))  {
						updateMember.append("company.delegate.phoneNumber", memberDto.getCompany().getDelegate().getPhoneNumber());
						updateMember.append("users.$.phoneNumber", memberDto.getCompany().getDelegate().getPhoneNumber());
						updateMember.append("contact.phoneNumber", memberDto.getCompany().getDelegate().getPhoneNumber());
						updateLoginAdmUser.append("phoneNumber", memberDto.getCompany().getDelegate().getPhoneNumber());
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
				
				// if status is changed, update all
				if (isStatusUpdated) {
					// set member to redis
					setMemberInfoRedis(collection, memberCode, refId);
					
					String memberUsername = Constant.BROKER_USER_PREFIX + memberCode;
					// logout all users if status is inactive
					if (Constant.STATUS_INACTIVE.equalsIgnoreCase(memberDto.getStatus())) {
						List<String> userList = new ArrayList<String>();
						userList.add(memberUsername);
						Utility.sendHandleLogout(userList, refId);
					}
				}
				
				// update login_admin_users if there's any change
				if (!updateLoginAdmUser.isEmpty()) {
					BasicDBObject logiAdmQuery = new BasicDBObject();
					logiAdmQuery.append("username", Constant.MEMBER_MASTER_USER_PREFIX + memberCode);
					
					BasicDBObject loginAdmUpdate = new BasicDBObject();
					loginAdmUpdate.append("$set", updateLoginAdmUser);
					
					MongoCollection<Document> loginAdmCollection = database.getCollection("login_admin_users");
					loginAdmCollection.updateOne(logiAdmQuery, loginAdmUpdate);
				}
				
				if (isNameUpdated) {
					// update broker's memberName
					updateBrokerMemberName(memberCode, memberDto.getName());
					// update collaborator's memberName
					updateCollaboratorMemberName(memberCode, memberDto.getName());
					// update investors's memberName
					updateInvestorMemberName(memberCode, memberDto.getName());
					// update login_admin_users' memberName
					updateLoginAdminUserMemberName(memberCode, memberDto.getName());
					// update login_investor_users' memberName
					updateLoginInvestorUserMemberName(memberCode, memberDto.getName());
					// update investor_margin_info's memberName
					updateInvestorMarginInfoMemberName(memberCode, memberDto.getName());
				}
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	private void updateBrokerMemberName(String memberCode, String memberName) {
		MongoDatabase database = MongoDBConnection.getMongoDatabase();
		MongoCollection<Document> collection = database.getCollection("brokers");
		
		Document query = new Document();
		query.append("memberCode", memberCode);
		
		Document updateDoc = new Document();
		updateDoc.append("memberName", memberName);
		
		Document update = new Document();
		update.append("$set", updateDoc);
		
		collection.updateMany(query, update);
	}
	
	private void updateCollaboratorMemberName(String memberCode, String memberName) {
		MongoDatabase database = MongoDBConnection.getMongoDatabase();
		MongoCollection<Document> collection = database.getCollection("collaborators");
		
		Document query = new Document();
		query.append("memberCode", memberCode);
		
		Document updateDoc = new Document();
		updateDoc.append("memberName", memberName);
		
		Document update = new Document();
		update.append("$set", updateDoc);
		
		collection.updateMany(query, update);
	}
	
	private void updateInvestorMemberName(String memberCode, String memberName) {
		MongoDatabase database = MongoDBConnection.getMongoDatabase();
		MongoCollection<Document> collection = database.getCollection("investors");
		
		Document query = new Document();
		query.append("memberCode", memberCode);
		
		Document updateDoc = new Document();
		updateDoc.append("memberName", memberName);
		
		Document update = new Document();
		update.append("$set", updateDoc);
		
		collection.updateMany(query, update);
	}
	
	private void updateLoginAdminUserMemberName(String memberCode, String memberName) {
		MongoDatabase database = MongoDBConnection.getMongoDatabase();
		MongoCollection<Document> collection = database.getCollection("login_admin_users");
		
		Document query = new Document();
		query.append("memberCode", memberCode);
		
		Document updateDoc = new Document();
		updateDoc.append("memberName", memberName);
		
		Document update = new Document();
		update.append("$set", updateDoc);
		
		collection.updateMany(query, update);
	}
	
	private void updateLoginInvestorUserMemberName(String memberCode, String memberName) {
		MongoDatabase database = MongoDBConnection.getMongoDatabase();
		MongoCollection<Document> collection = database.getCollection("login_investor_users");
		
		Document query = new Document();
		query.append("memberCode", memberCode);
		
		Document updateDoc = new Document();
		updateDoc.append("memberName", memberName);
		
		Document update = new Document();
		update.append("$set", updateDoc);
		
		collection.updateMany(query, update);
	}
	
	private void updateInvestorMarginInfoMemberName(String memberCode, String memberName) {
		MongoDatabase database = MongoDBConnection.getMongoDatabase();
		MongoCollection<Document> collection = database.getCollection("investor_margin_info");
		
		Document query = new Document();
		query.append("memberCode", memberCode);
		
		Document updateDoc = new Document();
		updateDoc.append("memberName", memberName);
		
		Document update = new Document();
		update.append("$set", updateDoc);
		
		collection.updateMany(query, update);
	}

	public void updateMemberPA(HttpServletRequest request, String memberCode, ApprovalUpdateMemberDTO memberDto, long refId) {
		String methodName = "updateMemberPA";
		try {
			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertMemberUpdatePA(userInfo, memberCode, memberDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_UPDATE_MEMBER,
					ActivityLogService.ACTIVITY_UPDATE_MEMBER_DESC, memberCode, approvalId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private String insertMemberUpdatePA(UserInfoDTO userInfo, String memberCode, ApprovalUpdateMemberDTO memberDto, long refId) {
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
			pendingData.setOldValue(Utility.getGson().toJson(memberDto.getOldData()));
			pendingData.setPendingValue(Utility.getGson().toJson(memberDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_UPDATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_UPDATE_NAME);
			pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(),
					memberCode));
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

	
	public MemberDTO getMemberDetail(HttpServletRequest request, String memberCode, long refId) {
		String methodName = "getMemberDetail";
		try {
			Document query = new Document();
            query.append("code", memberCode);
            
            // get redis user info
            UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
            if (Utility.isNotNull(userInfo.getMemberCode())) {
            	query = new Document();
                query.append("$and", Arrays.asList(
                        new Document()
                                .append("code", memberCode),
                        new Document()
                                .append("code", userInfo.getMemberCode())
                    )
                );
            }
         			
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
	
	public UserDTO getMemberMasterUserDetail(HttpServletRequest request, String memberCode, long refId) {
		String methodName = "getMemberMasterUserDetail";
		try {
            MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("members");
			
			Document matchQuery = new Document().append("code", memberCode);
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			if (Utility.isNotNull(userInfo.getMemberCode())) {
				matchQuery = new Document();
				matchQuery.append("$and", Arrays.asList(
			            new Document()
			                    .append("code", memberCode),
			            new Document()
			                    .append("code", userInfo.getMemberCode())
			        )
			    );
			}
            
			List<? extends Bson> pipeline = Arrays.asList(
                    new Document()
                            .append("$match", matchQuery
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
	
	public void createMemberFunctions(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "createMemberFunctions";
		try {
			String memberCode = pendingApproval.getPendingData().getQueryValue();
			FunctionsDTO memberDto = Utility.getGson().fromJson(pendingApproval.getPendingData().getPendingValue(), FunctionsDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_FUNCTIONS,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_FUNCTIONS_DESC, memberCode,
					pendingApproval.getId());
			
			List<Document> functions = new ArrayList<Document>();
			for (RoleFunction function : memberDto.getFunctions()) {
				Document func = new Document();
				func.append("code", function.getCode());
				func.append("name", function.getName());
				func.append("orderNumber", function.getOrderNumber());
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
	}
	
	public void createMemberFunctionsPA(HttpServletRequest request, String memberCode, ApprovalFunctionsDTO memberDto,
			long refId) {
		String methodName = "createMemberFunctionsPA";
		try {
			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertMemberFunctionsAssignPA(userInfo, memberCode, memberDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_CREATE_MEMBER_FUNCTIONS,
					ActivityLogService.ACTIVITY_CREATE_MEMBER_FUNCTIONS_DESC, String.valueOf(memberCode),
					approvalId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private String insertMemberFunctionsAssignPA(UserInfoDTO userInfo, String memberCode, ApprovalFunctionsDTO memberDto,
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
			pendingData.setOldValue(Utility.getGson().toJson(memberDto.getOldData()));
			pendingData.setPendingValue(Utility.getGson().toJson(memberDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_FUNCTIONS_ASSIGN_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_FUNCTIONS_ASSIGN_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), memberCode));
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

	public void assignCommodities(HttpServletRequest request, PendingApproval pendingApproval , long refId) {
		String methodName = "assignCommodities";
		try {
			String memberCode = pendingApproval.getPendingData().getQueryValue();
			CommoditiesDTO memberDto = Utility.getGson().fromJson(pendingApproval.getPendingData().getPendingValue(), CommoditiesDTO.class);
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_APPROVAL_MEMBER_COMMODITIES_ASSIGN,
					ActivityLogService.ACTIVITY_APPROVAL_MEMBER_COMMODITIES_ASSIGN_DESC, memberCode,
					pendingApproval.getId());
			
			if (memberDto.getCommodities() != null && memberDto.getCommodities().size() > 0) {
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
			} else {
				Document query = new Document();
				query.append("code", memberCode);
				
				Document updateMember = new Document();
				updateMember.append("commodities", null);
				updateMember.append("lastModifiedUser", Utility.getCurrentUsername());
				updateMember.append("lastModifiedDate", System.currentTimeMillis());
				
				Document update = new Document();
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
	
	public void setCommoditiesPositionLimit(HttpServletRequest request, PendingApproval pendingApproval , long refId) {
		String methodName = "setCommoditiesPositionLimit";
		try {
			String memberCode = pendingApproval.getPendingData().getQueryValue();
			CommoditiesDTO pendingCommDto = Utility.getGson().fromJson(pendingApproval.getPendingData().getPendingValue(), CommoditiesDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_APPROVAL_MEMBER_COMMODITIES_POSITION_LIMIT,
					ActivityLogService.ACTIVITY_APPROVAL_MEMBER_COMMODITIES_POSITION_LIMIT_DESC, memberCode,
					pendingApproval.getId());
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("members");
			
			Document query = new Document();
			query.append("code", memberCode);
			
			Document projection = new Document();
			projection.append("_id", 0.0);
			projection.append("commodities", 1.0);
			
			Document result = collection.find(query).projection(projection).first();
			MemberDTO memberDto = mongoTemplate.getConverter().read(MemberDTO.class, result);
			
			if (memberDto.getCommodities() != null && memberDto.getCommodities().size() > 0) {
				List<Document> newCommodities = new ArrayList<Document>();
				List<Commodity> pendingCommodities = pendingCommDto.getCommodities();
				
				for (Commodity comm : memberDto.getCommodities()) {
					Document commDoc = new Document();
					commDoc.append("commodityCode", comm.getCommodityCode());
					commDoc.append("commodityName", comm.getCommodityName());
					commDoc.append("commodityFee", comm.getCommodityFee());
					
					Commodity pendingComm = getCommodityInAListByCode(pendingCommodities, comm.getCommodityCode());
					if (pendingComm != null) {
						commDoc.append("positionLimitType", pendingComm.getPositionLimitType());
						commDoc.append("positionLimit", pendingComm.getPositionLimit());
					} else {
						commDoc.append("positionLimitType", comm.getPositionLimitType());
						commDoc.append("positionLimit", comm.getPositionLimit());
					}
					
					commDoc.append("currency", Constant.CURRENCY_VND);
					newCommodities.add(commDoc);
				}
				
				Document updateMember = new Document();
				updateMember.append("commodities", newCommodities);
				updateMember.append("lastModifiedUser", Utility.getCurrentUsername());
				updateMember.append("lastModifiedDate", System.currentTimeMillis());
				
				Document update = new Document();
				update.append("$set", updateMember);
				
				collection.updateOne(query, update);
				
				setMemberInfoRedis(collection, memberCode, refId);
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void setCommoditiesFee(HttpServletRequest request, PendingApproval pendingApproval , long refId) {
		String methodName = "setCommoditiesFee";
		try {
			String memberCode = pendingApproval.getPendingData().getQueryValue();
			CommoditiesDTO pendingCommDto = Utility.getGson().fromJson(pendingApproval.getPendingData().getPendingValue(), CommoditiesDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_APPROVAL_MEMBER_COMMODITIES_FEE,
					ActivityLogService.ACTIVITY_APPROVAL_MEMBER_COMMODITIES_FEE_DESC, memberCode,
					pendingApproval.getId());
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("members");
			
			Document query = new Document();
			query.append("code", memberCode);
			
			Document projection = new Document();
			projection.append("_id", 0.0);
			projection.append("commodities", 1.0);
			
			Document result = collection.find(query).projection(projection).first();
			MemberDTO memberDto = mongoTemplate.getConverter().read(MemberDTO.class, result);
			
			if (memberDto.getCommodities() != null && memberDto.getCommodities().size() > 0) {
				List<Document> newCommodities = new ArrayList<Document>();
				List<Commodity> pendingCommodities = pendingCommDto.getCommodities();
				
				for (Commodity comm : memberDto.getCommodities()) {
					Document commDoc = new Document();
					commDoc.append("commodityCode", comm.getCommodityCode());
					commDoc.append("commodityName", comm.getCommodityName());
					commDoc.append("positionLimitType", comm.getPositionLimitType());
					commDoc.append("positionLimit", comm.getPositionLimit());
					
					Commodity pendingComm = getCommodityInAListByCode(pendingCommodities, comm.getCommodityCode());
					if (pendingComm != null) {
						commDoc.append("commodityFee", pendingComm.getCommodityFee());
					} else {
						commDoc.append("commodityFee", comm.getCommodityFee());
					}
					
					commDoc.append("currency", Constant.CURRENCY_VND);
					newCommodities.add(commDoc);
				}
				
				Document updateMember = new Document();
				updateMember.append("commodities", newCommodities);
				updateMember.append("lastModifiedUser", Utility.getCurrentUsername());
				updateMember.append("lastModifiedDate", System.currentTimeMillis());
				
				Document update = new Document();
				update.append("$set", updateMember);
				
				collection.updateOne(query, update);
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private Commodity getCommodityInAListByCode(List<Commodity> commodities, String code) {
		if (commodities != null && commodities.size() > 0) {
			for (Commodity comm : commodities) {
				if (code.equals(comm.getCommodityCode())) return comm;
			}
		}
		
		return null;
	}
	
	public void createMemberCommoditiesPA(HttpServletRequest request, String memberCode, ApprovalCommoditiesDTO memberDto, long refId) {
		String methodName = "createMemberCommoditiesPA";
		try {
			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			// check if this is an assignment of commodities
			
			if (Utility.isNotNull(memberDto.getType())) {
				String functionCode = "";
				String functionName = "";
				String serviceFunctionName = "";
				
				if(Constant.COMMODITIES_TYPE_ASSIGN.equals(memberDto.getType())) {
					serviceFunctionName = ApprovalConstant.MEMBER_COMMODITIES_ASSIGN;
					functionCode = SystemFunctionCode.APPROVAL_MEMBER_COMMODITIES_ASSIGN_CREATE_CODE;
					functionName = SystemFunctionCode.MEMBER_COMMODITIES_ASSIGN_CREATE_NAME;
					
					String approvalId = insertMemberCommoditiesPA(userInfo, memberCode, memberDto, serviceFunctionName, functionCode, functionName, refId);
					// send activity log
					activityLogService.sendActivityLog(userInfo, request,
							ActivityLogService.ACTIVITY_CREATE_MEMBER_COMMODITIES_ASSIGN,
							ActivityLogService.ACTIVITY_CREATE_MEMBER_COMMODITIES_ASSIGN_DESC, memberCode,
							approvalId);
				} else if(Constant.COMMODITIES_TYPE_POSITION_LIMIT.equals(memberDto.getType())) {
					serviceFunctionName = ApprovalConstant.MEMBER_COMMODITIES_POSITION_LIMIT;
					functionCode = SystemFunctionCode.APPROVAL_MEMBER_LIMIT_CREATE_CODE;
					functionName = SystemFunctionCode.MEMBER_LIMIT_CREATE_NAME;
					
					String approvalId = insertMemberCommoditiesPA(userInfo, memberCode, memberDto, serviceFunctionName, functionCode, functionName, refId);
					// send activity log
					activityLogService.sendActivityLog(userInfo, request,
							ActivityLogService.ACTIVITY_CREATE_MEMBER_COMMODITIES_POSITION_LIMIT,
							ActivityLogService.ACTIVITY_CREATE_MEMBER_COMMODITIES_POSITION_LIMIT_DESC, memberCode,
							approvalId);
				} else if(Constant.COMMODITIES_TYPE_FEE.equals(memberDto.getType())) {
					serviceFunctionName = ApprovalConstant.MEMBER_COMMODITIES_FEE;
					functionCode = SystemFunctionCode.APPROVAL_MEMBER_COMMODITIES_FEE_CREATE_CODE;
					functionName = SystemFunctionCode.MEMBER_COMMODITIES_FEE_CREATE_NAME;
					
					String approvalId = insertMemberCommoditiesPA(userInfo, memberCode, memberDto, serviceFunctionName, functionCode, functionName, refId);
					// send activity log
					activityLogService.sendActivityLog(userInfo, request,
							ActivityLogService.ACTIVITY_CREATE_MEMBER_COMMODITIES_FEE,
							ActivityLogService.ACTIVITY_CREATE_MEMBER_COMMODITIES_FEE_DESC, memberCode,
							approvalId);
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
	
	private String insertMemberCommoditiesPA(UserInfoDTO userInfo, String memberCode, ApprovalCommoditiesDTO memberDto, String serviceFunctionName, String functionCode, String functionName, long refId) {
		String methodName = "insertMemberCommoditiesPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(serviceFunctionName);
			pendingData.setCollectionName("members");
			pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setOldValue(Utility.getGson().toJson(memberDto.getOldData()));
			pendingData.setPendingValue(Utility.getGson().toJson(memberDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(functionCode);
			pendingApproval.setFunctionName(functionName);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), memberCode));
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
	
	public void createOrderLimit(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "createOrderLimit";
		try {
			String memberCode = pendingApproval.getPendingData().getQueryValue();
			OrderLimitDTO memberDto = Utility.getGson().fromJson(pendingApproval.getPendingData().getPendingValue(), OrderLimitDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_ORDER_LIMIT,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_ORDER_LIMIT_DESC, memberCode, pendingApproval.getId());
			
			Document updateDocument = new Document();
			if (memberDto.getOrderLimit() > 0) {
				updateDocument.append("orderLimit", memberDto.getOrderLimit());
			}
			
			if (updateDocument.isEmpty()) {
				throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
			} else {
				// update cqg risk params
				if (Utility.isCQGSyncOn()) {
					List<Investor> investors = getInvestorsByMemberCode(memberCode, refId);
					if (investors != null) {
						for (Investor inv : investors) {
							boolean result = cqgService.updateCQGRiskParamsTSL(inv.getCqgInfo().getAccountId(), memberDto.getOrderLimit(), refId);
							if (!result) {
								AMLogger.logMessage(className, methodName, refId, "Cannot update trade size limit for " + inv.getInvestorCode());
							}
						}
					}
				}
				
				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("members");
				
				Document query = new Document();
				query.append("code", memberCode);
				
				updateDocument.append("lastModifiedUser", Utility.getCurrentUsername());
				updateDocument.append("lastModifiedDate", System.currentTimeMillis());
				
	            Document update = new Document();
				update.append("$set", updateDocument);
				
				collection.updateOne(query, update);
				
				// update to all investors
				BasicDBObject investorQuery = new BasicDBObject();
				investorQuery.append("memberCode", memberCode);
				
				BasicDBObject invUpdateDoc = new BasicDBObject();
				invUpdateDoc.append("orderLimit", memberDto.getOrderLimit());
				invUpdateDoc.append("lastModifiedUser", Utility.getCurrentUsername());
				invUpdateDoc.append("lastModifiedDate", System.currentTimeMillis());
				
				BasicDBObject invUpdate = new BasicDBObject();
				invUpdate.append("$set", invUpdateDoc);

				MongoCollection<Document> invCollection = database.getCollection("investors");
				invCollection.updateMany(investorQuery, invUpdate);
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private List<Investor> getInvestorsByMemberCode(String memberCode, long refId) {
		String methodName = "getInvestorsByMemberCode";
		
		List<Investor> investors = new ArrayList<Investor>();
		try {
			Document query = new Document();
			query.append("memberCode", memberCode);
			query.append("status", Constant.STATUS_ACTIVE);
			
			Document projection = new Document();
			projection.append("_id", 0.0);
			projection.append("investorCode", 1.0);
			projection.append("cqgInfo", 1.0);
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("investors");
			MongoCursor<Document> cur = collection.find(query).projection(projection).iterator();
			while (cur.hasNext()) {
				Investor inv = mongoTemplate.getConverter().read(Investor.class, cur.next());
				if (inv != null) investors.add(inv);
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return investors;
	}
	
	public void createDefaultPositionLimit(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "createDefaultPositionLimit";
		boolean needUpdateCommodities = false;
		try {
			String memberCode = pendingApproval.getPendingData().getQueryValue();
			DefaultPositionLimitDTO memberDto = Utility.getGson().fromJson(pendingApproval.getPendingData().getPendingValue(), DefaultPositionLimitDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_DEFAULT_POSITION_LIMIT,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_DEFAULT_POSITION_LIMIT_DESC, memberCode, pendingApproval.getId());
			
			Document updateDocument = new Document();
			if (memberDto.getDefaultPositionLimit() > 0) {
				needUpdateCommodities = true;
				updateDocument.append("defaultPositionLimit", memberDto.getDefaultPositionLimit());
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
		            if (memberComm != null && memberComm.getCommodities() != null) {
		            	for (Commodity comm : memberComm.getCommodities()) {
			            	Document newComm = new Document();
			            	newComm.append("commodityCode", comm.getCommodityCode());
			            	newComm.append("commodityName", comm.getCommodityCode());
			            	newComm.append("currency", Constant.CURRENCY_VND);
			            	newComm.append("commodityFee", comm.getCommodityFee());
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
		            }
		            
		            updateDocument.append("commodities", newCommodities);
				}
				
	            
	            Document update = new Document();
				update.append("$set", updateDocument);
				
				collection.updateOne(query, update);
				
				setMemberInfoRedis(collection, memberCode, refId);
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void createOrderLimitPA(HttpServletRequest request, String memberCode, ApprovalOrderLimitDTO memberDto, long refId) {
		String methodName = "createOrderLimitPA";
		try {
			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertOrderLimitSettingPA(userInfo, memberCode, memberDto, refId);
			
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_MEMBER_DEFAULT_SETTING,
					ActivityLogService.ACTIVITY_CREATE_MEMBER_DEFAULT_SETTING_DESC, memberCode, approvalId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void createDefaultPositionLimitPA(HttpServletRequest request, String memberCode, ApprovalDefaultPositionLimitDTO memberDto, long refId) {
		String methodName = "createDefaultPositionLimitPA";
		try {
			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertDefaultPositionLimitSettingPA(userInfo, memberCode, memberDto, refId);
			
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_MEMBER_DEFAULT_SETTING,
					ActivityLogService.ACTIVITY_CREATE_MEMBER_DEFAULT_SETTING_DESC, memberCode, approvalId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private String insertOrderLimitSettingPA(UserInfoDTO userInfo, String memberCode, ApprovalOrderLimitDTO memberDto,
			long refId) {
		String methodName = "insertOrderLimitSettingPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_ORDER_LIMIT_CREATE);
			pendingData.setCollectionName("members");
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setOldValue(Utility.getGson().toJson(memberDto.getOldData()));
			pendingData.setPendingValue(Utility.getGson().toJson(memberDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_ORDER_LIMIT_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_ORDER_LIMIT_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), memberCode));
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
	
	private String insertDefaultPositionLimitSettingPA(UserInfoDTO userInfo, String memberCode, ApprovalDefaultPositionLimitDTO memberDto,
			long refId) {
		String methodName = "insertDefaultPositionLimitSettingPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_DEFAULT_POSITION_LIMIT_CREATE);
			pendingData.setCollectionName("members");
			pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setOldValue(Utility.getGson().toJson(memberDto.getOldData()));
			pendingData.setPendingValue(Utility.getGson().toJson(memberDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_DEFAULT_POSITION_LIMIT_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_DEFAULT_POSITION_LIMIT_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), memberCode));
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
	
	public void setMemberNewPositionOrderLock(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "setMemberNewPositionOrderLock";
		try {
			String memberCode = pendingApproval.getPendingData().getQueryValue();
			RiskParametersDTO memberDto = Utility.getGson().fromJson(pendingApproval.getPendingData().getPendingValue(), RiskParametersDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_RISK_NEW_POSITION_ORDER_LOCK,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_RISK_NEW_POSITION_ORDER_LOCK_DESC, memberCode, pendingApproval.getId());
			
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
			
			setMemberInfoRedis(collection, memberCode, refId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void setMemberNewPositionOrderLockPA(HttpServletRequest request, String memberCode, ApprovalRiskParametersDTO memberDto, long refId) {
		String methodName = "setMemberNewPositionOrderLockPA";
		try {
			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertMemberNewPositionOrderLockPA(userInfo, memberCode, memberDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_MEMBER_RISK_NEW_POSITION_ORDER_LOCK,
					ActivityLogService.ACTIVITY_CREATE_MEMBER_RISK_NEW_POSITION_ORDER_LOCK_DESC, memberCode, approvalId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private String insertMemberNewPositionOrderLockPA(UserInfoDTO userInfo, String memberCode, ApprovalRiskParametersDTO memberDto,
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
			pendingData.setAppliedObject(String.format(ApprovalConstant.APPLIED_OBJ_MEMBER, memberCode));
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setOldValue(Utility.getGson().toJson(memberDto.getOldData()));
			pendingData.setPendingValue(Utility.getGson().toJson(memberDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_RISK_NEW_ORDER_LOCK_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_RISK_NEW_ORDER_LOCK_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), memberCode));
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
	
	public void setMemberOrderLock(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "setMemberOrderLock";
		try {
			String memberCode = pendingApproval.getPendingData().getQueryValue();
			RiskParametersDTO memberDto = Utility.getGson().fromJson(pendingApproval.getPendingData().getPendingValue(), RiskParametersDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_RISK_ORDER_LOCK,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_RISK_ORDER_LOCK_DESC, memberCode, pendingApproval.getId());
			
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
			
			setMemberInfoRedis(collection, memberCode, refId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void setMemberOrderLockPA(HttpServletRequest request, String memberCode, ApprovalRiskParametersDTO memberDto, long refId) {
		String methodName = "setMemberOrderLockPA";
		try {
			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertMemberOrderLockPA(userInfo, memberCode, memberDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_MEMBER_RISK_ORDER_LOCK,
					ActivityLogService.ACTIVITY_CREATE_MEMBER_RISK_ORDER_LOCK_DESC, memberCode, approvalId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private String insertMemberOrderLockPA(UserInfoDTO userInfo, String memberCode, ApprovalRiskParametersDTO memberDto,
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
			pendingData.setAppliedObject(String.format(ApprovalConstant.APPLIED_OBJ_MEMBER, memberCode));
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setOldValue(Utility.getGson().toJson(memberDto.getOldData()));
			pendingData.setPendingValue(Utility.getGson().toJson(memberDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_RISK_ORDER_LOCK_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_RISK_ORDER_LOCK_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), memberCode));
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
	
	public void setMemberMarginWithDrawalLock(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "setMemberMarginWithDrawalLock";
		try {
			String memberCode = pendingApproval.getPendingData().getQueryValue();
			RiskParametersDTO memberDto = Utility.getGson().fromJson(pendingApproval.getPendingData().getPendingValue(), RiskParametersDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_RISK_MARGIN_WITHDRAWAL,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_RISK_MARGIN_WITHDRAWAL_DESC, memberCode, pendingApproval.getId());

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
			
			setMemberInfoRedis(collection, memberCode, refId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void setMemberMarginWithDrawalLockPA(HttpServletRequest request, String memberCode, ApprovalRiskParametersDTO memberDto, long refId) {
		String methodName = "setMemberMarginWithDrawalLockPA";
		try {
			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertMemberMarginWithDrawalLockPA(userInfo, memberCode, memberDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_MEMBER_RISK_MARGIN_WITHDRAWAL,
					ActivityLogService.ACTIVITY_CREATE_MEMBER_RISK_MARGIN_WITHDRAWAL_DESC, memberCode, approvalId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private String insertMemberMarginWithDrawalLockPA(UserInfoDTO userInfo, String memberCode, ApprovalRiskParametersDTO memberDto,
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
			pendingData.setAppliedObject(String.format(ApprovalConstant.APPLIED_OBJ_MEMBER, memberCode));
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setOldValue(Utility.getGson().toJson(memberDto.getOldData()));
			pendingData.setPendingValue(Utility.getGson().toJson(memberDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_RISK_MARGIN_WITHDRAWAL_LOCK_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_RISK_MARGIN_WITHDRAWAL_LOCK_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), memberCode));
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
	
	private void setMemberInfoRedis(MongoCollection<Document> collection, String memberCode, long refId) {
		String methodName = "setMemberInfoRedis";
		try {
			Document query = new Document();
			query.append("code", memberCode);
			
			// delete redis info (if existed)
			Utility.deleteRedisInfo(template, memberCode, refId);
			
			// insert new redis info
			Document projection = new Document();
			projection.append("_id", 0.0);
			projection.append("status", 1.0);
			projection.append("riskParameters", 1.0);
			projection.append("commodities", 1.0);
			
			Document result = collection.find(query).projection(projection).first();
			MemberDTO memberResultDto = mongoTemplate.getConverter().read(MemberDTO.class, result);
			Utility.setRedisInfo(template, memberCode, memberResultDto, refId);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void setMarginMultiplierBulk(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "setMarginMultiplierBulk";
		try {
			String memberCode = pendingApproval.getPendingData().getQueryValue();
			MarginMultiplierDTO memberDto = Utility.getGson().fromJson(pendingApproval.getPendingData().getPendingValue(), MarginMultiplierDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_MARGIN_MULTIPLIER_BULK,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_MARGIN_MULTIPLIER_BULK_DESC, memberCode, pendingApproval.getId());
			
			// update cqg risk params
			if (Utility.isCQGSyncOn()) {
				List<Investor> investors = getInvestorsByMemberCode(memberCode, refId);
				if (investors != null) {
					for (Investor inv : investors) {
						boolean result = cqgService.updateCQGRiskParamsMM(inv.getCqgInfo().getAccountId(), memberDto.getMarginMultiplier(), refId);
						if (!result) {
							AMLogger.logMessage(className, methodName, refId, "Cannot update trade size limit for " + inv.getInvestorCode());
						}
					}
				}
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
	
	public void setMarginMultiplierBulkPA(HttpServletRequest request, String memberCode, ApprovalMarginMultiplierDTO memberDto, long refId) {
		String methodName = "setMarginMultiplierBulkPA";
		try {
			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertMemberMarginMultiplierBulkPA(userInfo, memberCode, memberDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_MEMBER_MARGIN_MULTIPLIER_BULK,
					ActivityLogService.ACTIVITY_CREATE_MEMBER_MARGIN_MULTIPLIER_BULK_DESC, memberCode, approvalId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private String insertMemberMarginMultiplierBulkPA(UserInfoDTO userInfo, String memberCode, ApprovalMarginMultiplierDTO memberDto,
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
			pendingData.setAppliedObject(String.format(ApprovalConstant.APPLIED_OBJ_MEMBER, memberCode));
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setOldValue(Utility.getGson().toJson(memberDto.getOldData()));
			pendingData.setPendingValue(Utility.getGson().toJson(memberDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_MARGIN_MULTIPLIER_BULK_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_MARGIN_MULTIPLIER_BULK_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), memberCode));
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
	
	public void setMarginRatioAlertBulk(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "setMarginRatioAlertBulk";
		try {
			String memberCode = pendingApproval.getPendingData().getQueryValue();
			MarginRatioAlertDTO memberDto = Utility.getGson().fromJson(pendingApproval.getPendingData().getPendingValue(), MarginRatioAlertDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_MARGIN_RATIO_BULK,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_MARGIN_RATIO_BULK_DESC, memberCode, pendingApproval.getId());
			
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
	
	public void setMarginRatioAlertBulkPA(HttpServletRequest request, String memberCode, ApprovalMarginRatioAlertDTO memberDto, long refId) {
		String methodName = "setMarginRatioAlertBulkPA";
		try {
			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			boolean isValidRatio = true;
			if (memberDto.getPendingData().getMarginRatioAlert().getFinalizationRatio() >= memberDto.getPendingData().getMarginRatioAlert().getCancelOrderRatio() || (memberDto.getPendingData().getMarginRatioAlert().getFinalizationRatio() >= memberDto.getPendingData().getMarginRatioAlert().getWarningRatio())) {
				isValidRatio = false;
			} else if (memberDto.getPendingData().getMarginRatioAlert().getCancelOrderRatio() >= memberDto.getPendingData().getMarginRatioAlert().getWarningRatio()) {
				isValidRatio = false;
			}
			 
			if (!isValidRatio) {
				throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
			}
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertMarginRatioAlertBulkPA(userInfo, memberCode, memberDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_MEMBER_MARGIN_RATIO_BULK,
					ActivityLogService.ACTIVITY_CREATE_MEMBER_MARGIN_RATIO_BULK_DESC, memberCode, approvalId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private String insertMarginRatioAlertBulkPA(UserInfoDTO userInfo, String memberCode, ApprovalMarginRatioAlertDTO memberDto,
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
			pendingData.setAppliedObject(String.format(ApprovalConstant.APPLIED_OBJ_MEMBER_INVESTORS, memberCode));
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setOldValue(Utility.getGson().toJson(memberDto.getOldData()));
			pendingData.setPendingValue(Utility.getGson().toJson(memberDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_MARGIN_RATIO_BULK_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_MARGIN_RATIO_BULK_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), memberCode));
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
	
	public void setGeneralFeeBulk(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "setGeneralFeeBulk";
		try {
			String memberCode = pendingApproval.getPendingData().getQueryValue();
			GeneralFeeDTO generalFeeDto = Utility.getGson().fromJson(pendingApproval.getPendingData().getPendingValue(), GeneralFeeDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_GENERAL_FEE_BULK,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_GENERAL_FEE_BULK_DESC, memberCode, pendingApproval.getId());
			
			if (generalFeeDto != null) {
				Document feeDoc = new Document();
				feeDoc.append("name", generalFeeDto.getName());
				feeDoc.append("processMethod", generalFeeDto.getProcessMethod());
				feeDoc.append("feeAmount", generalFeeDto.getFeeAmount());
				feeDoc.append("appliedDate", generalFeeDto.getAppliedDate());
				
				Document memberQuery = new Document();
				memberQuery.append("code", memberCode);
				
				// add new fee in memers
				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> memberCollection = database.getCollection("members");
				memberCollection.updateOne(memberQuery, Updates.addToSet("generalFees", feeDoc));
				
				// add new fee in investors
				BasicDBObject investorQuery = new BasicDBObject();
				investorQuery.append("memberCode", memberCode);
				
				MongoCollection<Document> invCollection = database.getCollection("investors");
				invCollection.updateMany(investorQuery, Updates.addToSet("generalFees", feeDoc));
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void setGeneralFeesBulkPA(HttpServletRequest request, String memberCode, GeneralFeeDTO generalFeeDto, long refId) {
		String methodName = "setGeneralFeesBulkPA";
		try {
			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			if (checkIfExistedFee(memberCode, generalFeeDto.getName(), refId)) {
				throw new CustomException(ErrorMessage.DOCUMENT_ALREADY_EXISTED, HttpStatus.OK);
			}
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertGeneralFeesBulkPA(userInfo, memberCode, generalFeeDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_MEMBER_GENERAL_FEE_BULK,
					ActivityLogService.ACTIVITY_CREATE_MEMBER_GENERAL_FEE_BULK_DESC, memberCode, approvalId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private boolean checkIfExistedFee(String memberCode, String feeName, long refId) {
		String methodName = "checkIfExistedFee";
		try {
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> memberCollection = database.getCollection("members");
			
			Document query = new Document();
			query.append("code", memberCode);
			query.append("generalFees.name", feeName);
			
			Document result = memberCollection.find(query).first();
			if (result != null) return true;
			return false;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private String insertGeneralFeesBulkPA(UserInfoDTO userInfo, String memberCode, GeneralFeeDTO generalFeeDto,
			long refId) {
		String methodName = "insertGeneralFeeBulkPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_GENERAL_FEE_BULK_CREATE);
			pendingData.setCollectionName("members");
			pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
			pendingData.setAppliedObject(String.format(ApprovalConstant.APPLIED_OBJ_MEMBER_INVESTORS, memberCode));
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setPendingValue(Utility.getGson().toJson(generalFeeDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_GENERAL_FEE_BULK_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_GENERAL_FEE_BULK_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), memberCode));
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
	
	public void updateGeneralFeeBulk(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "updateGeneralFeeBulk";
		try {
			String memberCode = pendingApproval.getPendingData().getQueryValue();
			String oldFeeName = pendingApproval.getPendingData().getQueryValue2();
			GeneralFeeDTO generalFeeDto = Utility.getGson().fromJson(pendingApproval.getPendingData().getPendingValue(), GeneralFeeDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_GENERAL_FEE_BULK,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_GENERAL_FEE_BULK_DESC, memberCode, pendingApproval.getId());
			
			if (generalFeeDto != null) {
				// update generalFees in members
				Document updateFeeDoc = new Document();
				updateFeeDoc.append("name", generalFeeDto.getName());
				updateFeeDoc.append("processMethod", generalFeeDto.getProcessMethod());
				updateFeeDoc.append("feeAmount", generalFeeDto.getFeeAmount());
				updateFeeDoc.append("appliedDate", generalFeeDto.getAppliedDate());
				
				Document memberQuery = new Document();
				memberQuery.append("code", memberCode);
				
				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> memberCollection = database.getCollection("members");
				
				Document memberFields = new Document("generalFees", new Document("name", oldFeeName));
				Document memberUpdate = new Document("$pull", memberFields);
				memberCollection.updateOne(memberQuery, memberUpdate);
				
				memberCollection.updateOne(memberQuery, Updates.addToSet("generalFees", updateFeeDoc));
				
				// update generalFees in investors
				Document investorQuery = new Document();
				investorQuery.append("memberCode", memberCode);
				
				MongoCollection<Document> invCollection = database.getCollection("investors");
				Document investorFields = new Document("generalFees", new Document("name", oldFeeName));
				Document investorUpdate = new Document("$pull", investorFields);
				invCollection.updateMany(investorQuery, investorUpdate);
				
				invCollection.updateMany(investorQuery, Updates.addToSet("generalFees", updateFeeDoc));
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void updateGeneralFeesBulkPA(HttpServletRequest request, String memberCode, ApprovalGeneralFeeDTO generalFeeDto, long refId) {
		String methodName = "updateGeneralFeesBulkPA";
		try {
			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertGeneralFeesBulkUpdatePA(userInfo, memberCode, generalFeeDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_MEMBER_GENERAL_FEE_BULK,
					ActivityLogService.ACTIVITY_CREATE_MEMBER_GENERAL_FEE_BULK_DESC, memberCode, approvalId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private String insertGeneralFeesBulkUpdatePA(UserInfoDTO userInfo, String memberCode, ApprovalGeneralFeeDTO generalFeeDto,
			long refId) {
		String methodName = "insertGeneralFeesBulkUpdatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_GENERAL_FEE_BULK_UPDATE);
			pendingData.setCollectionName("members");
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setAppliedObject(String.format(ApprovalConstant.APPLIED_OBJ_MEMBER_INVESTORS, memberCode));
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setQueryField2("generalFees.name");
			pendingData.setQueryValue2(generalFeeDto.getOldData().getName());
			pendingData.setOldValue(Utility.getGson().toJson(generalFeeDto.getOldData()));
			pendingData.setPendingValue(Utility.getGson().toJson(generalFeeDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_GENERAL_FEE_BULK_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_GENERAL_FEE_BULK_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), memberCode));
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
	
	public void setBrokerCommoditiesFeeBulk(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "setBrokerCommoditiesFeeBulk";
		try {
			String memberCode = pendingApproval.getPendingData().getQueryValue();
			CommodityFeesDTO memberDto = Utility.getGson().fromJson(pendingApproval.getPendingData().getPendingValue(), CommodityFeesDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_CREATE_BROKER_COMMODITIES_FEE_BULK,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_BROKER_COMMODITIES_FEE_BULK_DESC, memberCode, pendingApproval.getId());
			Member member = memberRepository.findByCode(memberCode);
			List<CommodityFee> currentCommodityFees = member.getCommodityFees();
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			
			// save commodityFees for member
			MongoCollection<Document> memberCollection = database.getCollection("members");
			Document memberQuery = new Document();
			memberQuery.append("code", memberCode);
			
			List<Document> commFees = new ArrayList<Document>();
			for (CommodityFee commFee : memberDto.getCommodityFees()) {
				long investorCommodityFee = 0;
				CommodityFee currentCommodityFee = getCommodityFeeInAListByCode(currentCommodityFees, commFee.getCommodityCode());
				if (currentCommodityFee != null) {
					investorCommodityFee = currentCommodityFee.getInvestorCommodityFee();
				}
				
				Document commFeeDoc = new Document();
				commFeeDoc.append("commodityCode", commFee.getCommodityCode());
				commFeeDoc.append("commodityName", commFee.getCommodityName());
				commFeeDoc.append("brokerCommodityFee", commFee.getBrokerCommodityFee());
				commFeeDoc.append("investorCommodityFee", investorCommodityFee);
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
							
							CommodityFee newComm = getCommodityFeeInAListByCode(memberDto.getCommodityFees(), comm.getCommodityCode());
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
	
	private CommodityFee getCommodityFeeInAListByCode(List<CommodityFee> commodities, String code) {
		if (commodities != null && commodities.size() > 0) {
			for (CommodityFee comm : commodities) {
				if (code.equals(comm.getCommodityCode())) return comm;
			}
		}
		
		return null;
	}
	
	public void setBrokerCommoditiesFeeBulkPA(HttpServletRequest request, String memberCode, ApprovalCommodityFeesDTO memberDto, long refId) {
		String methodName = "setBrokerCommoditiesFeeBulk";
		try {
			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			boolean isValidRequest = true;
			
			for (CommodityFee commFee : memberDto.getPendingData().getCommodityFees()) {
				if (! (commFee.getBrokerCommodityFee() > 0)) {
					isValidRequest = false;
					break;
				}
			}
			
			if (!isValidRequest) {
				throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
			}
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertBrokerCommoditiesFeeBulkPA(userInfo, memberCode, memberDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_MEMBER_COMMODITIES_FEE_BULK,
					ActivityLogService.ACTIVITY_CREATE_MEMBER_COMMODITIES_FEE_BULK_DESC, memberCode, approvalId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private String insertBrokerCommoditiesFeeBulkPA(UserInfoDTO userInfo, String memberCode, ApprovalCommodityFeesDTO memberDto,
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
			pendingData.setAppliedObject(String.format(ApprovalConstant.APPLIED_OBJ_MEMBER_BROKERS, memberCode));
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setOldValue(Utility.getGson().toJson(memberDto.getOldData()));
			pendingData.setPendingValue(Utility.getGson().toJson(memberDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_BROKER_COMMODITIES_FEE_BULK_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_BROKER_COMMODITIES_FEE_BULK_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), memberCode));
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
	
	public void setInvestorCommoditiesFeeBulk(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "setInvestorCommoditiesFeeBulk";
		try {
			String memberCode = pendingApproval.getPendingData().getQueryValue();
			CommodityFeesDTO memberDto = Utility.getGson().fromJson(pendingApproval.getPendingData().getPendingValue(), CommodityFeesDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_CREATE_INVESTOR_COMMODITIES_FEE_BULK,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_INVESTOR_COMMODITIES_FEE_BULK_DESC, memberCode, pendingApproval.getId());

			Member member = memberRepository.findByCode(memberCode);
			List<CommodityFee> currentCommodityFees = member.getCommodityFees();
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			
			// save commodityFees for member
			MongoCollection<Document> memberCollection = database.getCollection("members");
			Document memberQuery = new Document();
			memberQuery.append("code", memberCode);
			
			List<Document> commFees = new ArrayList<Document>();
			for (CommodityFee commFee : memberDto.getCommodityFees()) {
				long brokerCommodityFee = 0;
				CommodityFee currentCommodityFee = getCommodityFeeInAListByCode(currentCommodityFees, commFee.getCommodityCode());
				if (currentCommodityFee != null) {
					brokerCommodityFee = currentCommodityFee.getBrokerCommodityFee();
				}
				Document commFeeDoc = new Document();
				commFeeDoc.append("commodityCode", commFee.getCommodityCode());
				commFeeDoc.append("commodityName", commFee.getCommodityName());
				commFeeDoc.append("brokerCommodityFee", brokerCommodityFee);
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
							
							CommodityFee newComm = getCommodityFeeInAListByCode(memberDto.getCommodityFees(), comm.getCommodityCode());
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
	
	public void setInvestorCommoditiesFeeBulkPA(HttpServletRequest request, String memberCode, ApprovalCommodityFeesDTO memberDto, long refId) {
		String methodName = "setInvestorCommoditiesFeeBulkPA";
		try {
			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			boolean isValidRequest = true;
			for (CommodityFee commFee : memberDto.getPendingData().getCommodityFees()) {
				if (! (commFee.getInvestorCommodityFee() > 0)) {
					isValidRequest = false;
					break;
				}
			}
			
			if (!isValidRequest) {
				throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
			}
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertInvestorCommoditiesFeeBulkPA(userInfo, memberCode, memberDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_MEMBER_COMMODITIES_FEE_BULK,
					ActivityLogService.ACTIVITY_CREATE_MEMBER_COMMODITIES_FEE_BULK_DESC, memberCode, approvalId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private String insertInvestorCommoditiesFeeBulkPA(UserInfoDTO userInfo, String memberCode, ApprovalCommodityFeesDTO memberDto,
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
			pendingData.setAppliedObject(String.format(ApprovalConstant.APPLIED_OBJ_MEMBER_INVESTORS, memberCode));
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setOldValue(Utility.getGson().toJson(memberDto.getOldData()));
			pendingData.setPendingValue(Utility.getGson().toJson(memberDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_INVESTOR_COMMODITIES_FEE_BULK_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_INVESTOR_COMMODITIES_FEE_BULK_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), memberCode));
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
	
	public BasePagination<UserDTO> listMemberUsers(HttpServletRequest request, String memberCode, long refId) {
		String methodName = "listMemberUsers";
		BasePagination<UserDTO> pagination = null;
		try {
			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			Document query1 = new Document();
			query1.append("code", memberCode);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			if (Utility.isNotNull(userInfo.getMemberCode())) {
				query1 = new Document();
				query1.append("$and", Arrays.asList(
			            new Document()
			                    .append("code", memberCode),
			            new Document()
			                    .append("code", userInfo.getMemberCode())
			        )
			    );
			}

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
                            .append("$match", new Document()
                                    .append("users.username", new Document()
                                            .append("$ne", Constant.MEMBER_MASTER_USER_PREFIX + memberCode)
                                    )
                            ), 
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

			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			if (Utility.isNotNull(userInfo.getMemberCode())) {
				query1 = new Document();
			    query1.append("$and", Arrays.asList(
			            new Document()
			                    .append("code", memberCode),
			            new Document()
			                    .append("code", userInfo.getMemberCode())
			        )
			    );
			}
            
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
	
	public void createMemberUser(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "createMemberUser";
		try {
			String memberCode = pendingApproval.getPendingData().getQueryValue();
			UserDTO memberUserDto = Utility.getGson().fromJson(pendingApproval.getPendingData().getPendingValue(), UserDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog2(userInfo, request,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_USER,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_USER_DESC, memberUserDto.getUsername(), memberCode, pendingApproval.getId());

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("members");

			Document newUser = new Document();
			newUser.append("_id", new ObjectId());
			newUser.append("username", memberUserDto.getUsername());
			newUser.append("fullName", memberUserDto.getFullName());
			newUser.append("email", memberUserDto.getEmail());
			newUser.append("phoneNumber", memberUserDto.getPhoneNumber());
			newUser.append("status", Constant.STATUS_ACTIVE);
			newUser.append("note", memberUserDto.getNote());
			newUser.append("isPasswordExpiryCheck", memberUserDto.getIsPasswordExpiryCheck());
			newUser.append("passwordExpiryDays", memberUserDto.getPasswordExpiryDays());
			newUser.append("expiryAlertDays", memberUserDto.getExpiryAlertDays());
			newUser.append("createdUser", Utility.getCurrentUsername());
			newUser.append("createdDate", System.currentTimeMillis());

			BasicDBObject query = new BasicDBObject();
			query.append("code", memberCode);

			collection.updateOne(query, Updates.addToSet("users", newUser));

			// get memberName from member
			Document projection = new Document();
			projection.append("_id", 0.0);
			projection.append("name", 1.0);
			
			Document memberDoc = collection.find(query).projection(projection).first();
			MemberDTO memberDto = mongoTemplate.getConverter().read(MemberDTO.class, memberDoc);
			
			// insert loginAdminUser
			String password = Utility.generateRandomPassword();
			String pin = Utility.generateRandomPin();
			LoginAdminUser newLoginAdmUser = createLoginAdminUser(memberCode, memberDto.getName(), memberUserDto, password, pin, refId);

			// send email
			if (Utility.isNotifyOn()) {
				Utility.sendCreateNewUserEmail(Constant.MEMBER_MASTER_USER_PREFIX, "", newLoginAdmUser.getUsername(), password, pin, refId);
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void createMemberUserPA(HttpServletRequest request, String memberCode, UserDTO memberUserDto, long refId) {
		String methodName = "createMemberUserPA";
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
			} catch (Exception e) {
				AMLogger.logError(className, methodName, refId, e);
				throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			AMLogger.logMessage(className, methodName, refId, "This username already exists");
			throw new CustomException(ErrorMessage.DOCUMENT_ALREADY_EXISTED, HttpStatus.OK);
		}
	}
	
	private String insertMemberUserCreatePA(UserInfoDTO userInfo, String memberCode, UserDTO memberUserDto,
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
			pendingData.setPendingValue(Utility.getGson().toJson(memberUserDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_USER_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_USER_CREATE_NAME);
			pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription2(SystemFunctionCode.MEMBER_USER_CREATE_DESC, memberUserDto.getUsername(),
					memberCode));
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
	
	public void updateMemberUser(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "updateMemberUser";
		try {
			String memberCode = pendingApproval.getPendingData().getQueryValue();
			String username = pendingApproval.getPendingData().getQueryValue2();
			UpdateUserDTO userDto = Utility.getGson().fromJson(pendingApproval.getPendingData().getPendingValue(), UpdateUserDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog2(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_UPDATE_MEMBER_USER,
					ActivityLogService.ACTIVITY_APPROVAL_UPDATE_MEMBER_USER_DESC, username, memberCode, pendingApproval.getId());

			BasicDBObject newDocument = new BasicDBObject();

			if (Utility.isNotNull(userDto.getFullName()))
				newDocument.append("users.$.fullName", userDto.getFullName());
			if (Utility.isNotNull(userDto.getPhoneNumber()))
				newDocument.append("users.$.phoneNumber", userDto.getPhoneNumber());
			if (Utility.isNotNull(userDto.getEmail()))
				newDocument.append("users.$.email", userDto.getEmail());
			if (Utility.isNotNull(userDto.getStatus()))
				newDocument.append("users.$.status", userDto.getStatus().toUpperCase());
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
	
	public void updateMemberUserPA(HttpServletRequest request, String memberCode, String username,
			ApprovalUpdateUserDTO userDto, long refId) {
		String methodName = "updateMemberUserPA";
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
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String insertMemberUserUpdatePA(UserInfoDTO userInfo, String memberCode, String username,
			ApprovalUpdateUserDTO userDto, long refId) {
		String methodName = "insertMemberUserUpdatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setMemberCode(memberCode);

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_USER_UPDATE);
			pendingData.setCollectionName("members");
			pendingData.setQueryField("code");
			pendingData.setQueryValue(memberCode);
			pendingData.setQueryField2("users.username");
			pendingData.setQueryValue2(username);
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setOldValue(Utility.getGson().toJson(userDto.getOldData()));
			pendingData.setPendingValue(Utility.getGson().toJson(userDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_USER_UPDATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_USER_UPDATE_NAME);
			pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription2(SystemFunctionCode.MEMBER_USER_CREATE_DESC, username,
					memberCode));
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
	
	public UserDTO getMemberUser(HttpServletRequest request, String memberCode, String username, long refId) {
		String methodName = "getMemberUser";
		try {
			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("members");

			Document query = new Document().append("code", memberCode);
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			if (Utility.isNotNull(userInfo.getMemberCode())) {
				query = new Document();
			    query.append("$and", Arrays.asList(
			            new Document()
			                    .append("code", memberCode),
			            new Document()
			                    .append("code", userInfo.getMemberCode())
			        )
			    );
			}
            
			List<? extends Bson> pipeline = Arrays.asList(
					new Document().append("$match", query),
					new Document().append("$unwind", new Document().append("path", "$users")),
					new Document().append("$match", new Document().append("users.username", username)),
					new Document().append("$project", new Document().append("_id", 0.0).append("users", 1.0)),
					new Document().append("$replaceRoot", new Document().append("newRoot", "$users")));

			Document resultDoc = collection.aggregate(pipeline).first();
			UserDTO memberUserDto = mongoTemplate.getConverter().read(UserDTO.class, resultDoc);
			return memberUserDto;
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void saveMemberUserRoles(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "saveMemberUserRoles";
		try {
			String memberCode = pendingApproval.getPendingData().getQueryValue();
			String username = pendingApproval.getPendingData().getQueryValue2();
			UserRolesDTO userDto = Utility.getGson().fromJson(pendingApproval.getPendingData().getPendingValue(), UserRolesDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog2(userInfo, request,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_USER_ROLES,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_USER_ROLES_DESC, username, memberCode,
					pendingApproval.getId());

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("members");
			
			List<Document> roles = new ArrayList<Document>();
			for (UserRole role : userDto.getRoles()) {
				Document roleDoc = new Document();
				roleDoc.append("name", role.getName());
				roleDoc.append("description", role.getDescription());
				roleDoc.append("status", role.getStatus().toUpperCase());
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
	}
	
	public void saveMemberUserRolesPA(HttpServletRequest request, String memberCode, String username,
			ApprovalUserRolesDTO userDto, long refId) {
		String methodName = "saveMemberUserRolesPA";
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
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String insertMemberUserRoleCreatePA(UserInfoDTO userInfo, String memberCode, String username,
			ApprovalUserRolesDTO userDto, long refId) {
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
			pendingData.setOldValue(Utility.getGson().toJson(userDto.getOldData()));
			pendingData.setPendingValue(Utility.getGson().toJson(userDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_USER_ROLES_ASSIGN_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_USER_ROLES_ASSIGN_CREATE_NAME);
			pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription2(SystemFunctionCode.MEMBER_USER_ROLES_ASSIGN_CREATE_DESC,
					username, memberCode));
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

	public void saveMemberUserFunctions(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "saveMemberUserFunctions";
		try {
			String memberCode = pendingApproval.getPendingData().getQueryValue();
			String username = pendingApproval.getPendingData().getQueryValue2();
			FunctionsDTO userDto = Utility.getGson().fromJson(pendingApproval.getPendingData().getPendingValue(), FunctionsDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog2(userInfo, request,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_USER_FUNCTIONS,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_MEMBER_USER_FUNCTIONS_DESC, username, memberCode,
					pendingApproval.getId());

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
	}
	
	public void saveMemberUserFunctionsPA(HttpServletRequest request, String memberCode, String username,
			ApprovalFunctionsDTO userDto, long refId) {
		String methodName = "saveMemberUserFunctionsPA";
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
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String insertMemberUserFunctionsCreatePA(UserInfoDTO userInfo, String memberCode, String username,
			ApprovalFunctionsDTO userDto, long refId) {
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
			pendingData.setOldValue(Utility.getGson().toJson(userDto.getOldData()));
			pendingData.setPendingValue(Utility.getGson().toJson(userDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_USER_FUNCTIONS_ASSIGN_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_USER_FUNCTIONS_ASSIGN_CREATE_NAME);
			pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription2(SystemFunctionCode.MEMBER_USER_FUNCTIONS_ASSIGN_CREATE_DESC,
					username, memberCode));
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
	
	public List<RoleFunction> getMemberFunctions(HttpServletRequest request, String memberCode, long refId) {
		String methodName = "getMemberFunctions";
		try {
			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("members");
			
			Document query = new Document().append("code", memberCode);
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			if (Utility.isNotNull(userInfo.getMemberCode())) {
				query = new Document();
			    query.append("$and", Arrays.asList(
			            new Document()
			                    .append("code", memberCode),
			            new Document()
			                    .append("code", userInfo.getMemberCode())
			        )
			    );
			}
            
			List<? extends Bson> pipeline = Arrays.asList(
                    new Document()
                            .append("$match", query), 
                    new Document()
                            .append("$lookup", new Document()
                                    .append("from", "system_roles")
                                    .append("localField", "role.name")
                                    .append("foreignField", "name")
                                    .append("as", "roleObj")
                            ), 
                    new Document().append("$unwind", new Document().append("path", "$roleObj")),
                    new Document()
                            .append("$project", new Document()
                                    .append("_id", 0.0)
                                    .append("roleFunctions", "$roleObj.functions")
                                    .append("specificFunctions", "$functions")
                            )
            );
			
			Document result = collection.aggregate(pipeline).first();
			RoleFunctionsDTO roleFuncsDto = mongoTemplate.getConverter().read(RoleFunctionsDTO.class, result);
			if (Utility.isNull(roleFuncsDto)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			List<RoleFunction> allFunctions = new ArrayList<RoleFunction>();
			if (roleFuncsDto.getRoleFunctions() != null && roleFuncsDto.getRoleFunctions().size() > 0) {
				allFunctions.addAll(roleFuncsDto.getRoleFunctions());
			}
			if (roleFuncsDto.getSpecificFunctions() != null && roleFuncsDto.getSpecificFunctions().size() > 0) {
				allFunctions.addAll(roleFuncsDto.getSpecificFunctions());
			}
			
			List<RoleFunction> allFunctionsWithoutDuplicates = new ArrayList<RoleFunction>(new HashSet<RoleFunction>(allFunctions));
			return allFunctionsWithoutDuplicates;
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public MemberCommoditiesDTO getMemberCommodities(HttpServletRequest request, String memberCode, long refId) {
		String methodName = "getMemberCommodities";
		try {
			if (!memberRepository.existsMemberByCode(memberCode)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("members");
			
			Document query = new Document().append("code", memberCode);
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			if (Utility.isNotNull(userInfo.getMemberCode())) {
				query = new Document();
			    query.append("$and", Arrays.asList(
			            new Document()
			                    .append("code", memberCode),
			            new Document()
			                    .append("code", userInfo.getMemberCode())
			        )
			    );
			}
            
            Document projection = new Document();
            projection.append("_id", 0.0);
            projection.append("commodities.commodityCode", 1.0);
            projection.append("commodities.commodityName", 1.0);
            
            Document result = collection.find(query).projection(projection).first();
            MemberCommoditiesDTO memberDto = mongoTemplate.getConverter().read(MemberCommoditiesDTO.class, result);
			return memberDto;
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public List<ListElementDTO> getMemberBrokerList(HttpServletRequest request, String memberCode, long refId) {
		List<ListElementDTO> brokerList = new ArrayList<ListElementDTO>();
		
		MongoDatabase database = MongoDBConnection.getMongoDatabase();
		MongoCollection<Document> collection = database.getCollection("brokers");
		
		Document query = new Document();
		query.append("memberCode", memberCode);
		// get redis user info
		UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
		if (Utility.isNotNull(userInfo.getMemberCode())) {
			query = new Document();
		    query.append("$and", Arrays.asList(
		            new Document()
		                    .append("memberCode", memberCode),
		            new Document()
		                    .append("memberCode", userInfo.getMemberCode())
		        )
		    );
		}
		
		Document projection = new Document();
		projection.append("_id", 0.0);
		projection.append("code", 1.0);
		projection.append("name", 1.0);
		
		MongoCursor<Document> cur = collection.find(query).projection(projection).iterator();
		while (cur.hasNext()) {
			ListElementDTO elemDto = mongoTemplate.getConverter().read(ListElementDTO.class, cur.next());
			if (elemDto != null) brokerList.add(elemDto);
		}
		
		return brokerList;
	}
	
	public List<ListElementDTO> getMemberCollaboratorList(HttpServletRequest request, String memberCode, long refId) {
		List<ListElementDTO> brokerList = new ArrayList<ListElementDTO>();
		
		MongoDatabase database = MongoDBConnection.getMongoDatabase();
		MongoCollection<Document> collection = database.getCollection("collaborators");
		
		Document query = new Document();
		query.append("memberCode", memberCode);
		// get redis user info
		UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
		if (Utility.isNotNull(userInfo.getMemberCode())) {
			query = new Document();
		    query.append("$and", Arrays.asList(
		            new Document()
		                    .append("memberCode", memberCode),
		            new Document()
		                    .append("memberCode", userInfo.getMemberCode())
		        )
		    );
		}
		
		Document projection = new Document();
		projection.append("_id", 0.0);
		projection.append("code", 1.0);
		projection.append("name", 1.0);
		
		MongoCursor<Document> cur = collection.find(query).projection(projection).iterator();
		while (cur.hasNext()) {
			ListElementDTO elemDto = mongoTemplate.getConverter().read(ListElementDTO.class, cur.next());
			if (elemDto != null) brokerList.add(elemDto);
		}
		
		return brokerList;
	}
	
	public void moveAllInvestorsToNewMember(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "moveAllInvestorsToNewMember";
		try {
			String memberCode = pendingApproval.getPendingData().getQueryValue();
			ChangeGroupDTO changeGroupDto = Utility.getGson().fromJson(pendingApproval.getPendingData().getPendingValue(), ChangeGroupDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog2(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_MEMBER_MOVE_ALL_INVESTORS,
					ActivityLogService.ACTIVITY_APPROVAL_MEMBER_MOVE_ALL_INVESTORS_DESC, memberCode, changeGroupDto.getGroupCode(), pendingApproval.getId());

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> invCollection = database.getCollection("investors");

			BasicDBObject invQuery = new BasicDBObject();
			invQuery.put("memberCode", memberCode);

			BasicDBObject invUpdateDoc = new BasicDBObject();
			invUpdateDoc.append("memberCode", changeGroupDto.getGroupCode());
			invUpdateDoc.append("memberName", changeGroupDto.getGroupName());
			invUpdateDoc.append("brokerCode", null);
			invUpdateDoc.append("brokerName", null);
			invUpdateDoc.append("collaboratorCode", null);
			invUpdateDoc.append("collaboratorName", null);
			invUpdateDoc.append("orderLimit", getMemberOrderLimit(changeGroupDto.getGroupCode()));
			
			BasicDBObject invUpdate = new BasicDBObject();
			invUpdate.put("$set", invUpdateDoc);
			
			invCollection.updateMany(invQuery, invUpdate);

			MongoCollection<Document> loginAdmUserCollection = database.getCollection("login_investor_users");
			BasicDBObject loginAdmUserQuery = new BasicDBObject();
			loginAdmUserQuery.put("memberCode", memberCode);

			BasicDBObject newLoginAdmUser = new BasicDBObject();
			newLoginAdmUser.put("memberCode", changeGroupDto.getGroupCode());
			newLoginAdmUser.put("memberName", changeGroupDto.getGroupName());
			newLoginAdmUser.put("brokerCode", null);
			newLoginAdmUser.put("brokerName", null);
			newLoginAdmUser.put("collaboratorCode", null);
			newLoginAdmUser.put("collaboratorName", null);

			BasicDBObject updateObj = new BasicDBObject();
			updateObj.put("$set", newLoginAdmUser);

			loginAdmUserCollection.updateMany(loginAdmUserQuery, updateObj);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private long getMemberOrderLimit(String memberCode) {
		MongoDatabase database = MongoDBConnection.getMongoDatabase();
		MongoCollection<Document> collection = database.getCollection("members");
		
		Document query = new Document();
		query.append("code", memberCode);
		
		Document projection = new Document();
		projection.append("_id", 0.0);
		projection.append("orderLimit", 1.0);
		
		Document result = collection.find(query).projection(projection).first();
		MemberDTO memberDto = mongoTemplate.getConverter().read(MemberDTO.class, result);
		if (memberDto != null) {
			return memberDto.getOrderLimit();
		} else return 0;
	}
	
	public void moveAllInvestorsToNewMemberPA(HttpServletRequest request, ApprovalChangeGroupDTO changeGroupDto, long refId) {
		String methodName = "moveAllInvestorsToAnotherMemberPA";
		try {
			if (!memberRepository.existsMemberByCode(changeGroupDto.getPendingData().getGroupCode())) {
				throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
			}
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertMoveAllInvestorsToNewMemberPA(userInfo, changeGroupDto, refId);
			// send activity log
			activityLogService.sendActivityLog2(userInfo, request, ActivityLogService.ACTIVITY_MEMBER_MOVE_ALL_INVESTORS,
					ActivityLogService.ACTIVITY_MEMBER_MOVE_ALL_INVESTORS_DESC, changeGroupDto.getOldData().getGroupCode(), changeGroupDto.getPendingData().getGroupCode(), approvalId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private String insertMoveAllInvestorsToNewMemberPA(UserInfoDTO userInfo, ApprovalChangeGroupDTO changeGroupDto, long refId) {
		String methodName = "insertMoveAllInvestorsToAnotherMemberPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_MOVE_ALL_INVESTORS);
			pendingData.setCollectionName("investors");
			pendingData.setQueryField("memberCode");
			pendingData.setQueryValue(changeGroupDto.getOldData().getGroupCode());
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setOldValue(Utility.getGson().toJson(changeGroupDto.getOldData()));
			pendingData.setPendingValue(Utility.getGson().toJson(changeGroupDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_MOVE_ALL_INVESTORS_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_MOVE_ALL_INVESTORS_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription2(SystemFunctionCode.MEMBER_MOVE_ALL_INVESTORS_DESC, changeGroupDto.getOldData().getGroupCode(), changeGroupDto.getPendingData().getGroupCode()));
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
}
