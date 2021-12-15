package ar.edu.itba.paw.webapp.dto.custom_validators;

import ar.edu.itba.paw.webapp.dto.custom_constraints.Post;
import ar.edu.itba.paw.webapp.dto.validatedDTOs.ValidatedPostDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PostValidator  implements ConstraintValidator<Post, ValidatedPostDTO> {
    @Override
    public void initialize(Post post) {
    }

    @Override
    public boolean isValid(ValidatedPostDTO validatedPostDTO, ConstraintValidatorContext constraintValidatorContext) {
        boolean namesNullOrEmpty, categoriesNullOrEmpty, typesNullOrEmpty;
        namesNullOrEmpty = validatedPostDTO.getNames() == null || validatedPostDTO.getNames().isEmpty();
        categoriesNullOrEmpty = validatedPostDTO.getCategories() == null || validatedPostDTO.getCategories().isEmpty();
        typesNullOrEmpty = validatedPostDTO.getTypes() == null || validatedPostDTO.getCategories().isEmpty();

        return !(namesNullOrEmpty && categoriesNullOrEmpty && typesNullOrEmpty);
    }
}
