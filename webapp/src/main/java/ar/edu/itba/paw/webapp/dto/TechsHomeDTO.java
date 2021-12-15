package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Framework;

import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.stream.Collectors;

public class TechsHomeDTO {
    private List<FrameworkDTO> hots;
    private List<FrameworkDTO> interests;
    public static TechsHomeDTO fromTechs(List<Framework> frameworks, UriInfo uriInfo) {
        final TechsHomeDTO dto = new TechsHomeDTO();
        dto.hots = frameworks.stream().map((Framework framework) -> FrameworkDTO.fromExtern(framework,uriInfo)).collect(Collectors.toList());
        return dto;
    }

    public List<FrameworkDTO> getHots() {
        return hots;
    }

    public void setHots(List<FrameworkDTO> hots) {
        this.hots = hots;
    }

    public List<FrameworkDTO> getInterests() {
        return interests;
    }

    public void setInterests(List<FrameworkDTO> interests) {
        this.interests = interests;
    }
}
