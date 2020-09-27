package com.newgen.am.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.newgen.am.model.RiskParameters;

import lombok.Data;

@Data
public class RiskParametersDTO {
	@NotNull(message = "Required.")
    @Valid
	private RiskParameters riskParameters;
}
