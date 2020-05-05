package com.newgen.am.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.newgen.am.common.AMLogger;
import com.newgen.am.common.ErrorMessage;
import com.newgen.am.dto.SystemFunctionDTO;
import com.newgen.am.exception.CustomException;
import com.newgen.am.model.SystemFunction;
import com.newgen.am.repository.SystemFunctionRepository;

@Service
public class SystemFunctionService {
	private String className = "SystemFunctionService";

	@Autowired
	private SystemFunctionRepository sysFunctionRepository;

	@Autowired
	private ModelMapper modelMapper;

	public List<SystemFunctionDTO> list() {
		String methodName = "list";
		long refId = System.currentTimeMillis();
		try {
			List<SystemFunction> sysFuncs = sysFunctionRepository.findAll();
			List<SystemFunctionDTO> sysFuncDtos = sysFuncs.stream().map(entity -> modelMapper.map(entity, SystemFunctionDTO.class))
					.collect(Collectors.toList());
			return sysFuncDtos;
		} catch (Exception e) {
			AMLogger.logError(className, methodName, refId, e);
			throw new CustomException(ErrorMessage.ERROR_OCCURRED, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
}
