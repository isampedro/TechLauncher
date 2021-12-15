package ar.edu.itba.paw.webapp.dto.custom_constraints;

import ar.edu.itba.paw.webapp.dto.custom_validators.RegistrationEmailValidator;
import ar.edu.itba.paw.webapp.dto.custom_validators.RegistrationUsernameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = RegistrationEmailValidator.class)
@Documented
public @interface RegistrationEmail {
    String message() default "{ar.edu.itba.paw.validation.constraints.RegistrationEmail.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
