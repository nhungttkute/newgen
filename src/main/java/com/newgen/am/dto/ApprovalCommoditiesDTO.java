package com.newgen.am.dto;

import javax.validation.Valid;

import lombok.Data;

@Data
public class ApprovalCommoditiesDTO {
	private String type;
	private CommoditiesDTO oldData;
	@Valid
	private CommoditiesDTO pendingData;
}
