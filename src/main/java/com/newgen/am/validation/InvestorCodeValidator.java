package com.newgen.am.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class InvestorCodeValidator implements ConstraintValidator<ValidInvestorCode, String>{
	 Pattern pattern = Pattern.compile("^[A-Z0-9]{10}$");

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value != null) {
			Matcher matcher = pattern.matcher(value);
			if (matcher.matches()) return true;
		}
		return false;
	}

}
