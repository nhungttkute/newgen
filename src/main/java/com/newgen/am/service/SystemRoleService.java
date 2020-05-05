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
import org.modelmapper.ModelMapper;
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
import com.newgen.am.dto.BasePagination;
import com.newgen.am.dto.SystemRoleCSV;
import com.newgen.am.dto.SystemRoleDTO;
import com.newgen.am.dto.UpdateSystemRoleDTO;
import com.newgen.am.dto.UserInfoDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.NestedObjectInfo;
import com.newgen.am.model.PendingApproval;
import com.newgen.am.model.PendingData;
import com.newgen.am.model.SystemRole;
import com.newgen.am.repository.LoginAdminUserRepository;
import com.newgen.am.repository.PendingApprovalRepository;
import com.newgen.am.repository.SystemRoleRepository;

/**
 *
 * @author nhungtt
 */
@Service
public class SystemRoleService {
	private String className = "SystemRoleService";

	@Autowired
	SystemRoleRepository systemRoleRepo;

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	LoginAdminUserRepository loginAdmUserRepo;

	@Autowired
	DBSequenceService dbSeqService;

	@Autowired
	private RedisTemplate template;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private ActivityLogService activityLogService;

	@Autowired
	private RequestParamsParser rqParamsParser;

	@Autowired
	PendingApprovalRepository pendingApprovalRepo;

	public BasePagination<SystemRoleDTO> list(HttpServletRequest request, long refId) {
		String methodName = "list";
		BasePagination<SystemRoleDTO> pagination = null;
		try {
			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "", refId);

			List<? extends Bson> pipeline = Arrays
					.asList(new Document().append("$match", searchCriteria.getQuery()),
							new Document().append("$sort", searchCriteria.getSort()),
							new Document().append("$project",
									new Document().append("_id", new Document().append("$toString", "$_id")).append("name", 1.0).append("description", 1.0).append("status", 1.0)
											.append("createdDate", 1.0)),
							new Document().append("$facet", new Document()
									.append("stage1", Arrays.asList(new Document().append("$count", "total")))
									.append("stage2",
											Arrays.asList(new Document().append("$skip", searchCriteria.getSkip()),
													new Document().append("$limit", searchCriteria.getLimit())))),
							new Document().append("$unwind", new Document().append("path", "$stage1")),
							new Document().append("$project",
									new Document().append("count", "$stage1.total").append("data", "$stage2")));

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("system_roles");
			Document resultDoc = collection.aggregate(pipeline).first();
			pagination = mongoTemplate.getConverter().read(BasePagination.class, resultDoc);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return pagination;
	}

	public List<SystemRoleCSV> listCsv(HttpServletRequest request, long refId) {
		String methodName = "listCsv";
		List<SystemRoleCSV> systemRoleCSVs = new ArrayList<>();
		try {
			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "", refId);

			List<? extends Bson> pipeline = Arrays
					.asList(new Document().append("$match", searchCriteria.getQuery()),
							new Document().append("$sort", searchCriteria.getSort()),
							new Document().append("$project",
									new Document().append("_id", new Document().append("$toString", "$_id")).append("name", 1.0).append("description", 1.0).append("status", 1.0)
											.append("createdDate", new Document().append("$dateToString",
													new Document().append("format", "%d/%m/%Y %H:%M:%S").append("date",
															new Document().append("$toDate", "$createdDate"))))));

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("system_roles");
			MongoCursor<Document> cur = collection.aggregate(pipeline).iterator();

			while (cur.hasNext()) {
				SystemRoleCSV sysRoleCsv = mongoTemplate.getConverter().read(SystemRoleCSV.class, cur.next());
				if (sysRoleCsv != null)
					systemRoleCSVs.add(sysRoleCsv);
			}

		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return systemRoleCSVs;
	}

	public void createSystemRole(HttpServletRequest request, SystemRoleDTO sysRoleDto, long refId) {
		String methodName = "createSystemRole";
		boolean existedRole = false;
		try {
			existedRole = systemRoleRepo.existsSystemRoleByName(sysRoleDto.getName());
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!existedRole) {
			try {
				// get redis user info
				UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
				// insert data to pending_approvals
				String approvalId = insertSystemRoleCreatePA(userInfo, sysRoleDto, refId);
				// send activity log
				activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_SYS_ROLE,
						ActivityLogService.ACTIVITY_CREATE_SYS_ROLE_DESC, sysRoleDto.getName(), approvalId);

				Document newSysRole = new Document();
//				newSysRole.append("createdUser", Utility.getCurrentUsername());
//				newSysRole.append("createdDate", System.currentTimeMillis());
				newSysRole.append("name", sysRoleDto.getName());
				newSysRole.append("description", sysRoleDto.getDescription());
				newSysRole.append("status", sysRoleDto.getStatus());

				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("system_roles");
				collection.insertOne(newSysRole);
			} catch (Exception e) {
				AMLogger.logError(className, methodName, refId, e);
				throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			AMLogger.logMessage(className, methodName, refId, "This system role already exists");
			throw new CustomException(ErrorMessage.DOCUMENT_ALREADY_EXISTED, HttpStatus.OK);
		}
	}

