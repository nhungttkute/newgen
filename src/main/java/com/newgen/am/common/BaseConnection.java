/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.common;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

/**
 *
 * @author nhungtt
 */
public class BaseConnection {
    private final HttpClient httpClient;
    private final PutMethod putMethod;
    private final PostMethod postMethod;
    private final GetMethod getMethod;
    private final DeleteMethod deleteMethod;
    
    public BaseConnection(int connTimeout, int soTimeout) {
        httpClient = new HttpClient();
        // connection timeout
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(connTimeout);
        // read timeout
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(soTimeout);
        putMethod = new PutMethod();
        postMethod = new PostMethod();
        getMethod = new GetMethod();
        deleteMethod = new DeleteMethod();
    }
    
    public String[] sendPostRequest(String requestUrl, String requestBody) throws Exception {
        String[] responseBody = new String[2];
        try {
            StringRequestEntity entity = new StringRequestEntity(requestBody, "application/json", "UTF-8");
            postMethod.setURI(new URI(requestUrl));
            postMethod.setRequestEntity(entity);
            postMethod.addRequestHeader("Content-Type", "application/json;charset=UTF-8");
            httpClient.executeMethod(postMethod);
            responseBody[0] = String.valueOf(postMethod.getStatusCode());
            responseBody[1] = postMethod.getResponseBodyAsString();
        } catch (Exception e) {
            throw e;
        } finally {
            postMethod.releaseConnection();
        }
        return responseBody;
    }
    
    public String[] sendGetRequest(String requestUrl, String accessToken) throws Exception {
        String[] responseBody = new String[2];
        try {
            getMethod.setURI(new URI(requestUrl));
            if (Utility.isNotNull(accessToken)) {
            	getMethod.setRequestHeader("Authorization", "Bearer " + accessToken);
            }
            
            httpClient.executeMethod(getMethod);
            responseBody[0] = String.valueOf(getMethod.getStatusCode());
            responseBody[1] = getMethod.getResponseBodyAsString();
        } catch (Exception e) {
            throw e;
        } finally {
            getMethod.releaseConnection();
        }
        return responseBody;
    }
    
    public String[] sendPutRequest(String requestUrl, String requestBody) throws Exception {
        String[] responseBody = new String[2];
        try {
            StringRequestEntity entity = new StringRequestEntity(requestBody, "application/json", "UTF-8");
            putMethod.setURI(new URI(requestUrl));
            putMethod.setRequestEntity(entity);
            httpClient.executeMethod(putMethod);
            responseBody[0] = String.valueOf(putMethod.getStatusCode());
            responseBody[1] = putMethod.getResponseBodyAsString();
        } catch (Exception e) {
            throw e;
        } finally {
            putMethod.releaseConnection();
        }
        return responseBody;
    }
}
