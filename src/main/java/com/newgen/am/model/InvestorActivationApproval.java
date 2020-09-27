package com.newgen.am.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "investor_activation_approvals")
public class InvestorActivationApproval extends BaseApproval {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String investorCode;
	private double marginSurplusInterestRate;
    private double marginDeficitInterestRate;
}
