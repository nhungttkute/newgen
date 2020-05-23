package com.newgen.am.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class BrokerCommodity implements Serializable {

	private static final long serialVersionUID = 1L;
	@NotEmpty(message = "Required.")
    private String commodityCode;
	@NotEmpty(message = "Required.")
    private String commodityName;
	@Positive
    private long commodityFee;
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
}
