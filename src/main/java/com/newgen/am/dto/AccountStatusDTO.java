/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author nhungtt
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AccountStatusDTO {
	private String memberCode;
	private String memberName;
	private String brokerCode;
	private String brokerName;
	private String collaboratorCode;
	private String collaboratorName;
    private String investorName; // Ten TK
    private String investorCode; // So TK
    private long sodBalance; //So du TK dau ngay
    private long changedAmount; //Nop rut trong phien
    private long transactionFee; // Phi giao dich
    private long generalFee; // Thue/phi
    private long otherFee; // Phi khac
    private long initialRequiredMargin; // Ky quy ban dau yeu cau
    private long availableMargin; // Ky quy kha dung
    private long actualProfitVND; // Lo lai thuc te
    private long estimatedProfitVND; // Lo lai du kien
    private long netMargin; // Gia tri rong ky quy
    private long currentBalance; // So du hien tai
    private long additionalMargin; //Muc bo sung ky quy

    public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getBrokerCode() {
		return brokerCode;
	}

	public void setBrokerCode(String brokerCode) {
		this.brokerCode = brokerCode;
	}

	public String getBrokerName() {
		return brokerName;
	}

	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
	}

	public String getCollaboratorCode() {
		return collaboratorCode;
	}

	public void setCollaboratorCode(String collaboratorCode) {
		this.collaboratorCode = collaboratorCode;
	}

	public String getCollaboratorName() {
		return collaboratorName;
	}

	public void setCollaboratorName(String collaboratorName) {
		this.collaboratorName = collaboratorName;
	}

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

    public long getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(long transactionFee) {
        this.transactionFee = transactionFee;
    }

    public long getGeneralFee() {
        return generalFee;
    }

    public void setGeneralFee(long generalFee) {
        this.generalFee = generalFee;
    }

    public long getInitialRequiredMargin() {
        return initialRequiredMargin;
    }

    public void setInitialRequiredMargin(long initialRequiredMargin) {
        this.initialRequiredMargin = initialRequiredMargin;
    }

    public long getAvailableMargin() {
        return availableMargin;
    }

    public void setAvailableMargin(long availableMargin) {
        this.availableMargin = availableMargin;
    }

    public long getActualProfitVND() {
        return actualProfitVND;
    }

    public void setActualProfitVND(long actualProfitVND) {
        this.actualProfitVND = actualProfitVND;
    }

    public long getEstimatedProfitVND() {
        return estimatedProfitVND;
    }

    public void setEstimatedProfitVND(long estimatedProfitVND) {
        this.estimatedProfitVND = estimatedProfitVND;
    }

    public long getNetMargin() {
        return netMargin;
    }

    public void setNetMargin(long netMargin) {
        this.netMargin = netMargin;
    }

    public long getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(long currentBalance) {
        this.currentBalance = currentBalance;
    }

    public long getAdditionalMargin() {
        return additionalMargin;
    }

    public void setAdditionalMargin(long additionalMargin) {
        this.additionalMargin = additionalMargin;
    }

	public long getOtherFee() {
		return otherFee;
	}

	public void setOtherFee(long otherFee) {
		this.otherFee = otherFee;
	}
    
}
