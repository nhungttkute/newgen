package com.newgen.am.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.newgen.am.model.RiskParameters;

public class RiskParametersDTO {
	@NotNull(message = "Required.")
    @Valid
	private RiskParameters riskParameters;

	public RiskParameters getRiskParameters() {
		return riskParameters;
	}

	public void setRiskParameters(RiskParameters riskParameters) {
		this.riskParameters = riskParameters;
	}
}
