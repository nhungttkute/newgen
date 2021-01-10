package com.newgen.am.model;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
@Data
@Document(collection = "investors")
public class Investor extends AuditModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
    private String memberCode;
    private String memberName;
    private String brokerCode;
    private String brokerName;
    private String collaboratorCode;
    private String collaboratorName;
    private String investorCode;
    private String investorName;
    private String status;
    private String note;
    private String type;
    private InvestorCompany company;
    private InvestorIndividual individual;
    private Contact contact;
    private CqgInfo cqgInfo;
	private InvestorAccount account;
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
