package ar.edu.itba.paw.webapp.dto.validatedDTOs;

import javax.validation.constraints.Size;

public class ValidatedUserUpdateDTO {

    @Size(max = 200)
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
