package com.newgen.am.controller;

import java.util.Arrays;
import java.util.List;

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
import com.newgen.am.dto.ApprovalChangeGroupDTO;
import com.newgen.am.dto.ApprovalCommoditiesDTO;
import com.newgen.am.dto.ApprovalCommodityFeesDTO;
import com.newgen.am.dto.ApprovalDefaultPositionLimitDTO;
import com.newgen.am.dto.ApprovalFunctionsDTO;
import com.newgen.am.dto.ApprovalGeneralFeeDTO;
import com.newgen.am.dto.ApprovalMarginMultiplierDTO;
import com.newgen.am.dto.ApprovalMarginRatioAlertDTO;
import com.newgen.am.dto.ApprovalOrderLimitDTO;
import com.newgen.am.dto.ApprovalRiskParametersDTO;
import com.newgen.am.dto.ApprovalUpdateMemberDTO;
import com.newgen.am.dto.ApprovalUpdateUserDTO;
import com.newgen.am.dto.ApprovalUserRolesDTO;
import com.newgen.am.dto.BasePagination;
import com.newgen.am.dto.GeneralFeeDTO;
import com.newgen.am.dto.GeneralFeesDTO;
import com.newgen.am.dto.ListElementDTO;
import com.newgen.am.dto.MemberCSV;
import com.newgen.am.dto.MemberCommoditiesDTO;
import com.newgen.am.dto.MemberDTO;
import com.newgen.am.dto.OtherFeeDTO;
import com.newgen.am.dto.UserCSV;
import com.newgen.am.dto.UserDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.RoleFunction;
import com.newgen.am.service.MemberService;
import com.newgen.am.validation.ValidationSequence;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

@RestController
public class MemberController {
	private String className = "MemberController";
	
	@Autowired
	MemberService memberService;
	
