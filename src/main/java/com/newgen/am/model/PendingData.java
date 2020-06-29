/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

/**
 *
 * @author nhungtt
 */
public class PendingData {
    private String serviceFunctionName;
    private String collectionName;
    private String queryField;
    private String queryValue;
    private String queryField2;
    private String queryValue2;
    private String action;
    private String appliedObject;
    private String oldValue;
    private String pendingValue;

    public String getServiceFunctionName() {
        return serviceFunctionName;
    }

    public void setServiceFunctionName(String serviceFunctionName) {
        this.serviceFunctionName = serviceFunctionName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getQueryField() {
        return queryField;
    }

    public void setQueryField(String queryField) {
        this.queryField = queryField;
    }

    public String getQueryValue() {
        return queryValue;
    }

    public void setQueryValue(String queryValue) {
        this.queryValue = queryValue;
    }

    public String getQueryField2() {
        return queryField2;
    }

    public void setQueryField2(String queryField2) {
        this.queryField2 = queryField2;
    }

    public String getQueryValue2() {
        return queryValue2;
    }

    public void setQueryValue2(String queryValue2) {
        this.queryValue2 = queryValue2;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getPendingValue() {
		return pendingValue;
	}

	public void setPendingValue(String pendingValue) {
		this.pendingValue = pendingValue;
	}

	public String getAppliedObject() {
		return appliedObject;
	}

	public void setAppliedObject(String appliedObject) {
		this.appliedObject = appliedObject;
	}
  
}
