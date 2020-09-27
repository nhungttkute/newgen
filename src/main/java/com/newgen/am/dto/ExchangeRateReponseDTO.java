package com.newgen.am.dto;

import java.util.List;

import lombok.Data;

@Data
public class ExchangeRateReponseDTO {
	private String status;
	private Pagination pagination;
	private List<ExchangeRateDTO> data;
}
