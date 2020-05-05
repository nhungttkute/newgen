package com.newgen.am.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.newgen.am.common.AMLogger;
import com.newgen.am.common.Constant;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.dto.AdminDataObj;
import com.newgen.am.dto.AdminResponseObj;
import com.newgen.am.dto.SystemFunctionDTO;
import com.newgen.am.model.SystemFunction;
import com.newgen.am.service.SystemFunctionService;

@RestController
public class SystemFunctionController {
	private String className = "SystemFunctionController";
	
	@Autowired
	private SystemFunctionService sysFunctionService;
	
	@GetMapping("/admin/systemFunctions")
    public AdminResponseObj listSystemFunctions(HttpServletRequest request) {
        String methodName = "listSystemFunctions";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/systemFunctions");
        
        AdminResponseObj response = new AdminResponseObj();
        List<SystemFunctionDTO> systemFunctions = sysFunctionService.list();
        if (systemFunctions != null && systemFunctions.size() > 0) {
            response.setStatus(Constant.RESPONSE_OK);
            response.setData(new AdminDataObj());
            response.getData().setSystemFunctions(systemFunctions);
        } else {
            response.setStatus(Constant.RESPONSE_ERROR);
            response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
        }
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
}
