package com.newgen.am.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.newgen.am.model.Commodity;

public class CommoditiesDTO {
	@NotNull(message = "Required.")
    @Valid
	private List<Commodity> commodities;

	public List<Commodity> getCommodities() {
		return commodities;
	}

	public void setCommodities(List<Commodity> commodities) {
		this.commodities = commodities;
	}
	
}
