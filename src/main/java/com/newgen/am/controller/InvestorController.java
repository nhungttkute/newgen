/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.newgen.am.common.AMLogger;
import com.newgen.am.common.Constant;
import com.newgen.am.common.CustomMappingStrategy;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.common.Utility;
import com.newgen.am.dto.AccountStatusDTO;
import com.newgen.am.dto.AdminDataObj;
import com.newgen.am.dto.AdminResponseObj;
import com.newgen.am.dto.ApprovalChangeGroupDTO;
import com.newgen.am.dto.ApprovalRiskParametersDTO;
import com.newgen.am.dto.ApprovalUpdateInvestorDTO;
import com.newgen.am.dto.BasePagination;
import com.newgen.am.dto.CommoditiesDTO;
import com.newgen.am.dto.DataObj;
import com.newgen.am.dto.DefaultSettingDTO;
import com.newgen.am.dto.GeneralFeeDTO;
import com.newgen.am.dto.InvestorCSV;
import com.newgen.am.dto.InvestorCommodityFeeDTO;
import com.newgen.am.dto.InvestorDTO;
import com.newgen.am.dto.InvestorDetailDTO;
import com.newgen.am.dto.MarginInfoDTO;
import com.newgen.am.dto.MarginMultiplierDTO;
import com.newgen.am.dto.MarginRatioAlertDTO;
import com.newgen.am.dto.MarginTransCSV;
import com.newgen.am.dto.MarginTransactionDTO;
import com.newgen.am.dto.ResponseObj;
import com.newgen.am.dto.UpdateInvestorDTO;
import com.newgen.am.dto.UserCSV;
import com.newgen.am.dto.UserDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.InvestorMarginTransaction;
import com.newgen.am.service.InvestorService;
import com.newgen.am.validation.ValidationSequence;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

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
    public ResponseObj getAccountSummary(HttpServletRequest request) {
        String methodName = "getAccountSummary";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: /users/account");
        
        ResponseObj response = new ResponseObj();
        AccountStatusDTO investorAcc = investorService.getInvestorAccount(request, refId);
        if (investorAcc != null) {
            response.setStatus(Constant.RESPONSE_OK);
            response.setData(new DataObj());
            response.getData().setInvestorAccount(investorAcc);
        } else {
            response.setStatus(Constant.RESPONSE_ERROR);
            response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
        }
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
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
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }
    
    @PostMapping("/admin/investorInfo")
    public AdminResponseObj getInvestorInfo(HttpServletRequest request, @RequestBody InvestorDetailDTO investorDetailDto) {
        String methodName = "getInvestorInfo";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: /admin/investorInfo, INPUT:" + Utility.getGson().toJson(investorDetailDto));
        
        if (!Utility.isLocalRequest(request)) throw new CustomException(ErrorMessage.ACCESS_DENIED, HttpStatus.FORBIDDEN);
        
        AdminResponseObj response = new AdminResponseObj();
        
        InvestorDTO investorDto = investorService.getInvestorInfo(investorDetailDto.getInvestorCode(), refId);
        
		if (investorDto != null) {
			response.setStatus(Constant.RESPONSE_OK);
			response.setData(new AdminDataObj());
			response.getData().setInvestor(investorDto);
		} else {
			response.setStatus(Constant.RESPONSE_ERROR);
			response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
		}

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
    }
    
    @PostMapping("/admin/investorInfo/cqgAccount")
    public AdminResponseObj getInvestorInfoByCQGAccountId(HttpServletRequest request, @RequestBody InvestorDetailDTO investorDetailDto) {
        String methodName = "getInvestorInfoByCQGAccountId";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: /admin/getInvestorInfoByCQGAccountId, INPUT:" + Utility.getGson().toJson(investorDetailDto));
        
        if (!Utility.isLocalRequest(request)) throw new CustomException(ErrorMessage.ACCESS_DENIED, HttpStatus.FORBIDDEN);
        
        AdminResponseObj response = new AdminResponseObj();
        
        InvestorDTO investorDto = investorService.getInvestorInfoByCQGAccountId(investorDetailDto.getCqgAccountId(), refId);
        
		if (investorDto != null) {
			response.setStatus(Constant.RESPONSE_OK);
			response.setData(new AdminDataObj());
			response.getData().setInvestor(investorDto);
		} else {
			response.setStatus(Constant.RESPONSE_ERROR);
			response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
		}
		
		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
    }
    
    @GetMapping("/admin/investors")
	@PreAuthorize("hasAuthority('clientManagement.investorManagementinvestorList.view')")
	public AdminResponseObj listInvestors(HttpServletRequest request) {
		String methodName = "listInvestors";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/investors");
		AdminResponseObj response = new AdminResponseObj();
		
		try {
			BasePagination<InvestorDTO> pagination = investorService.list(request, refId);
			if (pagination != null && pagination.getData().size() > 0) {
				response.setStatus(Constant.RESPONSE_OK);
				response.setData(new AdminDataObj());
				response.getData().setInvestors(pagination.getData());
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
		
		AMLogger.logMessage(className, methodName, refId, "OUTPUT: OK");
		return response;
	}

	@GetMapping("/admin/investors/csv")
	@PreAuthorize("hasAuthority('clientManagement.investorManagementinvestorList.view')")
	public void downloadInvestorsCsv(HttpServletRequest request, HttpServletResponse response) {
		String methodName = "downloadInvestorsCsv";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/investors/csv");

		try {
			// set file name and content type
			String filename = Constant.CSV_INVESTORS;

			response.setContentType("text/csv");
			response.setCharacterEncoding("UTF-8");
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

			// create a csv writer
			CustomMappingStrategy<InvestorCSV> mappingStrategy = new CustomMappingStrategy<InvestorCSV>();
			mappingStrategy.setType(InvestorCSV.class);

			StatefulBeanToCsv<InvestorCSV> writer = new StatefulBeanToCsvBuilder<InvestorCSV>(response.getWriter())
					.withMappingStrategy(mappingStrategy).withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
					.withSeparator(CSVWriter.DEFAULT_SEPARATOR).withOrderedResults(false).build();

			// write all users to csv file
			writer.write(investorService.listCsv(request, refId));
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/admin/investors/excel")
	@PreAuthorize("hasAuthority('clientManagement.investorManagementinvestorList.view')")
	public ResponseEntity<Resource> downloadInvestorsExcel(HttpServletRequest request, HttpServletResponse response) {
		String methodName = "downloadInvestorsExcel";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/investors/excel");

		try {
			InputStreamResource file = new InputStreamResource(investorService.loadInvestorsExcel(request, refId));

			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + Constant.EXCEL_INVESTORS)
					.contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/admin/investors")
	@PreAuthorize("hasAuthority('clientManagement.investorManagementinvestorInfo.create')")
	public AdminResponseObj createInvestor(HttpServletRequest request, @Validated(ValidationSequence.class) @RequestBody InvestorDTO investorDto) {
		String methodName = "createInvestor";
		long refId = System.currentTimeMillis();
		
		// set null for image data
		InvestorDTO logRequest = (InvestorDTO) SerializationUtils.clone(investorDto);
		if (logRequest.getCompany() != null && logRequest.getCompany().getDelegate() != null) {
			logRequest.getCompany().getDelegate().setScannedBackIdCard(null);
			logRequest.getCompany().getDelegate().setScannedFrontIdCard(null);
			logRequest.getCompany().getDelegate().setScannedSignature(null);
		}
		
		if (logRequest.getIndividual() != null) {
			logRequest.getIndividual().setScannedBackIdCard(null);
			logRequest.getIndividual().setScannedFrontIdCard(null);
			logRequest.getIndividual().setScannedSignature(null);
		}
		
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/investors, INPUT:" + Utility.getGson().toJson(logRequest));

		investorService.createInvestorPA(request, investorDto, refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@PutMapping("/admin/investors/{investorCode}")
	@PreAuthorize("hasAuthority('clientManagement.investorManagement.investorInfo.update')")
	public AdminResponseObj updateInvestor(HttpServletRequest request, @PathVariable String investorCode,
			@Validated(ValidationSequence.class) @RequestBody ApprovalUpdateInvestorDTO investorDto) {
		String methodName = "updateInvestor";
		long refId = System.currentTimeMillis();
		
		// set null for image data
		ApprovalUpdateInvestorDTO logRequest = (ApprovalUpdateInvestorDTO) SerializationUtils.clone(investorDto);
		if (logRequest.getOldData().getCompany() != null && logRequest.getOldData().getCompany().getDelegate() != null) {
			logRequest.getOldData().getCompany().getDelegate().setScannedBackIdCard(null);
			logRequest.getOldData().getCompany().getDelegate().setScannedFrontIdCard(null);
			logRequest.getOldData().getCompany().getDelegate().setScannedSignature(null);
		}
		
		if (logRequest.getOldData().getIndividual() != null) {
			logRequest.getOldData().getIndividual().setScannedBackIdCard(null);
			logRequest.getOldData().getIndividual().setScannedFrontIdCard(null);
			logRequest.getOldData().getIndividual().setScannedSignature(null);
		}
		
		if (logRequest.getPendingData().getCompany() != null && logRequest.getPendingData().getCompany().getDelegate() != null) {
			logRequest.getPendingData().getCompany().getDelegate().setScannedBackIdCard(null);
			logRequest.getPendingData().getCompany().getDelegate().setScannedFrontIdCard(null);
			logRequest.getPendingData().getCompany().getDelegate().setScannedSignature(null);
		}
		
		if (logRequest.getPendingData().getIndividual() != null) {
			logRequest.getPendingData().getIndividual().setScannedBackIdCard(null);
			logRequest.getPendingData().getIndividual().setScannedFrontIdCard(null);
			logRequest.getPendingData().getIndividual().setScannedSignature(null);
		}
		
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [PUT]/admin/investors/" + investorCode + ", INPUT:" + Utility.getGson().toJson(logRequest));

		investorService.updateInvestorPA(request, investorCode, investorDto, refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@PutMapping("/admin/investors/{investorCode}/cqgInfo")
	@PreAuthorize("hasAuthority('clientManagement.investorManagement.cqgInfo.update')")
	public AdminResponseObj updateInvestorCQGInfo(HttpServletRequest request, @PathVariable String investorCode,
			@Validated(ValidationSequence.class) @RequestBody UpdateInvestorDTO investorDto) {
		String methodName = "updateInvestorCQGInfo";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [PUT]/admin/investors/" + investorCode + "/cqgInfo" + ", INPUT:" + Utility.getGson().toJson(investorDto));

		investorService.updateInvestorCQGInfo(request, investorCode, investorDto, refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}


	@GetMapping("/admin/investors/{investorCode}")
	@PreAuthorize("hasAuthority('clientManagement.investorManagementinvestorInfo.view')")
	public AdminResponseObj getInvestorDetail(@PathVariable String investorCode) {
		String methodName = "getInvestorDetail";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/investors/" + investorCode);
		AdminResponseObj response = new AdminResponseObj();

		InvestorDTO investorDto = investorService.getInvestorDetail(investorCode, refId);
		if (investorDto != null) {
			response.setStatus(Constant.RESPONSE_OK);
			response.setData(new AdminDataObj());
			response.getData().setInvestor(investorDto);
		} else {
			response.setStatus(Constant.RESPONSE_ERROR);
			response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
		}

		AdminResponseObj logResponse = (AdminResponseObj) SerializationUtils.clone(response);
		if (logResponse.getData().getInvestor() != null && logResponse.getData().getInvestor().getCompany() != null && logResponse.getData().getInvestor().getCompany().getDelegate() != null) {
			logResponse.getData().getInvestor().getCompany().getDelegate().setScannedBackIdCard("");
			logResponse.getData().getInvestor().getCompany().getDelegate().setScannedFrontIdCard("");
			logResponse.getData().getInvestor().getCompany().getDelegate().setScannedSignature("");
		}
		if (logResponse.getData().getInvestor() != null && logResponse.getData().getInvestor().getIndividual() != null) {
			logResponse.getData().getInvestor().getIndividual().setScannedBackIdCard("");
			logResponse.getData().getInvestor().getIndividual().setScannedFrontIdCard("");
			logResponse.getData().getInvestor().getIndividual().setScannedSignature("");
		}
		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(logResponse));
		return response;
	}
	
	@GetMapping("/admin/investors/{investorCode}/users")
	@PreAuthorize("hasAuthority('clientManagement.investorManagement.investorUserInfo.view')")
	public AdminResponseObj listInvestorUsers(HttpServletRequest request, @PathVariable String investorCode) {
		String methodName = "listInvestorUsers";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, String.format("REQUEST_API: [GET]/admin/investors/%s/users", investorCode));
		AdminResponseObj response = new AdminResponseObj();
		
		try {
			BasePagination<UserDTO> pagination = investorService.listInvestorUsers(request, investorCode, refId);
			if (pagination != null && pagination.getData().size() > 0) {
				response.setStatus(Constant.RESPONSE_OK);
				response.setData(new AdminDataObj());
				response.getData().setInvestorUsers(pagination.getData());
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
	
	@GetMapping("/admin/investors/{investorCode}/users/csv")
	@PreAuthorize("hasAuthority('clientManagement.investorManagement.investorUserInfo.view')")
	public void downloadInvestorUsersCsv(HttpServletRequest request, HttpServletResponse response, @PathVariable String investorCode) {
		String methodName = "downloadInvestorUsersCsv";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, String.format("REQUEST_API: [GET]/admin/investors/%s/users/csv", investorCode));

		try {
			// set file name and content type
			String filename = Constant.CSV_INVESTOR_USERS;

			response.setContentType("text/csv");
			response.setCharacterEncoding("UTF-8");
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

			// create a csv writer
			CustomMappingStrategy<UserCSV> mappingStrategy = new CustomMappingStrategy<UserCSV>();
			mappingStrategy.setType(UserCSV.class);

			StatefulBeanToCsv<UserCSV> writer = new StatefulBeanToCsvBuilder<UserCSV>(response.getWriter())
					.withMappingStrategy(mappingStrategy).withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
					.withSeparator(CSVWriter.DEFAULT_SEPARATOR).withOrderedResults(false).build();

			// write all users to csv file
			writer.write(investorService.listInvestorUsersCsv(request, investorCode, refId));
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/admin/investors/{investorCode}/users/{username}")
	@PreAuthorize("hasAuthority('clientManagement.investorManagement.investorUserInfo.view')")
	public AdminResponseObj getInvestorUser(@PathVariable String investorCode, @PathVariable String username) {
		String methodName = "getInvestorUser";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				String.format("REQUEST_API: [GET]/admin/investors/%s/users/%s", investorCode, username));
		AdminResponseObj response = new AdminResponseObj();

		UserDTO userDto = investorService.getInvestorUser(investorCode, username, refId);
		if (userDto != null) {
			response.setStatus(Constant.RESPONSE_OK);
			response.setData(new AdminDataObj());
			response.getData().setInvestorUser(userDto);
		} else {
			response.setStatus(Constant.RESPONSE_ERROR);
			response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
		}

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@PostMapping("/admin/investors/{investorCode}/users")
	@PreAuthorize("hasAuthority('clientManagement.investorManagement.investorUser.create')")
	public AdminResponseObj createInvestorUser(HttpServletRequest request, @PathVariable String investorCode,
			@Validated(ValidationSequence.class) @RequestBody UserDTO userDto) {
		String methodName = "createInvestorUser";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, String.format("REQUEST_API: [POST]/admin/investors/%s/users", investorCode) + ", INPUT:" + Utility.getGson().toJson(userDto));

		investorService.createInvestorUserPA(request, investorCode, userDto, refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@PostMapping("/admin/investors/{memberCode}/{investorCode}/defaultSetting")
    @PreAuthorize("hasAuthority('clientManagement.investorManagement.investorDefaultPositionLimit.create')")
    public AdminResponseObj createInvestorDefaultSetting(HttpServletRequest request, @PathVariable String memberCode, @PathVariable String investorCode, @Validated(ValidationSequence.class) @RequestBody DefaultSettingDTO investorDto) {
        String methodName = "createInvestorDefaultSetting";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, String.format("REQUEST_API: [POST]/admin/investors/%s/%s/defaultSetting", memberCode, investorCode) + ", INPUT:" + Utility.getGson().toJson(investorDto));
        
        investorService.createDefaultSetting(request, memberCode, investorCode, investorDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }
	
	@PostMapping("/admin/investors/{memberCode}/{investorCode}/commoditiesSetting")
    @PreAuthorize("hasAuthority('clientManagement.investorManagement.investorCommoditiesAssign.create') or hasAuthority('clientManagement.investorManagement.investorCommoditiesFee.create') or hasAuthority('clientManagement.investorManagement.investorOrderLimit.create')")
    public AdminResponseObj createInvestorCommoditiesSetting(HttpServletRequest request, @PathVariable String memberCode, @PathVariable String investorCode, @Validated(ValidationSequence.class) @RequestBody CommoditiesDTO investorDto) {
        String methodName = "createInvestorCommoditiesSetting";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, String.format("REQUEST_API: [POST]/admin/investors/%s/%s/commoditiesSetting", memberCode, investorCode) + ", INPUT:" + Utility.getGson().toJson(investorDto));
        
        investorService.createInvestorCommodities(request, memberCode, investorCode, investorDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }
	
	@PutMapping("/admin/investors/{investorCode}/newPositionOrderLock")
    @PreAuthorize("hasAuthority('clientManagement.investorManagement.investorRiskManagement.newOrderLockConfig.create')")
    public AdminResponseObj setInvestorNewPositionOrderLock(HttpServletRequest request, @PathVariable String investorCode, @Validated(ValidationSequence.class) @RequestBody ApprovalRiskParametersDTO investorDto) {
        String methodName = "setInvestorNewPositionOrderLock";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, String.format("REQUEST_API: [PUT]/admin/members/%s/newPositionOrderLock", investorCode) + ", INPUT:" + Utility.getGson().toJson(investorDto));
        
        if (Utility.isNull(investorDto) || Utility.isNull(investorDto.getPendingData()) || Utility.isNull(investorDto.getPendingData().getRiskParameters()) || Utility.isNull(investorDto.getPendingData().getRiskParameters().getNewPositionOrderLock())) {
        	throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
        }
        investorService.setInvestorNewPositionOrderLockPA(request, investorCode, investorDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }
	
	@PutMapping("/admin/investors/{investorCode}/orderLock")
    @PreAuthorize("hasAuthority('clientManagement.investorManagement.investorRiskManagement.orderLockConfig.create')")
    public AdminResponseObj setInvestorOrderLock(HttpServletRequest request, @PathVariable String investorCode, @Validated(ValidationSequence.class) @RequestBody ApprovalRiskParametersDTO investorDto) {
        String methodName = "setInvestorOrderLock";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, String.format("REQUEST_API: [PUT]/admin/investors/%s/orderLock", investorCode) + ", INPUT:" + Utility.getGson().toJson(investorDto));
        
        if (Utility.isNull(investorDto) || Utility.isNull(investorDto.getPendingData()) || Utility.isNull(investorDto.getPendingData().getRiskParameters()) || Utility.isNull(investorDto.getPendingData().getRiskParameters().getOrderLock())) {
        	throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
        }
        investorService.setInvestorOrderLockPA(request, investorCode, investorDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }
	
	@PutMapping("/admin/investors/{investorCode}/marginMultiplier")
    @PreAuthorize("hasAuthority('clientManagement.investorManagement.investorMarginMultiplier.create')")
    public AdminResponseObj setMarginMultiplier(HttpServletRequest request, @PathVariable String investorCode, @Validated(ValidationSequence.class) @RequestBody MarginMultiplierDTO investorDto) {
        String methodName = "setMarginMultiplier";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, String.format("REQUEST_API: [PUT]/admin/investors/%s/marginMultiplier", investorCode) + ", INPUT:" + Utility.getGson().toJson(investorDto));
        
        investorService.setMarginMultiplier(request, investorCode, investorDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }
	
	@PutMapping("/admin/investors/{investorCode}/marginRatio")
    @PreAuthorize("hasAuthority('clientManagement.investorManagement.investorMarginRatio.create')")
    public AdminResponseObj setMarginRatio(HttpServletRequest request, @PathVariable String investorCode, @Validated(ValidationSequence.class) @RequestBody MarginRatioAlertDTO investorDto) {
        String methodName = "setMarginRatio";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, String.format("REQUEST_API: [PUT]/admin/investors/%s/marginRatio", investorCode) + ", INPUT:" + Utility.getGson().toJson(investorDto));
        
        investorService.setMarginRatioAlert(request, investorCode, investorDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }
	
	@PostMapping("/admin/investors/{investorCode}/setGeneralFee")
    @PreAuthorize("hasAuthority('clientManagement.investorManagement.investorGeneralFee.create')")
    public AdminResponseObj setGeneralFee(HttpServletRequest request, @PathVariable String investorCode, @Validated(ValidationSequence.class) @RequestBody GeneralFeeDTO investorDto) {
        String methodName = "setGeneralFee";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, String.format("REQUEST_API: [PUT]/admin/investors/%s/setGeneralFee", investorCode) + ", INPUT:" + Utility.getGson().toJson(investorDto));
        
        investorService.setGeneralFee(request, investorCode, investorDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }
	
	@PutMapping("/admin/investors/{investorCode}/updateGeneralFee")
    @PreAuthorize("hasAuthority('clientManagement.investorManagement.investorGeneralFee.create')")
    public AdminResponseObj updateGeneralFee(HttpServletRequest request, @PathVariable String investorCode, @Validated(ValidationSequence.class) @RequestBody GeneralFeeDTO investorDto) {
        String methodName = "updateGeneralFee";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, String.format("REQUEST_API: [PUT]/admin/investors/%s/updateGeneralFee", investorCode) + ", INPUT:" + Utility.getGson().toJson(investorDto));
        
        investorService.updateGeneralFee(request, investorCode, investorDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }
	
	@PutMapping("/admin/investors/{investorCode}/changeBroker")
    @PreAuthorize("hasAuthority('clientManagement.investorManagement.investorNewBroker.transfer')")
    public AdminResponseObj changeBroker(HttpServletRequest request, @PathVariable String investorCode, @Validated(ValidationSequence.class) @RequestBody ApprovalChangeGroupDTO groupDto) {
        String methodName = "changeBroker";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, String.format("REQUEST_API: [PUT]/admin/investors/%s/changeBroker", investorCode) + ", INPUT:" + Utility.getGson().toJson(groupDto));
        
        investorService.changeBrokerPA(request, investorCode, groupDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }
	
	@PutMapping("/admin/investors/{investorCode}/changeCollaborator")
    @PreAuthorize("hasAuthority('clientManagement.investorManagement.investorNewCollaborator.transfer')")
    public AdminResponseObj changeCollaborator(HttpServletRequest request, @PathVariable String investorCode, @Validated(ValidationSequence.class) @RequestBody ApprovalChangeGroupDTO groupDto) {
        String methodName = "changeCollaborator";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, String.format("REQUEST_API: [PUT]/admin/investors/%s/changeCollaborator", investorCode) + ", INPUT:" + Utility.getGson().toJson(groupDto));
        
        investorService.changeCollaboratorPA(request, investorCode, groupDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }
	
	@PutMapping("/admin/investors/{investorCode}/marginInfo")
    @PreAuthorize("hasAuthority('clientManagement.investorManagement.investorAccount.activate')")
    public AdminResponseObj updateMarginInfo(HttpServletRequest request, @PathVariable String investorCode, @Validated(ValidationSequence.class) @RequestBody MarginInfoDTO marginInfoDto) {
        String methodName = "updateMarginInfo";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, String.format("REQUEST_API: [PUT]/admin/investors/%s/marginInfo", investorCode) + ", INPUT:" + Utility.getGson().toJson(marginInfoDto));
        
        investorService.updateMarginInfo(request, investorCode, marginInfoDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }
	
	@GetMapping("/admin/investors/{investorCode}/withdrawableAmount")
	@PreAuthorize("hasAuthority('clientManagement.marginDepositWithdrawalManagement.investorMarginDeposit.create') or hasAuthority('clientManagement.marginDepositWithdrawalManagement.investorMarginWithdrawal.create')")
	public AdminResponseObj getWithdrawableAmount(@PathVariable String investorCode) {
		String methodName = "getWithdrawableAmount";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				String.format("REQUEST_API: [GET]/admin/investors/%s/withdrawableAmount", investorCode));

		long amount = investorService.getWithdrawableAmount(refId);
		
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
		response.setData(new AdminDataObj());
		response.getData().setWithdrawableAmount(amount);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@PutMapping("/admin/investors/marginDeposit")
    @PreAuthorize("hasAuthority('clientManagement.marginDepositWithdrawalManagement.investorMarginDeposit.create')")
    public AdminResponseObj depositMargin(HttpServletRequest request, @Validated(ValidationSequence.class) @RequestBody MarginTransactionDTO marginTransDto) {
        String methodName = "depositMargin";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [PUT]/am/admin/investors/moneyDeposit, INPUT:" + Utility.getGson().toJson(marginTransDto));
        
        investorService.depositMarginPA(request, marginTransDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }
	
	@PutMapping("/admin/investors/marginWithdrawal")
    @PreAuthorize("hasAuthority('clientManagement.marginDepositWithdrawalManagement.investorMarginWithdrawal.create')")
    public AdminResponseObj withdrawMargin(HttpServletRequest request, @Validated(ValidationSequence.class) @RequestBody MarginTransactionDTO marginTransDto) {
        String methodName = "withdrawMargin";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [PUT]/am/admin/investors/moneyWithdrawal, INPUT:" + Utility.getGson().toJson(marginTransDto));
        
        investorService.withdrawMarginPA(request, marginTransDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }
	
	@GetMapping("/admin/investors/marginTransactionHistory")
	@PreAuthorize("hasAuthority('clientManagement.marginMoneyTransHistory')")
	public AdminResponseObj listInvestorMarginTransactions(HttpServletRequest request) {
		String methodName = "listInvestorMarginTransactions";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/investors/marginTransactionHistory");
		AdminResponseObj response = new AdminResponseObj();
		
		try {
			BasePagination<InvestorMarginTransaction> pagination = investorService.listMarginTransactions(request, refId);
			if (pagination != null && pagination.getData().size() > 0) {
				response.setStatus(Constant.RESPONSE_OK);
				response.setData(new AdminDataObj());
				response.getData().setInvestorMarginTransactions(pagination.getData());
				response.setPagination(Utility.getPagination(request, pagination.getCount()));
				response.setFilterList(Arrays.asList(Constant.MARGIN_TRANS_TYPE_DEPOSIT, Constant.MARGIN_TRANS_TYPE_WITHDRAW));
			} else {
				response.setStatus(Constant.RESPONSE_ERROR);
				response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
			}
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		AMLogger.logMessage(className, methodName, refId, "OUTPUT: OK");
		return response;
	}
	
	@GetMapping("/admin/investors/marginTransactionHistory/csv")
	@PreAuthorize("hasAuthority('clientManagement.marginMoneyTransHistory')")
	public void downloadMarginTransCsv(HttpServletRequest request, HttpServletResponse response) {
		String methodName = "downloadMarginTransCsv";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/investors/marginTransactionHistory/csv");

		try {
			// set file name and content type
			String filename = Constant.CSV_INVESTOR_MARGIN_TRANS;

			response.setContentType("text/csv");
			response.setCharacterEncoding("UTF-8");
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

			// create a csv writer
			CustomMappingStrategy<MarginTransCSV> mappingStrategy = new CustomMappingStrategy<MarginTransCSV>();
			mappingStrategy.setType(MarginTransCSV.class);

			StatefulBeanToCsv<MarginTransCSV> writer = new StatefulBeanToCsvBuilder<MarginTransCSV>(response.getWriter())
					.withMappingStrategy(mappingStrategy).withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
					.withSeparator(CSVWriter.DEFAULT_SEPARATOR).withOrderedResults(false).build();

			// write all users to csv file
			writer.write(investorService.listMarginTransactionsCsv(request, refId));
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/admin/investors/marginTransactionHistory/excel")
	@PreAuthorize("hasAuthority('clientManagement.marginMoneyTransHistory')")
	public ResponseEntity<Resource> downloadMarginTransExcel(HttpServletRequest request, HttpServletResponse response) {
		String methodName = "downloadMarginTransExcel";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/investors/marginTransactionHistory/excel");

		try {
			InputStreamResource file = new InputStreamResource(investorService.loadMarginTransactionsExcel(request, refId));

			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + Constant.EXCEL_INVESTOR_MARGIN_TRANS)
					.contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/admin/investors/refundMarginDeposit/{approvalId}")
    @PreAuthorize("hasAuthority('clientManagement.marginDepositWithdrawalManagement.investorMarginDeposit.refund')")
    public AdminResponseObj refundDepositMargin(HttpServletRequest request, @PathVariable String approvalId) {
        String methodName = "refundDepositMargin";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/am/admin/investors/refundMarginDeposit/" + approvalId);
        
        investorService.refundDepositMarginPA(request, approvalId, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }
	
	@PostMapping("/admin/investors/commodityFee")
    public AdminResponseObj getCommodityFee(HttpServletRequest request, @RequestBody InvestorCommodityFeeDTO invCommdityFeeDto) {
        String methodName = "getCommodityFee";
        if (!Utility.isLocalRequest(request)) throw new CustomException(ErrorMessage.ACCESS_DENIED, HttpStatus.FORBIDDEN);
        
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/am/admin/investors/commodityFee, INPUT: " + Utility.getGson().toJson(invCommdityFeeDto));
        
        InvestorCommodityFeeDTO invCommFee = investorService.getInvestorCommodityFee(invCommdityFeeDto.getInvestorCode(), invCommdityFeeDto.getCommodityCode(), refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setInvCommodityFee(invCommFee);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }
	
	@GetMapping("/admin/investorInfoList")
    public AdminResponseObj getInvestorInfoList(HttpServletRequest request) {
        String methodName = "getInvestorInfoList";
        if (!Utility.isLocalRequest(request)) throw new CustomException(ErrorMessage.ACCESS_DENIED, HttpStatus.FORBIDDEN);
        
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: /admin/investorInfoList");
        
        List<InvestorDTO> investorList = investorService.getInvestorInfoList(refId);
        
        AdminResponseObj response = new AdminResponseObj();
        if (investorList != null && investorList.size() > 0) {
            response.setStatus(Constant.RESPONSE_OK);
            response.setData(new AdminDataObj());
            response.getData().setInvestors(investorList);
        } else {
            response.setStatus(Constant.RESPONSE_ERROR);
            response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
        }
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT: OK");
        return response;
    }
}
