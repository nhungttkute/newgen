/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.io.Serializable;

import javax.validation.constraints.Positive;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
@Data
public class MarginRatioAlert implements Serializable {
	private static final long serialVersionUID = 1L;
	@Positive(message = "Invalid format.")
    private int warningRatio;
	@Positive(message = "Invalid format.")
    private int cancelOrderRatio;
	@Positive(message = "Invalid format.")
    private int finalizationRatio;
}
