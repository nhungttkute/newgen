/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 *
 * @author nhungtt
 */
@Document(collection = "collaborators")
public class Collaborator extends AuditModel implements Serializable {
    @Transient
    public static final String SEQUENCE_NAME = "collaborator_seq";
    @Id
    private Long id;
    @Field(name = "member_id")
    private Long memberId;
    @Field(name = "broker_id")
    private Long brokerId;
    @Field(name = "member_code")
    private String memberCode;
    @Field(name = "member_name")
    private String memberName;
    @Field(name = "broker_code")
    private String brokerCode;
    @Field(name = "broker_name")
    private String brokerName;
    private String code;
    private String name;
    private String note;
    private String status;
    @Field(name = "individual_info")
    private Individual individualInfo;
    private Contact contact;
    private CollaboratorUser user;
    private List<UserRole> roles;
    private List<RoleFunction> functions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(Long brokerId) {
        this.brokerId = brokerId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getMemberCode() {
        return memberCode;
    }

    public void setMemberCode(String memberCode) {
        this.memberCode = memberCode;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getBrokerCode() {
        return brokerCode;
    }

    public void setBrokerCode(String brokerCode) {
        this.brokerCode = brokerCode;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public Individual getIndividualInfo() {
        return individualInfo;
    }

    public void setIndividualInfo(Individual individualInfo) {
        this.individualInfo = individualInfo;
    }

    public CollaboratorUser getUser() {
        return user;
    }

    public void setUser(CollaboratorUser user) {
        this.user = user;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }

    public List<RoleFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(List<RoleFunction> functions) {
        this.functions = functions;
    }

}
