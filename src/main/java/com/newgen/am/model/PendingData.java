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
    private Object queryValue;
    private String queryField2;
    private Object queryValue2;
    private String action;
    private String value;

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

    public Object getQueryValue() {
        return queryValue;
    }

    public void setQueryValue(Object queryValue) {
        this.queryValue = queryValue;
    }

    public String getQueryField2() {
        return queryField2;
    }

    public void setQueryField2(String queryField2) {
        this.queryField2 = queryField2;
    }

    public Object getQueryValue2() {
        return queryValue2;
    }

    public void setQueryValue2(Object queryValue2) {
        this.queryValue2 = queryValue2;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}
