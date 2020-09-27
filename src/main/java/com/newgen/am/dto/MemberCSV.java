package com.newgen.am.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

import lombok.Data;

@Data
public class MemberCSV {
	@CsvBindByName(column = "ID")
	@CsvBindByPosition(position = 0)
    private String _id;
	@CsvBindByName(column = "CODE")
	@CsvBindByPosition(position = 1)
    private String code;
	@CsvBindByName(column = "NAME")
	@CsvBindByPosition(position = 2)
    private String name;
	@CsvBindByName(column = "STATUS")
	@CsvBindByPosition(position = 3)
    private String status;
	@CsvBindByName(column = "NOTE")
	@CsvBindByPosition(position = 4)
    private String note;
	@CsvBindByName(column = "CREATED_DATE")
	@CsvBindByPosition(position = 5)
	private String createdDate;
}
