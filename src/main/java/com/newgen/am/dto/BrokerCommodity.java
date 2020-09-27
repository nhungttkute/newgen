package com.newgen.am.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class BrokerCommodity implements Serializable {
	private static final long serialVersionUID = 1L;
	@NotEmpty(message = "Required.")
    private String commodityCode;
	@NotEmpty(message = "Required.")
    private String commodityName;
	@Positive
    private long commodityFee;
}
