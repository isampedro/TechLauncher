package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(name = "framework_votes")
public class FrameworkVote {

    /* Class modelling */

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "votes_vote_id_seq")
    @SequenceGenerator(sequenceName = "votes_vote_id_seq", name = "votes_vote_id_seq", allocationSize = 1)
    @Column(name = "vote_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "framework_id", nullable = false)
    private Framework framework;

    @Column(name = "stars", nullable = false)
    private Integer stars;

    /* Constructors */

    public FrameworkVote() {
        /* Empty constructor */
    }

    public FrameworkVote(Framework framework, User user, Integer stars) {
        this.framework = framework;
        this.user = user;
        this.stars = stars;
    }

    /* Getters and Setters */

    public String getFrameworkName() { return framework.getName(); }

    public int getStars() { return stars;   }

    public void setStars(int stars) { this.stars = stars; }

    public void setUser(User user) {
        this.user = user;
    }

    public void setFramework(Framework framework) {
        this.framework = framework;
    }

    public long getVoteId() { return id; }

    public long getFrameworkId() { return framework.getId(); }

    public long getUserId() { return user.getId(); }

    public FrameworkCategories getFrameworkCategory() { return framework.getCategory(); }

    public String getCategory(){ return framework.getCategory().name(); }

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Framework getFramework() {
        return framework;
    }
}
