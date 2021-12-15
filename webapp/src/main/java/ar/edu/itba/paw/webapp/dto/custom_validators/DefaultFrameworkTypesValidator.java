package ar.edu.itba.paw.webapp.dto.custom_validators;

import ar.edu.itba.paw.models.FrameworkType;
import ar.edu.itba.paw.webapp.dto.custom_constraints.DefaultFrameworkTypes;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultFrameworkTypesValidator implements ConstraintValidator<DefaultFrameworkTypes, List<String>> {
    private List<String> acceptedValues;

    @Override
    public void initialize(DefaultFrameworkTypes defaultFrameworkTypes) {
        acceptedValues = Stream.of(FrameworkType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(List<String> types, ConstraintValidatorContext constraintValidatorContext) {
        if( types != null ) {
            for (String type : types) {
                if( !acceptedValues.contains(type)) {
                    return false;
                }
            }
        }
        return true;
    }
}
