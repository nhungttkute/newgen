/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

/**
 *
 * @author nhungtt
 */
public class MarginRatioAlert {
    private int warningRatio;
    private int cancelOrderRatio;
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
