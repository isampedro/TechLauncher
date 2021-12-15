package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(name = "post_tags")
public class PostTag {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "posts_tag_id_seq")
    @SequenceGenerator(sequenceName = "posts_tag_id_seq", name = "posts_tag_id_seq", allocationSize = 1)
    @Column(name = "tag_id")
    private Long tagId;

    @Column(name="tag_name", length = 50, nullable = false)
    private String tagName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id",nullable = false)
    private Post post;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PostTagType type;

    public PostTag() {
        //For Hibernate
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public PostTagType getType() {
        return type;
    }

    public void setType(PostTagType type) {
        this.type = type;
    }
}
