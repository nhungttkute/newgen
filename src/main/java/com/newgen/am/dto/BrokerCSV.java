package com.newgen.am.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

import lombok.Data;

@Data
public class BrokerCSV {
	@CsvBindByName(column = "Mã TVKD")
	@CsvBindByPosition(position = 0)
    private String memberCode;
	@CsvBindByName(column = "Tên TVKD")
	@CsvBindByPosition(position = 1)
    private String memberName;
	@CsvBindByName(column = "Mã Môi giới")
	@CsvBindByPosition(position = 2)
    private String code;
	@CsvBindByName(column = "Tên Môi giới")
	@CsvBindByPosition(position = 3)
    private String name;
	@CsvBindByName(column = "Trạng thái")
	@CsvBindByPosition(position = 4)
    private String status;
	@CsvBindByName(column = "Ghi chú")
	@CsvBindByPosition(position = 5)
    private String note;
	@CsvBindByName(column = "Ngày tạo")
	@CsvBindByPosition(position = 6)
	private String createdDate;
}
