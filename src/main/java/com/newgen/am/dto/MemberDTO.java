package com.newgen.am.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.newgen.am.model.Commodity;
import com.newgen.am.model.CommodityFee;
import com.newgen.am.model.Company;
import com.newgen.am.model.Contact;
import com.newgen.am.model.CqgInfo;
import com.newgen.am.model.GeneralFee;
import com.newgen.am.model.MarginRatioAlert;
import com.newgen.am.model.MemberUser;
import com.newgen.am.model.RiskParameters;
import com.newgen.am.model.RoleFunction;
import com.newgen.am.model.UserRole;
import com.newgen.am.validation.FormatGroup;
import com.newgen.am.validation.LengthGroup;
import com.newgen.am.validation.UniqueGroup;
import com.newgen.am.validation.UniqueMemberCode;
import com.newgen.am.validation.ValidNumber;

import lombok.Data;

@Data
public class MemberDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	private String _id;
    @NotEmpty(message = "Required.")
    @ValidNumber(groups = FormatGroup.class)
    @Size(min = 3, max = 3, message = "Invalid format.", groups = LengthGroup.class)
	@UniqueMemberCode(groups = UniqueGroup.class)
    private String code;
    @NotEmpty(message = "Required.")
    @Size(min = 1, max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String name;
    @NotEmpty(message = "Required.")
    @Size(min = 1, max = 20, message = "Invalid format.", groups = LengthGroup.class)
    private String status;
    @Size(max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String note;
    private long createdDate;
    @NotNull(message = "Required.")
    @Valid
    private Company company;
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
