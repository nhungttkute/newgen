package com.newgen.am.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

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
import com.newgen.am.dto.DeptUserDTO;
import com.newgen.am.dto.EmailDTO;
import com.newgen.am.dto.MemberCSV;
import com.newgen.am.dto.MemberDTO;
import com.newgen.am.dto.MemberUserDTO;
import com.newgen.am.dto.UpdateMemberDTO;
import com.newgen.am.dto.UserInfoDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.DeptUser;
import com.newgen.am.model.LoginAdminUser;
import com.newgen.am.model.Member;
import com.newgen.am.model.NestedObjectInfo;
import com.newgen.am.model.PendingApproval;
import com.newgen.am.model.PendingData;
import com.newgen.am.model.SystemRole;
import com.newgen.am.model.UserRole;
import com.newgen.am.repository.LoginAdminUserRepository;
import com.newgen.am.repository.MemberRepository;
import com.newgen.am.repository.PendingApprovalRepository;
import com.newgen.am.repository.SystemRoleRepository;

@Service
public class MemberService {
	private String className = "MemberService";

	@Autowired
	DBSequenceService dbSeqService;

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
				delegate.append("scannedEndIdCard", memberDto.getCompany().getDelegate().getScannedEndIdCard());
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
				
				// insert new member
				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("members");
				collection.insertOne(newMember);
				
				// insert new member's master user to login_admin_users
				createMasterMemberUser(request, memberDto, memberId.toString(), refId);
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
	
