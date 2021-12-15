package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Framework;

import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.stream.Collectors;

public class CategoriesDTO {

    private List<FrameworkDTO> techs;
    private Integer amount;
    private String category;
    private String location;

    public static CategoriesDTO fromCategories(List<Framework> frameworks, int size, UriInfo uriInfo) {
        final CategoriesDTO dto = new CategoriesDTO();
        dto.amount = size;
        dto.techs = frameworks.stream().map((Framework framework) -> FrameworkDTO.fromExtern(framework,uriInfo)).collect(Collectors.toList());
        return dto;
    }
    public static CategoriesDTO fromSideBar(String category, UriInfo uriInfo){
        final CategoriesDTO dto = new CategoriesDTO();
        dto.category = category;
        dto.location = uriInfo.getBaseUriBuilder().path("techs/category/"+category).build().toString();
        return dto;
    }

    public List<FrameworkDTO> getTechs() {
        return techs;
    }

    public void setTechs(List<FrameworkDTO> techs) {
        this.techs = techs;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
