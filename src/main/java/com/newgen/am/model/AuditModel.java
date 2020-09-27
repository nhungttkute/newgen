package com.newgen.am.model;

import java.io.Serializable;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;

@Data
public abstract class AuditModel implements Serializable {
	private static final long serialVersionUID = 1L;

	@CreatedBy
    private String createdUser;

    @CreatedDate
    private Long createdDate;

    @LastModifiedBy
    private String lastModifiedUser;

    @LastModifiedDate
    private Long lastModifiedDate;
}
