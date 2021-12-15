package ar.edu.itba.paw.webapp.dto.custom_validators;

import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.dto.custom_constraints.RegistrationUsername;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RegistrationUsernameValidator implements ConstraintValidator<RegistrationUsername, String> {

    @Autowired
    private UserService userService;

    @Override
    public void initialize(RegistrationUsername constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !userService.findByUsername(value).isPresent();
    }
}