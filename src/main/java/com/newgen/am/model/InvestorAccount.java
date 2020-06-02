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
public class InvestorAccount extends AuditModel {
	private static final long serialVersionUID = 1L;
    private String currency;
    private double marginSurplusInterestRate;
    private double marginDeficitInterestRate;
    private long sodBalance; // So du TKKQ dau ngay
    private long changedAmount; // Nop rut trong phien
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public double getMarginSurplusInterestRate() {
		return marginSurplusInterestRate;
	}
	public void setMarginSurplusInterestRate(double marginSurplusInterestRate) {
		this.marginSurplusInterestRate = marginSurplusInterestRate;
	}
	public double getMarginDeficitInterestRate() {
		return marginDeficitInterestRate;
	}
	public void setMarginDeficitInterestRate(double marginDeficitInterestRate) {
		this.marginDeficitInterestRate = marginDeficitInterestRate;
	}
	public long getSodBalance() {
		return sodBalance;
	}
	public void setSodBalance(long sodBalance) {
		this.sodBalance = sodBalance;
	}
	public long getChangedAmount() {
		return changedAmount;
	}
	public void setChangedAmount(long changedAmount) {
		this.changedAmount = changedAmount;
	}

}
