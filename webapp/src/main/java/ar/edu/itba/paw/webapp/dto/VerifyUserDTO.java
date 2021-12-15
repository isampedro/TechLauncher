package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.VerifyUser;

import javax.ws.rs.core.UriInfo;
import java.util.Date;


public class VerifyUserDTO {
    private String username;
    private Boolean admin;
    private Boolean pending;
    private String frameworkName;
    private String location, techLocation, userLocation;
    private String description;
    private Date timestamp;
    private long userId;
    private long techId;

    public static VerifyUserDTO fromVerifyUser(VerifyUser verifyUser, UriInfo uriInfo) {
        VerifyUserDTO dto = new VerifyUserDTO();
        dto.username= verifyUser.getUser().getUsername();
        dto.admin = verifyUser.getUser().isAdmin();
        dto.pending = verifyUser.isPending();
        dto.frameworkName = verifyUser.getFrameworkName();
        dto.techLocation = uriInfo.getBaseUriBuilder().path("techs/" + verifyUser.getFramework().getId()).build().toString();
        if( !verifyUser.isPending()) {
            dto.location = uriInfo.getBaseUriBuilder().path("mod/" + verifyUser.getVerificationId()).build().toString();
        } else {
            dto.location = uriInfo.getBaseUriBuilder().path("mod/pending/" + verifyUser.getVerificationId()).build().toString();
        }
        dto.userLocation = uriInfo.getBaseUriBuilder().path("users/" + verifyUser.getUser().getId()).build().toString();
        dto.description = verifyUser.getCommentDescription();
        dto.userId = verifyUser.getUserId();
        dto.techId = verifyUser.getFrameworkId();
        if( dto.description != null) {
            dto.timestamp = verifyUser.getComment().getTimestamp();
        }
        return dto;
    }

    public String getTechLocation() {
        return techLocation;
    }

    public void setTechLocation(String techLocation) {
        this.techLocation = techLocation;
    }

    public static VerifyUserDTO fromProfile(VerifyUser verifyUser) {
        VerifyUserDTO dto = new VerifyUserDTO();
        dto.frameworkName = verifyUser.getFrameworkName();
        dto.techId = verifyUser.getFrameworkId();
        dto.pending = verifyUser.isPending();

        return dto;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Boolean getPending() {
        return pending;
    }

    public void setPending(Boolean pending) {
        this.pending = pending;
    }

    public String getFrameworkName() {
        return frameworkName;
    }

    public void setFrameworkName(String frameworkName) {
        this.frameworkName = frameworkName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getTechId() {
        return techId;
    }

    public void setTechId(long techId) {
        this.techId = techId;
    }
}
