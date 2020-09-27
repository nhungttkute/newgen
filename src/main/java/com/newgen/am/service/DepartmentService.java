/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import com.newgen.am.dto.ApprovalUpdateDepartmentDTO;
import com.newgen.am.dto.ApprovalUpdateUserDTO;
import com.newgen.am.dto.ApprovalUserRolesDTO;
import com.newgen.am.dto.BasePagination;
import com.newgen.am.dto.ChangeDepartmentDTO;
import com.newgen.am.dto.DepartmentCSV;
import com.newgen.am.dto.DepartmentDTO;
import com.newgen.am.dto.EmailDTO;
import com.newgen.am.dto.FunctionsDTO;
import com.newgen.am.dto.NotifyServiceDTO;
import com.newgen.am.dto.UpdateDepartmentDTO;
import com.newgen.am.dto.UpdateUserDTO;
import com.newgen.am.dto.UserCSV;
import com.newgen.am.dto.UserDTO;
import com.newgen.am.dto.UserInfoDTO;
import com.newgen.am.dto.UserRolesDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.LoginAdminUser;
import com.newgen.am.model.NestedObjectInfo;
import com.newgen.am.model.PendingApproval;
import com.newgen.am.model.PendingData;
import com.newgen.am.model.RoleFunction;
import com.newgen.am.model.UserRole;
import com.newgen.am.repository.DepartmentRepository;
import com.newgen.am.repository.LoginAdminUserRepository;
import com.newgen.am.repository.PendingApprovalRepository;

/**
 *
 * @author nhungtt
 */
@Service
public class DepartmentService {

	private String className = "DepartmentService";

	@Autowired
	DBSequenceService dbSeqService;

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	DepartmentRepository deptRepository;

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
	PasswordEncoder passwordEncoder;

	public BasePagination<DepartmentDTO> list(HttpServletRequest request, long refId) {
		String methodName = "list";
		BasePagination<DepartmentDTO> pagination = null;
		try {
			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "", refId);

			List<? extends Bson> pipeline = Arrays.asList(new Document().append("$match", searchCriteria.getQuery()),
					new Document().append("$sort", searchCriteria.getSort()),
					new Document().append("$project",
							new Document().append("_id", new Document().append("$toString", "$_id")).append("code", 1.0)
									.append("name", 1.0).append("status", 1.0).append("note", 1.0)
									.append("createdDate", 1.0)),
					new Document().append("$facet",
							new Document().append("stage1", Arrays.asList(new Document().append("$count", "total")))
									.append("stage2",
											Arrays.asList(new Document().append("$skip", searchCriteria.getSkip()),
													new Document().append("$limit", searchCriteria.getLimit())))),
					new Document().append("$unwind", new Document().append("path", "$stage1")), new Document().append(
							"$project", new Document().append("count", "$stage1.total").append("data", "$stage2")));
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("departments");
			Document resultDoc = collection.aggregate(pipeline).first();
			pagination = mongoTemplate.getConverter().read(BasePagination.class, resultDoc);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return pagination;
	}

