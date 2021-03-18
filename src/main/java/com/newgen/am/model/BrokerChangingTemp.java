package com.newgen.am.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "broker_changing_temp")
public class BrokerChangingTemp extends AuditModel implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
    private String id;
	private String investorCode;
	private String oldBrokerCode;
	private String oldBrokerName;
	private String newBrokerCode;
	private String newBrokerName;
	private String sessionDate;
}
