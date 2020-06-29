/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author nhungtt
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Commodity implements Serializable {
	private static final long serialVersionUID = 1L;
	@NotEmpty(message = "Required.")
    private String commodityCode;
	@NotEmpty(message = "Required.")
    private String commodityName;
    private long commodityFee;
    private String positionLimitType;
    private int positionLimit;
    private String currency;

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

    public long getCommodityFee() {
		return commodityFee;
	}

	public void setCommodityFee(long commodityFee) {
		this.commodityFee = commodityFee;
	}

	public int getPositionLimit() {
        return positionLimit;
    }

    public void setPositionLimit(int positionLimit) {
        this.positionLimit = positionLimit;
    }

    
    public String getPositionLimitType() {
		return positionLimitType;
	}

	public void setPositionLimitType(String positionLimitType) {
		this.positionLimitType = positionLimitType;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
}
