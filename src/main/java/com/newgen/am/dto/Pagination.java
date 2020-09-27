package com.newgen.am.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Pagination implements Serializable{
	private static final long serialVersionUID = 1L;
	private int totalRows;
	private int totalPages;
}
