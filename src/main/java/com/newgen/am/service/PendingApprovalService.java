/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.service;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.newgen.am.common.AMLogger;
import com.newgen.am.common.Constant;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.Utility;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.InvestorMarginTransApproval;
import com.newgen.am.model.PendingApproval;
import com.newgen.am.repository.InvestorMarginTransApprovalRepository;
import com.newgen.am.repository.PendingApprovalRepository;

/**
 *
 * @author nhungtt
 */
@Service
public class PendingApprovalService {
	private String className = "PendingApprovalService";
	
	@Autowired
	private ApplicationContext applicationContext;
	
    @Autowired
    private PendingApprovalRepository pendingApprovalRepo;
    
    @Autowired
    private InvestorMarginTransApprovalRepository invMarginTransApprovalRepo;
    
    public void approve(HttpServletRequest request, String approvalId, long refId) {
    	String methodName = "approve";
    	try {
    		PendingApproval pendingApproval = pendingApprovalRepo.findById(approvalId).get();
    		
    		if (Constant.STATUS_PENDING.equals(pendingApproval.getStatus())) {
    			// check authorization
        		if (AuthorityUtils.authorityListToSet(SecurityContextHolder.getContext().getAuthentication().getAuthorities()).contains(pendingApproval.getFunctionCode())) {
        			invokeMethod (request, pendingApproval, refId);
        			
        			//update pending approval status
        			pendingApproval.setStatus(Constant.APPROVAL_STATUS_APPROVED);
        			pendingApproval.setApprovalUser(Utility.getCurrentUsername());
        			pendingApproval.setApprovalDate(System.currentTimeMillis());
        			pendingApprovalRepo.save(pendingApproval);
        	    } else {
        	    	throw new CustomException(ErrorMessage.ACCESS_DENIED, HttpStatus.FORBIDDEN);
        	    }
    		}
    	} catch (CustomException e) {
    		throw e;
    	} catch (Exception e) {
    		AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    public void approveMarginTrans(HttpServletRequest request, String approvalId, long refId) {
    	String methodName = "approveMarginTrans";
    	try {
    		InvestorMarginTransApproval marginTransApproval = invMarginTransApprovalRepo.findById(approvalId).get();
    		
    		if (Constant.STATUS_PENDING.equals(marginTransApproval.getStatus())) {
    			// check authorization
        		if (AuthorityUtils.authorityListToSet(SecurityContextHolder.getContext().getAuthentication().getAuthorities()).contains(marginTransApproval.getFunctionCode())) {
        			invokeMethod2(request, marginTransApproval, refId);
        			
        			//update pending approval status
        			marginTransApproval.setStatus(Constant.APPROVAL_STATUS_APPROVED);
        			marginTransApproval.setApprovalUser(Utility.getCurrentUsername());
        			marginTransApproval.setApprovalDate(System.currentTimeMillis());
        			invMarginTransApprovalRepo.save(marginTransApproval);
        	    } else {
        	    	throw new CustomException(ErrorMessage.ACCESS_DENIED, HttpStatus.FORBIDDEN);
        	    }
    		}
    	} catch (CustomException e) {
    		throw e;
    	} catch (Exception e) {
    		AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    private void invokeMethod(HttpServletRequest request, PendingApproval pendingApproval, long refId) throws Exception {
    	String classMethodName = pendingApproval.getPendingData().getServiceFunctionName();
    	if (Utility.isNull(classMethodName)) {
    		throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    	
    	String className = Utility.getClassName(classMethodName);
    	String methodName = Utility.getMethodName(classMethodName);
    	String serviceName = Utility.getServiceName(className);
    	
    	if (Utility.isNull(className) || Utility.isNull(methodName)) {
    		throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    	
    	Class<?> c = Class.forName(className);
    	Object obj = applicationContext.getBean(serviceName);
    	
    	Class<?>[] parameterTypes = new Class[3];
    	parameterTypes[0] = HttpServletRequest.class;
    	parameterTypes[1] = PendingApproval.class;
    	parameterTypes[2] = long.class;
    	
    	Method method = c.getMethod(methodName, parameterTypes);
    	method.invoke(obj, request, pendingApproval, refId);
    }
    
    private void invokeMethod2(HttpServletRequest request, InvestorMarginTransApproval pendingApproval, long refId) throws Exception {
    	String classMethodName = pendingApproval.getPendingData().getServiceFunctionName();
    	if (Utility.isNull(classMethodName)) {
    		throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    	
    	String className = Utility.getClassName(classMethodName);
    	String methodName = Utility.getMethodName(classMethodName);
    	String serviceName = Utility.getServiceName(className);
    	
    	if (Utility.isNull(className) || Utility.isNull(methodName)) {
    		throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    	
    	Class<?> c = Class.forName(className);
    	Object obj = applicationContext.getBean(serviceName);
    	
    	Class<?>[] parameterTypes = new Class[3];
    	parameterTypes[0] = HttpServletRequest.class;
    	parameterTypes[1] = InvestorMarginTransApproval.class;
    	parameterTypes[2] = long.class;
    	
    	Method method = c.getMethod(methodName, parameterTypes);
    	method.invoke(obj, request, pendingApproval, refId);
    }
}
