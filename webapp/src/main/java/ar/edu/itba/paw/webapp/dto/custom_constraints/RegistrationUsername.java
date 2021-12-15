package ar.edu.itba.paw.webapp.dto.custom_constraints;

import ar.edu.itba.paw.webapp.dto.custom_validators.EmailValidator;
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
@Constraint(validatedBy = RegistrationUsernameValidator.class)
@Documented
public @interface RegistrationUsername {
    String message() default "{ar.edu.itba.paw.validation.constraints.RegistrationUsername.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
