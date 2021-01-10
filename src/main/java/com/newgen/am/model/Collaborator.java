/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
@Data
@Document(collection = "collaborators")
public class Collaborator extends AuditModel implements Serializable {
	private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private String memberCode;
    private String memberName;
    private String brokerCode;
    private String brokerName;
    private String code;
    private String name;
    private String note;
    private String status;
    private BrokerDelegate delegate;
    private Contact contact;
    private CollaboratorUser user;
    private UserRole role;
    private List<RoleFunction> functions;
}
