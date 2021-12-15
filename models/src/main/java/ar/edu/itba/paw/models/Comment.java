package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comments_comment_id_seq")
    @SequenceGenerator(sequenceName = "comments_comment_id_seq", name = "comments_comment_id_seq", allocationSize = 1)
    @Column(name = "comment_id")
    private long commentId;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "tstamp", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @Column(name = "reference")
    private Long reference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "framework_id",nullable = false)
    private Framework framework;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
    @JoinColumn(name = "reference")
    private List<Comment> replies;

    /*this refers to the other relation mapped in CommentVote*/
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "comment",cascade = CascadeType.REMOVE)
    private List<CommentVote> commentVotes;

    /*this refers to the other relation mapped in ReportComment*/
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "comment",cascade = CascadeType.REMOVE)
    private List<ReportComment> reports;

    /*this refers to the other relation mapped in VerifyUser*/
    @OneToOne(fetch = FetchType.LAZY,mappedBy = "comment", cascade = CascadeType.REMOVE)
    private VerifyUser verifyUser;

    @Transient
    private Long votesUp;
    @Transient
    private Long votesDown;

    public Comment() {
        //For Hibernate
    }

    public long getCommentId() {
        return commentId;
    }

    public long getFrameworkId() {
        return framework.getId();
    }

    public long getUserId() {
        return user.getId();
    }

    public String getDescription() {
        return description;
    }

    public Long getVotesUp() {
        if(votesUp == null)
            loadVotes();
        return votesUp;
    }

    private void loadVotes() {
        votesUp = 0L;
        votesDown = 0L;
        if(commentVotes == null){
            commentVotes = new ArrayList<>();
            return;
        }
        for (CommentVote vote: commentVotes) {
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

    public Date getTimestamp() {
        return timestamp;
    }

    public Long getReference() {
        return reference;
    }

    public String getFrameworkName() {
        return framework.getName();
    }

    public String getUserName() {
        return user.getUsername();
    }

    public String getCategory() {
        return framework.getCategory().name();
    }

    public FrameworkCategories getEnumCategory() {
        return framework.getCategory();
    }

    public boolean isVerify() {
        return user.isVerify();
    }

    public boolean isAdmin() {
        return user.isAdmin();
    }

    public int getUserAuthVote(String username) {
        for (CommentVote vote: commentVotes) {
            if(vote.getUser().getUsername().equals(username))
                return vote.getVote();
        }
        return 0;
    }
    public boolean hasUserAuthVote(String username){
        for (CommentVote vote: commentVotes) {
            if(vote.getUser().getUsername().equals(username))
                return true;
        }
        return false;
    }

    public boolean isReporter(String name){

        for (ReportComment rc:reports) {
            if(rc.getUser().getUsername().equals(name))
                return true;
        }
        return false;
    }

    public Framework getFramework() {
        return framework;
    }

    public void setFramework(Framework framework) {
        this.framework = framework;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public void setReference(Long reference) {
        this.reference = reference;
    }

    public void setCommentVotes(List<CommentVote> commentVotes) {
        this.commentVotes = commentVotes;
    }

    public void setReports(List<ReportComment> reports) {
        this.reports = reports;
    }

    public void setVerifyUser(VerifyUser verifyUser) {
        this.verifyUser = verifyUser;
    }

    public List<CommentVote> getCommentVotes() {
        return commentVotes;
    }

    public List<ReportComment> getReports() {
        return reports;
    }

    public VerifyUser getVerifyUser() {
        return verifyUser;
    }

    public List<Comment> getReplies() {
        return replies;
    }

    public void setReplies(List<Comment> replies) {
        this.replies = replies;
    }
}

