package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.FrameworkVote;

import javax.ws.rs.core.UriInfo;

public class VoteDTO {
    private String techLocation;
    private String techName;
    private String frameworkCategory;
    private Double count;
    private Integer vote;
    private long techId;

    public static VoteDTO fromFrameworkVote (FrameworkVote vote, UriInfo uriInfo) {
        VoteDTO dto = new VoteDTO();
        dto.count =  (double) vote.getStars();
        dto.techName = vote.getFrameworkName();
        dto.frameworkCategory = vote.getCategory();
        dto.techLocation = uriInfo.getBaseUriBuilder().path("/techs/"+vote.getFrameworkId()).build().toString();
        dto.techId = vote.getFrameworkId();
        return dto;
    }
    public static VoteDTO fromProfile (FrameworkVote vote,UriInfo uriInfo) {
        VoteDTO dto = new VoteDTO();
        dto.vote =  vote.getStars();
        dto.techName = vote.getFrameworkName();
        dto.techLocation = uriInfo.getBaseUriBuilder().path("/techs/"+vote.getFrameworkId()).build().toString();
        dto.techId = vote.getFrameworkId();
        return dto;
    }
    public Double getCount() {
        return count;
    }

    public void setCount(Double count) {
        this.count = count;
    }

    public String getTechLocation() {
        return techLocation;
    }

    public void setTechLocation(String techLocation) {
        this.techLocation = techLocation;
    }

    public String getTechName() {
        return techName;
    }

    public void setTechName(String techName) {
        this.techName = techName;
    }

    public String getFrameworkCategory() {
        return frameworkCategory;
    }

    public void setFrameworkCategory(String frameworkCategory) {
        this.frameworkCategory = frameworkCategory;
    }

    public Integer getVote() {
        return vote;
    }

    public void setVote(Integer vote) {
        this.vote = vote;
    }

    public long getTechId() {
        return techId;
    }

    public void setTechId(long techId) {
        this.techId = techId;
    }
}
