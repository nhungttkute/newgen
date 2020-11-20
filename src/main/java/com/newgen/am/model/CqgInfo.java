package com.newgen.am.model;

import java.io.Serializable;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newgen.am.validation.LengthGroup;
import com.newgen.am.validation.ValidNumberCharacter;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CqgInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	@ValidNumberCharacter
	@Size(max = 200, message = "Invalid format.", groups = LengthGroup.class)
	private String accountId;
	@ValidNumberCharacter
	@Size(max = 200, message = "Invalid format.", groups = LengthGroup.class)
	private String customerId;
	private String profileId;
	private String userId;
	private String balanceId;
}
