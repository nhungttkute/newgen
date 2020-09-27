/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
@Data
@Document(collection = "database_sequences")
public class DBSequence {
    @Id
    private String id;
    private long seq;

    public DBSequence(String id, long seq) {
        this.id = id;
        this.seq = seq;
    }
}
