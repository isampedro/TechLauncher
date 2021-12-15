package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(name = "post_votes")
public class PostVote {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_votes_post_vote_id_seq")
    @SequenceGenerator(sequenceName = "post_votes_post_vote_id_seq", name = "post_votes_post_vote_id_seq", allocationSize = 1)
    @Column(name = "post_vote_id")
    private long postVoteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "vote", nullable = false)
    private Integer vote;

    public PostVote() {
        //For Hibernate
    }

    public long getPostVoteId() {
        return postVoteId;
    }

    public void setPostVoteId(long postVoteId) {
        this.postVoteId = postVoteId;
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

    public Integer getVote() {
        return vote;
    }

    public void setVote(Integer vote) {
        this.vote = vote;
    }

    public boolean isPositive() {
        return vote == 1;
    }
}
