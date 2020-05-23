package com.newgen.am.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.newgen.am.model.CommodityFee;

public class CommodityFeesDTO {
	@NotNull(message = "Required.")
    @Valid
    private List<CommodityFee> commodityFees;

	public List<CommodityFee> getCommodityFees() {
		return commodityFees;
	}

	public void setCommodityFees(List<CommodityFee> commodityFees) {
		this.commodityFees = commodityFees;
	}
	
}
