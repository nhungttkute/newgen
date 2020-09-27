/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
@Data
@Document(collection = "system_functions")
public class SystemFunction extends AuditModel implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
    private String id;
    private String code;
    private String name;
    private String orderNumber;
}
