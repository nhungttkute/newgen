package com.newgen.am.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UpdateIntegerFieldValidator implements ConstraintValidator<ValidUpdateIntegerField, Integer>{

	@Override
	public boolean isValid(Integer value, ConstraintValidatorContext context) {
		return (value > 0);
	}
}