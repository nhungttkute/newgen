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
import com.newgen.am.dto.DataObj;
import com.newgen.am.dto.InvestorAccountDTO;
import com.newgen.am.service.InvestorService;
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
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("/investors/account"));
        ResponseObj response = new ResponseObj();
        try {
            InvestorAccountDTO investorAcc = investorService.getInvestorAccount(refId);
            if (investorAcc != null) {
                response.setStatus(Constant.RESPONSE_OK);
                response.setData(new DataObj());
                response.getData().setInvestorAccount(investorAcc);
            } else {
                response.setStatus(Constant.RESPONSE_ERROR);
                response.setErrMsg(ErrorMessage.ERROR_OCCURRED);
            }
        } catch (Exception ex) {
            AMLogger.logError(className, methodName, refId, ex);
            response.setStatus(Constant.RESPONSE_ERROR);
            response.setErrMsg(ex.getMessage());
        }
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
}
