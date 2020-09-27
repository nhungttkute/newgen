package com.newgen.am.dto;

import java.util.List;

import javax.validation.Valid;

import com.newgen.am.model.Commodity;

import lombok.Data;

@Data
public class CommoditiesDTO {
    @Valid
	private List<Commodity> commodities;
}
