package com.newgen.am.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

import lombok.Data;

@Data
public class MemberCSV {
	@CsvBindByName(column = "Mã TVKD")
	@CsvBindByPosition(position = 0)
    private String code;
	@CsvBindByName(column = "Tên TVKD")
	@CsvBindByPosition(position = 1)
    private String name;
	@CsvBindByName(column = "Trạng thái")
	@CsvBindByPosition(position = 2)
    private String status;
	@CsvBindByName(column = "Ghi chú")
	@CsvBindByPosition(position = 3)
    private String note;
	@CsvBindByName(column = "Ngày tạo")
	@CsvBindByPosition(position = 4)
	private String createdDate;
}
