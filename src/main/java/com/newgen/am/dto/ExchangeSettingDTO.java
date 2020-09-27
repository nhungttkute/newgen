package com.newgen.am.dto;

import java.util.List;

import javax.validation.Valid;

import com.newgen.am.model.Exchange;
import com.newgen.am.validation.ValidUpdateStringField;

import lombok.Data;

@Data
public class ExchangeSettingDTO {
	@ValidUpdateStringField
	private String deptCode;
	private String deptName;
	@ValidUpdateStringField
	private String memberCode;
	private String memberName;
	@ValidUpdateStringField
	private String brokerCode;
	private String brokerName;
	@ValidUpdateStringField
	private String collaboratorCode;
	private String collaboratorName;
	@ValidUpdateStringField
	private String investorCode;
	private String investorName;
	@ValidUpdateStringField
	private String username;
	@ValidUpdateStringField
	private String fullName;
	@Valid
	private List<UserBaseInfo> users;
	@Valid
	private List<Exchange> exchanges;
}
