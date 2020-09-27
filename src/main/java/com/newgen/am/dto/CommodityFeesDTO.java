package com.newgen.am.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.newgen.am.model.CommodityFee;

import lombok.Data;

@Data
public class CommodityFeesDTO {
	@NotNull(message = "Required.")
    @Valid
    private List<CommodityFee> commodityFees;
}
