package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "frameworks")
public class Framework {

    public static final long DEFAULT_PICTURE_ID = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "frameworks_framework_id_seq")
    @SequenceGenerator(sequenceName = "frameworks_framework_id_seq", name = "frameworks_framework_id_seq", allocationSize = 1)
    @Column(name = "framework_id")
    private long id;

    @Column(name = "framework_name", length = 50)
    private String name;

    @Column(name = "category", length = 50)
    @Enumerated(EnumType.STRING)
    private FrameworkCategories category;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "introduction", length = 1500)
    private String introduction;

    @Column(name = "type", length = 100)
    @Enumerated(EnumType.STRING)
    private FrameworkType type;

    /* References other relation mapped in User */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author", nullable = false)
    private User author;

    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date publishDate;

    /* Refers other relation mapped in UserBlob */
    @OneToOne(optional = true, fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "picture_id", referencedColumnName = "blob_id")
    private Blob picture;

    /* Relationships */

    /* References other relation mapped in Comment */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "framework",cascade = CascadeType.REMOVE)
    private List<Comment> comments;

    /* References other relation mapped in Content */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "framework",cascade = CascadeType.REMOVE)
    private List<Content> contents;

    /* References other relation mapped in Framework Votes */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "framework",cascade = CascadeType.REMOVE)
    private List<FrameworkVote> frameworkVotes;

    /* References other relation mapped in Verify User */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "framework",cascade = CascadeType.REMOVE)
    private List<VerifyUser> verifyUsers;

    /* Transient attributes loaded on Post Load */

    @Transient
    private double stars = 0;

    @Transient
    private int votesCant;

    @Transient
    private Date lastComment;

    @Transient
    private int commentsAmount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Framework framework = (Framework) o;
        return id == framework.id &&
                name.equals(framework.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public Framework(User author, String name, FrameworkCategories category, String description, String introduction, FrameworkType type, Date publishDate, Blob picture) {
        this(author, name, category, description, introduction, type, publishDate);
        this.picture = picture;
    }

    public Framework(User author, String name, FrameworkCategories category, String description, String introduction, FrameworkType type, Date publishDate) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.introduction = introduction;
        this.type = type;
        this.author = author;
        this.publishDate = publishDate;
    }

    public Framework(){
        /* Empty constructor */
    }

    @PostLoad
    public void postLoad() {
        if(frameworkVotes != null && !frameworkVotes.isEmpty()) {
            double rating = 0;
            for (FrameworkVote vote : frameworkVotes) {
                rating += vote.getStars();
            }
            stars = rating / frameworkVotes.size();
        }
        votesCant = frameworkVotes.size();
        if(comments != null && !comments.isEmpty()) {
            lastComment = Collections.max(comments, Comparator.comparingDouble((x) -> x.getTimestamp().getTime())).getTimestamp();
            commentsAmount = comments.size();
        } else
            commentsAmount = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FrameworkCategories getCategory() {
        return category;
    }

    public void setCategory(FrameworkCategories category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public FrameworkType getType() {
        return type;
    }

    public void setType(FrameworkType type) {
        this.type = type;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    public List<FrameworkVote> getFrameworkVotes() {
        return frameworkVotes;
    }

    public void setFrameworkVotes(List<FrameworkVote> frameworkVotes) {
        this.frameworkVotes = frameworkVotes;
    }

    public double getStars() {
        return stars;
    }
    public String getStarsFormated(){
        return String.format("%.2f", stars);
    }

    public void setStars(double stars) {
        this.stars = stars;
    }

    public int getVotesCant() {
        return votesCant;
    }

    public void setVotesCant(int votesCant) {
        this.votesCant = votesCant;
    }

    public Date getLastComment() {
        return lastComment;
    }

    public void setLastComment(Date lastComment) {
        this.lastComment = lastComment;
    }

    public int getCommentsAmount() {
        return commentsAmount;
    }

    public void setCommentsAmount(int commentsAmount) {
        this.commentsAmount = commentsAmount;
    }

    public List<Content> getContentByType(ContentTypes type){
        List<Content> content = new ArrayList<>();
        for (Content ct: contents) {
            if(ct.getType().equals(type))
                content.add(ct);
        }
        return content;
    }

    public long getBooksAmount() {
        return getContentAmount(ContentTypes.book);
    }

    public long getCoursesAmount() {
        return getContentAmount(ContentTypes.course);
    }

    public long getTutorialsAmount() {
        return getContentAmount(ContentTypes.tutorial);
    }

    public long getContentAmount(ContentTypes type) {
        return contents.stream().filter((x) -> x.getType() == type).count();
    }
    public long getCommentsWithoutReferenceAmount(){
        return comments.stream().filter((x) -> x.getReference() == null).count();
    }
    public Optional<FrameworkVote> getVoteOfUser(long userId){
        return frameworkVotes.stream().filter((x)->x.getUserId()==userId).findFirst();
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
