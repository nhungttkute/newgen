/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.io.Serializable;

/**
 *
 * @author nhungtt
 */
public class Commodity extends AuditModel implements Serializable {
    private String code;
    private String name;
    private String partner;
    private String partnerAccount;
    private long orderProcessFee;
    private int positionLimit;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getPartnerAccount() {
        return partnerAccount;
    }

    public void setPartnerAccount(String partnerAccount) {
        this.partnerAccount = partnerAccount;
    }

    public long getOrderProcessFee() {
        return orderProcessFee;
    }

    public void setOrderProcessFee(long orderProcessFee) {
        this.orderProcessFee = orderProcessFee;
    }

    public int getPositionLimit() {
        return positionLimit;
    }

    public void setPositionLimit(int positionLimit) {
        this.positionLimit = positionLimit;
    }

    
    @Override
    public String toString() {
        return "Commodity{" +
                "code='" + code + "'" +
                ", name='" + name + "'" +
                ", partner='" + partner + "'" +
                ", partnerAccount='" + partnerAccount + "'" +
                ", positionLimit='" + positionLimit + "'" +
                ", orderProcessFee='" + orderProcessFee + "'}";
    }
}
