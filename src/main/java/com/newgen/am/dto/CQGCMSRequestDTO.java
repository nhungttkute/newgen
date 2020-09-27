package com.newgen.am.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class CQGCMSRequestDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String email;
	private String phone;
	private String fax;
	private String fullName;
	private String address;
	private String username;
	private String customerId;
	private String profileId;
	private String number;
	private String accountId;
	private String currency;
	private double changedAmount;
	private String saleSeriesId;
	private List<CQGCMSCommodityDTO> commodities;
	private List<CQGCMSAccountAuthDTO> linksToSet;
	private double marginMultiplier;
	private long tradeSizeLimit;
	private long defaultPositionLimit;
}
