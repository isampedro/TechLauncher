package ar.edu.itba.paw.webapp.dto.custom_validators;

import ar.edu.itba.paw.models.ContentTypes;
import ar.edu.itba.paw.webapp.dto.custom_constraints.DefaultContentType;
import ar.edu.itba.paw.webapp.dto.custom_constraints.Email;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultContentTypeValidator implements ConstraintValidator<DefaultContentType, String> {

    private List<String> acceptedValues;

    @Override
    public void initialize(DefaultContentType constraintAnnotation) {
        acceptedValues = Stream.of(ContentTypes.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return acceptedValues.contains(value.toLowerCase());
    }

}