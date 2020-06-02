package com.newgen.am.dto;

import java.io.Serializable;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import com.newgen.am.validation.LengthGroup;
import com.newgen.am.validation.ValidUpdateStringField;

public class UpdateInvestorDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	@ValidUpdateStringField
    @Size(max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String investorName;
	@Size(max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String note;
    @ValidUpdateStringField
    @Size(max = 20, message = "Invalid format.", groups = LengthGroup.class)
    private String status;
    @Valid
    private UpdateCompanyDTO company;
    @Valid
    private UpdateIndividualDTO individual;
    private int orderLimit;
    private int defaultPositionLimit;
    private long defaultCommodityFee;
    
	public String getInvestorName() {
		return investorName;
	}
	public void setInvestorName(String investorName) {
		this.investorName = investorName;
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
	public UpdateCompanyDTO getCompany() {
		return company;
	}
	public void setCompany(UpdateCompanyDTO company) {
		this.company = company;
	}
	public UpdateIndividualDTO getIndividual() {
		return individual;
	}
	public void setIndividual(UpdateIndividualDTO individual) {
		this.individual = individual;
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
}
