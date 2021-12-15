package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(name = "post_comment_votes")
public class PostCommentVote {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_comment_votes_vote_id_seq")
    @SequenceGenerator(sequenceName = "post_comment_votes_vote_id_seq", name = "post_comment_votes_vote_id_seq", allocationSize = 1)
    @Column(name = "post_comment_vote_id")
    private long postCommentVoteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_comment_id", nullable = false)
    private PostComment postComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column
    private int vote;

    public PostCommentVote() {
        //For Hibernate
    }

    public long getPostCommentVoteId() {
        return postCommentVoteId;
    }

    public void setPostCommentVoteId(long postCommentVoteId) {
        this.postCommentVoteId = postCommentVoteId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PostComment getPostComment() {
        return postComment;
    }

    public void setPostComment(PostComment postComment) {
        this.postComment = postComment;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public boolean isVoteUp(){
        return vote > 0;
    }
}
