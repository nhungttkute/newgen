/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mongodb.lang.NonNull;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
@Data
@Document(collection = "members")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Member extends AuditModel implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
    private String id;
    @NonNull
    private String code;
    @NonNull
    private String name;
    private String note;
    private String status;
    private MemberCompany company;
    private Contact contact;
    private UserRole role;
    private List<RoleFunction> functions;
    private int orderLimit;
    private int defaultPositionLimit;
    private long defaultCommodityFee;
    private double marginMultiplier;
    private List<GeneralFee> generalFees;
    private MarginRatioAlert marginRatioAlert;
    private RiskParameters riskParameters;
    private List<Commodity> commodities;
    private List<CommodityFee> commodityFees;
    private List<MemberUser> users;
    private CqgInfo cqgInfo;
}
