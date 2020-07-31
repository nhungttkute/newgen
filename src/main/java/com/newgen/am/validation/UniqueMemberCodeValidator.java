package com.newgen.am.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.newgen.am.common.Utility;

public class UniqueMemberCodeValidator implements ConstraintValidator<UniqueMemberCode, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.trim().length() == 0) return true;
		return !Utility.checkExistedMemberCode(value);
	}

}
