package ar.edu.itba.paw.webapp.dto.custom_validators;

import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.dto.custom_constraints.RegistrationEmail;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RegistrationEmailValidator implements ConstraintValidator<RegistrationEmail, String> {

    @Autowired
    private UserService userService;

    @Override
    public void initialize(RegistrationEmail constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !userService.findByMail(value).isPresent();
    }
}