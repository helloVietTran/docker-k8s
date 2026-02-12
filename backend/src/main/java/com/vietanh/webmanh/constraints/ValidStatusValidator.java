package com.vietanh.webmanh.constraints;

import com.vietanh.webmanh.constants.ComicStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class ValidStatusValidator
        implements ConstraintValidator<ValidStatus, ComicStatus> {

    private Set<ComicStatus> allowStatus;

    @Override
    public void initialize(ValidStatus annotation) {
        allowStatus = Set.of(annotation.value());
    }

    @Override
    public boolean isValid(ComicStatus value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return allowStatus.contains(value);
    }
}
