/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.controller;

import com.google.gson.Gson;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.Constant;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.dto.ResponseObj;
import com.newgen.am.dto.AdminDataObj;
import com.newgen.am.dto.AdminResponseObj;
import com.newgen.am.dto.DataObj;
import com.newgen.am.dto.InvestorAccountDTO;
import com.newgen.am.service.InvestorService;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author nhungtt
 */
@RestController
public class InvestorController {
    private String className = "InvestorController";
    
    @Autowired
    InvestorService investorService;
    
    @GetMapping("/users/account")
    public ResponseObj getAccountSummary() {
        String methodName = "getAccountSummary";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: /users/account");
        
        ResponseObj response = new ResponseObj();
        InvestorAccountDTO investorAcc = investorService.getInvestorAccount(refId);
        if (investorAcc != null) {
            response.setStatus(Constant.RESPONSE_OK);
            response.setData(new DataObj());
            response.getData().setInvestorAccount(investorAcc);
        } else {
            response.setStatus(Constant.RESPONSE_ERROR);
            response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
        }
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
    
    @GetMapping("/admin/investorCodes")
    public AdminResponseObj getInvestorCodesByUser() {
        String methodName = "getInvestorCodesByUser";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: /admin/investorCodes");
        
        List<String> investorCodes = investorService.getInvestorCodesByUser(refId);
        
        AdminResponseObj response = new AdminResponseObj();
        if (investorCodes != null && investorCodes.size() > 0) {
            response.setStatus(Constant.RESPONSE_OK);
            response.setData(new AdminDataObj());
            response.getData().setInvestorCodes(investorCodes);
        } else {
            response.setStatus(Constant.RESPONSE_ERROR);
            response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
        }
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
}
