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
	private String code;
    private String currency;
    private double marginSurplusInterestRate;
    private double marginDeficitInterestRate;
    private double amountHold;
    private double sodBalance; // So du TKKQ dau ngay
    private double currentBalance; // So du TKKQ hien ta
    private double dayEndBalance; // So du TKKQ cuoi ngay
    private double changedAmount; // Nop rut trong phien
    private double transactionFee; // Phi giao dich
    private double generalFee; // Thue/phi
    private double otherFee; // Phi khac
    private double actualProfitVND; // Lo lai thuc te
    private double estimatedProfitVND; // Lo lai du kien
    private double initialRequiredMargin; // Ky quy ban dau yeu cau
    private double availableMargin; // Ky quy kha dung
    private double netMargin; // Gia tri rong ky quy
    private double temporaryInterest; // Lai phai thu/phai tra tam tinh
    private double additionalMargin; //Muc bo sung ky quy
    private double currentMarginRatio; //Ty le ky quy hien tai
    private long createdAt;
    private long updatedAt;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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

    public double getAmountHold() {
        return amountHold;
    }

    public void setAmountHold(double amountHold) {
        this.amountHold = amountHold;
    }

    public double getSodBalance() {
        return sodBalance;
    }

    public void setSodBalance(double sodBalance) {
        this.sodBalance = sodBalance;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public double getDayEndBalance() {
        return dayEndBalance;
    }

    public void setDayEndBalance(double dayEndBalance) {
        this.dayEndBalance = dayEndBalance;
    }

    public double getChangedAmount() {
        return changedAmount;
    }

    public void setChangedAmount(double changedAmount) {
        this.changedAmount = changedAmount;
    }

    public double getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(double transactionFee) {
        this.transactionFee = transactionFee;
    }

    public double getGeneralFee() {
        return generalFee;
    }

    public void setGeneralFee(double generalFee) {
        this.generalFee = generalFee;
    }

    public double getOtherFee() {
        return otherFee;
    }

    public void setOtherFee(double otherFee) {
        this.otherFee = otherFee;
    }

    public double getActualProfitVND() {
        return actualProfitVND;
    }

    public void setActualProfitVND(double actualProfitVND) {
        this.actualProfitVND = actualProfitVND;
    }

    public double getEstimatedProfitVND() {
        return estimatedProfitVND;
    }

    public void setEstimatedProfitVND(double estimatedProfitVND) {
        this.estimatedProfitVND = estimatedProfitVND;
    }

    public double getInitialRequiredMargin() {
        return initialRequiredMargin;
    }

    public void setInitialRequiredMargin(double initialRequiredMargin) {
        this.initialRequiredMargin = initialRequiredMargin;
    }

    public double getAvailableMargin() {
        return availableMargin;
    }

    public void setAvailableMargin(double availableMargin) {
        this.availableMargin = availableMargin;
    }

    public double getNetMargin() {
        return netMargin;
    }

    public void setNetMargin(double netMargin) {
        this.netMargin = netMargin;
    }

    public double getTemporaryInterest() {
        return temporaryInterest;
    }

    public void setTemporaryInterest(double temporaryInterest) {
        this.temporaryInterest = temporaryInterest;
    }

    public double getAdditionalMargin() {
        return additionalMargin;
    }

    public void setAdditionalMargin(double additionalMargin) {
        this.additionalMargin = additionalMargin;
    }

    public double getCurrentMarginRatio() {
        return currentMarginRatio;
    }

    public void setCurrentMarginRatio(double currentMarginRatio) {
        this.currentMarginRatio = currentMarginRatio;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

}
