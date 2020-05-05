package com.newgen.am.controller;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.newgen.am.dto.BasePagination;
import com.newgen.am.dto.DepartmentCSV;
import com.newgen.am.dto.DepartmentDTO;
import com.newgen.am.dto.MemberCSV;
import com.newgen.am.dto.MemberDTO;
import com.newgen.am.dto.UpdateDepartmentDTO;
import com.newgen.am.dto.UpdateMemberDTO;
import com.newgen.am.exception.CustomException;
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
		AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(memberDto));

		memberService.createMember(request, memberDto, refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
	
	@PutMapping("/admin/members/{memberId}")
	@PreAuthorize("hasAuthority('clientManagement.memberManagement.memberInfo.update')")
	public AdminResponseObj updateMember(HttpServletRequest request, @PathVariable String memberId,
			@Validated(ValidationSequence.class) @RequestBody UpdateMemberDTO memberDto) {
		String methodName = "updateMember";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [PUT]/admin/members/" + memberId);
		AMLogger.logMessage(className, methodName, refId, "INPUT:" + new Gson().toJson(memberDto));

		memberService.updateMember(request, memberId, memberDto, refId);

		AdminResponseObj response = new AdminResponseObj();
		response.setStatus(Constant.RESPONSE_OK);

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}

	@GetMapping("/admin/members/{memberId}")
	@PreAuthorize("hasAuthority('clientManagement.memberManagement.memberInfo.view')")
	public AdminResponseObj getMemberDetail(@PathVariable String memberId) {
		String methodName = "getMemberDetail";
		long refId = System.currentTimeMillis();
		AMLogger.logMessage(className, methodName, refId, "REQUEST_API: [GET]/admin/members/" + memberId);
		AdminResponseObj response = new AdminResponseObj();

		MemberDTO memberDto = memberService.getMemberDetail(memberId, refId);
		if (memberDto != null) {
			response.setStatus(Constant.RESPONSE_OK);
			response.setData(new AdminDataObj());
			response.getData().setMember(memberDto);
		} else {
			response.setStatus(Constant.RESPONSE_ERROR);
			response.setErrMsg(ErrorMessage.RESULT_NOT_FOUND);
		}

		AMLogger.logMessage(className, methodName, refId, "OUTPUT:" + new Gson().toJson(response));
		return response;
	}
}
