package com.newgen.am.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.newgen.am.model.BrokerCompany;
import com.newgen.am.model.BrokerIndividual;
import com.newgen.am.model.BrokerUser;
import com.newgen.am.model.Contact;
import com.newgen.am.model.RoleFunction;
import com.newgen.am.model.UserRole;
import com.newgen.am.validation.FormatGroup;
import com.newgen.am.validation.LengthGroup;
import com.newgen.am.validation.UniqueBrokerCode;
import com.newgen.am.validation.UniqueGroup;
import com.newgen.am.validation.ValidBrokerCode;
import com.newgen.am.validation.ValidNumber;

import lombok.Data;

@Data
public class BrokerDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String _id;
	@NotEmpty(message = "Required.")
    @ValidNumber(groups = FormatGroup.class)
    @Size(min = 3, max = 3, message = "Invalid format.", groups = LengthGroup.class)
    private String memberCode;
	@NotEmpty(message = "Required.")
    @Size(min = 1, max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String memberName;
	@NotEmpty(message = "Required.")
    @ValidBrokerCode(groups = FormatGroup.class)
    @UniqueBrokerCode(groups = UniqueGroup.class)
    private String code;
    @NotEmpty(message = "Required.")
    @Size(min = 1, max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String name;
    @Size(max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String note;
    @NotEmpty(message = "Required.")
    @Size(min = 1, max = 20, message = "Invalid format.", groups = LengthGroup.class)
    private String status;
    @NotEmpty(message = "Required.")
    @Size(min = 1, max = 20, message = "Invalid format.", groups = LengthGroup.class)
    private String type;
    private long createdDate;
    @Valid
    private BrokerCompany company;
    @Valid
    private BrokerIndividual individual;
    private Contact contact;
    private BrokerUser user;
    private UserRole role;
    private List<RoleFunction> functions;
    private long defaultCommodityFee;
    private List<BrokerCommodity> commodities;
}
