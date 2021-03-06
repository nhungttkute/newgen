/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
@Data
public class DepartmentCSV {
	@CsvBindByName(column = "ID")
	@CsvBindByPosition(position = 0)
    private String _id;
	@CsvBindByName(column = "CODE")
	@CsvBindByPosition(position = 0)
    private String code;
	@CsvBindByName(column = "NAME")
	@CsvBindByPosition(position = 1)
    private String name;
	@CsvBindByName(column = "STATUS")
	@CsvBindByPosition(position = 2)
    private String status;
	@CsvBindByName(column = "NOTE")
	@CsvBindByPosition(position = 3)
    private String note;
	@CsvBindByName(column = "CREATED_DATE")
	@CsvBindByPosition(position = 4)
	private String createdDate;
}
