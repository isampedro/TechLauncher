package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.PostComment;

import javax.ws.rs.core.UriInfo;
import java.util.Date;

public class PostCommentDTO {
    private String description;
    private Date timestamp;
    private Long reference;
    private SimpleUserDTO user;
    private String userLocation;
    private Long votesUp;
    private Long votesDown;
    private String location;
    private String voteLocation;
    private int loggedVote;

    public static PostCommentDTO fromComment(PostComment comment, UriInfo uriInfo){
        final PostCommentDTO postComment = new PostCommentDTO();
        postComment.description = comment.getDescription();
        postComment.timestamp = comment.getTimestamp();
        postComment.reference = comment.getReference();
        postComment.user = SimpleUserDTO.fromUser(comment.getUser(),uriInfo);
        postComment.votesUp = comment.getVotesUp();
        postComment.votesDown = comment.getVotesDown();
        postComment.userLocation = "users/"+comment.getUser().getId();
        postComment.location = uriInfo.getBaseUriBuilder().path("posts/" + comment.getPost().getPostId() + "/answers/" + comment.getPostCommentId()).build().toString();
        postComment.voteLocation = uriInfo.getBaseUriBuilder().path("posts/" + comment.getPost().getPostId() + "/answers/" + comment.getPostCommentId()).build().toString();
        return postComment;
    }

    public String getVoteLocation() {
        return voteLocation;
    }

    public void setVoteLocation(String voteLocation) {
        this.voteLocation = voteLocation;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Long getReference() {
        return reference;
    }

    public void setReference(Long reference) {
        this.reference = reference;
    }

    public SimpleUserDTO getUser() {
        return user;
    }

    public void setUser(SimpleUserDTO user) {
        this.user = user;
    }

    public Long getVotesUp() {
        return votesUp;
    }

    public void setVotesUp(Long votesUp) {
        this.votesUp = votesUp;
    }

    public Long getVotesDown() {
        return votesDown;
    }

    public void setVotesDown(Long votesDown) {
        this.votesDown = votesDown;
    }

    public int getLoggedVote() {
        return loggedVote;
    }

    public void setLoggedVote(int loggedVote) {
        this.loggedVote = loggedVote;
    }
}
