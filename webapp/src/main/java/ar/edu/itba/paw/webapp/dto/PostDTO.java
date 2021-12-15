package ar.edu.itba.paw.webapp.dto;


import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.PostTag;

import javax.ws.rs.core.UriInfo;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PostDTO {
    private String postTitle;
    private String postDescription;
    private SimpleUserDTO postOwner;
    private String ownerLocation;
    private Date timestamp;
    private List<PostTagDTO> postTags;
    private long votesUp, votesDown;
    private String comments;
    private String location;
    private int loggedVote;
    private long id;

    public static PostDTO fromPost(Post post, UriInfo uriInfo) {
        final PostDTO dto = new PostDTO();
        dto.postTitle = post.getTitle();
        dto.postDescription = post.getDescription();
        dto.postOwner = SimpleUserDTO.fromUser(post.getUser(),uriInfo);
        dto.timestamp = post.getTimestamp();
        dto.comments = uriInfo.getBaseUriBuilder().path("posts/" + post.getPostId()+ "/answers").build().toString();
        dto.postTags = post.getPostTags().stream().map(PostTagDTO::fromPostTag).collect(Collectors.toList());
        dto.ownerLocation = uriInfo.getBaseUriBuilder().path("users/"+post.getUser().getId()).build().toString();
        dto.votesUp = post.getVotesUp();
        dto.votesDown = post.getVotesDown();
        dto.location = uriInfo.getBaseUriBuilder().path("posts/"+post.getPostId()).build().toString();
        dto.id = post.getPostId();
        return dto;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOwnerLocation() {
        return ownerLocation;
    }

    public void setOwnerLocation(String ownerLocation) {
        this.ownerLocation = ownerLocation;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public SimpleUserDTO getPostOwner() {
        return postOwner;
    }

    public void setPostOwner(SimpleUserDTO postOwner) {
        this.postOwner = postOwner;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public List<PostTagDTO> getPostTags() {
        return postTags;
    }

    public void setPostTags(List<PostTagDTO> postTags) {
        this.postTags = postTags;
    }

    public long getVotesUp() {
        return votesUp;
    }

    public void setVotesUp(long votesUp) {
        this.votesUp = votesUp;
    }

    public long getVotesDown() {
        return votesDown;
    }

    public void setVotesDown(long votesDown) {
        this.votesDown = votesDown;
    }

    public int getLoggedVote() {
        return loggedVote;
    }

    public void setLoggedVote(int loggedVote) {
        this.loggedVote = loggedVote;
    }


}
