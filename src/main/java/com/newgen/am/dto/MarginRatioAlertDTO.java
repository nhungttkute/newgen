package com.newgen.am.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.newgen.am.model.MarginRatioAlert;

import lombok.Data;

@Data
public class MarginRatioAlertDTO {
	@NotNull(message = "Required.")
    @Valid
	private MarginRatioAlert marginRatioAlert;
}
