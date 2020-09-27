/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import java.io.Serializable;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
@Data
public class WatchList implements Serializable {
    private String name;
    private String[] contracts;
}
