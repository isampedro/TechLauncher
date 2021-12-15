package ar.edu.itba.paw.webapp.dto.validatedDTOs;

import ar.edu.itba.paw.webapp.dto.custom_constraints.DefaultFrameworkCategories;
import ar.edu.itba.paw.webapp.dto.custom_constraints.DefaultFrameworkNames;
import ar.edu.itba.paw.webapp.dto.custom_constraints.DefaultFrameworkTypes;
import ar.edu.itba.paw.webapp.dto.custom_constraints.Post;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Post
public class ValidatedPostDTO {
    
    @DefaultFrameworkTypes
    private List<String> types;


    @DefaultFrameworkCategories
    private List<String> categories;

    @DefaultFrameworkNames
    private List<String> names;

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 200)
    private String title;

    @NotNull
    @NotEmpty
    @Size(min=1, max = 5000)
    private String description;

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
