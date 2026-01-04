package com.vietanh.webmanh.constrains;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE}) // áp dụng cho class
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldMatchValidator.class)
@Documented
public @interface FieldMatch {

    String message() default "FIELDS_DO_NOT_MATCH";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    // Tên 2 field cần so sánh
    String first();

    String second();
}