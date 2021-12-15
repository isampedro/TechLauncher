package ar.edu.itba.paw.models;


import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "post_comments")
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_comments_post_comment_id_seq")
    @SequenceGenerator(sequenceName = "post_comments_post_comment_id_seq", name = "post_comments_post_comment_id_seq", allocationSize = 1)
    @Column(name = "post_comment_id")
    private long postCommentId;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "tstamp", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @Column(name = "reference")
    private Long reference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id",nullable = false)
    private Post post;

    /*this refers to the other relation mapped in PostCommentVote*/
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "postComment", cascade = CascadeType.REMOVE)
    private List<PostCommentVote> postCommentVotes;

    @Transient
    private Long votesUp;
    @Transient
    private Long votesDown;


    public PostComment() {
        //For Hibernate
    }

    public long getPostCommentId() {
        return postCommentId;
    }

    public void setPostCommentId(long postCommentId) {
        this.postCommentId = postCommentId;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public List<PostCommentVote> getPostCommentVotes() {
        return postCommentVotes;
    }

    public void setPostCommentVotes(List<PostCommentVote> postCommentVotes) {
        this.postCommentVotes = postCommentVotes;
    }

    public Long getVotesUp() {
        if(votesUp == null)
            loadVotes();
        return votesUp;
    }

    private void loadVotes() {
        votesUp = 0L;
        votesDown = 0L;
        for (PostCommentVote vote: postCommentVotes) {
            if(vote.isVoteUp())
                votesUp++;
            else
                votesDown++;
        }
    }

    public Long getVotesDown() {
        if(votesDown == null)
            loadVotes();
        return votesDown;
    }

    public int getUserAuthVote(String username) {
        for (PostCommentVote vote: postCommentVotes) {
            if(vote.getUser().getUsername().equals(username))
                return vote.getVote();
        }
        return 0;
    }
    public boolean hasUserAuthVote(String username){
        for (PostCommentVote vote: postCommentVotes) {
            if(vote.getUser().getUsername().equals(username))
                return true;
        }
        return false;
    }
}
