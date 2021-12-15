package ar.edu.itba.paw.webapp.dto.custom_validators;

import ar.edu.itba.paw.models.FrameworkCategories;
import ar.edu.itba.paw.webapp.dto.custom_constraints.DefaultFrameworkCategory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultFrameworkCategoryValidator implements ConstraintValidator<DefaultFrameworkCategory, String> {

    private List<String> acceptedValues;

    @Override
    public void initialize(DefaultFrameworkCategory constraintAnnotation) {
        acceptedValues = Stream.of(FrameworkCategories.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return acceptedValues.contains(value);
    }

}