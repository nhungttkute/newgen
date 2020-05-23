package com.newgen.am.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

public class BrokerCSV {
	@CsvBindByName(column = "ID")
	@CsvBindByPosition(position = 0)
    private String _id;
	@CsvBindByName(column = "MEMBER_CODE")
	@CsvBindByPosition(position = 1)
    private String memberCode;
	@CsvBindByName(column = "MEMBER_NAME")
	@CsvBindByPosition(position = 2)
    private String memberName;
	@CsvBindByName(column = "CODE")
	@CsvBindByPosition(position = 3)
    private String code;
	@CsvBindByName(column = "NAME")
	@CsvBindByPosition(position = 4)
    private String name;
	@CsvBindByName(column = "STATUS")
	@CsvBindByPosition(position = 5)
    private String status;
	@CsvBindByName(column = "NOTE")
	@CsvBindByPosition(position = 6)
    private String note;
	@CsvBindByName(column = "CREATED_DATE")
	@CsvBindByPosition(position = 7)
	private String createdDate;
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getMemberCode() {
		return memberCode;
	}
	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}
	public String getMemberName() {
		return memberName;
	}
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	
}
