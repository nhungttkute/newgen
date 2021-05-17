package com.newgen.am.controller;

import java.util.Arrays;

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
import com.newgen.am.dto.AdminDataObj;
import com.newgen.am.dto.AdminResponseObj;
import com.newgen.am.dto.ApprovalFunctionsDTO;
import com.newgen.am.dto.ApprovalUpdateCollaboratorDTO;
import com.newgen.am.dto.BasePagination;
import com.newgen.am.dto.CollaboratorCSV;
import com.newgen.am.dto.CollaboratorDTO;
import com.newgen.am.dto.UserDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.service.CollaboratorService;
import com.newgen.am.validation.ValidationSequence;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

@RestController
public class CollaboratorController {
private String className = "CollaboratorController";
	
	@Autowired
	private CollaboratorService collaboratorService;
	
	@GetMapping("/admin/collaborators")
	@PreAuthorize("hasAuthority('clientManagement.brokerCollaboratorManagement.collaboratorList.view')")
	public AdminResponseObj listBrokers(HttpServletRequest request) {
		String methodName = "listBrokers";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/collaborators");
		AdminResponseObj response = new AdminResponseObj();
		
		try {
			BasePagination<CollaboratorDTO> pagination = collaboratorService.list(request, refId);
			if (pagination != null && pagination.getData().size() > 0) {
				response.setStatus(Constant.RESPONSE_OK);
				response.setData(new AdminDataObj());
				response.getData().setCollaborators(pagination.getData());
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

	@GetMapping("/admin/collaborators/csv")
	@PreAuthorize("hasAuthority('clientManagement.brokerCollaboratorManagement.collaboratorList.view')")
	public void downloadCollaboratorsCsv(HttpServletRequest request, HttpServletResponse response) {
		String methodName = "downloadCollaboratorsCsv";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/collaborators/csv");

		try {
			// set file name and content type
			String filename = Constant.CSV_COLLABORATORS;

			response.setContentType("text/csv");
			response.setCharacterEncoding("UTF-8");
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

			// create a csv writer
			CustomMappingStrategy<CollaboratorCSV> mappingStrategy = new CustomMappingStrategy<CollaboratorCSV>();
			mappingStrategy.setType(CollaboratorCSV.class);

			StatefulBeanToCsv<CollaboratorCSV> writer = new StatefulBeanToCsvBuilder<CollaboratorCSV>(response.getWriter())
					.withMappingStrategy(mappingStrategy).withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
					.withSeparator(CSVWriter.DEFAULT_SEPARATOR).withOrderedResults(false).build();

			// write all users to csv file
			writer.write(collaboratorService.listCsv(request, refId));
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/admin/collaborators/excel")
	@PreAuthorize("hasAuthority('clientManagement.brokerCollaboratorManagement.collaboratorList.view')")
	public ResponseEntity<Resource> downloadCollaboratorsExcel(HttpServletRequest request, HttpServletResponse response) {
		String methodName = "downloadCollaboratorsCsv";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/collaborators/excel");

		try {
			InputStreamResource file = new InputStreamResource(collaboratorService.loadCollaboratorsExcel(request, refId));

			return ResponseEntity.ok().header("Access-Control-Expose-Headers", "Content-Disposition").header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + Constant.EXCEL_COLLABORATORS)
					.contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/admin/collaborators")
	@PreAuthorize("hasAuthority('clientManagement.brokerCollaboratorManagement.collaboratorInfo.create')")
	public AdminResponseObj createCollaborator(HttpServletRequest request, @Validated(ValidationSequence.class) @RequestBody CollaboratorDTO collaboratorDto) {
		String methodName = "createCollaborator";
		long refId = System.currentTimeMillis();
		
		// set null for image data
		CollaboratorDTO logRequest = (CollaboratorDTO) SerializationUtils.clone(collaboratorDto);
		if (logRequest.getDelegate() != null) {
			logRequest.getDelegate().setScannedBackIdCard(null);
			logRequest.getDelegate().setScannedFrontIdCard(null);
			logRequest.getDelegate().setScannedSignature(null);
		}
		
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/collaborators, INPUT:" + Utility.getGson().toJson(logRequest));

		collaboratorService.createCollaboratorPA(request, collaboratorDto, refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@PutMapping("/admin/collaborators/{collaboratorCode}")
	@PreAuthorize("hasAuthority('clientManagement.brokerCollaboratorManagement.collaboratorInfo.update')")
	public AdminResponseObj updateCollaborator(HttpServletRequest request, @PathVariable String collaboratorCode,
			@Validated(ValidationSequence.class) @RequestBody ApprovalUpdateCollaboratorDTO collaboratorDto) {
		String methodName = "updateCollaborator";
		long refId = System.currentTimeMillis();
		
		// set null for image data
		ApprovalUpdateCollaboratorDTO logRequest = (ApprovalUpdateCollaboratorDTO) SerializationUtils.clone(collaboratorDto);
		if (logRequest.getOldData().getDelegate() != null) {
			logRequest.getOldData().getDelegate().setScannedBackIdCard(null);
			logRequest.getOldData().getDelegate().setScannedFrontIdCard(null);
			logRequest.getOldData().getDelegate().setScannedSignature(null);
		}
		
		if (logRequest.getPendingData().getDelegate() != null) {
			logRequest.getPendingData().getDelegate().setScannedBackIdCard(null);
			logRequest.getPendingData().getDelegate().setScannedFrontIdCard(null);
			logRequest.getPendingData().getDelegate().setScannedSignature(null);
		}
		
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [PUT]/admin/collaborators/" + collaboratorCode + ", INPUT:" + Utility.getGson().toJson(logRequest));

		collaboratorService.updateCollaboratorPA(request, collaboratorCode, collaboratorDto, refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}

	@GetMapping("/admin/collaborators/{collaboratorCode}")
	@PreAuthorize("hasAuthority('clientManagement.brokerCollaboratorManagement.collaboratorInfo.view')")
	public AdminResponseObj getCollaboratorDetail(HttpServletRequest request, @PathVariable String collaboratorCode) {
		String methodName = "getCollaboratorDetail";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/collaborators/" + collaboratorCode);
		AdminResponseObj response = new AdminResponseObj();

		CollaboratorDTO collaboratorDto = collaboratorService.getCollaboratorDetail(request, collaboratorCode, refId);
		if (collaboratorDto != null) {
			response.setStatus(Constant.RESPONSE_OK);
			response.setData(new AdminDataObj());
			response.getData().setCollaborator(collaboratorDto);
		} else {
			response.setStatus(Constant.RESPONSE_ERROR);
			response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
		}

		AdminResponseObj logResponse = (AdminResponseObj) SerializationUtils.clone(response);
		if (logResponse.getData().getCollaborator() != null && logResponse.getData().getCollaborator().getDelegate() != null) {
			logResponse.getData().getCollaborator().getDelegate().setScannedBackIdCard("");
			logResponse.getData().getCollaborator().getDelegate().setScannedFrontIdCard("");
			logResponse.getData().getCollaborator().getDelegate().setScannedSignature("");
		}
		
		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(logResponse));
		return response;
	}
	
	@GetMapping("/admin/collaborators/{collaboratorCode}/user")
	@PreAuthorize("hasAuthority('clientManagement.brokerCollaboratorManagement.collaboratorUserInfo.view')")
	public AdminResponseObj getCollaboratorUser(HttpServletRequest request, @PathVariable String collaboratorCode) {
		String methodName = "getCollaboratorUser";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/collaborators/" + collaboratorCode + "/user");
		AdminResponseObj response = new AdminResponseObj();

		UserDTO collaboratorUserDto = collaboratorService.getCollaboratorUserDetail(request, collaboratorCode, refId);
		if (collaboratorUserDto != null) {
			response.setStatus(Constant.RESPONSE_OK);
			response.setData(new AdminDataObj());
			response.getData().setCollaboratorUser(collaboratorUserDto);
		} else {
			response.setStatus(Constant.RESPONSE_ERROR);
			response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
		}

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
		return response;
	}
	
	@PostMapping("/admin/collaborators/{collaboratorCode}/functions")
    @PreAuthorize("hasAuthority('clientManagement.brokerCollaboratorManagement.collaboratorFunctionsAssign.create')")
    public AdminResponseObj createCollaboratorFunctions(HttpServletRequest request, @PathVariable String collaboratorCode, @Validated(ValidationSequence.class) @RequestBody ApprovalFunctionsDTO collaboratorDto) {
        String methodName = "createCollaboratorFunctions";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[POST]/admin/collaborators/%s/functions", collaboratorCode) + ", INPUT:" + Utility.getGson().toJson(collaboratorDto));
        
        collaboratorService.createCollaboratorFunctionsPA(request, collaboratorCode, collaboratorDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + Utility.getGson().toJson(response));
        return response;
    }
}
