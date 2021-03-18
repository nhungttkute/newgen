package com.newgen.am.dto;

import lombok.Data;

@Data
public class NotificationDTO {
	private int type;
	private String investor;
	private String content;
	private String from;
	private int sendType;
}
