package com.newgen.am.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BrokerCodeValidator implements ConstraintValidator<ValidBrokerCode, String>{
	 Pattern pattern = Pattern.compile("^[0-9]{8}$");

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value != null) {
			Matcher matcher = pattern.matcher(value);
			if (matcher.matches()) return true;
		}
		return false;
	}

}
