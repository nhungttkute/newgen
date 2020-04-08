/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.service;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
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
import com.newgen.am.common.SystemFunctionCode;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.DepartmentDTO;
import com.newgen.am.dto.DeptUserDTO;
import com.newgen.am.dto.EmailDTO;
import com.newgen.am.dto.LoginUserDataInputDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.Department;
import com.newgen.am.model.DeptUser;
import com.newgen.am.model.LoginAdminUser;
import com.newgen.am.model.NestedObjectInfo;
import com.newgen.am.model.PendingApproval;
import com.newgen.am.model.PendingData;
import com.newgen.am.repository.DepartmentRepository;
import com.newgen.am.repository.LoginAdminUserRepository;
import com.newgen.am.repository.PendingApprovalRepository;
import java.util.List;
import org.bson.Document;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
    PasswordEncoder passwordEncoder;
    
    public List<Department> list(long refId) {
        String methodName = "list";
        try {
            return deptRepository.findAll();
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException("Cannot list departments", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public boolean createDepartment(DepartmentDTO deptDto, long refId) {
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
                //insert data to pending_approvals
                insertDepartmentCreatePA(deptDto, refId);

                Department dept = modelMapper.map(deptDto, Department.class);
                dept.setId(dbSeqService.generateSequence(Department.SEQUENCE_NAME, refId));
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

    public void insertDepartmentCreatePA(DepartmentDTO deptDto, long refId) {
        String methodName = "insertDepartmentCreatePA";
        try {
            String creatorUser = Utility.getUsername();
            LoginAdminUser loginAdminUser = loginAdmUserRepo.findByUsername(creatorUser);
            
            NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
            nestedObjInfo.setDepartmentId(loginAdminUser.getDepartmentId());
            nestedObjInfo.setDeptUserId(loginAdminUser.getUserId());

            PendingData pendingData = new PendingData();
            pendingData.setServiceFunctionName(ApprovalConstant.DEPARTMENT_CREATE);
            pendingData.setCollectionName("departments");
            pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
            pendingData.setValue(new Gson().toJson(deptDto));

            PendingApproval pendingApproval = new PendingApproval();
            pendingApproval.setId(dbSeqService.generateSequence(PendingApproval.SEQUENCE_NAME, refId));
            pendingApproval.setCreatorDate(System.currentTimeMillis());
            pendingApproval.setCreatorUser(creatorUser);
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
    
    public boolean updateDepartment(Long deptId, DepartmentDTO deptDto, long refId) {
        String methodName = "udpdateDeparment";
        try {
            //insert data to pending_approvals
            insertDepartmentUpdatePA(deptId, deptDto, refId);
            
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
    
    public void insertDepartmentUpdatePA(Long deptId, DepartmentDTO deptDto, long refId) {
        String methodName = "insertDepartmentUpdatePA";
        try {
            String creatorUser = Utility.getUsername();
            LoginAdminUser loginAdminUser = loginAdmUserRepo.findByUsername(creatorUser);
            
            NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
            nestedObjInfo.setDepartmentId(loginAdminUser.getDepartmentId());
            nestedObjInfo.setDeptUserId(loginAdminUser.getUserId());

            PendingData pendingData = new PendingData();
            pendingData.setServiceFunctionName(ApprovalConstant.DEPARTMENT_UPDATE);
            pendingData.setCollectionName("departments");
            pendingData.setQueryField("_id");
            pendingData.setQueryValue(deptId);
            pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
            pendingData.setValue(new Gson().toJson(deptDto));

            PendingApproval pendingApproval = new PendingApproval();
            pendingApproval.setId(dbSeqService.generateSequence(PendingApproval.SEQUENCE_NAME, refId));
            pendingApproval.setCreatorDate(System.currentTimeMillis());
            pendingApproval.setCreatorUser(creatorUser);
            pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_DEPARTMENT_INFO_UPDATE_CODE);
            pendingApproval.setFunctionName(SystemFunctionCode.DEPARTMENT_INFO_UPDATE_NAME);
            pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), deptDto.getCode()));
            pendingApproval.setStatus(Constant.APPROVAL_STATUS_PENDING);
            pendingApproval.setNestedObjInfo(nestedObjInfo);
            pendingApproval.setPendingData(pendingData);
            pendingApprovalRepo.save(pendingApproval);
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
        }
    }
    
    public boolean createDepartmentUser(Long deptId, DeptUserDTO deptUserDto, long refId) {
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
                //insert data to pending_approvals
                insertDepartmentUserCreatePA(deptId, deptUserDto, refId);

                MongoDatabase database = MongoDBConnection.getMongoDatabase();
                MongoCollection<Document> collection = database.getCollection("departments");

                deptUserDto.setId(dbSeqService.generateSequence(DeptUser.SEQUENCE_NAME, refId));
                deptUserDto.setStatus(Constant.STATUS_ACTIVE);
                Document newDeptUser = Document.parse(new Gson().toJson(deptUserDto));

                BasicDBObject query = new BasicDBObject();
                query.put("_id", deptId);

                collection.updateOne(query, Updates.addToSet("users", newDeptUser));
                
                // insert loginAdminUser
                String password = Utility.generateRandomPassword();
                LoginAdminUser newLoginAdmUser = createLoginAdminUser(deptId, deptUserDto, password, refId);
                
                //send email
                sendCreateNewUserEmail(deptUserDto.getEmail(), newLoginAdmUser, password, refId);
                
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
    
    public void insertDepartmentUserCreatePA(Long deptId, DeptUserDTO deptUserDto, long refId) {
        String methodName = "insertDepartmentUserCreatePA";
        try {
            String creatorUser = Utility.getUsername();
            LoginAdminUser loginAdminUser = loginAdmUserRepo.findByUsername(creatorUser);
            
            NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
            nestedObjInfo.setDepartmentId(loginAdminUser.getDepartmentId());
            nestedObjInfo.setDeptUserId(loginAdminUser.getUserId());

            PendingData pendingData = new PendingData();
            pendingData.setServiceFunctionName(ApprovalConstant.DEPARTMENT_USER_CREATE);
            pendingData.setCollectionName("departments");
            pendingData.setQueryField("_id");
            pendingData.setQueryValue(deptId);
            pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
            pendingData.setValue(new Gson().toJson(deptUserDto));

            PendingApproval pendingApproval = new PendingApproval();
            pendingApproval.setId(dbSeqService.generateSequence(PendingApproval.SEQUENCE_NAME, refId));
            pendingApproval.setCreatorDate(System.currentTimeMillis());
            pendingApproval.setCreatorUser(creatorUser);
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
    }
    
    private LoginAdminUser createLoginAdminUser(Long deptId, DeptUserDTO deptUserDto, String password, long refId) {
        String methodName = "createLoginAdminUser";
        try {
            LoginAdminUser loginAdmUser = modelMapper.map(deptUserDto, LoginAdminUser.class);
            loginAdmUser.setId(dbSeqService.generateSequence(LoginAdminUser.SEQUENCE_NAME, refId));
            loginAdmUser.setPassword(passwordEncoder.encode(password));
            loginAdmUser.setStatus(deptUserDto.getStatus());
            loginAdmUser.setDepartmentId(deptId);
            loginAdmUser.setUserId(deptUserDto.getId());
            LoginAdminUser newLoginAdmUser = loginAdmUserRepo.save(loginAdmUser);
            return newLoginAdmUser;
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.DOCUMENT_ALREADY_EXISTS, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
    
    private void sendCreateNewUserEmail(String toEmail, LoginAdminUser loginAdmUser, String password, long refId) {
        String methodName = "sendCreateNewUserEmail";
        try {
            LocalServiceConnection serviceCon = new LocalServiceConnection();
            EmailDTO email = new EmailDTO();
            email.setTo(toEmail);
            email.setSubject(FileUtility.CREATE_NEW_USER_EMAIL_SUBJECT);

            FileUtility fileUtility = new FileUtility();
            String emailBody = String.format(fileUtility.loadFileContent(ConfigLoader.getMainConfig().getString(FileUtility.CREATE_NEW_USER_EMAIL_FILE), refId), loginAdmUser.getUsername(), password, loginAdmUser.getPin());
            email.setBodyStr(emailBody);
            String emailJson = new Gson().toJson(email);
            AMLogger.logMessage(className, methodName, refId, "Email: " + emailJson);
            serviceCon.sendPostRequest(serviceCon.getEmailNotificationServiceURL(), emailJson);
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
        }
    }
    
    public boolean updateDepartmentUser(Long deptId, Long deptUserId, DeptUserDTO deptUserDto, long refId) {
        String methodName = "updateDepartmentUser";
        try {
            //insert data to pending_approvals
            insertDepartmentUserUpdatePA(deptId, deptUserId, deptUserDto, refId);

            MongoDatabase database = MongoDBConnection.getMongoDatabase();
            MongoCollection<Document> collection = database.getCollection("departments");
            
            BasicDBObject query = new BasicDBObject();
            query.put("_id", deptId);
            query.put("users.id", deptUserId);
            
            BasicDBObject newDocument = new BasicDBObject();
            
            if (Utility.isNotNull(deptUserDto.getFullName())) newDocument.put("users.$.fullName", deptUserDto.getFullName());
            if (Utility.isNotNull(deptUserDto.getPhoneNumber())) newDocument.put("users.$.phoneNumber", deptUserDto.getPhoneNumber());
            if (Utility.isNotNull(deptUserDto.getEmail())) newDocument.put("users.$.email", deptUserDto.getEmail());
            if (Utility.isNotNull(deptUserDto.getStatus())) newDocument.put("users.$.status", deptUserDto.getStatus());
            if (Utility.isNotNull(deptUserDto.getNote())) newDocument.put("users.$.note", deptUserDto.getNote());
            if (Utility.isNotNull(deptUserDto.isIsPasswordExpiryCheck())) newDocument.put("users.$.isPasswordExpiryCheck", deptUserDto.isIsPasswordExpiryCheck());
            if (Utility.isNotNull(deptUserDto.getPasswordExpiryDays()) && deptUserDto.getPasswordExpiryDays() > 0) newDocument.put("users.$.passwordExpiryDays", deptUserDto.getPasswordExpiryDays());
            if (Utility.isNotNull(deptUserDto.getExpiryAlertDays()) && deptUserDto.getExpiryAlertDays() > 0) newDocument.put("users.$.expiryAlertDays", deptUserDto.getExpiryAlertDays());

            BasicDBObject updateObj = new BasicDBObject();
            updateObj.put("$set", newDocument);
            
            collection.updateOne(query, updateObj);

            return true;
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException("Cannot update this user", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
    
    public void insertDepartmentUserUpdatePA(Long deptId, Long deptUserId, DeptUserDTO deptUserDto, long refId) {
        String methodName = "insertDepartmentUserUpdatePA";
        try {
            String creatorUser = Utility.getUsername();
            LoginAdminUser loginAdminUser = loginAdmUserRepo.findByUsername(creatorUser);
            
            NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
            nestedObjInfo.setDepartmentId(loginAdminUser.getDepartmentId());
            nestedObjInfo.setDeptUserId(loginAdminUser.getUserId());

            PendingData pendingData = new PendingData();
            pendingData.setServiceFunctionName(ApprovalConstant.DEPARTMENT_USER_UPDATE);
            pendingData.setCollectionName("departments");
            pendingData.setQueryField("_id");
            pendingData.setQueryValue(deptId);
            pendingData.setQueryField2("users.id");
            pendingData.setQueryValue2(deptUserId);
            pendingData.setAction(Constant.APPROVAL_ACTION_UPDATE);
            pendingData.setValue(new Gson().toJson(deptUserDto));

            PendingApproval pendingApproval = new PendingApproval();
            pendingApproval.setId(dbSeqService.generateSequence(PendingApproval.SEQUENCE_NAME, refId));
            pendingApproval.setCreatorDate(System.currentTimeMillis());
            pendingApproval.setCreatorUser(creatorUser);
            pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_ADMIN_USER_UPDATE_CODE);
            pendingApproval.setFunctionName(SystemFunctionCode.ADMIN_USER_UPDATE_NAME);
            pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), deptUserDto.getUsername()));
            pendingApproval.setStatus(Constant.APPROVAL_STATUS_PENDING);
            pendingApproval.setNestedObjInfo(nestedObjInfo);
            pendingApproval.setPendingData(pendingData);
            pendingApprovalRepo.save(pendingApproval);
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
        }
    }
    
    public DeptUserDTO getDepartmentUser(Long deptId, Long deptUserId, long refId) {
        String methodName = "getDepartmentUser";
        try {
            MongoDatabase database = MongoDBConnection.getMongoDatabase();
            MongoCollection<Document> collection = database.getCollection("departments");
            
            BasicDBObject query = new BasicDBObject();
            query.put("_id", deptId);
            query.put("users.id", deptUserId);
            
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
}
