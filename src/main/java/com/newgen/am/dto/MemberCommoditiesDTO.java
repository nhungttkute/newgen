package com.newgen.am.dto;

import java.util.List;

import com.newgen.am.model.Commodity;

import lombok.Data;

@Data
public class MemberCommoditiesDTO {
	private List<Commodity> commodities;
}
