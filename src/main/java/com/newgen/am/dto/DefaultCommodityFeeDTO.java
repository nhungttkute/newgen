package com.newgen.am.dto;

import javax.validation.constraints.Positive;

import lombok.Data;

@Data
public class DefaultCommodityFeeDTO {
	@Positive
	private long defaultCommodityFee;
}
