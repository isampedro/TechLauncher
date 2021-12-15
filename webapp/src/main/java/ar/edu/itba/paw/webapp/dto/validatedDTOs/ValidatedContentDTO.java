package ar.edu.itba.paw.webapp.dto.validatedDTOs;

import ar.edu.itba.paw.webapp.dto.custom_constraints.Content;
import ar.edu.itba.paw.webapp.dto.custom_constraints.DefaultContentType;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Content
public class ValidatedContentDTO {

    private static final int MAX_RECOMMENDED_URL_SIZE = 2048;

    @NotNull
    @Min(value = 1)
    private Integer id;

    @NotNull
    @Size(min = 3,max = MAX_RECOMMENDED_URL_SIZE)
    private String link;

    @NotNull
    @DefaultContentType
    @Size(max = 10)
    private String type;

    @NotNull
    @NotEmpty
    @Size(min = 4, max=40)
    private String title;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
