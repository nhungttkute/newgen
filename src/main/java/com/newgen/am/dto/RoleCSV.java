package com.newgen.am.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

public class RoleCSV {
	@CsvBindByName(column = "ID")
	@CsvBindByPosition(position = 0)
	private String id;
	@CsvBindByName(column = "NAME")
	@CsvBindByPosition(position = 1)
    private String name;
	@CsvBindByName(column = "DESCRIPTION")
	@CsvBindByPosition(position = 2)
    private String description;
	@CsvBindByName(column = "STATUS")
	@CsvBindByPosition(position = 3)
    private String status;
	@CsvBindByName(column = "CREATED_DATE")
	@CsvBindByPosition(position = 4)
	private String createdDate;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
    
}
