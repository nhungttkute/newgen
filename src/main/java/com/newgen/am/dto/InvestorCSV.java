package com.newgen.am.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

import lombok.Data;

@Data
public class InvestorCSV {
	@CsvBindByName(column = "Mã TVKD")
	@CsvBindByPosition(position = 0)
    private String memberCode;
	@CsvBindByName(column = "Tên TVKD")
	@CsvBindByPosition(position = 1)
    private String memberName;
	@CsvBindByName(column = "Mã MG")
	@CsvBindByPosition(position = 2)
    private String brokerCode;
	@CsvBindByName(column = "Tên MG")
	@CsvBindByPosition(position = 3)
    private String brokerName;
	@CsvBindByName(column = "Mã CTV")
	@CsvBindByPosition(position = 4)
    private String collaboratorCode;
	@CsvBindByName(column = "Tên CTV")
	@CsvBindByPosition(position = 5)
    private String collaboratorName;
	@CsvBindByName(column = "Mã TKGD")
	@CsvBindByPosition(position = 6)
    private String investorCode;
	@CsvBindByName(column = "Tên TKGD")
	@CsvBindByPosition(position = 7)
    private String investorName;
	@CsvBindByName(column = "Trạng thái")
	@CsvBindByPosition(position = 8)
    private String status;
	@CsvBindByName(column = "Ghi chú")
	@CsvBindByPosition(position = 9)
    private String note;
	@CsvBindByName(column = "Ngày tạo")
	@CsvBindByPosition(position = 10)
	private String createdDate;
}
