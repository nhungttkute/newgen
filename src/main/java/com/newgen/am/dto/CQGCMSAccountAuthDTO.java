package com.newgen.am.dto;

import lombok.Data;

@Data
public class CQGCMSAccountAuthDTO {
	private long accountId;
	private String userId;
	private boolean isViewOnly;
}
