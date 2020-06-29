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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.ApprovalConstant;
import com.newgen.am.common.Constant;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.MongoDBConnection;
import com.newgen.am.common.RequestParamsParser;
import com.newgen.am.common.SystemFunctionCode;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.ApprovalFunctionsDTO;
import com.newgen.am.dto.ApprovalUpdateRoleDTO;
import com.newgen.am.dto.BasePagination;
import com.newgen.am.dto.FunctionsDTO;
import com.newgen.am.dto.RoleCSV;
import com.newgen.am.dto.RoleDTO;
import com.newgen.am.dto.UpdateRoleDTO;
import com.newgen.am.dto.UserInfoDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.NestedObjectInfo;
import com.newgen.am.model.PendingApproval;
import com.newgen.am.model.PendingData;
import com.newgen.am.model.RoleFunction;
import com.newgen.am.model.SystemRole;
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
	private RedisTemplate template;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private ActivityLogService activityLogService;

	@Autowired
	private RequestParamsParser rqParamsParser;

	@Autowired
	PendingApprovalRepository pendingApprovalRepo;

	public BasePagination<RoleDTO> list(HttpServletRequest request, long refId) {
		String methodName = "list";
		BasePagination<RoleDTO> pagination = null;
		try {
			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "", refId);

			List<? extends Bson> pipeline = Arrays
					.asList(new Document().append("$match", searchCriteria.getQuery()),
							new Document().append("$sort", searchCriteria.getSort()),
							new Document().append("$project",
									new Document().append("_id", new Document().append("$toString", "$_id")).append("name", 1.0).append("description", 1.0).append("status", 1.0)
											.append("createdDate", 1.0).append("functions", 1.0)),
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

	public List<RoleCSV> listCsv(HttpServletRequest request, long refId) {
		String methodName = "listCsv";
		List<RoleCSV> systemRoleCSVs = new ArrayList<>();
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
				RoleCSV sysRoleCsv = mongoTemplate.getConverter().read(RoleCSV.class, cur.next());
				if (sysRoleCsv != null)
					systemRoleCSVs.add(sysRoleCsv);
			}

		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return systemRoleCSVs;
	}

	public void createSystemRole(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "createSystemRole";
		
		try {
			RoleDTO sysRoleDto = new Gson().fromJson(pendingApproval.getPendingData().getPendingValue(), RoleDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_CREATE_SYS_ROLE,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_SYS_ROLE_DESC, sysRoleDto.getName(), pendingApproval.getId());

			Document newSysRole = new Document();
			newSysRole.append("createdUser", Utility.getCurrentUsername());
			newSysRole.append("createdDate", System.currentTimeMillis());
			newSysRole.append("name", sysRoleDto.getName());
			newSysRole.append("description", sysRoleDto.getDescription());
			newSysRole.append("status", Constant.STATUS_ACTIVE);

			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("system_roles");
			collection.insertOne(newSysRole);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void createSystemRolePA(HttpServletRequest request, RoleDTO sysRoleDto, long refId) {
		String methodName = "createSystemRolePA";
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
			} catch (Exception e) {
				AMLogger.logError(className, methodName, refId, e);
				throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			AMLogger.logMessage(className, methodName, refId, "This system role already exists");
			throw new CustomException(ErrorMessage.DOCUMENT_ALREADY_EXISTED, HttpStatus.OK);
		}
	}

	private String insertSystemRoleCreatePA(UserInfoDTO userInfo, RoleDTO sysRoleDto, long refId) {
		String methodName = "insertSystemRoleCreatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setDeptCode(userInfo.getDeptCode());

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.SYSTEM_ROLE_CREATE);
			pendingData.setCollectionName("system_roles");
			pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
			pendingData.setPendingValue(new Gson().toJson(sysRoleDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
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

	public void updateSystemRole(HttpServletRequest request, PendingApproval pendingApproval, long refId) {
		String methodName = "updateSystemRole";
		
		try {
			UpdateRoleDTO sysRoleDto = new Gson().fromJson(pendingApproval.getPendingData().getPendingValue(), UpdateRoleDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_APPROVAL_UPDATE_SYS_ROLE,
					ActivityLogService.ACTIVITY_APPROVAL_UPDATE_SYS_ROLE_DESC, getRoleName(pendingApproval.getPendingData().getQueryValue()), pendingApproval.getId());

			Document updateDoc = new Document();
			if (Utility.isNotNull(sysRoleDto.getName())) updateDoc.append("name", sysRoleDto.getName());
			if (Utility.isNotNull(sysRoleDto.getDescription())) updateDoc.append("description", sysRoleDto.getDescription());
			if (Utility.isNotNull(sysRoleDto.getStatus())) updateDoc.append("status", sysRoleDto.getStatus());
			
			updateDoc.put("lastModifiedUser", Utility.getCurrentUsername());
			updateDoc.put("lastModifiedDate", System.currentTimeMillis());
			
			Document query = new Document();
			query.append("_id", new ObjectId(pendingApproval.getPendingData().getQueryValue()));
			
			Document update = new Document();
			update.append("$set", updateDoc);
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("system_roles");
			collection.updateOne(query, update);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void updateSystemRolePA(HttpServletRequest request, String sysRoleId, ApprovalUpdateRoleDTO sysRoleDto,
			long refId) {
		String methodName = "updateSystemRolePA";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertSystemRoleUpdatePA(userInfo, sysRoleId, sysRoleDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_UPDATE_SYS_ROLE,
					ActivityLogService.ACTIVITY_UPDATE_SYS_ROLE_DESC, getRoleName(sysRoleId), approvalId);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String insertSystemRoleUpdatePA(UserInfoDTO userInfo, String sysRoleId, ApprovalUpdateRoleDTO sysRoleDto,
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
			pendingData.setOldValue(new Gson().toJson(sysRoleDto.getOldData()));
			pendingData.setPendingValue(new Gson().toJson(sysRoleDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_SYSTEM_ROLE_UPDATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.SYSTEM_ROLE_UPDATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), getRoleName(sysRoleId)));
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

	public void createSystemRoleFunctions(HttpServletRequest request, PendingApproval pendingApproval,
			long refId) {
		String methodName = "createSystemRoleFunctions";
		
		try {
			FunctionsDTO sysRoleDto = new Gson().fromJson(pendingApproval.getPendingData().getPendingValue(), FunctionsDTO.class);
			
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_SYS_ROLE_FUNCTIONS,
					ActivityLogService.ACTIVITY_APPROVAL_CREATE_SYS_ROLE_FUNCTIONS_DESC, getRoleName(pendingApproval.getPendingData().getQueryValue()),
					pendingApproval.getId());

			List<Document> functions = new ArrayList<Document>();
			if (Utility.isNotNull(sysRoleDto.getFunctions()) && sysRoleDto.getFunctions().size() > 0) {
				for (RoleFunction func : sysRoleDto.getFunctions()) {
					Document funcDoc = new Document();
					funcDoc.append("code", func.getCode());
					funcDoc.append("name", func.getName());
					functions.add(funcDoc);
				}
			}
			
			BasicDBObject query = new BasicDBObject();
			query.append("_id", new ObjectId(pendingApproval.getPendingData().getQueryValue()));
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("system_roles");
			collection.updateOne(query, Updates.set("functions", functions));
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void createSystemRoleFunctionsPA(HttpServletRequest request, String sysRoleId, ApprovalFunctionsDTO sysRoleDto,
			long refId) {
		String methodName = "createSystemRoleFunctionsPA";
		
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertSystemRoleFunctionsAssignPA(userInfo, sysRoleId, sysRoleDto, refId);
			// send activity log
			activityLogService.sendActivityLog(userInfo, request,
					ActivityLogService.ACTIVITY_CREATE_SYS_ROLE_FUNCTIONS,
					ActivityLogService.ACTIVITY_CREATE_SYS_ROLE_FUNCTIONS_DESC, getRoleName(sysRoleId),
					approvalId);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	private String insertSystemRoleFunctionsAssignPA(UserInfoDTO userInfo, String sysRoleId, ApprovalFunctionsDTO sysRoleDto,
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
			pendingData.setOldValue(new Gson().toJson(sysRoleDto.getOldData()));
			pendingData.setPendingValue(new Gson().toJson(sysRoleDto.getPendingData()));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_SYSTEM_ROLE_FUNCTIONS_ASSIGN_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.SYSTEM_ROLE_FUNCTIONS_ASSIGN_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), getRoleName(sysRoleId)));
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
	
	private String getRoleName(String roleId) {
		MongoDatabase database = MongoDBConnection.getMongoDatabase();
		MongoCollection<Document> collection = database.getCollection("system_roles");
		
		Document query = new Document();
		query.append("_id", new ObjectId(roleId));
		
		Document projection = new Document();
		projection.append("name", 1.0);
		
		Document result = collection.find(query).projection(projection).first();
		RoleDTO role = mongoTemplate.getConverter().read(RoleDTO.class, result);
		return role.getName();
	}
}
