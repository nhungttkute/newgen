/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.io.Serializable;

import javax.validation.constraints.Positive;

/**
 *
 * @author nhungtt
 */
public class MarginRatioAlert implements Serializable {
	private static final long serialVersionUID = 1L;
	@Positive(message = "Invalid format.")
    private int warningRatio;
	@Positive(message = "Invalid format.")
    private int cancelOrderRatio;
	@Positive(message = "Invalid format.")
    private int finalizationRatio;

    public int getWarningRatio() {
        return warningRatio;
    }

    public void setWarningRatio(int warningRatio) {
        this.warningRatio = warningRatio;
    }

    public int getCancelOrderRatio() {
        return cancelOrderRatio;
    }

    public void setCancelOrderRatio(int cancelOrderRatio) {
        this.cancelOrderRatio = cancelOrderRatio;
    }

    public int getFinalizationRatio() {
        return finalizationRatio;
    }

    public void setFinalizationRatio(int finalizationRatio) {
        this.finalizationRatio = finalizationRatio;
    }

}
