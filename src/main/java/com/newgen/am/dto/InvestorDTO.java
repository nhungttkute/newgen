package com.newgen.am.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newgen.am.model.Commodity;
import com.newgen.am.model.Contact;
import com.newgen.am.model.CqgInfo;
import com.newgen.am.model.GeneralFee;
import com.newgen.am.model.InvestorCompany;
import com.newgen.am.model.InvestorIndividual;
import com.newgen.am.model.InvestorUser;
import com.newgen.am.model.MarginRatioAlert;
import com.newgen.am.model.RiskParameters;
import com.newgen.am.model.UserRole;
import com.newgen.am.validation.FormatGroup;
import com.newgen.am.validation.LengthGroup;
import com.newgen.am.validation.UniqueGroup;
import com.newgen.am.validation.UniqueInvestorCode;
import com.newgen.am.validation.ValidInvestorCode;
import com.newgen.am.validation.ValidNumber;
import com.newgen.am.validation.ValidUpdateStringField;

import lombok.Data;

@Data
//@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class InvestorDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String _id;
	@NotEmpty(message = "Required.")
    @ValidNumber(groups = FormatGroup.class)
    @Size(min = 3, max = 3, message = "Invalid format.", groups = LengthGroup.class)
	private String memberCode;
	@NotEmpty(message = "Required.")
    @Size(min = 1, max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String memberName;
	@ValidUpdateStringField
    @ValidNumber(groups = FormatGroup.class)
    @Size(min = 8, max = 8, message = "Invalid format.", groups = LengthGroup.class)
    private String brokerCode;
	@ValidUpdateStringField
    @Size(min = 1, max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String brokerName;
	@ValidUpdateStringField
	@ValidNumber(groups = FormatGroup.class)
    @Size(min = 6, max = 6, message = "Invalid format.", groups = LengthGroup.class)
    private String collaboratorCode;
	@ValidUpdateStringField
    @Size(min = 1, max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String collaboratorName;
    @NotEmpty(message = "Required.")
    @ValidInvestorCode(groups = FormatGroup.class)
    @UniqueInvestorCode(groups = UniqueGroup.class)
    private String investorCode;
    @NotEmpty(message = "Required.")
    @Size(min = 1, max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String investorName;
    @Size(max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String note;
    @NotEmpty(message = "Required.")
    @Size(max = 20, message = "Invalid format.", groups = LengthGroup.class)
    private String status;
    @NotEmpty(message = "Required.")
    @Size(max = 20, message = "Invalid format.", groups = LengthGroup.class)
    private String type;
    private long createdDate;
    @Valid
    private InvestorCompany company;
    @Valid
    private InvestorIndividual individual;
    private Contact contact;
    private CqgInfo cqgInfo;
    private MarginInfoDTO account;
    private List<InvestorUser> users;
    private UserRole role;
    private int orderLimit;
    private int defaultPositionLimit;
    private long defaultCommodityFee;
    private double marginMultiplier;
    private List<GeneralFee> generalFees;
    private MarginRatioAlert marginRatioAlert;
    private RiskParameters riskParameters;
    private List<Commodity> commodities;
}
