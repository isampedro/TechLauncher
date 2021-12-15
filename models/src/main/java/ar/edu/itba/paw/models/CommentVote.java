package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(name = "comment_votes")
public class CommentVote {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_votes_vote_id_seq")
    @SequenceGenerator(sequenceName = "comment_votes_vote_id_seq", name = "comment_votes_vote_id_seq", allocationSize = 1)
    @Column(name = "vote_id")
    private long commentVoteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column
    private int vote;

    public CommentVote(Comment comment, User user, int vote) {

        this.comment = comment;
        this.user = user;
        this.vote = vote;
    }

    public CommentVote() {
    }

    public long getCommentVoteId() {
        return commentVoteId;
    }

    public long getCommentId() {
        return comment.getCommentId();
    }

    public long getUserId() {
        return user.getId();
    }

    public int getVote() {
        return vote;
    }

    public boolean isVoteUp() {
        return vote==1;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public Comment getComment() {
        return comment;
    }

    public User getUser() {
        return user;
    }

    public void setCommentVoteId(long commentVoteId) {
        this.commentVoteId = commentVoteId;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