	public List<DepartmentCSV> listCsv(HttpServletRequest request, long refId) {
		String methodName = "listCsv";
		List<DepartmentCSV> deptCSVList = new ArrayList<>();
		try {
			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "", refId);

			List<? extends Bson> pipeline = Arrays
					.asList(new Document().append("$match", searchCriteria.getQuery()),
							new Document().append("$sort", searchCriteria.getSort()),
							new Document().append("$project",
									new Document().append("_id", new Document().append("$toString", "$_id"))
											.append("code", 1.0).append("name", 1.0).append("status", 1.0).append("note", 1.0)
											.append("createdDate", new Document().append("$dateToString",
													new Document().append("format", "%d/%m/%Y %H:%M:%S").append("date",
															new Document().append("$toDate", "$createdDate"))))));

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("departments");
			MongoCursor<Document> cur = collection.aggregate(pipeline).iterator();

			while (cur.hasNext()) {
				DepartmentCSV deptCSVDto = mongoTemplate.getConverter().read(DepartmentCSV.class, cur.next());
				if (deptCSVDto != null)
					deptCSVList.add(deptCSVDto);
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return deptCSVList;
	}

	public void createDepartment(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "createDeparment";
		
		try {
			DepartmentDTO deptDto = new Gson().fromJson(pendingApproval.getPendingData().getPendingValue(), DepartmentDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_CREATE_DEPARTMENT,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_DEPARTMENT_DESC, deptDto.getCode(), pendingApproval.getId());

			Document newDept = new Document();
			newDept.append("createdUser", Utility.getCurrentUsername());
			newDept.append("createdDate", System.currentTimeMillis());
			newDept.append("code", deptDto.getCode().trim());
			newDept.append("name", deptDto.getName());
			newDept.append("status", Constant.STATUS_ACTIVE);
			newDept.append("note", deptDto.getNote());

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("departments");
			collection.insertOne(newDept);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void createDepartmentPA(HttpServletRequest request, DepartmentDTO deptDto, long refId) {
		String methodName = "createDepartmentPA";
		boolean existedDept = false;
		try {
			existedDept = deptRepository.existsDepartmentByCode(deptDto.getCode());
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!existedDept) {
			try {
				// get redis user info
				UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
				// insert data to pending_approvals
				String approvalId = insertDepartmentCreatePA(userInfo, deptDto, refId);
				// send activity log
				activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_DEPARTMENT,
						ActivityLogService.ACTIVITY_CREATE_DEPARTMENT_DESC, deptDto.getCode(), approvalId);
			} catch (Exception e) {
				AMLogger.logError(className, methodName, refId, e);
				throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			AMLogger.logMessage(className, methodName, refId, "This deparment code already exists");
			throw new CustomException(ErrorMessage.DOCUMENT_ALREADY_EXISTED, HttpStatus.OK);
		}
	}

	private String insertDepartmentCreatePA(UserInfoDTO userInfo, DepartmentDTO deptDto, long refId) {
		String methodName = "insertDepartmentCreatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.DEPARTMENT_CREATE);
			pendingData.setCollectionName("departments");
			pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
			pendingData.setPendingValue(new Gson().toJson(deptDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_DEPARTMENT_INFO_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.DEPARTMENT_INFO_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), deptDto.getCode()));
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

	public List<UserCSV> listDeptUsersCsv(HttpServletRequest request, String deptId, long refId) {
		String methodName = "listDeptUsers";
		List<UserCSV> deptUserList = new ArrayList<>();
		try {
			Document query1 = new Document();
			query1.append("_id", new ObjectId(deptId));

			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "users.", refId);

			List<? extends Bson> pipeline = Arrays
					.asList(new Document().append("$match", query1),
							new Document().append("$unwind", new Document().append("path", "$users")),
							new Document().append("$match", searchCriteria.getQuery()),
							new Document().append("$sort", searchCriteria.getSort()),
							new Document().append("$project",
									new Document().append("_id", new Document().append("$toString", "$users._id"))
											.append("username", "$users.username").append("fullName", "$users.fullName")
											.append("email", "$users.email").append("phoneNumber", "$users.phoneNumber")
											.append("status", "$users.status")
											.append("isPasswordExpiryCheck", "$users.isPasswordExpiryCheck")
											.append("passwordExpiryDays", "$users.passwordExpiryDays")
											.append("expiryAlertDays", "$users.expiryAlertDays")
											.append("createdDate", new Document().append("$dateToString",
													new Document().append("format", "%d/%m/%Y %H:%M:%S").append("date",
															new Document().append("$toDate", "$users.createdDate")))))
							);

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("departments");
			try (MongoCursor<Document> cur = collection.aggregate(pipeline).iterator()) {

				while (cur.hasNext()) {
					UserCSV userCSV = mongoTemplate.getConverter().read(UserCSV.class, cur.next());
					if (userCSV != null)
						deptUserList.add(userCSV);
				}
			}

		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return deptUserList;
	}

	public BasePagination<UserDTO> listDeptUsers(HttpServletRequest request, String deptId, long refId) {
		String methodName = "listDeptUsers";
		BasePagination<UserDTO> pagination = null;
		try {
			Document query1 = new Document();
			query1.append("_id", new ObjectId(deptId));

			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "users.", refId);

			List<? extends Bson> pipeline = Arrays.asList(new Document().append("$match", query1),
					new Document().append("$unwind", new Document().append("path", "$users")),
					new Document().append("$match", searchCriteria.getQuery()),
					new Document().append("$sort", searchCriteria.getSort()),
					new Document().append("$project",
							new Document().append("_id", new Document().append("$toString", "$users._id"))
									.append("username", "$users.username").append("fullName", "$users.fullName")
									.append("email", "$users.email").append("phoneNumber", "$users.phoneNumber")
									.append("status", "$users.status")
									.append("isPasswordExpiryCheck", "$users.isPasswordExpiryCheck")
									.append("passwordExpiryDays", "$users.passwordExpiryDays")
									.append("expiryAlertDays", "$users.expiryAlertDays")
									.append("createdDate", "$users.createdDate")),
					new Document().append("$facet",
							new Document().append("stage1", Arrays.asList(new Document().append("$count", "total")))
									.append("stage2",
											Arrays.asList(new Document().append("$skip", searchCriteria.getSkip()),
													new Document().append("$limit", searchCriteria.getLimit())))),
					new Document().append("$unwind", new Document().append("path", "$stage1")), new Document().append(
							"$project", new Document().append("count", "$stage1.total").append("data", "$stage2")));
			
			System.out.println("pipeline: " + new Gson().toJson(pipeline));
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("departments");
			Document resultDoc = collection.aggregate(pipeline).first();
			pagination = mongoTemplate.getConverter().read(BasePagination.class, resultDoc);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return pagination;
	}

	public DepartmentDTO getDepartmentDetail(String deptId, long refId) {
		String methodName = "getDepartmentDetail";
		try {
			Document query = new Document();
            query.append("_id", new ObjectId(deptId));
            
            MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("departments");
			Document deptDoc = collection.find(query).first();
			DepartmentDTO deptDto = mongoTemplate.getConverter().read(DepartmentDTO.class, deptDoc);
			return deptDto;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void updateDepartment(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "udpdateDeparment";
		try {
			String deptId = pendingApproval.getPendingData().getQueryValue();
			UpdateDepartmentDTO deptDto = new Gson().fromJson(pendingApproval.getPendingData().getPendingValue(), UpdateDepartmentDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_UPDATE_DEPARTMENT,
					ActivityLogService.ACTIVITY_APPROVAL_UPDATE_DEPARTMENT_DESC, getDepartmentInfo(deptId).getCode(), pendingApproval.getId());

			if (!deptRepository.existsById(deptId)) {
				throw new CustomException(ErrorMessage.RESULT_NOT_FOUND, HttpStatus.OK);
			}
			
			boolean isStatusUpdated = false;
			Document updateDoc = new Document();
			if (Utility.isNotNull(deptDto.getName())) updateDoc.append("name", deptDto.getName());
			if (Utility.isNotNull(deptDto.getStatus())) {
				isStatusUpdated = true;
				updateDoc.append("status", deptDto.getStatus().toUpperCase());
				updateDoc.append("users.$[].status", deptDto.getStatus().toUpperCase());
			}
			if (Utility.isNotNull(deptDto.getNote())) updateDoc.append("note", deptDto.getNote());
			
			updateDoc.put("lastModifiedUser", Utility.getCurrentUsername());
			updateDoc.put("lastModifiedDate", System.currentTimeMillis());
			
			Document query = new Document();
			query.append("_id", new ObjectId(deptId));
			query.append("users", new Document().append("$exists", true));
			
			Document update = new Document();
			update.append("$set", updateDoc);
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> deptCollection = database.getCollection("departments");
			deptCollection.updateMany(query, update);
			
			// update status of all login admin users belong to this department
			if (isStatusUpdated) {
				MongoCollection<Document> loginAdmCollection = database.getCollection("login_admin_users");
				
				Document loginAdmQuery = new Document();
				loginAdmQuery.append("deptCode", getDepartmentInfo(deptId).getCode());
				
				Document loginAdmUpdateDoc = new Document();
				loginAdmUpdateDoc.append("status", deptDto.getStatus().toUpperCase());
				
				Document loginAdmUpdate = new Document();
				loginAdmUpdate.append("$set", loginAdmUpdateDoc);
				
				loginAdmCollection.updateMany(loginAdmQuery, loginAdmUpdate);
				
				// logout all users if status is invactive
				if (Constant.STATUS_INACTIVE.equalsIgnoreCase(deptDto.getStatus())) {
					List<? extends Bson> pipeline = Arrays.asList(
		                    new Document()
		                            .append("$match", new Document()
		                                    .append("_id", new ObjectId(deptId))
		                            ), 
		                    new Document()
		                            .append("$project", new Document()
		                                    .append("_id", 0.0)
		                                    .append("userID", new Document()
		                                            .append("$concatArrays", Arrays.asList(
		                                                    "$users.username"
		                                                )
		                                            )
		                                    )
		                            )
		            );
					Document result = deptCollection.aggregate(pipeline).first();
					NotifyServiceDTO notifyDto = mongoTemplate.getConverter().read(NotifyServiceDTO.class, result);
					
					Utility.sendHandleLogout(notifyDto.getUserID(), refId);
				}
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void updateDepartmentPA(HttpServletRequest request, String deptId, ApprovalUpdateDepartmentDTO deptDto, long refId) {
		String methodName = "updateDepartmentPA";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertDepartmentUpdatePA(userInfo, deptId, deptDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_UPDATE_DEPARTMENT,
					ActivityLogService.ACTIVITY_UPDATE_DEPARTMENT_DESC, getDepartmentInfo(deptId).getCode(), approvalId);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String insertDepartmentUpdatePA(UserInfoDTO userInfo, String deptId, ApprovalUpdateDepartmentDTO deptDto,
			long refId) {
		String methodName = "insertDepartmentUpdatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.DEPARTMENT_UPDATE);
			pendingData.setCollectionName("departments");
			pendingData.setQueryField("_id");
			pendingData.setQueryValue(deptId);
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setOldValue(new Gson().toJson(deptDto.getOldData()));
			pendingData.setPendingValue(new Gson().toJson(deptDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_DEPARTMENT_INFO_UPDATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.DEPARTMENT_INFO_UPDATE_NAME);
			pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(),
					getDepartmentInfo(deptId).getCode()));
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

	public void createDepartmentUser(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "createDepartmentUser";
		
		try {
			String deptId = pendingApproval.getPendingData().getQueryValue();
			UserDTO deptUserDto = new Gson().fromJson(pendingApproval.getPendingData().getPendingValue(), UserDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_DEPARTMENT_USER,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_DEPARTMENT_USER_DESC, deptUserDto.getUsername(), pendingApproval.getId());

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("departments");

			Document newDeptUser = new Document();
			newDeptUser.append("_id", new ObjectId());
			newDeptUser.append("username", deptUserDto.getUsername());
			newDeptUser.append("fullName", deptUserDto.getFullName());
			newDeptUser.append("email", deptUserDto.getEmail());
			newDeptUser.append("phoneNumber", deptUserDto.getPhoneNumber());
			newDeptUser.append("status", Constant.STATUS_ACTIVE);
			newDeptUser.append("note", deptUserDto.getNote());
			newDeptUser.append("isPasswordExpiryCheck", deptUserDto.getIsPasswordExpiryCheck());
			newDeptUser.append("passwordExpiryDays", deptUserDto.getPasswordExpiryDays());
			newDeptUser.append("expiryAlertDays", deptUserDto.getExpiryAlertDays());
			newDeptUser.append("createdUser", Utility.getCurrentUsername());
			newDeptUser.append("createdDate", System.currentTimeMillis());
			newDeptUser.append("lastModifiedDate", System.currentTimeMillis());

			BasicDBObject query = new BasicDBObject();
			query.put("_id", new ObjectId(deptId));

			collection.updateOne(query, Updates.addToSet("users", newDeptUser));

			// insert loginAdminUser
			String password = Utility.generateRandomPassword();
			String pin = Utility.generateRandomPin();
			LoginAdminUser newLoginAdmUser = createLoginAdminUser(deptId, deptUserDto, password, pin, refId);

			// send email
			sendCreateNewUserEmail(deptUserDto.getEmail(), newLoginAdmUser.getUsername(), password, pin, refId);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void createDepartmentUserPA(HttpServletRequest request, String deptId, UserDTO deptUserDto, long refId) {
		String methodName = "createDepartmentUserPA";
		boolean existedUser = false;
		try {
			existedUser = loginAdmUserRepo.existsLoginAdminUserByUsername(deptUserDto.getUsername());
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!existedUser) {
			try {
				//check if isPasswordExpiryCheck
				if (deptUserDto.getIsPasswordExpiryCheck()) {
					if (deptUserDto.getPasswordExpiryDays() <= 0 || deptUserDto.getExpiryAlertDays() <= 0) {
						throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
					}
				}
				
				// get redis user info
				UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
				// insert data to pending_approvals
				String approvalId = insertDepartmentUserCreatePA(userInfo, deptId, deptUserDto, refId);
				// send activity log
				activityLogService.sendActivityLog(userInfo, request,
						ActivityLogService.ACTIVITY_CREATE_DEPARTMENT_USER,
						ActivityLogService.ACTIVITY_CREATE_DEPARTMENT_USER_DESC, deptUserDto.getUsername(), approvalId);
			} catch (Exception e) {
				AMLogger.logError(className, methodName, refId, e);
				throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			AMLogger.logMessage(className, methodName, refId, "This username already exists");
			throw new CustomException(ErrorMessage.DOCUMENT_ALREADY_EXISTED, HttpStatus.OK);
		}
	}

	private String insertDepartmentUserCreatePA(UserInfoDTO userInfo, String deptId, UserDTO deptUserDto,
			long refId) {
		String methodName = "insertDepartmentUserCreatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.DEPARTMENT_USER_CREATE);
			pendingData.setCollectionName("departments");
			pendingData.setQueryField("_id");
			pendingData.setQueryValue(deptId);
			pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
			pendingData.setPendingValue(new Gson().toJson(deptUserDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_ADMIN_USER_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.ADMIN_USER_CREATE_NAME);
			pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(),
					deptUserDto.getUsername()));
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

	private LoginAdminUser createLoginAdminUser(String deptId, UserDTO deptUserDto, String password, String pin,
			long refId) {
		String methodName = "createLoginAdminUser";
		try {
			DepartmentDTO deptDto = getDepartmentInfo(deptId);
			LoginAdminUser loginAdmUser = modelMapper.map(deptUserDto, LoginAdminUser.class);
			loginAdmUser.setPassword(passwordEncoder.encode(password));
			loginAdmUser.setPin(passwordEncoder.encode(pin));
			loginAdmUser.setStatus(Constant.STATUS_ACTIVE);
			loginAdmUser.setDeptCode(deptDto.getCode());
			loginAdmUser.setDeptName(deptDto.getName());
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
			serviceCon.sendPostRequest(serviceCon.getEmailNotificationServiceURL(), emailJson, null);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void updateDepartmentUser(HttpServletRequest request, PendingApproval pendingAprroval, long refId) {
		String methodName = "updateDepartmentUser";
		try {
			String deptId = pendingAprroval.getPendingData().getQueryValue();
			String deptUserId = pendingAprroval.getPendingData().getQueryValue2();
			UpdateUserDTO deptUserDto = new Gson().fromJson(pendingAprroval.getPendingData().getPendingValue(), UpdateUserDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_UPDATE_DEPARTMENT_USER,
					ActivityLogService.ACTIVITY_APPROVAL_UPDATE_DEPARTMENT_USER_DESC, String.valueOf(deptUserId), pendingAprroval.getId());

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("departments");

			BasicDBObject query = new BasicDBObject();
			query.put("_id", new ObjectId(deptId));
			query.put("users._id", new ObjectId(deptUserId));

			boolean isStatusUpdated = false;
			BasicDBObject newDocument = new BasicDBObject();

			if (Utility.isNotNull(deptUserDto.getFullName()))
				newDocument.put("users.$.fullName", deptUserDto.getFullName());
			if (Utility.isNotNull(deptUserDto.getPhoneNumber()))
				newDocument.put("users.$.phoneNumber", deptUserDto.getPhoneNumber());
			if (Utility.isNotNull(deptUserDto.getEmail()))
				newDocument.put("users.$.email", deptUserDto.getEmail());
			if (Utility.isNotNull(deptUserDto.getStatus())) {
				isStatusUpdated = true;
				newDocument.put("users.$.status", deptUserDto.getStatus().toUpperCase());
			}
			
			if (Utility.isNotNull(deptUserDto.getNote()))
				newDocument.put("users.$.note", deptUserDto.getNote());
			if (Utility.isNotNull(deptUserDto.getIsPasswordExpiryCheck()))
				newDocument.put("users.$.isPasswordExpiryCheck", deptUserDto.getIsPasswordExpiryCheck());
			if (Utility.isNotNull(deptUserDto.getPasswordExpiryDays()) && deptUserDto.getPasswordExpiryDays() > 0)
				newDocument.put("users.$.passwordExpiryDays", deptUserDto.getPasswordExpiryDays());
			if (Utility.isNotNull(deptUserDto.getExpiryAlertDays()) && deptUserDto.getExpiryAlertDays() > 0)
				newDocument.put("users.$.expiryAlertDays", deptUserDto.getExpiryAlertDays());

			newDocument.put("users.$.lastModifiedUser", Utility.getCurrentUsername());
			newDocument.put("users.$.lastModifiedDate", System.currentTimeMillis());

			BasicDBObject update = new BasicDBObject();
			update.put("$set", newDocument);

			collection.updateOne(query, update);
			
			// update status to login admin users
			if (isStatusUpdated) {
				String username = getUsername(deptUserId);
				
				MongoCollection<Document> loginAdmCollection = database.getCollection("login_admin_users");
				
				Document loginAdmQuery = new Document();
				loginAdmQuery.append("username", username);
				
				Document loginAdmUpdateDoc = new Document();
				loginAdmUpdateDoc.append("status", deptUserDto.getStatus().toUpperCase());
				
				Document loginAdmUpdate = new Document();
				loginAdmUpdate.append("$set", loginAdmUpdateDoc);
				
				loginAdmCollection.updateOne(loginAdmQuery, loginAdmUpdate);
				
				// logout user if status is inactive
				if (Constant.STATUS_INACTIVE.equalsIgnoreCase(deptUserDto.getStatus())) {
					List<String> usernameList = new ArrayList<String>();
					usernameList.add(username);
					Utility.sendHandleLogout(usernameList, refId);
				}
			}
			
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void updateDepartmentUserPA(HttpServletRequest request, String deptId, String deptUserId,
			ApprovalUpdateUserDTO deptUserDto, long refId) {
		String methodName = "updateDepartmentUserPA";
		try {
			//check if isPasswordExpiryCheck
			if (deptUserDto.getPendingData().getIsPasswordExpiryCheck()) {
				if (deptUserDto.getPendingData().getPasswordExpiryDays() <= 0 || deptUserDto.getPendingData().getExpiryAlertDays() <= 0) {
					throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
				}
			}
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertDepartmentUserUpdatePA(userInfo, deptId, deptUserId, deptUserDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_UPDATE_DEPARTMENT_USER,
					ActivityLogService.ACTIVITY_UPDATE_DEPARTMENT_USER_DESC, String.valueOf(deptUserId), approvalId);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String insertDepartmentUserUpdatePA(UserInfoDTO userInfo, String deptId, String deptUserId,
			ApprovalUpdateUserDTO deptUserDto, long refId) {
		String methodName = "insertDepartmentUserUpdatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.DEPARTMENT_USER_UPDATE);
			pendingData.setCollectionName("departments");
			pendingData.setQueryField("_id");
			pendingData.setQueryValue(deptId);
			pendingData.setQueryField2("users._id");
			pendingData.setQueryValue2(deptUserId);
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setOldValue(new Gson().toJson(deptUserDto.getOldData()));
			pendingData.setPendingValue(new Gson().toJson(deptUserDto.getPendingData()));

			String username = getDepartmentUser(deptId, deptUserId, refId).getUsername();
			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_ADMIN_USER_UPDATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.ADMIN_USER_UPDATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), username));
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

	public UserDTO getDepartmentUser(String deptId, String deptUserId, long refId) {
		String methodName = "getDepartmentUser";
		try {
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("departments");

			List<? extends Bson> pipeline = Arrays.asList(
					new Document().append("$match", new Document().append("_id", new ObjectId(deptId))),
					new Document().append("$unwind", new Document().append("path", "$users")),
					new Document().append("$match", new Document().append("users._id", new ObjectId(deptUserId))),
					new Document().append("$project", new Document().append("_id", 0.0).append("users", 1.0)),
					new Document().append("$replaceRoot", new Document().append("newRoot", "$users")));

			Document resultDoc = collection.aggregate(pipeline).first();
			UserDTO deptUserDto = mongoTemplate.getConverter().read(UserDTO.class, resultDoc);
			return deptUserDto;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void saveDepartmentUserRoles(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "saveDepartmentUserRoles";
		try {
			String deptId = pendingApproval.getPendingData().getQueryValue();
			String deptUserId = pendingApproval.getPendingData().getQueryValue2();
			UserRolesDTO userDto = new Gson().fromJson(pendingApproval.getPendingData().getPendingValue(), UserRolesDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_DEPARTMENT_USER_ROLE,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_DEPARTMENT_USER_ROLE_DESC, String.valueOf(deptUserId),
					pendingApproval.getId());

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("departments");
			
			List<Document> roles = new ArrayList<Document>();
			for (UserRole role : userDto.getRoles()) {
				Document roleDoc = new Document();
				roleDoc.append("name", role.getName());
				roleDoc.append("description", role.getDescription());
				roles.add(roleDoc);
			}
			BasicDBObject query = new BasicDBObject();
			query.append("_id", new ObjectId(deptId));
			query.append("users", new BasicDBObject("$elemMatch", new BasicDBObject("_id", new ObjectId(deptUserId))));
			
			collection.updateOne(query, Updates.set("users.$.roles", roles));
			
//			//logout user
//			Utility.sendHandleLogout(getUsername(deptUserId),  refId);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void saveDepartmentUserRolesPA(HttpServletRequest request, String deptId, String deptUserId,
			ApprovalUserRolesDTO userDto, long refId) {
		String methodName = "saveDepartmentUserRolesPA";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertDeptUserRoleCreatePA(userInfo, deptId, deptUserId, userDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_CREATE_DEPARTMENT_USER_ROLE,
					ActivityLogService.ACTIVITY_CREATE_DEPARTMENT_USER_ROLE_DESC, String.valueOf(deptUserId),
					approvalId);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String insertDeptUserRoleCreatePA(UserInfoDTO userInfo, String deptId, String deptUserId,
			ApprovalUserRolesDTO deptUserDto, long refId) {
		String methodName = "insertDeptUserRoleCreatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.DEPARTMENT_USER_ROLES_CREATE);
			pendingData.setCollectionName("departments");
			pendingData.setQueryField("_id");
			pendingData.setQueryValue(deptId);
			pendingData.setQueryField2("users._id");
			pendingData.setQueryValue2(deptUserId);
			pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
			pendingData.setOldValue(new Gson().toJson(deptUserDto.getOldData()));
			pendingData.setPendingValue(new Gson().toJson(deptUserDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_ADMIN_USER_ROLE_ASSIGN_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.ADMIN_USER_ROLE_ASSIGN_CREATE_NAME);
			pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(),
					getUsername(deptUserId)));
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

	public void saveDepartmentUserFunctions(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "saveDepartmentUserFunctions";
		
		try {
			String deptId = pendingApproval.getPendingData().getQueryValue();
			String deptUserId = pendingApproval.getPendingData().getQueryValue2();
			FunctionsDTO userDto = new Gson().fromJson(pendingApproval.getPendingData().getPendingValue(), FunctionsDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_CREATE_DEPARTMENT_USER_FUNCTIONS,
					ActivityLogService.ACTIVITY_CREATE_DEPARTMENT_USER_FUNCTIONS_DESC, String.valueOf(deptUserId),
					pendingApproval.getId());

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("departments");
			
			List<Document> functions = new ArrayList<Document>();
			for (RoleFunction function : userDto.getFunctions()) {
				Document funcDoc = new Document();
				funcDoc.append("code", function.getCode());
				funcDoc.append("name", function.getName());
				functions.add(funcDoc);
			}
			BasicDBObject query = new BasicDBObject();
			query.append("_id", new ObjectId(deptId));
			query.append("users", new BasicDBObject("$elemMatch", new BasicDBObject("_id", new ObjectId(deptUserId))));
			
			collection.updateOne(query, Updates.set("users.$.functions", functions));
			
//			//logout user
//			Utility.sendHandleLogout(getUsername(deptUserId),  refId);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void saveDepartmentUserFunctionsPA(HttpServletRequest request, String deptId, String deptUserId,
			ApprovalFunctionsDTO userDto, long refId) {
		String methodName = "saveDepartmentUserFunctionsPA";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertDeptUserFunctionsCreatePA(userInfo, deptId, deptUserId, userDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_CREATE_DEPARTMENT_USER_FUNCTIONS,
					ActivityLogService.ACTIVITY_CREATE_DEPARTMENT_USER_FUNCTIONS_DESC, String.valueOf(deptUserId),
					approvalId);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String insertDeptUserFunctionsCreatePA(UserInfoDTO userInfo, String deptId, String deptUserId,
			ApprovalFunctionsDTO deptUserDto, long refId) {
		String methodName = "insertDeptUserFunctionsCreatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.DEPARTMENT_USER_FUNCTIONS_CREATE);
			pendingData.setCollectionName("departments");
			pendingData.setQueryField("_id");
			pendingData.setQueryValue(deptId);
			pendingData.setQueryField2("users._id");
			pendingData.setQueryValue2(deptUserId);
			pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
			pendingData.setOldValue(new Gson().toJson(deptUserDto.getOldData()));
			pendingData.setPendingValue(new Gson().toJson(deptUserDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_ADMIN_USER_FUNCTIONS_ASSIGN_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.ADMIN_USER_FUNCTIONS_ASSIGN_CREATE_NAME);
			pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(),
					getUsername(deptUserId)));
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

	public void changeUserDepartment(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "changeUserDepartment";
		String fromDeptCode = pendingApproval.getPendingData().getQueryValue();
		String toDeptCode = pendingApproval.getPendingData().getPendingValue();
		String username = pendingApproval.getPendingData().getQueryValue2();
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_UPDATE_USER_DEPARTMENT,
					ActivityLogService.ACTIVITY_APPROVAL_UPDATE_USER_DEPARTMENT_DESC, username, pendingApproval.getId());

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> deptCollection = database.getCollection("departments");

			List<? extends Bson> pipeline = Arrays.asList(
					new Document().append("$match", new Document().append("code", fromDeptCode)),
					new Document().append("$unwind", new Document().append("path", "$users")),
					new Document().append("$match", new Document().append("users.username", username)),
					new Document().append("$project", new Document().append("_id", 0.0).append("users", 1.0)),
					new Document().append("$replaceRoot", new Document().append("newRoot", "$users")));

			Document deptUserDoc = deptCollection.aggregate(pipeline).first();
			deptUserDoc.append("roles", null);
			deptUserDoc.append("functions", null);

			if (Utility.isNull(deptUserDoc)) {
				throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
			}
			
			BasicDBObject deptQuery1 = new BasicDBObject();
			deptQuery1.put("code", fromDeptCode);

			BasicDBObject fields = new BasicDBObject("users", new BasicDBObject("username", username));
			BasicDBObject update = new BasicDBObject("$pull", fields);
			deptCollection.findOneAndUpdate(deptQuery1, update);

			BasicDBObject deptQuery2 = new BasicDBObject();
			deptQuery2.put("code", toDeptCode);

			deptCollection.updateOne(deptQuery2, Updates.addToSet("users", deptUserDoc));

			MongoCollection<Document> loginAdmUserCollection = database.getCollection("login_admin_users");
			BasicDBObject loginAdmUserQuery = new BasicDBObject();
			loginAdmUserQuery.put("username", username);

			BasicDBObject newLoginAdmUser = new BasicDBObject();
			newLoginAdmUser.put("deptCode", toDeptCode);

			BasicDBObject updateObj = new BasicDBObject();
			updateObj.put("$set", newLoginAdmUser);

			loginAdmUserCollection.updateOne(loginAdmUserQuery, updateObj);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void changeUserDepartmentPA(HttpServletRequest request, String fromDeptId, String toDeptId, String username,
			long refId) {
		String methodName = "changeUserDepartmentPA";
		if (!deptRepository.existsById(fromDeptId) || !deptRepository.existsById(toDeptId)) {
			throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
		}
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertUserDepartmentChangePA(userInfo, fromDeptId, toDeptId, username, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_UPDATE_USER_DEPARTMENT,
					ActivityLogService.ACTIVITY_UPDATE_USER_DEPARTMENT_DESC, username, approvalId);
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String insertUserDepartmentChangePA(UserInfoDTO userInfo, String fromDeptId, String toDeptId,
			String username, long refId) {
		String methodName = "insertDeptUserFunctionsCreatePA";
		String approvalId = "";
		try {
			String fromDeptCode = getDepartmentInfo(fromDeptId).getCode();
			String toDeptCode = getDepartmentInfo(toDeptId).getCode();
			
			ChangeDepartmentDTO changeDeptDto = new ChangeDepartmentDTO();
			changeDeptDto.setFromDeptCode(fromDeptCode);
			changeDeptDto.setUsername(userInfo.getUsername());
			changeDeptDto.setFullName(userInfo.getFullName());
			changeDeptDto.setPhoneNumber(userInfo.getPhoneNumber());
			changeDeptDto.setEmail(userInfo.getEmail());
			
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());
			
			
			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.DEPARTMENT_CHANGE_USER_DEPT);
			pendingData.setCollectionName("departments");
			pendingData.setQueryField("code");
			pendingData.setQueryValue(fromDeptCode);
			pendingData.setQueryField2("users.username");
			pendingData.setQueryValue2(username);
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setOldValue(new Gson().toJson(changeDeptDto));
			pendingData.setPendingValue(toDeptCode);

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_ADMIN_USER_DEPT_CHANGE_UPDATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.ADMIN_USER_DEPT_CHANGE_UPDATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), username));
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
	
	private DepartmentDTO getDepartmentInfo(String deptId) {
		MongoDatabase database = MongoDBConnection.getMongoDatabase();
		MongoCollection<Document> deptCollection = database.getCollection("departments");
		
		Document query = new Document();
		query.append("_id", new ObjectId(deptId));
		
		Document projection = new Document();
		projection.append("_id", 0.0);
		projection.append("code", 1.0);
		projection.append("name", 1.0);
		
		Document result = deptCollection.find(query).projection(projection).first();
		DepartmentDTO deptDto = mongoTemplate.getConverter().read(DepartmentDTO.class, result);
		return deptDto;
	}
	
	private String getUsername(String deptUserId) {
		MongoDatabase database = MongoDBConnection.getMongoDatabase();
		MongoCollection<Document> deptCollection = database.getCollection("departments");
		
		List<? extends Bson> pipeline = Arrays.asList(
                new Document()
                        .append("$match", new Document()
                                .append("users._id", new ObjectId(deptUserId))
                        ), 
                new Document()
                        .append("$unwind", new Document()
                                .append("path", "$users")
                        ), 
                new Document()
                        .append("$match", new Document()
                                .append("users._id", new ObjectId(deptUserId))
                        ), 
                new Document()
                        .append("$project", new Document()
                                .append("_id", 0.0)
                                .append("username", "$users.username")
                        )
        );
		
		Document result = deptCollection.aggregate(pipeline).first();
		UserDTO userDto = mongoTemplate.getConverter().read(UserDTO.class, result);
		if (userDto != null) {
			return userDto.getUsername();
		} else return "";
	}
}
