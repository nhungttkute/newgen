/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author nhungtt
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AdminResponseObj implements Serializable {
	private static final long serialVersionUID = 1L;
	private String status;
    private String errMsg;
    private AdminDataObj data;
    private Pagination pagination;
    private List<String> filterList;
}
