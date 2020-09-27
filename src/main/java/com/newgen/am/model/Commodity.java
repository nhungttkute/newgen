/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
@Data
public class Commodity implements Serializable {
	private static final long serialVersionUID = 1L;
	@NotEmpty(message = "Required.")
    private String commodityCode;
	@NotEmpty(message = "Required.")
    private String commodityName;
    private long commodityFee;
    private String positionLimitType;
    private int positionLimit;
    private String currency;
}
