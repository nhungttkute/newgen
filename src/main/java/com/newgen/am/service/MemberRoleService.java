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
import com.newgen.am.repository.MemberRoleRepository;
import com.newgen.am.repository.PendingApprovalRepository;

@Service
public class MemberRoleService {
	private String className = "MemberRoleService";
	
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
	
	@Autowired
	MemberRoleRepository memberRoleRepo;
	
	public BasePagination<RoleDTO> list(HttpServletRequest request, String memberCode, long refId) {
		String methodName = "list";
		BasePagination<RoleDTO> pagination = null;
		try {
			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "", refId);

			List<? extends Bson> pipeline = Arrays.asList(
                    new Document()
                            .append("$match", new Document()
                                    .append("memberCode", memberCode)
                            ), 
                    new Document()
                            .append("$match", searchCriteria.getQuery()), 
                    new Document()
                            .append("$sort", searchCriteria.getSort()), 
                    new Document()
                            .append("$project", new Document()
                            		.append("_id", new Document().append("$toString", "$_id"))
                                    .append("name", 1.0)
                                    .append("description", 1.0)
                                    .append("status", 1.0)
                                    .append("createdDate", 1.0)
                                    .append("functions", 1.0)
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
			MongoCollection<Document> collection = database.getCollection("member_roles");
			Document resultDoc = collection.aggregate(pipeline).first();
			pagination = mongoTemplate.getConverter().read(BasePagination.class, resultDoc);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return pagination;
	}
	
	public List<RoleCSV> listCsv(HttpServletRequest request, String memberCode, long refId) {
		String methodName = "listCsv";
		List<RoleCSV> systemRoleCSVs = new ArrayList<>();
		try {
			RequestParamsParser.SearchCriteria searchCriteria = rqParamsParser
					.getSearchCriteria(request.getQueryString(), "", refId);

			List<? extends Bson> pipeline = Arrays.asList(
                    new Document()
                            .append("$match", new Document()
                                    .append("memberCode", memberCode)
                            ), 
                    new Document()
                            .append("$match", searchCriteria.getQuery()), 
                    new Document()
                            .append("$sort", searchCriteria.getSort()), 
                    new Document()
                            .append("$project", new Document()
                            		.append("_id", new Document().append("$toString", "$_id"))
                                    .append("name", 1.0)
                                    .append("description", 1.0)
                                    .append("status", 1.0)
                                    .append("createdDate", new Document().append("$dateToString",
											new Document().append("format", "%d/%m/%Y %H:%M:%S").append("date",
													new Document().append("$toDate", "$createdDate"))))
                            ));
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("member_roles");
			MongoCursor<Document> cur = collection.aggregate(pipeline).iterator();

			while (cur.hasNext()) {
				RoleCSV roleCsv = mongoTemplate.getConverter().read(RoleCSV.class, cur.next());
				if (roleCsv != null)
					systemRoleCSVs.add(roleCsv);
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return systemRoleCSVs;
	}
	
	public void createMemberRole(HttpServletRequest request, String memberCode, RoleDTO roleDto, long refId) {
		String methodName = "createSystemRole";
		boolean existedRole = false;
		try {
			existedRole = memberRoleRepo.existsMemberRoleByName(roleDto.getName());
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!existedRole) {
			try {
				// get redis user info
				UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
				// insert data to pending_approvals
				String approvalId = insertMemberRoleCreatePA(userInfo, memberCode, roleDto, refId);
				// send activity log
				activityLogService.sendActivityLog2(userInfo, request, ActivityLogService.ACTIVITY_CREATE_MEMBER_ROLE,
						ActivityLogService.ACTIVITY_CREATE_MEMBER_ROLE_DESC, roleDto.getName(), memberCode, approvalId);

				Document memberRole = new Document();
				memberRole.append("createdUser", Utility.getCurrentUsername());
				memberRole.append("createdDate", System.currentTimeMillis());
				memberRole.append("name", roleDto.getName());
				memberRole.append("description", roleDto.getDescription());
				memberRole.append("status", roleDto.getStatus());
				memberRole.append("memberCode", memberCode);

				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("member_roles");
				collection.insertOne(memberRole);
			} catch (Exception e) {
				AMLogger.logError(className, methodName, refId, e);
				throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			AMLogger.logMessage(className, methodName, refId, "This role already exists");
			throw new CustomException(ErrorMessage.DOCUMENT_ALREADY_EXISTED, HttpStatus.OK);
		}
	}
	
	public String insertMemberRoleCreatePA(UserInfoDTO userInfo, String memberCode, RoleDTO roleDto, long refId) {
		String methodName = "insertMemberRoleCreatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setMemberCode(memberCode);

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_ROLE_CREATE);
			pendingData.setCollectionName("member_roles");
			pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
			pendingData.setQueryField("memberCode");
			pendingData.setQueryValue(memberCode);
			pendingData.setValue(new Gson().toJson(roleDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_ROLE_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_ROLE_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription2(SystemFunctionCode.MEMBER_ROLE_CREATE_DESC, roleDto.getName(), memberCode));
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
	
	public void updateMemberRole(HttpServletRequest request, String memberCode, String roleId, UpdateRoleDTO roleDto,
			long refId) {
		String methodName = "updateMemberRole";
		try {
			// get redis user info
			UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
			// insert data to pending_approvals
			String approvalId = insertMemberRoleUpdatePA(userInfo, memberCode, roleId, roleDto, refId);
			// send activity log
			activityLogService.sendActivityLog2(userInfo, request, ActivityLogService.ACTIVITY_UPDATE_MEMBER_ROLE,
					ActivityLogService.ACTIVITY_UPDATE_MEMBER_ROLE_DESC, getRoleName(roleId), memberCode, approvalId);

			Document updateDoc = new Document();
			if (Utility.isNotNull(roleDto.getName())) updateDoc.append("name", roleDto.getName());
			if (Utility.isNotNull(roleDto.getDescription())) updateDoc.append("description", roleDto.getDescription());
			if (Utility.isNotNull(roleDto.getStatus())) updateDoc.append("status", roleDto.getStatus());
			
			if (updateDoc.isEmpty()) {
				throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
			}
			updateDoc.put("lastModifiedUser", Utility.getCurrentUsername());
			updateDoc.put("lastModifiedDate", System.currentTimeMillis());
			
			Document query = new Document();
			query.append("_id", new ObjectId(roleId));
			
			Document update = new Document();
			update.append("$set", updateDoc);
			
			MongoDatabase database = MongoDBConnection.getMongoDatabase();
			MongoCollection<Document> collection = database.getCollection("member_roles");
			collection.updateOne(query, update);
		} catch(CustomException e) {
			throw e;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public String insertMemberRoleUpdatePA(UserInfoDTO userInfo, String memberCode, String roleId, UpdateRoleDTO roleDto,
			long refId) {
		String methodName = "insertMemberRoleUpdatePA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setMemberCode(memberCode);

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_ROLE_UPDATE);
			pendingData.setCollectionName("member_roles");
			pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
			pendingData.setQueryField("memberCode");
			pendingData.setQueryValue(memberCode);
			pendingData.setQueryField2("_id");
			pendingData.setQueryValue2(roleId);
			pendingData.setValue(new Gson().toJson(roleDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setId(approvalId);
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_ROLE_UPDATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_ROLE_UPDATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription2(SystemFunctionCode.MEMBER_ROLE_UPDATE_DESC, getRoleName(roleId), memberCode));
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
	
	public void createMemberRoleFunctions(HttpServletRequest request, String memberCode, String roleId, FunctionsDTO functionsDto,
			long refId) {
		String methodName = "createMemberRoleFunctions";
		if (Utility.isNotNull(functionsDto.getFunctions()) && functionsDto.getFunctions().size() > 0) {
			try {
				// get redis user info
				UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
				// insert data to pending_approvals
				String approvalId = insertMemberRoleFunctionsAssignPA(userInfo, memberCode, roleId, functionsDto, refId);
				// send activity log
				activityLogService.sendActivityLog(userInfo, request,
						ActivityLogService.ACTIVITY_CREATE_SYS_ROLE_FUNCTIONS,
						ActivityLogService.ACTIVITY_CREATE_SYS_ROLE_FUNCTIONS_DESC, getRoleName(roleId),
						approvalId);
				
				List<Document> functions = new ArrayList<Document>();
				for (RoleFunction func : functionsDto.getFunctions()) {
					Document funcDoc = new Document();
					funcDoc.append("code", func.getCode());
					funcDoc.append("name", func.getName());
					functions.add(funcDoc);
				}
				BasicDBObject query = new BasicDBObject();
				query.append("_id", new ObjectId(roleId));
				
				BasicDBObject updateDocument = new BasicDBObject();
				updateDocument.append("functions", functions);
				updateDocument.append("lastModifiedUser", Utility.getCurrentUsername());
				updateDocument.append("lastModifiedDate", System.currentTimeMillis());
				
				BasicDBObject update = new BasicDBObject();
				update.append("$set", updateDocument);
				
				MongoDatabase database = MongoDBConnection.getMongoDatabase();
				MongoCollection<Document> collection = database.getCollection("member_roles");
				collection.updateOne(query, update);
			} catch (Exception e) {
				AMLogger.logError(className, methodName, refId, e);
				throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			AMLogger.logMessage(className, methodName, refId, "Invalid input data");
			throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
		}
	}

	public String insertMemberRoleFunctionsAssignPA(UserInfoDTO userInfo, String memberCode, String roleId, FunctionsDTO roleDto,
			long refId) {
		String methodName = "insertMemberRoleFunctionsAssignPA";
		String approvalId = "";
		try {
			NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
			nestedObjInfo.setMemberCode(memberCode);

			PendingData pendingData = new PendingData();
			pendingData.setServiceFunctionName(ApprovalConstant.MEMBER_ROLE_FUNCTIONS_CREATE);
			pendingData.setCollectionName("member_roles");
			pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
			pendingData.setQueryField("memberCode");
			pendingData.setQueryValue(memberCode);
			pendingData.setQueryField2("_id");
			pendingData.setQueryValue2(roleId);
			pendingData.setValue(new Gson().toJson(roleDto));

			PendingApproval pendingApproval = new PendingApproval();
			pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
			pendingApproval.setCreatorDate(System.currentTimeMillis());
			pendingApproval.setCreatorUser(userInfo.getUsername());
			pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_MEMBER_ROLE_FUNCTIONS_ASSIGN_CREATE_CODE);
			pendingApproval.setFunctionName(SystemFunctionCode.MEMBER_ROLE_FUNCTIONS_ASSIGN_CREATE_NAME);
			pendingApproval.setDescription(
					SystemFunctionCode.getApprovalDescription2(SystemFunctionCode.MEMBER_ROLE_FUNCTIONS_ASSIGN_CREATE_DESC, getRoleName(roleId), memberCode));
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
		MongoCollection<Document> collection = database.getCollection("member_roles");
		
		Document query = new Document();
		query.append("_id", new ObjectId(roleId));
		
		Document projection = new Document();
		projection.append("name", 1.0);
		
		Document result = collection.find(query).projection(projection).first();
		RoleDTO role = mongoTemplate.getConverter().read(RoleDTO.class, result);
		return role.getName();
	}
}
