package com.vietanh.webmanh.constraints;

import com.vietanh.webmanh.constants.StoryStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class ValidStatusValidator
        implements ConstraintValidator<ValidStatus, StoryStatus> {

    private Set<StoryStatus> allowStatus;

    @Override
    public void initialize(ValidStatus annotation) {
        allowStatus = Set.of(annotation.value());
    }

    @Override
    public boolean isValid(StoryStatus value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return allowStatus.contains(value);
    }
}
