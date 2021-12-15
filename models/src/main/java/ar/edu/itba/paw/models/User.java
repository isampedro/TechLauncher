package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
public class User {

    public static final long DEFAULT_PICTURE_ID = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_user_id_seq")
    @SequenceGenerator(sequenceName = "users_user_id_seq", name = "users_user_id_seq", allocationSize = 1)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_name",length = 100, nullable = false, unique = true)
    private String username;

    @Column(length = 100, nullable = false, unique = true)
    private String mail;

    @Column(length = 100)
    private String password;

    @Column(name = "enabled",nullable = false)
    private boolean enable = false;
    @Column(name = "user_description",length = 200, nullable = false)
    private String description;
    @Column(name = "allow_moderator", nullable = false)
    private boolean allowMod;

    /* Refers other relation mapped in UserBlob */
    @OneToOne(optional = true, fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "picture_id", referencedColumnName = "blob_id")
    private Blob picture;

    /*this refers to the other relation mapped in Admin*/
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user",cascade = CascadeType.REMOVE)
    private Admin admin;

    /*this refers to the other relation mapped in VerificationToken*/
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user",cascade = CascadeType.REMOVE)
    private VerificationToken verificationToken;

    /*this refers to the other relation mapped in VerifyUser*/
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user",cascade = CascadeType.REMOVE)
    private List<VerifyUser> applications;

    /*this refers to the other relation mapped in Comment*/
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user",cascade = CascadeType.REMOVE)
    private List<Comment> comments;

    /* References other relation mapped in Content */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user",cascade = CascadeType.REMOVE)
    private List<Content> contents;

    /*this refers to the other relation mapped in CommentVote*/
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user",cascade = CascadeType.REMOVE)
    private List<CommentVote> commentVotes;

    /*this refers to the other relation mapped in ReportComment*/
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user",cascade = CascadeType.REMOVE)
    private List<ReportComment> commentsReported;

    /*this refers to the other relation mapped in ReportContent*/
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user",cascade = CascadeType.REMOVE)
    private List<ReportContent> contentsReported;

    /*this refers to the other relation mapped in Framework*/
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
    private List<Framework> ownedFrameworks;

    /*this refers to the other relation mapped in FrameworkVotes */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user",cascade = CascadeType.REMOVE)
    private List<FrameworkVote> frameworkVotes;

    /*this refers to the other relation mapped in Post*/
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Post> posts;

    /*this refers to the other relation mapped in PostVotes */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<PostVote> postVotes;

    /*this refers to the other relation mapped in PostComment*/
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<PostComment> postComments;

    /*this refers to the other relation mapped in PostCommentVote*/
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<PostCommentVote> postCommentVotes;


    public User() {
    }

    public User(String username, String mail, String password, boolean enable, String description, boolean allowMod) {
        this.username = username;
        this.mail = mail;
        this.password = password;
        this.enable = enable;
        this.description = description;
        this.allowMod = allowMod;
    }

    public User(String username, String mail, String password, boolean enable, String description, boolean allowMod, Blob picture) {
        this(username, mail, password, enable, description, allowMod);
        this.picture = picture;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAllowMod() {
        return allowMod;
    }

    public void setAllowMod(boolean allowMod) {
        this.allowMod = allowMod;
    }

    public boolean isAdmin() {
        return admin!=null;
    }

    public void setApplications(List<VerifyUser> verifications) {
        //this.verifications.addAll(verifications);
        this.applications = verifications;
    }

    public List<VerifyUser> getApplications() {
        return applications;
    }

    public boolean isVerifyForFramework(long frameworkId){
        for (VerifyUser v: applications) {
            if(v.getFrameworkId()==frameworkId){
                return !v.isPending();
            }

        }
        return false;
    }
    public List<VerifyUser> getVerifications(){
        if (applications != null) {
            return applications.stream().filter((x)-> !x.isPending()).collect(Collectors.toList());
        }
        return null;
    }

    public boolean hasAppliedToFramework(long frameworkId){
        for (VerifyUser v: applications) {
            if(v.getFrameworkId()==frameworkId){
                return true;
            }

        }
        return false;
    }
    public boolean isVerify() {
        for (VerifyUser v: applications) {
            if(!v.isPending())
                return true;
        }
        return false;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public boolean hasCommentVote(long commentId){
        for (CommentVote cv: commentVotes) {
            if(cv.getCommentId() == commentId)
                return true;
        }
        return false;
    }
    public CommentVote getCommentVote(long commentId){
        for (CommentVote cv: commentVotes) {
            if(cv.getCommentId() == commentId)
                return cv;
        }
        return null;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public List<Content> getContents() {
        return contents;
    }

    public List<CommentVote> getCommentVotes() {
        return commentVotes;
    }

    public List<ReportComment> getCommentsReported() {
        return commentsReported;
    }

    public List<ReportContent> getContentsReported() {
        return contentsReported;
    }

    public List<Framework> getOwnedFrameworks() {
        return ownedFrameworks;
    }

    public List<FrameworkVote> getFrameworkVotes() {
        return frameworkVotes;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    public void setCommentVotes(List<CommentVote> commentVotes) {
        this.commentVotes = commentVotes;
    }

    public void setCommentsReported(List<ReportComment> commentsReported) {
        this.commentsReported = commentsReported;
    }

    public void setContentsReported(List<ReportContent> contentsReported) {
        this.contentsReported = contentsReported;
    }

    public void setOwnedFrameworks(List<Framework> ownedFrameworks) {
        this.ownedFrameworks = ownedFrameworks;
    }

    public void setFrameworkVotes(List<FrameworkVote> frameworkVotes) {
        this.frameworkVotes = frameworkVotes;
    }

    public Optional<VerificationToken> getVerificationToken() {
        return Optional.ofNullable(verificationToken);
    }

    public void setVerificationToken(VerificationToken verificationToken) {
        this.verificationToken = verificationToken;
    }
    public Optional<FrameworkVote> getVoteForFramework(long id){
        return frameworkVotes.stream().filter((x)->x.getFramework().getId() == id).findFirst();
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<PostVote> getPostVotes() {
        return postVotes;
    }

    public void setPostVotes(List<PostVote> postVotes) {
        this.postVotes = postVotes;
    }

    public List<PostComment> getPostComments() {
        return postComments;
    }

    public void setPostComments(List<PostComment> postComments) {
        this.postComments = postComments;
    }

    public List<PostCommentVote> getPostCommentVotes() {
        return postCommentVotes;
    }

    public void setPostCommentVotes(List<PostCommentVote> postCommentVotes) {
        this.postCommentVotes = postCommentVotes;
    }
    public int getVoteForPost(long id){
        Optional<PostVote> vote =postVotes.stream().filter((x)->x.getPost().getPostId() == id).findFirst();
        return vote.isPresent() ? vote.get().getVote() : 0;
    }

    public long getPictureId() {
        if(picture != null) {
            return picture.getId();
        }
        return DEFAULT_PICTURE_ID;
    }

    public void setPicture(Blob picture) {
        this.picture = picture;
    }
}
