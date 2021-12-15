package ar.edu.itba.paw.webapp.dto.validatedDTOs;

import ar.edu.itba.paw.webapp.dto.custom_constraints.DefaultFrameworkCategory;
import ar.edu.itba.paw.webapp.dto.custom_constraints.DefaultFrameworkType;
import ar.edu.itba.paw.webapp.dto.custom_constraints.Framework;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Framework
public class ValidatedFrameworkDTO {

    private Long id;

    @NotNull
    @NotEmpty
    @Size(min = 1, max = 50)
    @Pattern(regexp = "[a-zA-Z0-9 -+#*]+")
    private String name;

    @NotNull
    @NotEmpty
    @Size(min = 1, max = 500)
    private String description;

    @NotNull
    @NotEmpty
    @Size(min = 1, max = 5000)
    private String introduction;

    @NotNull
    @NotEmpty
    @DefaultFrameworkCategory
    @Size(min = 1, max = 50)
    private String category;

    @NotNull
    @NotEmpty
    @DefaultFrameworkType
    @Size(min = 1, max = 100)
    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
