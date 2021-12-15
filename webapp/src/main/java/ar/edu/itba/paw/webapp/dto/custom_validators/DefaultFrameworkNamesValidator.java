package ar.edu.itba.paw.webapp.dto.custom_validators;

import ar.edu.itba.paw.service.FrameworkService;
import ar.edu.itba.paw.webapp.dto.custom_constraints.DefaultFrameworkNames;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class DefaultFrameworkNamesValidator implements ConstraintValidator<DefaultFrameworkNames, List<String>> {
    @Autowired
    private FrameworkService fs;
    private List<String> acceptedValues;

    @Override
    public void initialize(DefaultFrameworkNames defaultFrameworkNames) {
        acceptedValues = fs.getFrameworkNames();
    }

    @Override
    public boolean isValid(List<String> names, ConstraintValidatorContext constraintValidatorContext) {
        if( names != null) {
            for (String name : names) {
                if( !acceptedValues.contains(name)) {
                    return false;
                }
            }
        }
        return true;
    }
}
