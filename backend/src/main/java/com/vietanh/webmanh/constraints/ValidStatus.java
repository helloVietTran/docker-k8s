package com.vietanh.webmanh.constraints;


import com.vietanh.webmanh.constants.ComicStatus;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidStatusValidator.class)
public @interface ValidStatus {
    ComicStatus[] value();
    String message() default "Invalid story status";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
