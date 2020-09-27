/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
@Data
public class ListUserDTO {
    private String id;
    private String username;

    public ListUserDTO() {}
    
    public ListUserDTO (String id, String username) {
        this.id = id;
        this.username = username;
    }
}
