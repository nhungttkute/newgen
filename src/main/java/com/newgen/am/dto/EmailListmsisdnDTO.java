package com.newgen.am.dto;

import java.util.List;

import lombok.Data;

@Data
public class EmailListmsisdnDTO {
	List<String> emails;
	List<String> admin;
	List<String> member;
	List<String> memberUser;
	List<String> broker;
	List<String> collaborator;
	List<String> investor;
	List<String> investorUser;
}
