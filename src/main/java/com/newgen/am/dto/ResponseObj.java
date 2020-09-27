/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author nhungtt
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseObj implements Serializable {
    private String status;
    private String errMsg;
    private DataObj data;
}
