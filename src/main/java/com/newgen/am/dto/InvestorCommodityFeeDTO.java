package com.newgen.am.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class InvestorCommodityFeeDTO {
	private String investorCode;
	private String commodityCode;
	private String brokerCommodityFee;
	private String memberCommodityFee;
}
