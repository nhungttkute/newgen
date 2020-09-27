/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
@Data
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
}
