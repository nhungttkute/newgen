package com.newgen.am.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.newgen.am.common.Utility;

public class UniqueIdentityCardValidator implements ConstraintValidator<UniqueIdentityCard, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) return true;
		return !Utility.checkExistedIdentityCard(value);
	}

}
