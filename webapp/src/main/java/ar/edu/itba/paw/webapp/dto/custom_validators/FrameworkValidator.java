package ar.edu.itba.paw.webapp.dto.custom_validators;

import ar.edu.itba.paw.service.FrameworkService;
import ar.edu.itba.paw.webapp.dto.custom_constraints.Framework;
import ar.edu.itba.paw.webapp.dto.validatedDTOs.ValidatedFrameworkDTO;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FrameworkValidator implements ConstraintValidator<Framework, ValidatedFrameworkDTO> {

    @Autowired
    private FrameworkService frameworkService;

    @Override
    public void initialize(Framework constraintAnnotation) {

    }

    @Override
    public boolean isValid(ValidatedFrameworkDTO dto, ConstraintValidatorContext context) {
        if (dto.getName() != null && dto.getName().length() > 0) {
            return frameworkService.nameAvailable(dto.getName(), dto.getId());
        }
        return false;
    }
}
