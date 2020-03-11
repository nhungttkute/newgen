/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

/**
 *
 * @author nhungtt
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AMResponseObj implements Serializable {
    private String status;
    private String errMsg;
    private DataObj data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public DataObj getData() {
        return data;
    }

    public void setData(DataObj data) {
        this.data = data;
    }
}
