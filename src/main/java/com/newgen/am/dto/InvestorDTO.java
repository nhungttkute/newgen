package com.newgen.am.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.newgen.am.model.Commodity;
import com.newgen.am.model.Company;
import com.newgen.am.model.Contact;
import com.newgen.am.model.CqgInfo;
import com.newgen.am.model.Individual;
import com.newgen.am.model.InvestorAccount;
import com.newgen.am.model.InvestorUser;
import com.newgen.am.model.MarginRatioAlert;
import com.newgen.am.model.RiskParameters;
import com.newgen.am.model.UserRole;
import com.newgen.am.validation.FormatGroup;
import com.newgen.am.validation.LengthGroup;
import com.newgen.am.validation.ValidInvestorCode;
import com.newgen.am.validation.ValidNumber;
import com.newgen.am.validation.ValidUpdateStringField;

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
    private Company company;
    @Valid
    private Individual individual;
    private Contact contact;
    private CqgInfo cqgInfo;
    private MarginInfoDTO account;
    private List<InvestorUser> users;
    private UserRole role;
    private int orderLimit;
    private int defaultPositionLimit;
    private long defaultCommodityFee;
    private double marginMultiplier;
    private long generalFee;
    private long otherFee;
    private MarginRatioAlert marginRatioAlert;
    private RiskParameters riskParameters;
    private List<Commodity> commodities;
    
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getMemberCode() {
		return memberCode;
	}
	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}
	public String getMemberName() {
		return memberName;
	}
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	public String getBrokerCode() {
		return brokerCode;
	}
	public void setBrokerCode(String brokerCode) {
		this.brokerCode = brokerCode;
	}
	public String getBrokerName() {
		return brokerName;
	}
	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
	}
	public String getCollaboratorCode() {
		return collaboratorCode;
	}
	public void setCollaboratorCode(String collaboratorCode) {
		this.collaboratorCode = collaboratorCode;
	}
	public String getCollaboratorName() {
		return collaboratorName;
	}
	public void setCollaboratorName(String collaboratorName) {
		this.collaboratorName = collaboratorName;
	}
	public String getInvestorCode() {
		return investorCode;
	}
	public void setInvestorCode(String investorCode) {
		this.investorCode = investorCode;
	}
	public String getInvestorName() {
		return investorName;
	}
	public void setInvestorName(String investorName) {
		this.investorName = investorName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}
	public Individual getIndividual() {
		return individual;
	}
	public void setIndividual(Individual individual) {
		this.individual = individual;
	}
	public Contact getContact() {
		return contact;
	}
	public void setContact(Contact contact) {
		this.contact = contact;
	}
	public MarginInfoDTO getAccount() {
		return account;
	}
	public void setAccount(MarginInfoDTO account) {
		this.account = account;
	}
	public List<InvestorUser> getUsers() {
		return users;
	}
	public void setUsers(List<InvestorUser> users) {
		this.users = users;
	}
	public UserRole getRole() {
		return role;
	}
	public void setRole(UserRole role) {
		this.role = role;
	}
	public int getOrderLimit() {
		return orderLimit;
	}
	public void setOrderLimit(int orderLimit) {
		this.orderLimit = orderLimit;
	}
	public List<Commodity> getCommodities() {
		return commodities;
	}
	public void setCommodities(List<Commodity> commodities) {
		this.commodities = commodities;
	}
	public MarginRatioAlert getMarginRatioAlert() {
		return marginRatioAlert;
	}
	public void setMarginRatioAlert(MarginRatioAlert marginRatioAlert) {
		this.marginRatioAlert = marginRatioAlert;
	}
	public double getMarginMultiplier() {
		return marginMultiplier;
	}
	public void setMarginMultiplier(double marginMultiplier) {
		this.marginMultiplier = marginMultiplier;
	}
	public long getGeneralFee() {
		return generalFee;
	}
	public void setGeneralFee(long generalFee) {
		this.generalFee = generalFee;
	}
	public long getOtherFee() {
		return otherFee;
	}
	public void setOtherFee(long otherFee) {
		this.otherFee = otherFee;
	}
	public int getDefaultPositionLimit() {
		return defaultPositionLimit;
	}
	public void setDefaultPositionLimit(int defaultPositionLimit) {
		this.defaultPositionLimit = defaultPositionLimit;
	}
	public long getDefaultCommodityFee() {
		return defaultCommodityFee;
	}
	public void setDefaultCommodityFee(long defaultCommodityFee) {
		this.defaultCommodityFee = defaultCommodityFee;
	}
	public RiskParameters getRiskParameters() {
		return riskParameters;
	}
	public void setRiskParameters(RiskParameters riskParameters) {
		this.riskParameters = riskParameters;
	}
	public long getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(long createdDate) {
		this.createdDate = createdDate;
	}
	public CqgInfo getCqgInfo() {
		return cqgInfo;
	}
	public void setCqgInfo(CqgInfo cqgInfo) {
		this.cqgInfo = cqgInfo;
	}
    
}
