/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.service;

import com.google.gson.Gson;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.ApprovalConstant;
import com.newgen.am.common.Constant;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.SystemFunctionCode;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.SystemRoleDTO;
import com.newgen.am.dto.UserInfoDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.LoginAdminUser;
import com.newgen.am.model.NestedObjectInfo;
import com.newgen.am.model.PendingApproval;
import com.newgen.am.model.PendingData;
import com.newgen.am.model.SystemRole;
import com.newgen.am.repository.LoginAdminUserRepository;
import com.newgen.am.repository.PendingApprovalRepository;
import com.newgen.am.repository.SystemRoleRepository;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
    private ActivityLogService activityLogService;
    
    @Autowired
    PendingApprovalRepository pendingApprovalRepo;
    
    public List<SystemRoleDTO> list(long refId) {
        String methodName = "list";
        try {
            List<SystemRole> systemRoles = systemRoleRepo.findAll();
            List<SystemRoleDTO> systemRoleDTOs = systemRoles.stream().map(source -> modelMapper.map(source, SystemRoleDTO.class)).collect(Collectors.toList());
            return systemRoleDTOs;
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException("Cannot list system roles", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
    
    public boolean createSystemRole(HttpServletRequest request, SystemRoleDTO sysRoleDto, long refId) {
        String methodName = "createSystemRole";
        boolean existedRole = false;
        try {
            existedRole = systemRoleRepo.existsSystemRoleByName(sysRoleDto.getName());
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        
        if (!existedRole) {
            try {
                // get redis user info
                UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
                //insert data to pending_approvals
                long approvalId = insertSystemRoleCreatePA(userInfo, sysRoleDto, refId);
                //send activity log
                activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_SYS_ROLE, ActivityLogService.ACTIVITY_CREATE_SYS_ROLE_DESC, sysRoleDto.getName(), approvalId);

                SystemRole sysRole = modelMapper.map(sysRoleDto, SystemRole.class);
                sysRole.setId(dbSeqService.generateSequence(SystemRole.SEQUENCE_NAME, refId));
                sysRole.setCreatedDate(System.currentTimeMillis());
                sysRole.setCreatedUser(Utility.getCurrentUsername());
                
                systemRoleRepo.save(sysRole);
                return true;
            } catch (Exception e) {
                AMLogger.logError(className, methodName, refId, e);
                throw new CustomException("Cannot create this system role", HttpStatus.UNPROCESSABLE_ENTITY);
            }
        } else {
            AMLogger.logMessage(className, methodName, refId, "This system role already exists");
            throw new CustomException(ErrorMessage.DOCUMENT_ALREADY_EXISTS, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
    
    public long insertSystemRoleCreatePA(UserInfoDTO userInfo, SystemRoleDTO sysRoleDto, long refId) {
        String methodName = "insertSystemRoleCreatePA";
        long approvalId = 0;
        try {
            approvalId = dbSeqService.generateSequence(PendingApproval.SEQUENCE_NAME, refId);
            
            NestedObjectInfo nestedObjInfo = new NestedObjectInfo();
            nestedObjInfo.setDeptCode(userInfo.getDeptCode());

            PendingData pendingData = new PendingData();
            pendingData.setServiceFunctionName(ApprovalConstant.SYSTEM_ROLE_CREATE);
            pendingData.setCollectionName("system_roles");
            pendingData.setAction(Constant.APPROVAL_ACTION_CREATE);
            pendingData.setValue(new Gson().toJson(sysRoleDto));

            PendingApproval pendingApproval = new PendingApproval();
            pendingApproval.setId(approvalId);
            pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
            pendingApproval.setCreatorDate(System.currentTimeMillis());
            pendingApproval.setCreatorUser(userInfo.getUsername());
            pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_SYSTEM_ROLE_CREATE_CODE);
            pendingApproval.setFunctionName(SystemFunctionCode.SYSTEM_ROLE_CREATE_NAME);
            pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), sysRoleDto.getName()));
            pendingApproval.setStatus(Constant.APPROVAL_STATUS_PENDING);
            pendingApproval.setNestedObjInfo(nestedObjInfo);
            pendingApproval.setPendingData(pendingData);
            pendingApprovalRepo.save(pendingApproval);
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
        }
        return approvalId;
    }
    
    public boolean updateSystemRole(HttpServletRequest request, Long sysRoleId, SystemRoleDTO sysRoleDto, long refId) {
        String methodName = "updateSystemRole";
        try {
            // get redis user info
            UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
            //insert data to pending_approvals
            long approvalId = insertSystemRoleUpdatePA(userInfo, sysRoleId, sysRoleDto, refId);
            //send activity log
            activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_UPDATE_SYS_ROLE, ActivityLogService.ACTIVITY_UPDATE_SYS_ROLE_DESC, String.valueOf(sysRoleId), approvalId);
            
            SystemRole sysRole = systemRoleRepo.findById(sysRoleId).get();
            if (Utility.isNotNull(sysRoleDto.getName())) sysRole.setName(sysRoleDto.getName());
            if (Utility.isNotNull(sysRoleDto.getDescription())) sysRole.setDescription(sysRoleDto.getDescription());
            if (Utility.isNotNull(sysRoleDto.getStatus())) sysRole.setStatus(sysRoleDto.getStatus());
            systemRoleRepo.save(sysRole);
            return true;
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
            throw new CustomException("Cannot update this department", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
    
    public long insertSystemRoleUpdatePA(UserInfoDTO userInfo, Long sysRoleId, SystemRoleDTO sysRoleDto, long refId) {
        String methodName = "insertSystemRoleUpdatePA";
        long approvalId = 0;
        try {
            approvalId = dbSeqService.generateSequence(PendingApproval.SEQUENCE_NAME, refId);
            
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
            pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), sysRole.getName()));
            pendingApproval.setStatus(Constant.APPROVAL_STATUS_PENDING);
            pendingApproval.setNestedObjInfo(nestedObjInfo);
            pendingApproval.setPendingData(pendingData);
            pendingApprovalRepo.save(pendingApproval);
        } catch (Exception e) {
            AMLogger.logError(className, methodName, refId, e);
        }
        return approvalId;
    }
    
    public boolean createSystemRoleFunctions(HttpServletRequest request, Long sysRoleId, SystemRoleDTO sysRoleDto, long refId) {
        String methodName = "createSystemRoleFunctions";
        if (Utility.isNotNull(sysRoleDto.getFunctions()) && sysRoleDto.getFunctions().size() > 0) {
            try {
                // get redis user info
                UserInfoDTO userInfo = Utility.getRedisUserInfo(template, Utility.getAccessToken(request), refId);
                //insert data to pending_approvals
                long approvalId = insertSystemRoleFunctionsAssignPA(userInfo, sysRoleId, sysRoleDto, refId);
                //send activity log
                activityLogService.sendActivityLog(userInfo, request, ActivityLogService.ACTIVITY_CREATE_SYS_ROLE_FUNCTIONS, ActivityLogService.ACTIVITY_CREATE_SYS_ROLE_FUNCTIONS_DESC, String.valueOf(sysRoleId), approvalId);

                SystemRole sysRole = systemRoleRepo.findById(sysRoleId).get();
                sysRole.setFunctions(sysRoleDto.getFunctions());
                systemRoleRepo.save(sysRole);
                return true;
            } catch (Exception e) {
                AMLogger.logError(className, methodName, refId, e);
                throw new CustomException("Cannot update this department", HttpStatus.UNPROCESSABLE_ENTITY);
            }
        } else {
            AMLogger.logMessage(className, methodName, refId, "Invalid input data");
            throw new CustomException(ErrorMessage.INVALID_INPUT_DATA, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
    
    public long insertSystemRoleFunctionsAssignPA(UserInfoDTO userInfo, Long sysRoleId, SystemRoleDTO sysRoleDto, long refId) {
        String methodName = "insertSystemRoleFunctionsAssignPA";
        long approvalId = 0;
        try {
            approvalId = dbSeqService.generateSequence(PendingApproval.SEQUENCE_NAME, refId);
            
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
            pendingApproval.setId(approvalId);
            pendingApproval.setApiUrl(String.format(ApprovalConstant.APPROVAL_PENDING_URL, approvalId));
            pendingApproval.setCreatorDate(System.currentTimeMillis());
            pendingApproval.setCreatorUser(userInfo.getUsername());
            pendingApproval.setFunctionCode(SystemFunctionCode.APPROVAL_SYSTEM_ROLE_FUNCTIONS_ASSIGN_CREATE_CODE);
            pendingApproval.setFunctionName(SystemFunctionCode.SYSTEM_ROLE_FUNCTIONS_ASSIGN_CREATE_NAME);
            pendingApproval.setDescription(SystemFunctionCode.getApprovalDescription(pendingApproval.getFunctionName(), sysRole.getName()));
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
