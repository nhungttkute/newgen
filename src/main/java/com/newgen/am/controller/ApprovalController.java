/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.google.gson.Gson;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.Constant;
import com.newgen.am.dto.AdminResponseObj;

/**
 *
 * @author nhungtt
 */
public class ApprovalController {
    private String className = "ApprovalController";
    
    @PostMapping("/admin/pendingApprovals/{approvalId}")
    public AdminResponseObj processPendingApproval(@PathVariable Long approvalId) {
        String methodName = "processPendingApproval";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/pendingApprovals/" + approvalId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
    
    @PostMapping("/admin/accountTransApprovals/{approvalId}")
    public AdminResponseObj processAccountTransApproval(@PathVariable Long approvalId) {
        String methodName = "processAccountTransApproval";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/accountTransApprovals/" + approvalId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
    
    @PostMapping("/admin/accActivationApprovals/{approvalId}")
    public AdminResponseObj processAccountActivationApproval(@PathVariable Long approvalId) {
        String methodName = "processAccountActivationApproval";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/accActivationApprovals/" + approvalId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
}
