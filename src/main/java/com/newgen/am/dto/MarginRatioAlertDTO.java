package com.newgen.am.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.newgen.am.model.MarginRatioAlert;

public class MarginRatioAlertDTO {
	@NotNull(message = "Required.")
    @Valid
	private MarginRatioAlert marginRatioAlert;

	public MarginRatioAlert getMarginRatioAlert() {
		return marginRatioAlert;
	}

	public void setMarginRatioAlert(MarginRatioAlert marginRatioAlert) {
		this.marginRatioAlert = marginRatioAlert;
	}
	
}
