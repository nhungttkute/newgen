package com.newgen.am.controller;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.Constant;
import com.newgen.am.common.CustomMappingStrategy;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.AdminDataObj;
import com.newgen.am.dto.AdminResponseObj;
import com.newgen.am.dto.ApprovalFunctionsDTO;
import com.newgen.am.dto.ApprovalUpdateRoleDTO;
import com.newgen.am.dto.BasePagination;
import com.newgen.am.dto.RoleCSV;
import com.newgen.am.dto.RoleDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.service.MemberRoleService;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

@RestController
public class MemberRoleController {
private String className = "MemberRoleController";
    
    @Autowired
    MemberRoleService memberRoleService;
    
    @GetMapping("/admin/members/{memberCode}/memberRoles")
    @PreAuthorize("hasAuthority('clientManagement.memberManagement.memberRoleManagement.memberRoleList.view') or hasAuthority('clientManagement.memberManagement.memberUserManagement.memberUserRole.view')")
    public AdminResponseObj listMemberRoles(HttpServletRequest request, @PathVariable String memberCode) {
        String methodName = "listMemberRoles";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[GET]/admin/members/%s/memberRoles", memberCode));
        
        AdminResponseObj response = new AdminResponseObj();
        try {
        	BasePagination<RoleDTO> pagination = memberRoleService.list(request, memberCode, refId);
            if (pagination != null && pagination.getData().size() > 0) {
                response.setStatus(Constant.RESPONSE_OK);
                response.setData(new AdminDataObj());
                response.getData().setMemberRoles(pagination.getData());
                response.setPagination(Utility.getPagination(request, pagination.getCount()));
                response.setFilterList(Arrays.asList(Constant.STATUS_ACTIVE, Constant.STATUS_INACTIVE));
            } else {
                response.setStatus(Constant.RESPONSE_ERROR);
                response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
            }
        } catch (Exception e) {
        	AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }
    
    @GetMapping("/admin/members/{memberCode}/memberRoles/csv")
    @PreAuthorize("hasAuthority('clientManagement.memberManagement.memberRoleManagement.memberRoleList.view')")
    public void downloadMemberRolesCsv(HttpServletRequest request, HttpServletResponse response, @PathVariable String memberCode) {
        String methodName = "downloadMemberRolesCsv";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[GET]/admin/members/%s/memberRoles/csv", memberCode));
        
        try {
        	//set file name and content type
            String filename = Constant.CSV_MEMBER_ROLES;

            response.setContentType("text/csv");
            response.setCharacterEncoding("UTF-8");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + filename + "\"");

            //create a csv writer
            CustomMappingStrategy<RoleCSV> mappingStrategy = new CustomMappingStrategy<RoleCSV>();
            mappingStrategy.setType(RoleCSV.class);
            
            StatefulBeanToCsv<RoleCSV> writer = new StatefulBeanToCsvBuilder<RoleCSV>(response.getWriter())
            		.withMappingStrategy(mappingStrategy)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withOrderedResults(false)
                    .build();

            //write all users to csv file
            writer.write(memberRoleService.listCsv(request, memberCode, refId));
        } catch (Exception e) {
        	AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/admin/members/{memberCode}/memberRoles")
    @PreAuthorize("hasAuthority('clientManagement.memberManagement.memberRoleManagement.memberRole.create')")
    public AdminResponseObj createMemberRole(HttpServletRequest request, @PathVariable String memberCode, @Valid @RequestBody RoleDTO roleDto) {
        String methodName = "createMemberRole";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[POST]/admin/members/%s/memberRoles", memberCode));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + Utility.getGson().toJson(roleDto));
        
        memberRoleService.createMemberRolePA(request, memberCode, roleDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }
    
    @PutMapping("/admin/members/{memberCode}/memberRoles/{roleId}")
    @PreAuthorize("hasAuthority('clientManagement.memberManagement.memberRoleManagement.memberRole.update')")
    public AdminResponseObj updateMemberRole(HttpServletRequest request, @PathVariable String memberCode, @PathVariable String roleId, @Valid @RequestBody ApprovalUpdateRoleDTO roleDto) {
        String methodName = "updateMemberRole";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[PUT]/admin/members/%s/memberRoles/%s", memberCode, roleId));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + Utility.getGson().toJson(roleDto));
        
        memberRoleService.updateMemberRolePA(request, memberCode, roleId, roleDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }
    
    @PostMapping("/admin/members/{memberCode}/memberRoles/{roleId}/functions")
    @PreAuthorize("hasAuthority('clientManagement.memberManagement.memberRoleManagement.memberRoleFunctionsAssign.create')")
    public AdminResponseObj createMemberRoleFunctions(HttpServletRequest request, @PathVariable String memberCode, @PathVariable String roleId, @Valid @RequestBody ApprovalFunctionsDTO roleDto) {
        String methodName = "createMemberRoleFunctions";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[POST]/admin/members/%s/memberRoles/%s/functions", memberCode, roleId));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + Utility.getGson().toJson(roleDto));
        
        memberRoleService.createMemberRoleFunctionsPA(request, memberCode, roleId, roleDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }
}
