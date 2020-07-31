package com.newgen.am.controller;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
import com.newgen.am.dto.ApprovalUpdateBrokerDTO;
import com.newgen.am.dto.BasePagination;
import com.newgen.am.dto.BrokerCSV;
import com.newgen.am.dto.BrokerCommoditiesDTO;
import com.newgen.am.dto.BrokerDTO;
import com.newgen.am.dto.DefaultCommodityFeeDTO;
import com.newgen.am.dto.FunctionsDTO;
import com.newgen.am.dto.UserDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.service.BrokerService;
import com.newgen.am.validation.ValidationSequence;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

@RestController
public class BrokerController {
	private String className = "BrokerController";
	
	@Autowired
	private BrokerService brokerService;
	
	@GetMapping("/admin/brokers")
	@PreAuthorize("hasAuthority('clientManagement.brokerManagement.brokerList.view')")
	public AdminResponseObj listBrokers(HttpServletRequest request) {
		String methodName = "listBrokers";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/brokers");
		AdminResponseObj response = new AdminResponseObj();
		
		try {
			BasePagination<BrokerDTO> pagination = brokerService.list(request, refId);
			if (pagination != null && pagination.getData().size() > 0) {
				response.setStatus(Constant.RESPONSE_OK);
				response.setData(new AdminDataObj());
				response.getData().setBrokers(pagination.getData());
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
		
		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}

	@GetMapping("/admin/brokers/csv")
	@PreAuthorize("hasAuthority('clientManagement.brokerManagement.brokerList.view')")
	public void downloadBrokersCsv(HttpServletRequest request, HttpServletResponse response) {
		String methodName = "downloadBrokersCsv";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/brokers/csv");

		try {
			// set file name and content type
			String filename = Constant.CSV_BROKERS;

			response.setContentType("text/csv");
			response.setCharacterEncoding("UTF-8");
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

			// create a csv writer
			CustomMappingStrategy<BrokerCSV> mappingStrategy = new CustomMappingStrategy<BrokerCSV>();
			mappingStrategy.setType(BrokerCSV.class);

			StatefulBeanToCsv<BrokerCSV> writer = new StatefulBeanToCsvBuilder<BrokerCSV>(response.getWriter())
					.withMappingStrategy(mappingStrategy).withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
					.withSeparator(CSVWriter.DEFAULT_SEPARATOR).withOrderedResults(false).build();

			// write all users to csv file
			writer.write(brokerService.listCsv(request, refId));
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/admin/brokers")
	@PreAuthorize("hasAuthority('clientManagement.brokerManagement.brokerInfo.create')")
	public AdminResponseObj createBroker(HttpServletRequest request, @Validated(ValidationSequence.class) @RequestBody BrokerDTO brokerDto) {
		String methodName = "createBroker";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/brokers");
		AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(brokerDto));

		brokerService.createBrokerPA(request, brokerDto, refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
	
	@PutMapping("/admin/brokers/{brokerCode}")
	@PreAuthorize("hasAuthority('clientManagement.brokerManagement.brokerInfo.update')")
	public AdminResponseObj updateBroker(HttpServletRequest request, @PathVariable String brokerCode,
			@Validated(ValidationSequence.class) @RequestBody ApprovalUpdateBrokerDTO brokerDto) {
		String methodName = "updateBroker";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [PUT]/admin/brokers/" + brokerCode);
		AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(brokerDto));

		brokerService.updateBrokerPA(request, brokerCode, brokerDto, refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}

	@GetMapping("/admin/brokers/{brokerCode}")
	@PreAuthorize("hasAuthority('clientManagement.brokerManagement.brokerInfo.view')")
	public AdminResponseObj getBrokerDetail(@PathVariable String brokerCode) {
		String methodName = "getBrokerDetail";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/brokers/" + brokerCode);
		AdminResponseObj response = new AdminResponseObj();

		BrokerDTO brokerDto = brokerService.getBrokerDetail(brokerCode, refId);
		if (brokerDto != null) {
			response.setStatus(Constant.RESPONSE_OK);
			response.setData(new AdminDataObj());
			response.getData().setBroker(brokerDto);
		} else {
			response.setStatus(Constant.RESPONSE_ERROR);
			response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
		}

		AdminResponseObj logResponse = (AdminResponseObj) SerializationUtils.clone(response);
		if (logResponse.getData().getBroker() != null && logResponse.getData().getBroker().getCompany() != null && logResponse.getData().getBroker().getCompany().getDelegate() != null) {
			logResponse.getData().getBroker().getCompany().getDelegate().setScannedBackIdCard("");
			logResponse.getData().getBroker().getCompany().getDelegate().setScannedFrontIdCard("");
			logResponse.getData().getBroker().getCompany().getDelegate().setScannedSignature("");
		}
		if (logResponse.getData().getBroker() != null && logResponse.getData().getBroker().getIndividual() != null) {
			logResponse.getData().getBroker().getIndividual().setScannedBackIdCard("");
			logResponse.getData().getBroker().getIndividual().setScannedFrontIdCard("");
			logResponse.getData().getBroker().getIndividual().setScannedSignature("");
		}
		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(logResponse));
		return response;
	}
	
	@GetMapping("/admin/brokers/{brokerCode}/user")
	@PreAuthorize("hasAuthority('clientManagement.brokerManagement.brokerUserInfo.view')")
	public AdminResponseObj getBrokerUser(@PathVariable String brokerCode) {
		String methodName = "getBrokerUser";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/brokers/" + brokerCode + "/user");
		AdminResponseObj response = new AdminResponseObj();

		UserDTO brokerUserDto = brokerService.getBrokerUserDetail(brokerCode, refId);
		if (brokerUserDto != null) {
			response.setStatus(Constant.RESPONSE_OK);
			response.setData(new AdminDataObj());
			response.getData().setBrokerUser(brokerUserDto);
		} else {
			response.setStatus(Constant.RESPONSE_ERROR);
			response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
		}

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
	
	@PostMapping("/admin/brokers/{brokerCode}/functions")
    @PreAuthorize("hasAuthority('approval.clientManagement.brokerManagement.brokerFunctionsAssign.create')")
    public AdminResponseObj createBrokerFunctions(HttpServletRequest request, @PathVariable String brokerCode, @Validated(ValidationSequence.class) @RequestBody ApprovalFunctionsDTO brokerDto) {
        String methodName = "createBrokerFunctions";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[POST]/admin/brokers/%s/functions", brokerCode));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(brokerDto));
        
        brokerService.createBrokerFunctionsPA(request, brokerCode, brokerDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
	
	@PostMapping("/admin/brokers/{brokerCode}/defaultSetting")
    @PreAuthorize("hasAuthority('clientManagement.brokerManagement.brokerCommoditiesFeeConfig.create')")
    public AdminResponseObj createBrokerDefaultSetting(HttpServletRequest request, @PathVariable String brokerCode, @Validated(ValidationSequence.class) @RequestBody DefaultCommodityFeeDTO brokerDto) {
        String methodName = "createBrokerDefaultSetting";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[POST]/admin/brokers/%s/defaultSetting", brokerCode));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(brokerDto));
        
        brokerService.createDefaultSetting(request, brokerCode, brokerDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
	
	@PostMapping("/admin/brokers/{brokerCode}/commoditiesSetting")
    @PreAuthorize("hasAuthority('clientManagement.brokerManagement.brokerCommoditiesAssign.create') or hasAuthority('clientManagement.brokerManagement.brokerCommoditiesFeeConfig.create')")
    public AdminResponseObj createBrokerCommoditiesSetting(HttpServletRequest request, @PathVariable String brokerCode, @Validated(ValidationSequence.class) @RequestBody BrokerCommoditiesDTO brokerDto) {
        String methodName = "createBrokerCommoditiesSetting";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[POST]/admin/brokers/%s/commoditiesSetting", brokerCode));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(brokerDto));
        
        brokerService.createBrokerCommodities(request, brokerCode, brokerDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
	
	@GetMapping("/admin/brokers/{brokerCode}/commodities")
//	@PreAuthorize("hasAuthority('clientManagement.brokerManagement.brokerCommoditiesAssign.create')")
	public AdminResponseObj getBrokerCommodities(@PathVariable String brokerCode) {
		String methodName = "getBrokerCommodities";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				String.format("REQUEST_API: [GET]/admin/brokers/%s/commodities", brokerCode));
		
		AdminResponseObj response = new AdminResponseObj();
		BrokerCommoditiesDTO brokerDto = brokerService.getBrokerCommodities(brokerCode, refId);
		if (brokerDto != null) {
			response.setStatus(Constant.RESPONSE_OK);
			response.setData(new AdminDataObj());
			response.getData().setBrokerCommodities(brokerDto);
		} else {
			response.setStatus(Constant.RESPONSE_ERROR);
			response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
		}

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
}
