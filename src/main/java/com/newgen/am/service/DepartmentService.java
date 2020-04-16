/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.service;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.newgen.am.common.*;
import com.newgen.am.dto.DepartmentDTO;
import com.newgen.am.dto.DeptUserDTO;
import com.newgen.am.dto.EmailDTO;
import com.newgen.am.dto.UserInfoDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.Department;
import com.newgen.am.model.DeptUser;
import com.newgen.am.model.LoginAdminUser;
import com.newgen.am.model.NestedObjectInfo;
import com.newgen.am.model.PendingApproval;
import com.newgen.am.model.PendingData;
import com.newgen.am.mongodb.pojo.UserFunctionResult;
import com.newgen.am.repository.DepartmentRepository;
import com.newgen.am.repository.LoginAdminUserRepository;
import com.newgen.am.repository.PendingApprovalRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.bson.Document;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    private RedisTemplate template;
    
    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    PasswordEncoder passwordEncoder;
    
    public List<Department> list(HttpServletRequest request, long refId) {
        String methodName = "list";
        List<Department> deptList = new ArrayList<>();
        try {
            int skip = 0;
            int limit = 20;
            Document defaultSort = new Document();
            defaultSort.append("lastModifiedDate", -1);
            Document query = null;
            if (Utility.isNotNull(request.getQueryString())) {
                String reqParams = Utility.decode(request.getQueryString());
                skip = RequestParamsParser.getOffset(reqParams);
                limit = RequestParamsParser.getLimit(reqParams);

                //build sort
                Document sort = RequestParamsParser.buildSortDocument(reqParams);
                if (sort != null) {
                    defaultSort = sort;
                }

                // build query
                query = RequestParamsParser.buildQueryDocument(reqParams);
                if (query == null) {
                    query = new Document();
                }
            }

            MongoDatabase database = MongoDBConnection.getMongoDatabase();
            MongoCollection<Document> collection = database.getCollection("departments");
            MongoCursor<Document> cur = collection.find(query).sort(defaultSort).skip(skip).limit(limit).iterator();

            while (cur.hasNext()) {
                Document doc = cur.next();
                Department dept = new Gson().fromJson(doc.toJson(Utility.getJsonWriterSettings()), Department.class);
                if (dept != null) deptList.add(dept);
            }
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException("Cannot list departments", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return deptList;
    }

    public boolean createDepartment(HttpServletRequest request, DepartmentDTO deptDto, long refId) {
        String methodName = "createDeparment";
        boolean existedDept = false;
        try {
            existedDept = deptRepository.existsDepartmentByCode(deptDto.getCode());
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        
        if (!existedDept) {
            try {
                // get redis user info
                UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
                //insert data to pending_approvals
                long approvalId = insertDepartmentCreatePA(userInfo, deptDto, refId);
                //send activity log
                activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_DEPARTMENT, ActivityLogService.ACTIVITY_CREATE_DEPARTMENT_DESC, deptDto.getCode(), approvalId);

                Department dept = modelMapper.map(deptDto, Department.class);
                dept.setId(dbSeqService.generateSequence(Department.SEQUENCE_NAME, refId));
                dept.setCreatedUser(Utility.getCurrentUsername());
                dept.setCreatedDate(System.currentTimeMillis());
                deptRepository.save(dept);
                return true;
            } catch (Exception e) {
                AMLogger.logError(className, methodName, refId, e);
                throw new CustomException("Cannot create this department", HttpStatus.UNPROCESSABLE_ENTITY);
            }
        } else {
            AMLogger.logMessage(className, methodName, refId, "This deparment code already exists");
            throw new CustomException(ErrorMessage.DOCUMENT_ALREADY_EXISTS, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public long insertDepartmentCreatePA(UserInfoDTO userInfo, DepartmentDTO deptDto, long refId) {
        String methodName = "insertDepartmentCreatePA";
        long approvalId = 0;
        try {
            approvalId = dbSeqService.generateSequence(PendingApproval.SEQUENCE_NAME, refId);
            
            NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
            nestedObjInfo.setDeptCode(userInfo.getDeptCode());

            PendingData pendingData = new PendingData();
            pendingData.setServiceFunctionName(ApprovalConstant.DEPARTMENT_CREATE);
            pendingData.setCollectionName("departments");
            pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
            pendingData.setValue(new Gson().toJson(deptDto));

            PendingApproval pendingApproval = new PendingApproval();
            pendingApproval.setId(approvalId);
            pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
            pendingApproval.setCreatorDate(System.currentTimeMillis());
            pendingApproval.setCreatorUser(userInfo.getUsername());
            pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_DEPARTMENT_INFO_CREATE_CODE);
            pendingApproval.setFunctionName(SystemFunctionCode.DEPARTMENT_INFO_CREATE_NAME);
            pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), deptDto.getCode()));
            pendingApproval.setStatus(Constant.APPROVAL_STATUS_PENDING);
            pendingApproval.setNestedObjInfo(nestedObjInfo);
            pendingApproval.setPendingData(pendingData);
            pendingApprovalRepo.save(pendingApproval);
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
        }
        return approvalId;
    }
    
    public DepartmentDTO getDepartmentDetail(Long deptId, long refId) {
        String methodName = "getDepartmentDetail";
        try {
            Department dept = deptRepository.findById(deptId).get();
            DepartmentDTO deptDto = modelMapper.map(dept, DepartmentDTO.class);
            return deptDto;
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException("Cannot get department", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
    
    public boolean updateDepartment(HttpServletRequest request, Long deptId, DepartmentDTO deptDto, long refId) {
        String methodName = "udpdateDeparment";
        try {
            // get redis user info
            UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
            //insert data to pending_approvals
            long approvalId = insertDepartmentUpdatePA(userInfo, deptId, deptDto, refId);
            //send activity log
            activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_UPDATE_DEPARTMENT, ActivityLogService.ACTIVITY_UPDATE_DEPARTMENT_DESC, String.valueOf(deptId), approvalId);
            
            Department dept = deptRepository.findById(deptId).get();
            if (Utility.isNotNull(deptDto.getCode())) dept.setCode(deptDto.getCode());
            if (Utility.isNotNull(deptDto.getName())) dept.setName(deptDto.getName());
            if (Utility.isNotNull(deptDto.getStatus())) dept.setStatus(deptDto.getStatus());
            if (Utility.isNotNull(deptDto.getNote())) dept.setNote(deptDto.getNote());
            deptRepository.save(dept);
            return true;
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException("Cannot update this department", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
    
    public long insertDepartmentUpdatePA(UserInfoDTO userInfo, Long deptId, DepartmentDTO deptDto, long refId) {
        String methodName = "insertDepartmentUpdatePA";
        long approvalId = 0;
        try {
            approvalId = dbSeqService.generateSequence(PendingApproval.SEQUENCE_NAME, refId);
            NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
            nestedObjInfo.setDeptCode(userInfo.getDeptCode());

            PendingData pendingData = new PendingData();
            pendingData.setServiceFunctionName(ApprovalConstant.DEPARTMENT_UPDATE);
            pendingData.setCollectionName("departments");
            pendingData.setQueryField("_id");
            pendingData.setQueryValue(deptId);
            pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
            pendingData.setValue(new Gson().toJson(deptDto));

            Department deptpartment = deptRepository.findById(deptId).get();
            
            PendingApproval pendingApproval = new PendingApproval();
            pendingApproval.setId(approvalId);
            pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
            pendingApproval.setCreatorDate(System.currentTimeMillis());
            pendingApproval.setCreatorUser(userInfo.getUsername());
            pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_DEPARTMENT_INFO_UPDATE_CODE);
            pendingApproval.setFunctionName(SystemFunctionCode.DEPARTMENT_INFO_UPDATE_NAME);
            pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), deptpartment.getCode()));
            pendingApproval.setStatus(Constant.APPROVAL_STATUS_PENDING);
            pendingApproval.setNestedObjInfo(nestedObjInfo);
            pendingApproval.setPendingData(pendingData);
            pendingApprovalRepo.save(pendingApproval);
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
        }
        return approvalId;
    }
    
    public boolean createDepartmentUser(HttpServletRequest request, Long deptId, DeptUserDTO deptUserDto, long refId) {
        String methodName = "createDepartmentUser";
        boolean existedUser = false;
        try {
            existedUser = loginAdmUserRepo.existsLoginAdminUserByUsername(deptUserDto.getUsername());
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        
        if (!existedUser) {
            try {
                // get redis user info
                UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
                //insert data to pending_approvals
                long approvalId = insertDepartmentUserCreatePA(userInfo, deptId, deptUserDto, refId);
                //send activity log
                activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_DEPARTMENT_USER, ActivityLogService.ACTIVITY_CREATE_DEPARTMENT_USER_DESC, deptUserDto.getUsername(), approvalId);

                MongoDatabase database = MongoDBConnection.getMongoDatabase();
                MongoCollection<Document> collection = database.getCollection("departments");

                deptUserDto.setId(dbSeqService.generateSequence(DeptUser.SEQUENCE_NAME, refId));
                deptUserDto.setStatus(Constant.STATUS_ACTIVE);
                deptUserDto.setCreatedUser(Utility.getCurrentUsername());
                deptUserDto.setCreatedDate(System.currentTimeMillis());
                Document newDeptUser = Document.parse(new Gson().toJson(deptUserDto));

                BasicDBObject query = new BasicDBObject();
                query.put("_id", deptId);

                collection.updateOne(query, Updates.addToSet("users", newDeptUser));
                
                // insert loginAdminUser
                String password = Utility.generateRandomPassword();
                String pin = Utility.generateRandomPin();
                LoginAdminUser newLoginAdmUser = createLoginAdminUser(deptId, deptUserDto, password, pin, refId);
                
                //send email
                sendCreateNewUserEmail(deptUserDto.getEmail(), newLoginAdmUser.getUsername(), password, pin, refId);
                
                return true;
            } catch (Exception e) {
                AMLogger.logError(className, methodName, refId, e);
                throw new CustomException("Cannot insert this user", HttpStatus.UNPROCESSABLE_ENTITY);
            }
        } else {
            AMLogger.logMessage(className, methodName, refId, "This username already exists");
            throw new CustomException(ErrorMessage.DOCUMENT_ALREADY_EXISTS, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
    
    public long insertDepartmentUserCreatePA(UserInfoDTO userInfo, Long deptId, DeptUserDTO deptUserDto, long refId) {
        String methodName = "insertDepartmentUserCreatePA";
        long approvalId = 0;
        try {
            approvalId = dbSeqService.generateSequence(PendingApproval.SEQUENCE_NAME, refId);
            
            NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
            nestedObjInfo.setDeptCode(userInfo.getDeptCode());

            PendingData pendingData = new PendingData();
            pendingData.setServiceFunctionName(ApprovalConstant.DEPARTMENT_USER_CREATE);
            pendingData.setCollectionName("departments");
            pendingData.setQueryField("_id");
            pendingData.setQueryValue(deptId);
            pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
            pendingData.setValue(new Gson().toJson(deptUserDto));

            PendingApproval pendingApproval = new PendingApproval();
            pendingApproval.setId(approvalId);
            pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
            pendingApproval.setCreatorDate(System.currentTimeMillis());
            pendingApproval.setCreatorUser(userInfo.getUsername());
            pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_ADMIN_USER_CREATE_CODE);
            pendingApproval.setFunctionName(SystemFunctionCode.ADMIN_USER_CREATE_NAME);
            pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), deptUserDto.getUsername()));
            pendingApproval.setStatus(Constant.APPROVAL_STATUS_PENDING);
            pendingApproval.setNestedObjInfo(nestedObjInfo);
            pendingApproval.setPendingData(pendingData);
            pendingApprovalRepo.save(pendingApproval);
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
        }
        return approvalId;
    }
    
    private LoginAdminUser createLoginAdminUser(Long deptId, DeptUserDTO deptUserDto, String password, String pin, long refId) {
        String methodName = "createLoginAdminUser";
        try {
            LoginAdminUser loginAdmUser = modelMapper.map(deptUserDto, LoginAdminUser.class);
            loginAdmUser.setId(dbSeqService.generateSequence(LoginAdminUser.SEQUENCE_NAME, refId));
            loginAdmUser.setPassword(passwordEncoder.encode(password));
            loginAdmUser.setPin(passwordEncoder.encode(pin));
            loginAdmUser.setStatus(deptUserDto.getStatus());
            loginAdmUser.setDeptId(deptId);
            loginAdmUser.setDeptUserId(deptUserDto.getId());
            loginAdmUser.setCreatedUser(Utility.getCurrentUsername());
            loginAdmUser.setCreatedDate(System.currentTimeMillis());
            LoginAdminUser newLoginAdmUser = loginAdmUserRepo.save(loginAdmUser);
            return newLoginAdmUser;
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.DOCUMENT_ALREADY_EXISTS, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
    
    private void sendCreateNewUserEmail(String toEmail, String username, String password, String pin, long refId) {
        String methodName = "sendCreateNewUserEmail";
        try {
            LocalServiceConnection serviceCon = new LocalServiceConnection();
            EmailDTO email = new EmailDTO();
            email.setTo(toEmail);
            email.setSubject(FileUtility.CREATE_NEW_USER_EMAIL_SUBJECT);

            FileUtility fileUtility = new FileUtility();
            String emailBody = String.format(fileUtility.loadFileContent(ConfigLoader.getMainConfig().getString(FileUtility.CREATE_NEW_USER_EMAIL_FILE), refId), username, password, pin);
            email.setBodyStr(emailBody);
            String emailJson = new Gson().toJson(email);
            AMLogger.logMessage(className, methodName, refId, "Email: " + emailJson);
            serviceCon.sendPostRequest(serviceCon.getEmailNotificationServiceURL(), emailJson);
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
        }
    }
    
    public boolean updateDepartmentUser(HttpServletRequest request, Long deptId, Long deptUserId, DeptUserDTO deptUserDto, long refId) {
        String methodName = "updateDepartmentUser";
        try {
            // get redis user info
            UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
            //insert data to pending_approvals
            long approvalId = insertDepartmentUserUpdatePA(userInfo, deptId, deptUserId, deptUserDto, refId);
            //send activity log
            activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_UPDATE_DEPARTMENT_USER, ActivityLogService.ACTIVITY_UPDATE_DEPARTMENT_USER_DESC, String.valueOf(deptUserId), approvalId);
            
            MongoDatabase database = MongoDBConnection.getMongoDatabase();
            MongoCollection<Document> collection = database.getCollection("departments");
            
            BasicDBObject query = new BasicDBObject();
            query.put("_id", deptId);
            query.put("users._id", deptUserId);
            
            BasicDBObject newDocument = new BasicDBObject();
            
            if (Utility.isNotNull(deptUserDto.getFullName())) newDocument.put("users.$.fullName", deptUserDto.getFullName());
            if (Utility.isNotNull(deptUserDto.getPhoneNumber())) newDocument.put("users.$.phoneNumber", deptUserDto.getPhoneNumber());
            if (Utility.isNotNull(deptUserDto.getEmail())) newDocument.put("users.$.email", deptUserDto.getEmail());
            if (Utility.isNotNull(deptUserDto.getStatus())) newDocument.put("users.$.status", deptUserDto.getStatus());
            if (Utility.isNotNull(deptUserDto.getNote())) newDocument.put("users.$.note", deptUserDto.getNote());
            if (Utility.isNotNull(deptUserDto.isIsPasswordExpiryCheck())) newDocument.put("users.$.isPasswordExpiryCheck", deptUserDto.isIsPasswordExpiryCheck());
            if (Utility.isNotNull(deptUserDto.getPasswordExpiryDays()) && deptUserDto.getPasswordExpiryDays() > 0) newDocument.put("users.$.passwordExpiryDays", deptUserDto.getPasswordExpiryDays());
            if (Utility.isNotNull(deptUserDto.getExpiryAlertDays()) && deptUserDto.getExpiryAlertDays() > 0) newDocument.put("users.$.expiryAlertDays", deptUserDto.getExpiryAlertDays());

            newDocument.put("users.$.lastModifiedUser", Utility.getCurrentUsername());
            newDocument.put("users.$.lastModifiedDate", System.currentTimeMillis());
            
            BasicDBObject update = new BasicDBObject();
            update.put("$set", newDocument);
            
            collection.updateOne(query, update);

            return true;
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException("Cannot update this user", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
    
    public long insertDepartmentUserUpdatePA(UserInfoDTO userInfo, Long deptId, Long deptUserId, DeptUserDTO deptUserDto, long refId) {
        String methodName = "insertDepartmentUserUpdatePA";
        long approvalId = 0;
        try {
            approvalId = dbSeqService.generateSequence(PendingApproval.SEQUENCE_NAME, refId);
            
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
            pendingData.setValue(new Gson().toJson(deptUserDto));

            String username = getDepartmentUser(deptId, deptUserId, refId).getUsername();
            PendingApproval pendingApproval = new PendingApproval();
            pendingApproval.setId(approvalId);
            pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
            pendingApproval.setCreatorDate(System.currentTimeMillis());
            pendingApproval.setCreatorUser(userInfo.getUsername());
            pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_ADMIN_USER_UPDATE_CODE);
            pendingApproval.setFunctionName(SystemFunctionCode.ADMIN_USER_UPDATE_NAME);
            pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), username));
            pendingApproval.setStatus(Constant.APPROVAL_STATUS_PENDING);
            pendingApproval.setNestedObjInfo(nestedObjInfo);
            pendingApproval.setPendingData(pendingData);
            pendingApprovalRepo.save(pendingApproval);
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
        }
        return approvalId;
    }
    
    public DeptUserDTO getDepartmentUser(Long deptId, Long deptUserId, long refId) {
        String methodName = "getDepartmentUser";
        try {
            MongoDatabase database = MongoDBConnection.getMongoDatabase();
            MongoCollection<Document> collection = database.getCollection("departments");
            
            BasicDBObject query = new BasicDBObject();
            query.put("_id", deptId);
            query.put("users._id", deptUserId);
            
            BasicDBObject projection = new BasicDBObject();
            projection.append("_id", 0);
            projection.append("users.$", 1);
            
            Document deptDoc = collection.find(query).projection(projection).first();
            DepartmentDTO deptDto = new Gson().fromJson(deptDoc.toJson(Utility.getJsonWriterSettings()), DepartmentDTO.class);
            return deptDto.getUsers().get(0);
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException("Cannot view this user", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
    
    public boolean saveDepartmentUserRoles(HttpServletRequest request, Long deptId, Long deptUserId, DeptUserDTO userDto, long refId) {
        String methodName = "saveDepartmentUserRoles";
        boolean userFound = false;
        if (Utility.isNotNull(userDto.getRoles()) && userDto.getRoles().size() > 0) {
            try {
                // get redis user info
                UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
                //insert data to pending_approvals
                long approvalId = insertDeptUserRoleCreatePA(userInfo, deptId, deptUserId, userDto, refId);
                //send activity log
                activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_DEPARTMENT_USER_ROLE, ActivityLogService.ACTIVITY_CREATE_DEPARTMENT_USER_ROLE_DESC, String.valueOf(deptUserId), approvalId);
                
                Department dept = deptRepository.findById(deptId).get();
                for (DeptUser user : dept.getUsers()) {
                    if (deptUserId.equals(user.getId())) {
                        userFound = true;
                        user.setRoles(userDto.getRoles());
                        break;
                    }
                }
                if (userFound) {
                    deptRepository.save(dept);
                } else {
                    AMLogger.logMessage(className, methodName, refId, "Cannot find user with id=" + deptUserId);
                    throw new CustomException(ErrorMessage.USER_DOES_NOT_EXIST, HttpStatus.UNPROCESSABLE_ENTITY);
                }
                return true;
            } catch (Exception e) {
                AMLogger.logError(className, methodName, refId, e);
                throw new CustomException("Cannot view this user", HttpStatus.UNPROCESSABLE_ENTITY);
            }
        } else {
            AMLogger.logMessage(className, methodName, refId, "Invalid input data");
            throw new CustomException(ErrorMessage.INVALID_INPUT_DATA, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
    
    public long insertDeptUserRoleCreatePA(UserInfoDTO userInfo, Long deptId, Long deptUserId, DeptUserDTO deptUserDto, long refId) {
        String methodName = "insertDeptUserRoleCreatePA";
        long approvalId = 0;
        try {
            approvalId = dbSeqService.generateSequence(PendingApproval.SEQUENCE_NAME, refId);
            
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
            pendingData.setValue(new Gson().toJson(deptUserDto));

            PendingApproval pendingApproval = new PendingApproval();
            pendingApproval.setId(approvalId);
            pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
            pendingApproval.setCreatorDate(System.currentTimeMillis());
            pendingApproval.setCreatorUser(userInfo.getUsername());
            pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_ADMIN_USER_ROLE_ASSIGN_CREATE_CODE);
            pendingApproval.setFunctionName(SystemFunctionCode.ADMIN_USER_ROLE_ASSIGN_CREATE_NAME);
            pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), deptUserDto.getUsername()));
            pendingApproval.setStatus(Constant.APPROVAL_STATUS_PENDING);
            pendingApproval.setNestedObjInfo(nestedObjInfo);
            pendingApproval.setPendingData(pendingData);
            pendingApprovalRepo.save(pendingApproval);
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
        }
        return approvalId;
    }
    
    public boolean saveDepartmentUserFunctions(HttpServletRequest request, Long deptId, Long deptUserId, DeptUserDTO userDto, long refId) {
        String methodName = "saveDepartmentUserFunctions";
        boolean userFound = false;
        if (Utility.isNotNull(userDto.getFunctions()) && userDto.getFunctions().size() > 0) {
            try {
                // get redis user info
                UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
                //insert data to pending_approvals
                long approvalId = insertDeptUserFunctionsCreatePA(userInfo, deptId, deptUserId, userDto, refId);
                //send activity log
                activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_DEPARTMENT_USER_FUNCTIONS, ActivityLogService.ACTIVITY_CREATE_DEPARTMENT_USER_FUNCTIONS_DESC, String.valueOf(deptUserId), approvalId);
                
                Department dept = deptRepository.findById(deptId).get();
                for (DeptUser user : dept.getUsers()) {
                    if (deptUserId.equals(user.getId())) {
                        userFound = true;
                        user.setFunctions(userDto.getFunctions());
                        break;
                    }
                }
                if (userFound) {
                    deptRepository.save(dept);
                } else {
                    AMLogger.logMessage(className, methodName, refId, "Cannot find user with id=" + deptUserId);
                    throw new CustomException(ErrorMessage.USER_DOES_NOT_EXIST, HttpStatus.UNPROCESSABLE_ENTITY);
                }
                return true;
            } catch (Exception e) {
                AMLogger.logError(className, methodName, refId, e);
                throw new CustomException("Cannot view this user", HttpStatus.UNPROCESSABLE_ENTITY);
            }
        } else {
            AMLogger.logMessage(className, methodName, refId, "Invalid input data");
            throw new CustomException(ErrorMessage.INVALID_INPUT_DATA, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
    
    public long insertDeptUserFunctionsCreatePA(UserInfoDTO userInfo, Long deptId, Long deptUserId, DeptUserDTO deptUserDto, long refId) {
        String methodName = "insertDeptUserFunctionsCreatePA";
        long approvalId = 0;
        try {
            approvalId = dbSeqService.generateSequence(PendingApproval.SEQUENCE_NAME, refId);
            
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
            pendingData.setValue(new Gson().toJson(deptUserDto));

            PendingApproval pendingApproval = new PendingApproval();
            pendingApproval.setId(approvalId);
            pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
            pendingApproval.setCreatorDate(System.currentTimeMillis());
            pendingApproval.setCreatorUser(userInfo.getUsername());
            pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_ADMIN_USER_FUNCTIONS_ASSIGN_CREATE_CODE);
            pendingApproval.setFunctionName(SystemFunctionCode.ADMIN_USER_FUNCTIONS_ASSIGN_CREATE_NAME);
            pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), deptUserDto.getUsername()));
            pendingApproval.setStatus(Constant.APPROVAL_STATUS_PENDING);
            pendingApproval.setNestedObjInfo(nestedObjInfo);
            pendingApproval.setPendingData(pendingData);
            pendingApprovalRepo.save(pendingApproval);
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
        }
        return approvalId;
    }
}
