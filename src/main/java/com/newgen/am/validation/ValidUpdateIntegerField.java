package com.newgen.am.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = UpdateIntegerFieldValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface ValidUpdateIntegerField {
	public String message() default "Required.";

	public Class<?>[] groups() default {};

	public Class<? extends Payload>[] payload() default {};
}
