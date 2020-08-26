/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mongodb.lang.NonNull;

/**
 *
 * @author nhungtt
 */
@Document(collection = "members")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Member extends AuditModel implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
    private String id;
    @NonNull
    private String code;
    @NonNull
    private String name;
    private String note;
    private String status;
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

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

    public List<MemberUser> getUsers() {
        return users;
    }

    public void setUsers(List<MemberUser> users) {
        this.users = users;
    }

    public List<Commodity> getCommodities() {
		return commodities;
	}

	public void setCommodities(List<Commodity> commodities) {
		this.commodities = commodities;
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
