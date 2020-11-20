package com.newgen.am.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.newgen.am.common.Utility;

public class UniqueBrokerCodeValidator implements ConstraintValidator<UniqueBrokerCode, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.trim().length() == 0) return true;
		return !Utility.checkExistedBrokerCode(value);
	}

}
