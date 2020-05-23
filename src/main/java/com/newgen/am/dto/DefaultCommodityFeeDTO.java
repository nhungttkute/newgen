package com.newgen.am.dto;

import javax.validation.constraints.Positive;

public class DefaultCommodityFeeDTO {
	@Positive
	private long defaultCommodityFee;

	public long getDefaultCommodityFee() {
		return defaultCommodityFee;
	}

	public void setDefaultCommodityFee(long defaultCommodityFee) {
		this.defaultCommodityFee = defaultCommodityFee;
	}
	
}
