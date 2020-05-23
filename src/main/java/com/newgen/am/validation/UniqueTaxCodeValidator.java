package com.newgen.am.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.newgen.am.common.Utility;

public class UniqueTaxCodeValidator implements ConstraintValidator<UniqueTaxCode, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) return true;
		return !Utility.checkExistedTaxCode(value);
	}

}
