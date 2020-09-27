/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
@Data
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
}
