/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author nhungtt
 */
@Document(collection = "inv_margin_trans_approvals")
public class InvestorMarginTransApproval extends BaseApproval {
	private static final long serialVersionUID = 1L;
	@Id
    private String id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
