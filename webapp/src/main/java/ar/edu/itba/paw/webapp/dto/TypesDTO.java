package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Framework;

import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.stream.Collectors;

public class TypesDTO {
    private String type;

    public static TypesDTO fromTypes(String type){
        final TypesDTO dto = new TypesDTO();
        dto.type = type;
        return dto;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