	public void createMasterMemberUser(HttpServletRequest request, MemberDTO memberDto, String memberId, long refId) {
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

				// select default member role
				SystemRole memberRole = sysRoleRepository.findByName(Constant.MEMBER_DEFAULT_ROLE);
				Document masterUserRole = new Document();
				masterUserRole.append("_id", memberRole.getId());
				masterUserRole.append("name", memberRole.getName());
				masterUserRole.append("description", memberRole.getDescription());
				
				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("members");

				ObjectId memberUserId = new ObjectId();
				Document masterUser = new Document();
				masterUser.append("_id", memberUserId);
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
				masterUser.append("roles", Arrays.asList(masterUserRole));

				BasicDBObject query = new BasicDBObject();
				query.put("_id", new ObjectId(memberId));

				collection.updateOne(query, Updates.addToSet("users", masterUser));

				// insert loginAdminUser
				MemberUserDTO memberUser = new MemberUserDTO();
				memberUser.setUsername(username);
				
				String password = Utility.generateRandomPassword();
				String pin = Utility.generateRandomPin();
				createLoginAdminUser(memberId, memberUserId.toString(), memberUser, password, pin, refId);

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
	
	private LoginAdminUser createLoginAdminUser(String memberId, String memberUserId, MemberUserDTO memberUserDto, String password, String pin,
			long refId) {
		String methodName = "createLoginAdminUser";
		try {
			LoginAdminUser loginAdmUser = modelMapper.map(memberUserDto, LoginAdminUser.class);
			loginAdmUser.setPassword(passwordEncoder.encode(password));
			loginAdmUser.setPin(passwordEncoder.encode(pin));
			loginAdmUser.setStatus(memberUserDto.getStatus());
			loginAdmUser.setMemberId(memberId);
			loginAdmUser.setMemberUserId(memberUserId);
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
	
	public void updateMember(HttpServletRequest request, String memberId, UpdateMemberDTO memberDto, long refId) {
		String methodName = "updateMember";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertMemberUpdatePA(userInfo, memberId, memberDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_UPDATE_DEPARTMENT,
					ActivityLogService.ACTIVITY_UPDATE_DEPARTMENT_DESC, memberId, approvalId);

			Member member = memberRepository.findById(memberId).get();
			if (Utility.isNotNull(memberDto.getName()))
				member.setName(memberDto.getName());
			if (Utility.isNotNull(memberDto.getStatus()))
				member.setStatus(memberDto.getStatus());
			if (Utility.isNotNull(memberDto.getNote()))
				member.setNote(memberDto.getNote());
			if (Utility.isNotNull(memberDto.getCompany())) {
				if (Utility.isNotNull(memberDto.getCompany().getName()))
					member.getCompany().setName((memberDto.getCompany().getName()));
				if (Utility.isNotNull(memberDto.getCompany().getTaxCode()))
					member.getCompany().setTaxCode((memberDto.getCompany().getTaxCode()));
				if (Utility.isNotNull(memberDto.getCompany().getAddress()))
					member.getCompany().setAddress((memberDto.getCompany().getAddress()));
				if (Utility.isNotNull(memberDto.getCompany().getPhoneNumber()))
					member.getCompany().setPhoneNumber((memberDto.getCompany().getPhoneNumber()));
				if (Utility.isNotNull(memberDto.getCompany().getFax()))
					member.getCompany().setFax((memberDto.getCompany().getFax()));
				if (Utility.isNotNull(memberDto.getCompany().getEmail()))
					member.getCompany().setEmail((memberDto.getCompany().getEmail()));
				
				if (Utility.isNotNull(memberDto.getCompany().getDelegate())) {
					if (Utility.isNotNull(memberDto.getCompany().getDelegate().getFullName()))
						member.getCompany().getDelegate().setFullName((memberDto.getCompany().getDelegate().getFullName()));
					if (Utility.isNotNull(memberDto.getCompany().getDelegate().getBirthDay()))
						member.getCompany().getDelegate().setBirthDay((memberDto.getCompany().getDelegate().getBirthDay()));
					if (Utility.isNotNull(memberDto.getCompany().getDelegate().getIdentityCard()))
						member.getCompany().getDelegate().setIdentityCard((memberDto.getCompany().getDelegate().getIdentityCard()));
					if (Utility.isNotNull(memberDto.getCompany().getDelegate().getIdCreatedDate()))
						member.getCompany().getDelegate().setIdCreatedDate((memberDto.getCompany().getDelegate().getIdCreatedDate()));
					if (Utility.isNotNull(memberDto.getCompany().getDelegate().getIdCreatedLocation()))
						member.getCompany().getDelegate().setIdCreatedLocation((memberDto.getCompany().getDelegate().getIdCreatedLocation()));
					if (Utility.isNotNull(memberDto.getCompany().getDelegate().getEmail()))
						member.getCompany().getDelegate().setEmail((memberDto.getCompany().getDelegate().getEmail()));
					if (Utility.isNotNull(memberDto.getCompany().getDelegate().getPhoneNumber()))
						member.getCompany().getDelegate().setPhoneNumber((memberDto.getCompany().getDelegate().getPhoneNumber()));
					if (Utility.isNotNull(memberDto.getCompany().getDelegate().getAddress()))
						member.getCompany().getDelegate().setAddress((memberDto.getCompany().getDelegate().getAddress()));
					if (Utility.isNotNull(memberDto.getCompany().getDelegate().getScannedFrontIdCard()))
						member.getCompany().getDelegate().setScannedFrontIdCard((memberDto.getCompany().getDelegate().getScannedFrontIdCard()));
					if (Utility.isNotNull(memberDto.getCompany().getDelegate().getScannedEndIdCard()))
						member.getCompany().getDelegate().setScannedEndIdCard((memberDto.getCompany().getDelegate().getScannedEndIdCard()));
					if (Utility.isNotNull(memberDto.getCompany().getDelegate().getScannedSignature()))
						member.getCompany().getDelegate().setScannedSignature((memberDto.getCompany().getDelegate().getScannedSignature()));
				}
			}
			memberRepository.save(member);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public String insertMemberUpdatePA(UserInfoDTO userInfo, String memberId, UpdateMemberDTO memberDto, long refId) {
		String methodName = "insertMemberUpdatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_UPDATE);
			pendingData.setCollectionName("members");
			pendingData.setQueryField("_id");
			pendingData.setQueryValue(memberId);
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setValue(new Gson().toJson(memberDto));

			Member member = memberRepository.findById(memberId).get();

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_UPDATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_UPDATE_NAME);
			pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(),
					member.getCode()));
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

	
	public MemberDTO getMemberDetail(String memberId, long refId) {
		String methodName = "getMemberDetail";
		try {
			Member member = memberRepository.findById(memberId).get();
			MemberDTO memberDto = modelMapper.map(member, MemberDTO.class);
			return memberDto;
		} catch (NoSuchElementException e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
