package ar.edu.itba.paw.webapp.dto.custom_validators;

import ar.edu.itba.paw.models.Framework;
import ar.edu.itba.paw.service.ContentService;
import ar.edu.itba.paw.service.FrameworkService;
import ar.edu.itba.paw.webapp.dto.custom_constraints.Content;
import ar.edu.itba.paw.webapp.dto.validatedDTOs.ValidatedContentDTO;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

public class ContentValidator implements ConstraintValidator<Content, ValidatedContentDTO> {
    @Autowired
    private ContentService contentService;

    @Autowired
    private FrameworkService frameworkService;

    @Override
    public void initialize(Content constraintAnnotation) {

    }

    @Override
    public boolean isValid(ValidatedContentDTO dto, ConstraintValidatorContext context) {
        if (dto.getId() != null && dto.getTitle() != null && dto.getTitle().length() > 0 && dto.getLink() != null && dto.getLink().length() > 0) {
            final Optional<Framework> framework = frameworkService.findById(dto.getId());
            return framework.isPresent() && contentService.titleIsAvailable(dto.getId(), dto.getTitle(), dto.getType());
        }

        return false;
    }
}
