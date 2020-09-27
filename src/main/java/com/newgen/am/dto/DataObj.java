/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newgen.am.model.WatchList;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author nhungtt
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DataObj implements Serializable {

    private LoginInvestorUserOutputDTO user;
    private String accessToken;
    private List<WatchList> watchLists;
    private String layout;
    private String language;
    private String theme;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int fontSize;
    private List<ListUserDTO> users;
    private AccountStatusDTO investorAccount;
}
