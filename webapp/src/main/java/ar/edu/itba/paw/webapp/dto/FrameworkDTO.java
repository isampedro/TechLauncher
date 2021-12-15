package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Framework;

import javax.ws.rs.core.UriInfo;

public class FrameworkDTO {
    private String name;
    private String description;
    private String introduction;
    private String category;
    private String author;
    private String authorLocation;
    private String type;
    private String date;
    private Integer votesCant;
    private Float stars;
    private Integer commentsAmount;
    private String comments;
    private String books;
    private String tutorials;
    private String courses;
    private String location;
    private String picture;
    private String relatedPosts;
    private Boolean hasPicture;
    private String competitors;
    private String modLocation;
    private int loggedStars;
    private long id;
    private long authorId;

    public static FrameworkDTO fromFramework(Framework framework, UriInfo uriInfo) {
        final FrameworkDTO dto = new FrameworkDTO();
        dto.name = framework.getName();
        dto.description = framework.getDescription();
        dto.introduction = framework.getIntroduction();
        dto.category = framework.getCategory().name();
        dto.author = framework.getAuthor().getUsername();
        dto.authorLocation = uriInfo.getBaseUriBuilder().path("users/"+framework.getAuthor().getId()).build().toString();
        dto.authorId = framework.getAuthor().getId();
        dto.type = framework.getType().name();
        dto.date = framework.getPublishDate().toLocaleString();
        dto.votesCant = framework.getVotesCant();
        dto.stars = (float) framework.getStars();
        dto.commentsAmount = framework.getCommentsAmount();
        dto.hasPicture = framework.getPictureId() != 0;
        dto.location = uriInfo.getBaseUriBuilder().path("techs/"+framework.getId()).build().toString();
        dto.picture = "api/techs/"+framework.getId()+"/image";
        dto.comments = uriInfo.getBaseUriBuilder().path("techs/"+framework.getId()+"/comment").build().toString();
        dto.books = uriInfo.getBaseUriBuilder().path("techs/"+framework.getId()).build().toString()+"/content?type=book";
        dto.courses = uriInfo.getBaseUriBuilder().path("techs/"+framework.getId()).build().toString()+"/content?type=course";
        dto.tutorials = uriInfo.getBaseUriBuilder().path("techs/"+framework.getId()).build().toString()+"/content?type=tutorial";
        dto.relatedPosts = "explore?toSearch="+framework.getName()+"&isPost=true";
        dto.competitors = uriInfo.getBaseUriBuilder().path("techs/"+framework.getId()+"/competitors").build().toString();
        dto.modLocation = uriInfo.getBaseUriBuilder().path("mod/tech/"+framework.getId()).build().toString();
        dto.id = framework.getId();
        return dto;
    }

    public static FrameworkDTO fromExtern(Framework framework,UriInfo uriInfo) {
        final FrameworkDTO dto = new FrameworkDTO();
        dto.name = framework.getName();
        dto.hasPicture = framework.getPictureId() != 0;
        dto.picture = "api/techs/"+framework.getId()+"/image";
        dto.authorId = framework.getAuthor().getId();
        dto.id = framework.getId();
        dto.location = uriInfo.getBaseUriBuilder().path("techs/"+framework.getId()).build().toString();
        dto.stars = (float) framework.getStars();
        return dto;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getIntroduction() {
        return introduction;
    }

    public String getCategory() {
        return category;
    }

    public String getAuthor() {
        return author;
    }

    public String getAuthorLocation() {
        return authorLocation;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getPicture() {
        return picture;
    }

    public Integer getVotesCant() {
        return votesCant;
    }

    public Float getStars() {
        return stars;
    }

    public Integer getCommentsAmount() {
        return commentsAmount;
    }

    public String getComments() {
        return comments;
    }

    public String getBooks() {
        return books;
    }

    public String getTutorials() {
        return tutorials;
    }

    public String getCourses() {
        return courses;
    }

    public String getLocation() {
        return location;
    }
    public void setName(String techName) {
        this.name = techName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setAuthorLocation(String authorLocation) {
        this.authorLocation = authorLocation;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setVotesCant(Integer votesCant) {
        this.votesCant = votesCant;
    }

    public void setStars(Float stars) {
        this.stars = stars;
    }

    public void setCommentsAmount(Integer commentsAmount) {
        this.commentsAmount = commentsAmount;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setBooks(String books) {
        this.books = books;
    }

    public void setTutorials(String tutorials) {
        this.tutorials = tutorials;
    }

    public void setCourses(String courses) {
        this.courses = courses;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getHasPicture() {
        return hasPicture;
    }

    public void setHasPicture(Boolean hasPicture) {
        this.hasPicture = hasPicture;
    }

    public String getRelatedPosts() {
        return relatedPosts;
    }

    public void setRelatedPosts(String relatedPosts) {
        this.relatedPosts = relatedPosts;
    }

    public String getCompetitors() {
        return competitors;
    }

    public void setCompetitors(String competitors) {
        this.competitors = competitors;
    }

    public int getLoggedStars() {
        return loggedStars;
    }

    public void setLoggedStars(int loggedStars) {
        this.loggedStars = loggedStars;
    }

    public String getModLocation() {
        return modLocation;
    }

    public void setModLocation(String modLocation) {
        this.modLocation = modLocation;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }
}
