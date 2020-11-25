package com.newgen.am.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

import lombok.Data;

@Data
public class MarginTransCSV {
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
	@CsvBindByName(column = "Mã cộng tác viên")
	@CsvBindByPosition(position = 4)
    private String collaboratorCode;
	@CsvBindByName(column = "Tên cộng tác viên")
	@CsvBindByPosition(position = 5)
    private String collaboratorName;
	@CsvBindByName(column = "Mã TKGD")
	@CsvBindByPosition(position = 6)
    private String investorCode;
	@CsvBindByName(column = "Tên TKGD")
	@CsvBindByPosition(position = 7)
    private String investorName;
	@CsvBindByName(column = "Loại GD")
	@CsvBindByPosition(position = 8)
	private String transactionType;
	@CsvBindByName(column = "Số tiền nộp/rút")
	@CsvBindByPosition(position = 9)
	private long amount;
	@CsvBindByName(column = "Tiền tệ")
	@CsvBindByPosition(position = 10)
	private String currency;
	@CsvBindByName(column = "Người phê duyệt")
	@CsvBindByPosition(position = 11)
	private String approvalUser;
	@CsvBindByName(column = "Ngày phê duyệt")
	@CsvBindByPosition(position = 12)
	private String approvalDate;
	@CsvBindByName(column = "Người tạo")
	@CsvBindByPosition(position = 13)
	private String createdUser;
	@CsvBindByName(column = "Ngày tạo")
	@CsvBindByPosition(position = 14)
	private String createdDate;
	@CsvBindByName(column = "Ghi chú")
	@CsvBindByPosition(position = 15)
	private String note;
	@CsvBindByName(column = "Ngày phiên")
	@CsvBindByPosition(position = 16)
	private String sessionDate;
}