	@GetMapping("/admin/members")
	@PreAuthorize("hasAuthority('clientManagement.memberManagement.memberList.view')")
	public AdminResponseObj listMembers(HttpServletRequest request) {
		String methodName = "listMembers";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/members");
		AdminResponseObj response = new AdminResponseObj();
		
		try {
			BasePagination<MemberDTO> pagination = memberService.list(request, refId);
			if (pagination != null && pagination.getData().size() > 0) {
				response.setStatus(Constant.RESPONSE_OK);
				response.setData(new AdminDataObj());
				response.getData().setMembers(pagination.getData());
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

	@GetMapping("/admin/members/csv")
	@PreAuthorize("hasAuthority('clientManagement.memberManagement.memberList.view')")
	public void downloadMembersCsv(HttpServletRequest request, HttpServletResponse response) {
		String methodName = "downloadMembersCsv";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/members/csv");

		try {
			// set file name and content type
			String filename = Constant.CSV_MEMBERS;

			response.setContentType("text/csv");
			response.setCharacterEncoding("UTF-8");
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

			// create a csv writer
			CustomMappingStrategy<MemberCSV> mappingStrategy = new CustomMappingStrategy<MemberCSV>();
			mappingStrategy.setType(MemberCSV.class);

			StatefulBeanToCsv<MemberCSV> writer = new StatefulBeanToCsvBuilder<MemberCSV>(response.getWriter())
					.withMappingStrategy(mappingStrategy).withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
					.withSeparator(CSVWriter.DEFAULT_SEPARATOR).withOrderedResults(false).build();

			// write all users to csv file
			writer.write(memberService.listCsv(request, refId));
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/admin/members")
	@PreAuthorize("hasAuthority('clientManagement.memberManagement.memberInfo.create')")
	public AdminResponseObj createMember(HttpServletRequest request, @Validated(ValidationSequence.class) @RequestBody MemberDTO memberDto) {
		String methodName = "createMember";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [POST]/admin/members");
		
		// set null image data
		MemberDTO logRequest = (MemberDTO) SerializationUtils.clone(memberDto);
		logRequest.getCompany().getDelegate().setScannedBackIdCard(null);
		logRequest.getCompany().getDelegate().setScannedFrontIdCard(null);
		logRequest.getCompany().getDelegate().setScannedSignature(null);
		
		AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(logRequest));

		memberService.createMemberPA(request, memberDto, refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
	
	@PutMapping("/admin/members/{memberCode}")
	@PreAuthorize("hasAuthority('clientManagement.memberManagement.memberInfo.update')")
	public AdminResponseObj updateMember(HttpServletRequest request, @PathVariable String memberCode,
			@Validated(ValidationSequence.class) @RequestBody ApprovalUpdateMemberDTO memberDto) {
		String methodName = "updateMember";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [PUT]/admin/members/" + memberCode);
		
		// set null image data
		ApprovalUpdateMemberDTO logRequest = (ApprovalUpdateMemberDTO) SerializationUtils.clone(memberDto);
		if (logRequest.getPendingData().getCompany() != null && logRequest.getPendingData().getCompany().getDelegate() != null) {
			logRequest.getPendingData().getCompany().getDelegate().setScannedBackIdCard(null);
			logRequest.getPendingData().getCompany().getDelegate().setScannedFrontIdCard(null);
			logRequest.getPendingData().getCompany().getDelegate().setScannedSignature(null);
		}
		
		AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(logRequest));

		memberService.updateMemberPA(request, memberCode, memberDto, refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}

	@GetMapping("/admin/members/{memberCode}")
	@PreAuthorize("hasAuthority('clientManagement.memberManagement.memberInfo.view')")
	public AdminResponseObj getMemberDetail(@PathVariable String memberCode) {
		String methodName = "getMemberDetail";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/members/" + memberCode);
		AdminResponseObj response = new AdminResponseObj();

		MemberDTO memberDto = memberService.getMemberDetail(memberCode, refId);
		if (memberDto != null) {
			response.setStatus(Constant.RESPONSE_OK);
			response.setData(new AdminDataObj());
			response.getData().setMember(memberDto);
		} else {
			response.setStatus(Constant.RESPONSE_ERROR);
			response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
		}
		
		AdminResponseObj logResponse = (AdminResponseObj) SerializationUtils.clone(response);
		if (logResponse.getData().getMember() != null && logResponse.getData().getMember().getCompany() != null && logResponse.getData().getMember().getCompany().getDelegate() != null) {
			logResponse.getData().getMember().getCompany().getDelegate().setScannedBackIdCard("");
			logResponse.getData().getMember().getCompany().getDelegate().setScannedFrontIdCard("");
			logResponse.getData().getMember().getCompany().getDelegate().setScannedSignature("");
		}
		
		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(logResponse));
		return response;
	}
	
	@GetMapping("/admin/members/{memberCode}/masterUser")
	@PreAuthorize("hasAuthority('clientManagement.memberManagement.memberMasterUserInfo.view')")
	public AdminResponseObj getMemberUserMaster(@PathVariable String memberCode) {
		String methodName = "getMemberUserMaster";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/members/" + memberCode + "/masterUser");
		AdminResponseObj response = new AdminResponseObj();

		UserDTO memberUserDto = memberService.getMemberMasterUserDetail(memberCode, refId);
		if (memberUserDto != null) {
			response.setStatus(Constant.RESPONSE_OK);
			response.setData(new AdminDataObj());
			response.getData().setMemberUser(memberUserDto);
		} else {
			response.setStatus(Constant.RESPONSE_ERROR);
			response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
		}

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
	
	@PostMapping("/admin/members/{memberCode}/functions")
    @PreAuthorize("hasAuthority('clientManagement.memberManagement.memberFunctionsAssign.create')")
    public AdminResponseObj createMemberFunctions(HttpServletRequest request, @PathVariable String memberCode, @Validated(ValidationSequence.class) @RequestBody ApprovalFunctionsDTO memberDto) {
        String methodName = "createMemberFunctions";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[POST]/admin/members/%s/functions", memberCode));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(memberDto));
        
        memberService.createMemberFunctionsPA(request, memberCode, memberDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
	
	@PostMapping("/admin/members/{memberCode}/orderLimit")
    @PreAuthorize("hasAuthority('clientManagement.memberManagement.memberOrderLimitConfig.create')")
    public AdminResponseObj createMemberOrderLimit(HttpServletRequest request, @PathVariable String memberCode, @Validated(ValidationSequence.class) @RequestBody ApprovalOrderLimitDTO memberDto) {
        String methodName = "createMemberOrderLimit";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[POST]/admin/members/%s/orderLimit", memberCode));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(memberDto));
        
        memberService.createOrderLimitPA(request, memberCode, memberDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
	
	@PostMapping("/admin/members/{memberCode}/defaultPositionLimit")
    @PreAuthorize("hasAuthority('clientManagement.memberManagement.defaultPositionLimitConfig.create')")
    public AdminResponseObj createMemberDefaultPositionLimit(HttpServletRequest request, @PathVariable String memberCode, @Validated(ValidationSequence.class) @RequestBody ApprovalDefaultPositionLimitDTO memberDto) {
        String methodName = "createMemberDefaultPositionLimit";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[POST]/admin/members/%s/defaultPositionLimit", memberCode));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(memberDto));
        
        memberService.createDefaultPositionLimitPA(request, memberCode, memberDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
	
	@PostMapping("/admin/members/{memberCode}/commoditiesSetting")
    @PreAuthorize("hasAuthority('clientManagement.memberManagement.memberCommoditiesAssign.create') or hasAuthority('clientManagement.memberManagement.memberCommoditiesFeeConfig.create') or hasAuthority('clientManagement.memberManagement.memberOrderLimitConfig.create')")
    public AdminResponseObj createMemberCommoditiesSetting(HttpServletRequest request, @PathVariable String memberCode, @Validated(ValidationSequence.class) @RequestBody ApprovalCommoditiesDTO memberDto) {
        String methodName = "createMemberCommoditiesSetting";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[POST]/admin/members/%s/commoditiesSetting", memberCode));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(memberDto));
        
        memberService.createMemberCommoditiesPA(request, memberCode, memberDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
	
	@PutMapping("/admin/members/{memberCode}/newPositionOrderLock")
    @PreAuthorize("hasAuthority('clientManagement.memberManagement.memberRiskManagement.newOrderLockConfig.create')")
    public AdminResponseObj setMemberNewPositionOrderLock(HttpServletRequest request, @PathVariable String memberCode, @Validated(ValidationSequence.class) @RequestBody ApprovalRiskParametersDTO memberDto) {
        String methodName = "setMemberNewPositionOrderLock";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[PUT]/admin/members/%s/newPositionOrderLock", memberCode));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(memberDto));
        
        if (Utility.isNull(memberDto) || Utility.isNull(memberDto.getPendingData()) || Utility.isNull(memberDto.getPendingData().getRiskParameters()) || Utility.isNull(memberDto.getPendingData().getRiskParameters().getNewPositionOrderLock())) {
        	throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
        }
        memberService.setMemberNewPositionOrderLockPA(request, memberCode, memberDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
	
	@PutMapping("/admin/members/{memberCode}/orderLock")
    @PreAuthorize("hasAuthority('clientManagement.memberManagement.memberRiskManagement.orderLockConfig.create')")
    public AdminResponseObj setMemberOrderLock(HttpServletRequest request, @PathVariable String memberCode, @Validated(ValidationSequence.class) @RequestBody ApprovalRiskParametersDTO memberDto) {
        String methodName = "setMemberOrderLock";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[PUT]/admin/members/%s/orderLock", memberCode));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(memberDto));
        
        if (Utility.isNull(memberDto) || Utility.isNull(memberDto.getPendingData()) || Utility.isNull(memberDto.getPendingData().getRiskParameters()) || Utility.isNull(memberDto.getPendingData().getRiskParameters().getOrderLock())) {
        	throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
        }
        memberService.setMemberOrderLockPA(request, memberCode, memberDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
	
	@PutMapping("/admin/members/{memberCode}/marginWithdrawalLock")
    @PreAuthorize("hasAuthority('clientManagement.memberManagement.memberRiskManagement.marginWithdrawalLockConfig.create')")
    public AdminResponseObj setMemberMarginWithdrawalLock(HttpServletRequest request, @PathVariable String memberCode, @Validated(ValidationSequence.class) @RequestBody ApprovalRiskParametersDTO memberDto) {
        String methodName = "setMemberMarginWithdrawalLock";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[PUT]/admin/members/%s/marginWithdrawalLock", memberCode));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(memberDto));
        
        if (Utility.isNull(memberDto) || Utility.isNull(memberDto.getPendingData()) || Utility.isNull(memberDto.getPendingData().getRiskParameters()) ||  Utility.isNull(memberDto.getPendingData().getRiskParameters().getMarginWithdrawalLock())) {
        	throw new CustomException(ErrorMessage.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
        }
        memberService.setMemberMarginWithDrawalLockPA(request, memberCode, memberDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
	
	@PutMapping("/admin/members/{memberCode}/marginMultiplierBulk")
    @PreAuthorize("hasAuthority('clientManagement.memberManagement.investorMarginMultiplierBulkConfig.create')")
    public AdminResponseObj setMarginMultiplierBulk(HttpServletRequest request, @PathVariable String memberCode, @Validated(ValidationSequence.class) @RequestBody ApprovalMarginMultiplierDTO memberDto) {
        String methodName = "setMarginMultiplierBulk";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[PUT]/admin/members/%s/marginMultiplierBulk", memberCode));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(memberDto));
        
        memberService.setMarginMultiplierBulkPA(request, memberCode, memberDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
	
	@PutMapping("/admin/members/{memberCode}/marginRatioBulk")
    @PreAuthorize("hasAuthority('clientManagement.memberManagement.investorMarginRatioBulkConfig.create')")
    public AdminResponseObj setMarginRatioBulk(HttpServletRequest request, @PathVariable String memberCode, @Validated(ValidationSequence.class) @RequestBody ApprovalMarginRatioAlertDTO memberDto) {
        String methodName = "setMarginRatioBulk";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[PUT]/admin/members/%s/marginRatioBulk", memberCode));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(memberDto));
        
        memberService.setMarginRatioAlertBulkPA(request, memberCode, memberDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
	
	@PostMapping("/admin/members/{memberCode}/setGeneralFeeBulk")
    @PreAuthorize("hasAuthority('clientManagement.memberManagement.investorGeneralFeeBulkConfig.create')")
    public AdminResponseObj setGeneralFeeBulk(HttpServletRequest request, @PathVariable String memberCode, @Validated(ValidationSequence.class) @RequestBody GeneralFeeDTO memberDto) {
        String methodName = "setGeneralFeeBulk";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[POST]/admin/members/%s/setGeneralFeeBulk", memberCode));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(memberDto));
        
        memberService.setGeneralFeesBulkPA(request, memberCode, memberDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
	
	@PutMapping("/admin/members/{memberCode}/updateGeneralFeeBulk")
    @PreAuthorize("hasAuthority('clientManagement.memberManagement.investorGeneralFeeBulkConfig.create')")
    public AdminResponseObj updateGeneralFeeBulk(HttpServletRequest request, @PathVariable String memberCode, @Validated(ValidationSequence.class) @RequestBody ApprovalGeneralFeeDTO memberDto) {
        String methodName = "setGeneralFeeBulk";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[PUT]/admin/members/%s/updateGeneralFeeBulk", memberCode));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(memberDto));
        
        memberService.updateGeneralFeesBulkPA(request, memberCode, memberDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
	
	@PutMapping("/admin/members/{memberCode}/brokerCommoditiesFeeBulk")
    @PreAuthorize("hasAuthority('clientManagement.memberManagement.brokerCommoditiesFeeBulkConfig.create')")
    public AdminResponseObj setBrokerCommoditiesFeeBulk(HttpServletRequest request, @PathVariable String memberCode, @Validated(ValidationSequence.class) @RequestBody ApprovalCommodityFeesDTO memberDto) {
        String methodName = "setBrokerCommoditiesFeeBulk";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[PUT]/admin/members/%s/brokerCommoditiesFeeBulk", memberCode));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(memberDto));

        memberService.setBrokerCommoditiesFeeBulkPA(request, memberCode, memberDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
	
	@PutMapping("/admin/members/{memberCode}/investorCommoditiesFeeBulk")
    @PreAuthorize("hasAuthority('clientManagement.memberManagement.investorCommoditiesFeeBulkConfig.create')")
    public AdminResponseObj setInvestorCommoditiesFeeBulk(HttpServletRequest request, @PathVariable String memberCode, @Validated(ValidationSequence.class) @RequestBody ApprovalCommodityFeesDTO memberDto) {
        String methodName = "setInvestorCommoditiesFeeBulk";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[PUT]/admin/members/%s/investorCommoditiesFeeBulk", memberCode));
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(memberDto));

        memberService.setInvestorCommoditiesFeeBulkPA(request, memberCode, memberDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
	
	@GetMapping("/admin/members/{memberCode}/users")
	@PreAuthorize("hasAuthority('clientManagement.memberManagement.memberUserManagement.memberUserList.view')")
	public AdminResponseObj listMemberUsers(HttpServletRequest request, @PathVariable String memberCode) {
		String methodName = "listMemberUsers";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[GET]/admin/members/%s/users", memberCode));
		AdminResponseObj response = new AdminResponseObj();
		
		try {
			BasePagination<UserDTO> pagination = memberService.listMemberUsers(request, memberCode, refId);
			if (pagination != null && pagination.getData().size() > 0) {
				response.setStatus(Constant.RESPONSE_OK);
				response.setData(new AdminDataObj());
				response.getData().setMemberUsers(pagination.getData());
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
	
	@GetMapping("/admin/members/{memberCode}/users/csv")
	@PreAuthorize("hasAuthority('clientManagement.memberManagement.memberList.view')")
	public void downloadMemberUsersCsv(HttpServletRequest request, HttpServletResponse response, @PathVariable String memberCode) {
		String methodName = "downloadMemberUsersCsv";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[GET]/admin/members/%s/users/csv", memberCode));

		try {
			// set file name and content type
			String filename = Constant.CSV_MEMBER_USERS;

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
			writer.write(memberService.listMemberUsersCsv(request, memberCode, refId));
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/admin/members/{memberCode}/users")
	@PreAuthorize("hasAuthority('clientManagement.memberManagement.memberUserManagement.memberUserInfo.create')")
	public AdminResponseObj createMemberUser(HttpServletRequest request, @PathVariable String memberCode,
			@Validated(ValidationSequence.class) @RequestBody UserDTO userDto) {
		String methodName = "createMemberUser";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[POST]/admin/members/%s/users", memberCode));
		AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(userDto));

		memberService.createMemberUserPA(request, memberCode, userDto, refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
	
	@PutMapping("/admin/members/{memberCode}/users/{username}")
	@PreAuthorize("hasAuthority('clientManagement.memberManagement.memberUserManagement.memberUserInfo.update')")
	public AdminResponseObj updateMemberUser(HttpServletRequest request, @PathVariable String memberCode, @PathVariable String username,
			@Validated(ValidationSequence.class) @RequestBody ApprovalUpdateUserDTO userDto) {
		String methodName = "updateMemberUser";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: " + String.format("[PUT]/admin/members/%s/users/%s", memberCode, username));
		AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(userDto));

		memberService.updateMemberUserPA(request, memberCode, username, userDto, refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/members/{memberCode}/users/{username}")
	@PreAuthorize("hasAuthority('clientManagement.memberManagement.memberUserManagement.memberUserInfo.view')")
	public AdminResponseObj getMemberUser(@PathVariable String memberCode, @PathVariable String username) {
		String methodName = "getMemberUser";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				String.format("REQUEST_API: [GET]/admin/members/%s/users/%s", memberCode, username));
		AdminResponseObj response = new AdminResponseObj();

		UserDTO userDto = memberService.getMemberUser(memberCode, username, refId);
		if (userDto != null) {
			response.setStatus(Constant.RESPONSE_OK);
			response.setData(new AdminDataObj());
			response.getData().setMemberUser(userDto);
		} else {
			response.setStatus(Constant.RESPONSE_ERROR);
			response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
		}

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}

	@PostMapping("/admin/members/{memberCode}/users/{username}/roles")
	@PreAuthorize("hasAuthority('clientManagement.memberManagement.memberUserManagement.memberUserRoleAssign.create')")
	public AdminResponseObj saveMemberUserRoles(HttpServletRequest request, @PathVariable String memberCode,
			@PathVariable String username, @Validated(ValidationSequence.class) @RequestBody ApprovalUserRolesDTO userDto) {
		String methodName = "saveMemberUserRoles";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				String.format("REQUEST_API: [POST]/admin/members/%s/users/%s/roles", memberCode, username));

		memberService.saveMemberUserRolesPA(request, memberCode, username, userDto, refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}

	@PostMapping("/admin/members/{memberCode}/users/{username}/functions")
	@PreAuthorize("hasAuthority('clientManagement.memberManagement.memberUserManagement.memberUserFunctionsAssign.create')")
	public AdminResponseObj saveMemberUserFunctions(HttpServletRequest request, @PathVariable String memberCode,
			@PathVariable String username, @Validated(ValidationSequence.class) @RequestBody ApprovalFunctionsDTO userDto) {
		String methodName = "saveMemberUserFunctions";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				String.format("REQUEST_API: [POST]/admin/members/%s/users/%s/functions", memberCode, username));

		memberService.saveMemberUserFunctionsPA(request, memberCode, username, userDto, refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/members/{memberCode}/functions")
	@PreAuthorize("hasAuthority('clientManagement.memberManagement.memberRoleManagement.memberRoleFunctionsAssign.create') or hasAuthority('clientManagement.memberManagement.memberUserManagement.memberUserRole.view') or hasAuthority('clientManagement.memberManagement.memberUserManagement.memberUserFunctionsAssign.create')")
	public AdminResponseObj getMemberFunctions(@PathVariable String memberCode) {
		String methodName = "getMemberFunctions";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				String.format("REQUEST_API: [GET]/admin/members/%s/functions", memberCode));

		List<RoleFunction> functions = memberService.getMemberFunctions(memberCode, refId);

		AdminResponseObj response = new AdminResponseObj();
		if (functions != null && functions.size() > 0) {
            response.setStatus(Constant.RESPONSE_OK);
            response.setData(new AdminDataObj());
            response.getData().setMemberFunctions(functions);
        } else {
            response.setStatus(Constant.RESPONSE_ERROR);
            response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
        }

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/members/{memberCode}/commodities")
//	@PreAuthorize("hasAuthority('clientManagement.brokerManagement.brokerCommoditiesAssign.create')")
	public AdminResponseObj getMemberCommodities(@PathVariable String memberCode) {
		String methodName = "getMemberCommodities";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				String.format("REQUEST_API: [GET]/admin/members/%s/commodities", memberCode));
		
		AdminResponseObj response = new AdminResponseObj();
		MemberCommoditiesDTO memberDto = memberService.getMemberCommodities(memberCode, refId);
		if (memberDto != null) {
			response.setStatus(Constant.RESPONSE_OK);
			response.setData(new AdminDataObj());
			response.getData().setMemberCommodities(memberDto);
		} else {
			response.setStatus(Constant.RESPONSE_ERROR);
			response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
		}

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/members/{memberCode}/brokerList")
	public AdminResponseObj getMemberBrokerList(HttpServletRequest request, @PathVariable String memberCode) {
		String methodName = "getMemberBrokerList";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				String.format("REQUEST_API: [GET]/admin/members/%s/brokerList", memberCode));

		List<ListElementDTO> brokerList = memberService.getMemberBrokerList(request, memberCode, refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setBrokerList(brokerList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
	
	@GetMapping("/admin/members/{memberCode}/collaboratorList")
	public AdminResponseObj getMemberCollaboratorList(HttpServletRequest request, @PathVariable String memberCode) {
		String methodName = "getMemberCollaboratorList";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId,
				String.format("REQUEST_API: [GET]/admin/members/%s/collaboratorList", memberCode));

		List<ListElementDTO> collaboratorList = memberService.getMemberCollaboratorList(request, memberCode, refId);
		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);
        response.setData(new AdminDataObj());
        response.getData().setCollaboratorList(collaboratorList);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
	
	@PutMapping("/admin/members/moveAllInvestors")
    @PreAuthorize("hasAuthority('clientManagement.memberManagement.allInvestorsNewMember.transfer')")
    public AdminResponseObj moveAllInvestors(HttpServletRequest request, @Validated(ValidationSequence.class) @RequestBody ApprovalChangeGroupDTO groupDto) {
        String methodName = "moveAllInvestors";
        long refId = System.currentTimeMillis();
        AMLogger.logMessage(className, methodName, refId, "admin/members/moveAllInvestors");
        AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(groupDto));
        
        memberService.moveAllInvestorsToNewMemberPA(request, groupDto, refId);
        
        AdminResponseObj response = new AdminResponseObj();
        response.setStatus(Constant.RESPONSE_OK);
        
        AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
        return response;
    }
}
