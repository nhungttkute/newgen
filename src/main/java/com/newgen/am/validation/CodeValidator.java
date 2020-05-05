package com.newgen.am.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CodeValidator implements ConstraintValidator<ValidCode, String>{
	 Pattern pattern = Pattern.compile("^[a-zA-Z0-9]([._](?![._])|[a-zA-Z0-9]){1,50}[a-zA-Z0-9]$");

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) return true;
		if (value != null) {
			Matcher matcher = pattern.matcher(value);
			if (matcher.matches()) return true;
		}
		return false;
	}

}
