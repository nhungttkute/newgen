package com.newgen.am.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NumberValidator implements ConstraintValidator<ValidNumber, String> {
	Pattern pattern = Pattern.compile("\\d+");

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		boolean isValid = false;
		if (value == null) isValid = true;
		if (value != null) {
			Matcher matcher = pattern.matcher(value);
			if (matcher.matches())
				isValid = true;
		}
		return isValid;
	}
}
