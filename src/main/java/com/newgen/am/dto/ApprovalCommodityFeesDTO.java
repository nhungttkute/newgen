package com.newgen.am.dto;

import javax.validation.Valid;

import lombok.Data;

@Data
public class ApprovalCommodityFeesDTO {
	private CommodityFeesDTO oldData;
	@Valid
	private CommodityFeesDTO pendingData;
}
