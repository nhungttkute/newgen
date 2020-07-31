package com.newgen.am.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidNumberCharacterValidator implements ConstraintValidator<ValidNumberCharacter, String>{
	 Pattern pattern = Pattern.compile("^[a-zA-Z0-9]*$");

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		boolean isValid = false;
		if (value == null || value.trim().length() == 0) isValid = true;
		if (value != null) {
			Matcher matcher = pattern.matcher(value);
			if (matcher.matches()) isValid = true;
		}
		return isValid;
	}

}