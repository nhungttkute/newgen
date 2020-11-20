/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.dto;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.newgen.am.validation.FormatGroup;
import com.newgen.am.validation.LengthGroup;
import com.newgen.am.validation.ValidCode;

import lombok.Data;

/**
 *
 * @author nhungtt
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepartmentDTO {
    private String _id;
    @NotEmpty(message = "Required.")
    @ValidCode(groups = FormatGroup.class)
    private String code;
    @NotEmpty(message = "Required.")
    @Size(min = 1, max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String name;
    private String status;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private long createdDate;
    @Size(max = 200, message = "Invalid format.", groups = LengthGroup.class)
    private String note;
    private List<UserDTO> users;
}
