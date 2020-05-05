package com.newgen.am.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UpdateStringFieldValidator implements ConstraintValidator<ValidUpdateStringField, String>{

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		boolean isValid = false;
		if (value == null) isValid = true;
		if (value != null && value.trim().length() > 0) isValid = true;
		return isValid;
	}
}
