/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import com.newgen.am.model.WatchList;
import java.util.List;

/**
 *
 * @author nhungtt
 */
public class InvestorUserWatchListDTO {
    private List<WatchList> watchlists;

    public List<WatchList> getWatchlists() {
        return watchlists;
    }

    public void setWatchlists(List<WatchList> watchlists) {
        this.watchlists = watchlists;
    }
    
}
