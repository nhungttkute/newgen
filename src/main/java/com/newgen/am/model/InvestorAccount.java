/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author nhungtt
 */
@Data
public class InvestorAccount extends AuditModel {
	private static final long serialVersionUID = 1L;
    private String currency;
    private double marginSurplusInterestRate;
    private double marginDeficitInterestRate;
}
