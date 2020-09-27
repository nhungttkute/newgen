package com.newgen.am.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class InvestorActivationDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String investorCode;
	private String investorName;
	private String phoneNumber;
	private String email;
	private String identityCard;
	private String note;
}
