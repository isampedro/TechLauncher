package ar.edu.itba.paw.webapp.dto.validatedDTOs;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class ValidatedVoteDTO {
    @NotNull
    @Min(value = 1)
    @Max(value = 5)
    private Integer value;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
