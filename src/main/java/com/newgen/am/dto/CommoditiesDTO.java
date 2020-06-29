package com.newgen.am.dto;

import java.util.List;

import javax.validation.Valid;

import com.newgen.am.model.Commodity;

public class CommoditiesDTO {
    @Valid
	private List<Commodity> commodities;

	public List<Commodity> getCommodities() {
		return commodities;
	}

	public void setCommodities(List<Commodity> commodities) {
		this.commodities = commodities;
	}
	
}
