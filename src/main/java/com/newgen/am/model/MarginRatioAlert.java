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
    private Integer warningRatio;
    private Integer cancelOrderRatio;
    private Integer finalizationRatio;

    public Integer getWarningRatio() {
        return warningRatio;
    }

    public void setWarningRatio(Integer warningRatio) {
        this.warningRatio = warningRatio;
    }

    public Integer getCancelOrderRatio() {
        return cancelOrderRatio;
    }

    public void setCancelOrderRatio(Integer cancelOrderRatio) {
        this.cancelOrderRatio = cancelOrderRatio;
    }

    public Integer getFinalizationRatio() {
        return finalizationRatio;
    }

    public void setFinalizationRatio(Integer finalizationRatio) {
        this.finalizationRatio = finalizationRatio;
    }
    
}
