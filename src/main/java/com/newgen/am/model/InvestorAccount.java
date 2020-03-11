/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.util.Date;

/**
 *
 * @author nhungtt
 */
public class InvestorAccount {
    private String code;
    private String currency;
    private Double marginSurplusInterestRate;
    private Double marginDeficitInterestRate;
    private Double amountHold;
    private Double dayBeginBalance; // So du TKKQ dau ngay
    private Double currentBalance; // So du TKKQ hien ta
    private Double dayEndBalance; // So du TKKQ cuoi ngay
    private Double changedAmount; // Nop rut trong phien
    private Double transactionTotalFee; // Phi giao dich
    private Double generalFee; // Thue/phi
    private Double otherFee; // Phi khac
    private Double actualProfitOrLoss; // Lo lai thuc te
    private Double expectedProfitOrLoss; // Lo lai du kien
    private Double initialRequiredMargin; // Ky quy ban dau yeu cau
    private Double availableMargin; // Ky quy kha dung
    private Double netMargin; // Gia tri rong ky quy
    private Double temporaryInterest; // Lai phai thu/phai tra tam tinh
    private Double additionalMargin; //Muc bo sung ky quy
    private Double currentMarginRatio; //Ty le ky quy hien tai
    private Date createdAt;
    private Date updatedAt;

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

    public Double getMarginSurplusInterestRate() {
        return marginSurplusInterestRate;
    }

    public void setMarginSurplusInterestRate(Double marginSurplusInterestRate) {
        this.marginSurplusInterestRate = marginSurplusInterestRate;
    }

    public Double getMarginDeficitInterestRate() {
        return marginDeficitInterestRate;
    }

    public void setMarginDeficitInterestRate(Double marginDeficitInterestRate) {
        this.marginDeficitInterestRate = marginDeficitInterestRate;
    }

    public Double getAmountHold() {
        return amountHold;
    }

    public void setAmountHold(Double amountHold) {
        this.amountHold = amountHold;
    }

    public Double getDayBeginBalance() {
        return dayBeginBalance;
    }

    public void setDayBeginBalance(Double dayBeginBalance) {
        this.dayBeginBalance = dayBeginBalance;
    }

    public Double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public Double getDayEndBalance() {
        return dayEndBalance;
    }

    public void setDayEndBalance(Double dayEndBalance) {
        this.dayEndBalance = dayEndBalance;
    }

    public Double getChangedAmount() {
        return changedAmount;
    }

    public void setChangedAmount(Double changedAmount) {
        this.changedAmount = changedAmount;
    }

    public Double getTransactionTotalFee() {
        return transactionTotalFee;
    }

    public void setTransactionTotalFee(Double transactionTotalFee) {
        this.transactionTotalFee = transactionTotalFee;
    }

    public Double getGeneralFee() {
        return generalFee;
    }

    public void setGeneralFee(Double generalFee) {
        this.generalFee = generalFee;
    }

    public Double getOtherFee() {
        return otherFee;
    }

    public void setOtherFee(Double otherFee) {
        this.otherFee = otherFee;
    }

    public Double getActualProfitOrLoss() {
        return actualProfitOrLoss;
    }

    public void setActualProfitOrLoss(Double actualProfitOrLoss) {
        this.actualProfitOrLoss = actualProfitOrLoss;
    }

    public Double getExpectedProfitOrLoss() {
        return expectedProfitOrLoss;
    }

    public void setExpectedProfitOrLoss(Double expectedProfitOrLoss) {
        this.expectedProfitOrLoss = expectedProfitOrLoss;
    }

    public Double getInitialRequiredMargin() {
        return initialRequiredMargin;
    }

    public void setInitialRequiredMargin(Double initialRequiredMargin) {
        this.initialRequiredMargin = initialRequiredMargin;
    }

    public Double getAvailableMargin() {
        return availableMargin;
    }

    public void setAvailableMargin(Double availableMargin) {
        this.availableMargin = availableMargin;
    }

    public Double getNetMargin() {
        return netMargin;
    }

    public void setNetMargin(Double netMargin) {
        this.netMargin = netMargin;
    }

    public Double getTemporaryInterest() {
        return temporaryInterest;
    }

    public void setTemporaryInterest(Double temporaryInterest) {
        this.temporaryInterest = temporaryInterest;
    }

    public Double getAdditionalMargin() {
        return additionalMargin;
    }

    public void setAdditionalMargin(Double additionalMargin) {
        this.additionalMargin = additionalMargin;
    }

    public Double getCurrentMarginRatio() {
        return currentMarginRatio;
    }

    public void setCurrentMarginRatio(Double currentMarginRatio) {
        this.currentMarginRatio = currentMarginRatio;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
}
