/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.Constant;
import com.newgen.am.dto.AdminResponseObj;
import com.newgen.am.dto.InterestRateDTO;
import com.newgen.am.service.InvestorActivationService;
import com.newgen.am.service.PendingApprovalService;
import com.newgen.am.validation.ValidationSequence;

/**
 *
 * @author nhungtt
 */
@RestController
public class ApprovalController {
    private String className = "ApprovalController";
    
    @Autowired
    private PendingApprovalService pendingApprovalService;
    
    @Autowired
    private InvestorActivationService invActivationService;
    
    @PostMapping("/admin/pendingApprovals/{approvalId}")
    public AdminResponseObj processPendingApproval(HttpServletRequest request, @PathVariable String approvalId) {
        String methodName = "processPendingApproval";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/pendingApprovals/" + approvalId);
        
        pendingApprovalService.approve(request, approvalId, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
    
    @PostMapping("/admin/accountTransApprovals/{approvalId}")
    public AdminResponseObj processAccountTransApproval(HttpServletRequest request, @PathVariable String approvalId) {
        String methodName = "processAccountTransApproval";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/accountTransApprovals/" + approvalId);
        
        pendingApprovalService.approveMarginTrans(request, approvalId, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
    
    @PostMapping("/admin/accActivationApprovals/{approvalId}")
    public AdminResponseObj processAccountActivationApproval(HttpServletRequest request, @PathVariable String approvalId, @Validated(ValidationSequence.class) @RequestBody InterestRateDTO interestRateDto) {
        String methodName = "processAccountActivationApproval";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/accActivationApprovals/" + approvalId);
        
        invActivationService.activateInvestor(request, approvalId, interestRateDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
}
