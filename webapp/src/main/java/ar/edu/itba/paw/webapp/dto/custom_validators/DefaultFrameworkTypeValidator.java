package ar.edu.itba.paw.webapp.dto.custom_validators;

import ar.edu.itba.paw.models.FrameworkType;
import ar.edu.itba.paw.webapp.dto.custom_constraints.DefaultFrameworkType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultFrameworkTypeValidator implements ConstraintValidator<DefaultFrameworkType, String> {

    private List<String> acceptedValues;

    @Override
    public void initialize(DefaultFrameworkType constraintAnnotation) {
        acceptedValues = Stream.of(FrameworkType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return acceptedValues.contains(value);
    }

}