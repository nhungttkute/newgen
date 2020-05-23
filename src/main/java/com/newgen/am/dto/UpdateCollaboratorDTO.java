package com.newgen.am.dto;

import java.io.Serializable;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import com.newgen.am.validation.LengthGroup;
import com.newgen.am.validation.ValidUpdateStringField;

public class UpdateCollaboratorDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	@ValidUpdateStringField
    @Size(min = 1, max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String name;
    @Size(min = 1, max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String note;
    @ValidUpdateStringField
    @Size(min = 1, max = 20, message = "Invalid format.", groups = LengthGroup.class)
    private String status;
    @Valid
    private UpdateDelegateDTO delegate;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public UpdateDelegateDTO getDelegate() {
		return delegate;
	}
	public void setDelegate(UpdateDelegateDTO delegate) {
		this.delegate = delegate;
	}
}
