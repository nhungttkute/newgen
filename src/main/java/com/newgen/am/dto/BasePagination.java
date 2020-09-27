package com.newgen.am.dto;

import java.util.List;

import lombok.Data;

@Data
public class BasePagination<T> {
	private int count;
	private List<T> data;
}
