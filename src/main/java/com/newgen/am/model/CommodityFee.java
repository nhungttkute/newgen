/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

import com.newgen.am.common.Constant;

/**
 *
 * @author nhungtt
 */
public class CommodityFee extends AuditModel implements Serializable {
	private static final long serialVersionUID = 1L;
	@NotEmpty(message = "Required.")
    private String commodityCode;
	@NotEmpty(message = "Required.")
    private String commodityName;
    private long brokerCommodityFee;
    private long investorCommodityFee;
    private String currency = Constant.CURRENCY_VND;

    public String getCommodityCode() {
		return commodityCode;
	}

	public void setCommodityCode(String commodityCode) {
		this.commodityCode = commodityCode;
	}

	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public long getBrokerCommodityFee() {
		return brokerCommodityFee;
	}

	public void setBrokerCommodityFee(long brokerCommodityFee) {
		this.brokerCommodityFee = brokerCommodityFee;
	}

	public long getInvestorCommodityFee() {
		return investorCommodityFee;
	}

	public void setInvestorCommodityFee(long investorCommodityFee) {
		this.investorCommodityFee = investorCommodityFee;
	}
}
