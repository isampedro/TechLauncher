package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(name = "verify_users")
public class VerifyUser {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "verify_users_verification_id_seq")
    @SequenceGenerator(sequenceName = "verify_users_verification_id_seq", name = "verify_users_verification_id_seq", allocationSize = 1)
    @Column(name = "verification_id")
    private Long verificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "framework_id",nullable = false)
    private Framework framework;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column(nullable = false)
    private boolean pending;

    public VerifyUser() {
    }

    public VerifyUser(User user,Framework framework, Comment comment, boolean pending) {
        this.framework = framework;
        this.user = user;
        this.comment = comment;
        this.pending = pending;
    }

    public String getCategory() {
        return framework.getCategory().name();
    }

    public boolean isAdmin() {
        return user.isAdmin();
    }

    public long getVerificationId() {
        return verificationId;
    }

    public boolean isRequested() {
        return comment == null;
    }

    public boolean isPending() {
        return pending;
    }

    public Comment getComment() {
        return comment;
    }
    public long getFrameworkId(){
        return framework.getId();
    }
    public long getUserId(){
        return user.getId();
    }
    public String getCommentDescription(){
        if(comment!=null)
            return comment.getDescription();
        return null;
    }
    public String getUserName(){
        return user.getUsername();
    }
    public String getFrameworkName(){
        return framework.getName();
    }

    public void setVerificationId(Long verificationId) {
        this.verificationId = verificationId;
    }

    public void setFramework(Framework framework) {
        this.framework = framework;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    public Framework getFramework() {
        return framework;
    }

    public User getUser() {
        return user;
    }
}
