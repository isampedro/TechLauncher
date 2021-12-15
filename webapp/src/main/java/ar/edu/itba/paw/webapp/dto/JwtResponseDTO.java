package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.User;

import javax.ws.rs.core.UriInfo;

public class JwtResponseDTO {

    private String token;
    private String location;

    public JwtResponseDTO() {

    }

    public JwtResponseDTO(String token, User user, UriInfo uriInfo) {
        this.token = token;
        this.location = uriInfo.getBaseUriBuilder().path("users/"+user.getId()).build().toString();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}