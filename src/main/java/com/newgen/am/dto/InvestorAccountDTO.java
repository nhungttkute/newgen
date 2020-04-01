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
    private double sodBalance; //So du TK dau ngay
    private double changedAmount; //Nop rut trong phien
    private double transactionFee; // Phi giao dich
    private double generalFee; // Thue/phi
    private double initialRequiredMargin; // Ky quy ban dau yeu cau
    private double availableMargin; // Ky quy kha dung
    private double actualProfitVND; // Lo lai thuc te
    private double estimatedProfitVND; // Lo lai du kien
    private double netMargin; // Gia tri rong ky quy
    private double currentBalance; // So du hien tai
    private double additionalMargin; //Muc bo sung ky quy

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

    public double getSodBalance() {
        return sodBalance;
    }

    public void setSodBalance(double sodBalance) {
        this.sodBalance = sodBalance;
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

    public double getNetMargin() {
        return netMargin;
    }

    public void setNetMargin(double netMargin) {
        this.netMargin = netMargin;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public double getAdditionalMargin() {
        return additionalMargin;
    }

    public void setAdditionalMargin(double additionalMargin) {
        this.additionalMargin = additionalMargin;
    }
    
}
