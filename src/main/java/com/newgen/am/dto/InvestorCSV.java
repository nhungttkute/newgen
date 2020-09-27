package com.newgen.am.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

import lombok.Data;

@Data
public class InvestorCSV {
	@CsvBindByName(column = "ID")
	@CsvBindByPosition(position = 0)
    private String _id;
	@CsvBindByName(column = "MEMBER_CODE")
	@CsvBindByPosition(position = 1)
    private String memberCode;
	@CsvBindByName(column = "MEMBER_NAME")
	@CsvBindByPosition(position = 2)
    private String memberName;
	@CsvBindByName(column = "BROKER_CODE")
	@CsvBindByPosition(position = 3)
    private String brokerCode;
	@CsvBindByName(column = "BROKER_NAME")
	@CsvBindByPosition(position = 4)
    private String brokerName;
	@CsvBindByName(column = "COLLABORATOR_CODE")
	@CsvBindByPosition(position = 5)
    private String collaboratorCode;
	@CsvBindByName(column = "COLLABORATOR_NAME")
	@CsvBindByPosition(position = 6)
    private String collaboratorName;
	@CsvBindByName(column = "INVESTOR_CODE")
	@CsvBindByPosition(position = 7)
    private String investorCode;
	@CsvBindByName(column = "INVESTOR_NAME")
	@CsvBindByPosition(position = 8)
    private String investorName;
	@CsvBindByName(column = "STATUS")
	@CsvBindByPosition(position = 9)
    private String status;
	@CsvBindByName(column = "NOTE")
	@CsvBindByPosition(position = 10)
    private String note;
	@CsvBindByName(column = "CREATED_DATE")
	@CsvBindByPosition(position = 11)
	private String createdDate;
}