	public String insertSystemRoleCreatePA(UserInfoDTO userInfo, SystemRoleDTO sysRoleDto, long refId) {
		String methodName = "insertSystemRoleCreatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.SYSTEM_ROLE_CREATE);
			pendingData.setCollectionName("system_roles");
			pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
			pendingData.setValue(new Gson().toJson(sysRoleDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_SYSTEM_ROLE_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.SYSTEM_ROLE_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), sysRoleDto.getName()));
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

	public void updateSystemRole(HttpServletRequest request, String sysRoleId, UpdateSystemRoleDTO sysRoleDto,
			long refId) {
		String methodName = "updateSystemRole";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertSystemRoleUpdatePA(userInfo, sysRoleId, sysRoleDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_UPDATE_SYS_ROLE,
					ActivityLogService.ACTIVITY_UPDATE_SYS_ROLE_DESC, String.valueOf(sysRoleId), approvalId);

			SystemRole sysRole = systemRoleRepo.findById(sysRoleId).get();
			if (Utility.isNotNull(sysRoleDto.getName()))
				sysRole.setName(sysRoleDto.getName());
			if (Utility.isNotNull(sysRoleDto.getDescription()))
				sysRole.setDescription(sysRoleDto.getDescription());
			if (Utility.isNotNull(sysRoleDto.getStatus()))
				sysRole.setStatus(sysRoleDto.getStatus());
			systemRoleRepo.save(sysRole);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public String insertSystemRoleUpdatePA(UserInfoDTO userInfo, String sysRoleId, UpdateSystemRoleDTO sysRoleDto,
			long refId) {
		String methodName = "insertSystemRoleUpdatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.SYSTEM_ROLE_UPDATE);
			pendingData.setCollectionName("system_roles");
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setQueryField("_id");
			pendingData.setQueryValue(sysRoleId);
			pendingData.setValue(new Gson().toJson(sysRoleDto));

			SystemRole sysRole = systemRoleRepo.findById(sysRoleId).get();

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setId(approvalId);
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_SYSTEM_ROLE_UPDATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.SYSTEM_ROLE_UPDATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), sysRole.getName()));
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

	public void createSystemRoleFunctions(HttpServletRequest request, String sysRoleId, SystemRoleDTO sysRoleDto,
			long refId) {
		String methodName = "createSystemRoleFunctions";
		if (Utility.isNotNull(sysRoleDto.getFunctions()) && sysRoleDto.getFunctions().size() > 0) {
			try {
				// get redis user info
				UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
				// insert data to pending_approvals
				String approvalId = insertSystemRoleFunctionsAssignPA(userInfo, sysRoleId, sysRoleDto, refId);
				// send activity log
				activityLogService.sendActivityLog(userInfo, request,
						ActivityLogService.ACTIVITY_CREATE_SYS_ROLE_FUNCTIONS,
						ActivityLogService.ACTIVITY_CREATE_SYS_ROLE_FUNCTIONS_DESC, String.valueOf(sysRoleId),
						approvalId);

				SystemRole sysRole = systemRoleRepo.findById(sysRoleId).get();
				sysRole.setFunctions(sysRoleDto.getFunctions());
				systemRoleRepo.save(sysRole);
			} catch (Exception e) {
				AMLogger.logError(className, methodName, refId, e);
				throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			AMLogger.logMessage(className, methodName, refId, "Invalid input data");
			throw new CustomException(ErrorMessage.INVALID_INPUT_DATA, HttpStatus.OK);
		}
	}

	public String insertSystemRoleFunctionsAssignPA(UserInfoDTO userInfo, String sysRoleId, SystemRoleDTO sysRoleDto,
			long refId) {
		String methodName = "insertSystemRoleFunctionsAssignPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.SYSTEM_ROLE_FUNCTIONS_CREATE);
			pendingData.setCollectionName("system_roles");
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setQueryField("_id");
			pendingData.setQueryValue(sysRoleId);
			pendingData.setValue(new Gson().toJson(sysRoleDto));

			SystemRole sysRole = systemRoleRepo.findById(sysRoleId).get();

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_SYSTEM_ROLE_FUNCTIONS_ASSIGN_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.SYSTEM_ROLE_FUNCTIONS_ASSIGN_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), sysRole.getName()));
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
