package ar.edu.itba.paw.webapp.dto.custom_validators;

import ar.edu.itba.paw.models.FrameworkCategories;
import ar.edu.itba.paw.webapp.dto.custom_constraints.DefaultFrameworkCategories;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultFrameworkCategoriesValidator implements ConstraintValidator<DefaultFrameworkCategories, List<String>> {
    private List<String> acceptedValues;

    @Override
    public void initialize(DefaultFrameworkCategories defaultFrameworkCategories) {
        acceptedValues = Stream.of(FrameworkCategories.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(List<String> categories, ConstraintValidatorContext constraintValidatorContext) {
        if( categories != null ) {
            for (String category : categories) {
                if( !acceptedValues.contains(category)) {
                    return false;
                }
            }
        }
        return true;
    }
}
