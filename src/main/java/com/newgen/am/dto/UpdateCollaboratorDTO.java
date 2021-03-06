package com.newgen.am.dto;

import java.io.Serializable;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import com.newgen.am.validation.LengthGroup;
import com.newgen.am.validation.ValidUpdateStringField;

import lombok.Data;

@Data
public class UpdateCollaboratorDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	@ValidUpdateStringField
    @Size(max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String name;
    @Size(max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String note;
    @ValidUpdateStringField
    @Size(max = 20, message = "Invalid format.", groups = LengthGroup.class)
    private String status;
    @Valid
    private UpdateDelegateDTO delegate;
}
