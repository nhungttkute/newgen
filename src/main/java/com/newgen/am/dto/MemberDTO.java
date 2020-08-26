package com.newgen.am.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.newgen.am.model.Commodity;
import com.newgen.am.model.CommodityFee;
import com.newgen.am.model.Company;
import com.newgen.am.model.Contact;
import com.newgen.am.model.CqgInfo;
import com.newgen.am.model.GeneralFee;
import com.newgen.am.model.MarginRatioAlert;
import com.newgen.am.model.MemberUser;
import com.newgen.am.model.RiskParameters;
import com.newgen.am.model.RoleFunction;
import com.newgen.am.model.UserRole;
import com.newgen.am.validation.FormatGroup;
import com.newgen.am.validation.LengthGroup;
import com.newgen.am.validation.UniqueGroup;
import com.newgen.am.validation.UniqueMemberCode;
import com.newgen.am.validation.UniqueTaxCode;
import com.newgen.am.validation.ValidNumber;

public class MemberDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	private String _id;
    @NotEmpty(message = "Required.")
    @ValidNumber(groups = FormatGroup.class)
    @Size(min = 3, max = 3, message = "Invalid format.", groups = LengthGroup.class)
	@UniqueMemberCode(groups = UniqueGroup.class)
    private String code;
    @NotEmpty(message = "Required.")
    @Size(min = 1, max = 25, message = "Invalid format.", groups = LengthGroup.class)
    private String name;
    @NotEmpty(message = "Required.")
    @Size(min = 1, max = 20, message = "Invalid format.", groups = LengthGroup.class)
    private String status;
    @Size(max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String note;
    private long createdDate;
    @NotNull(message = "Required.")
    @Valid
    private Company company;
    private Contact contact;
    private UserRole role;
    private List<RoleFunction> functions;
    private int orderLimit;
    private int defaultPositionLimit;
    private long defaultCommodityFee;
    private double marginMultiplier;
    private List<GeneralFee> generalFees;
    private MarginRatioAlert marginRatioAlert;
    private RiskParameters riskParameters;
    private List<Commodity> commodities;
    private List<CommodityFee> commodityFees;
    private List<MemberUser> users;
    private CqgInfo cqgInfo;
    
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}
	public Contact getContact() {
		return contact;
	}
	public void setContact(Contact contact) {
		this.contact = contact;
	}
	public UserRole getRole() {
		return role;
	}
	public void setRole(UserRole role) {
		this.role = role;
	}
	public List<RoleFunction> getFunctions() {
		return functions;
	}
	public void setFunctions(List<RoleFunction> functions) {
		this.functions = functions;
	}
	public int getOrderLimit() {
		return orderLimit;
	}
	public void setOrderLimit(int orderLimit) {
		this.orderLimit = orderLimit;
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
	public List<Commodity> getCommodities() {
		return commodities;
	}
	public void setCommodities(List<Commodity> commodities) {
		this.commodities = commodities;
	}
	public RiskParameters getRiskParameters() {
		return riskParameters;
	}
	public void setRiskParameters(RiskParameters riskParameters) {
		this.riskParameters = riskParameters;
	}
	public double getMarginMultiplier() {
		return marginMultiplier;
	}
	public void setMarginMultiplier(double marginMultiplier) {
		this.marginMultiplier = marginMultiplier;
	}
	public List<GeneralFee> getGeneralFees() {
		return generalFees;
	}
	public void setGeneralFees(List<GeneralFee> generalFees) {
		this.generalFees = generalFees;
	}
	public MarginRatioAlert getMarginRatioAlert() {
		return marginRatioAlert;
	}
	public void setMarginRatioAlert(MarginRatioAlert marginRatioAlert) {
		this.marginRatioAlert = marginRatioAlert;
	}
	public long getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(long createdDate) {
		this.createdDate = createdDate;
	}
	public List<MemberUser> getUsers() {
		return users;
	}
	public void setUsers(List<MemberUser> users) {
		this.users = users;
	}
	public List<CommodityFee> getCommodityFees() {
		return commodityFees;
	}
	public void setCommodityFees(List<CommodityFee> commodityFees) {
		this.commodityFees = commodityFees;
	}
	public CqgInfo getCqgInfo() {
		return cqgInfo;
	}
	public void setCqgInfo(CqgInfo cqgInfo) {
		this.cqgInfo = cqgInfo;
	}
}
