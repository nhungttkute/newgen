/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

/**
 *
 * @author nhungtt
 */
public class InvestorAccountDTO {
    private String investorName; // Ten TK
    private String investorCode; // So TK
    private Double sodBalance; //So du TK dau ngay
    private Double changedAmount; //Nop rut trong phien
    private Double transactionFee; // Phi giao dich
    private Double generalFee; // Thue/phi
    private Double initialRequiredMargin; // Ky quy ban dau yeu cau
    private Double availableMargin; // Ky quy kha dung
    private Double actualProfitVND; // Lo lai thuc te
    private Double estimatedProfitVND; // Lo lai du kien
    private Double netMargin; // Gia tri rong ky quy
    private Double currentBalance; // So du hien tai
    private Double additionalMargin; //Muc bo sung ky quy

    public String getInvestorName() {
        return investorName;
    }

    public void setInvestorName(String investorName) {
        this.investorName = investorName;
    }

    public String getInvestorCode() {
        return investorCode;
    }

    public void setInvestorCode(String investorCode) {
        this.investorCode = investorCode;
    }

    public Double getSodBalance() {
        return sodBalance;
    }

    public void setSodBalance(Double sodBalance) {
        this.sodBalance = sodBalance;
    }

    public Double getChangedAmount() {
        return changedAmount;
    }

    public void setChangedAmount(Double changedAmount) {
        this.changedAmount = changedAmount;
    }

    public Double getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(Double transactionFee) {
        this.transactionFee = transactionFee;
    }

    public Double getGeneralFee() {
        return generalFee;
    }

    public void setGeneralFee(Double generalFee) {
        this.generalFee = generalFee;
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

    public Double getActualProfitVND() {
        return actualProfitVND;
    }

    public void setActualProfitVND(Double actualProfitVND) {
        this.actualProfitVND = actualProfitVND;
    }

    public Double getEstimatedProfitVND() {
        return estimatedProfitVND;
    }

    public void setEstimatedProfitVND(Double estimatedProfitVND) {
        this.estimatedProfitVND = estimatedProfitVND;
    }

    public Double getNetMargin() {
        return netMargin;
    }

    public void setNetMargin(Double netMargin) {
        this.netMargin = netMargin;
    }

    public Double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public Double getAdditionalMargin() {
        return additionalMargin;
    }

    public void setAdditionalMargin(Double additionalMargin) {
        this.additionalMargin = additionalMargin;
    }
    
}
