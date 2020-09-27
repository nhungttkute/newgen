package com.newgen.am.model;

import java.io.Serializable;

import com.newgen.am.validation.ValidUpdateStringField;

import lombok.Data;

@Data
public class RiskParameters implements Serializable {
	private static final long serialVersionUID = 1L;
	@ValidUpdateStringField
	private String newPositionOrderLock;
	@ValidUpdateStringField
	private String orderLock;
	@ValidUpdateStringField
	private String marginWithdrawalLock;
}
