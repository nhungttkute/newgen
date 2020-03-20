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
}
