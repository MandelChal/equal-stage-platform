package com.equal_stage_platform.dev.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidWorkingAreasValidator.class)
public @interface ValidWorkingAreas {
    String message() default "Invalid working areas specified";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}