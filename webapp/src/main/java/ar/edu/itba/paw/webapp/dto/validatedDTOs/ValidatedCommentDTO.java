package ar.edu.itba.paw.webapp.dto.validatedDTOs;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ValidatedCommentDTO {

    @NotNull
    @Size(min = 3, max = 500)
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

