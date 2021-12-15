package ar.edu.itba.paw.webapp.dto.custom_constraints;

import ar.edu.itba.paw.webapp.dto.custom_validators.DefaultFrameworkTypesValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = DefaultFrameworkTypesValidator.class)
@Documented
public @interface DefaultFrameworkTypes {
    String message() default "{ar.edu.itba.paw.validation.constraints.DefaultFrameworkType.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
