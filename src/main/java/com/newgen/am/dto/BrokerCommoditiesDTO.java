package com.newgen.am.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class BrokerCommoditiesDTO {
	@NotNull(message = "Required.")
    @Valid
	private List<BrokerCommodity> commodities;

	public List<BrokerCommodity> getCommodities() {
		return commodities;
	}

	public void setCommodities(List<BrokerCommodity> commodities) {
		this.commodities = commodities;
	}
}
