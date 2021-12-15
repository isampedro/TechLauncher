package ar.edu.itba.paw.webapp.dto;

import java.util.List;

public class SearchDTO {
    private List<FrameworkDTO> frameworks;
    private List<PostDTO> posts;
    private Integer frameworksAmount, postsAmount;
    private String toSearch;
    private List<String> types, categories;
    private Integer starsLeft, starsRight;
    private boolean nameFlag;
    private Integer order, sort;
    private Integer lastComment, lastUpdate;
    private Long page, postsPage;
    private boolean isPost;

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<FrameworkDTO> getFrameworks() {
        return frameworks;
    }

    public void setFrameworks(List<FrameworkDTO> frameworks) {
        this.frameworks = frameworks;
    }

    public List<PostDTO> getPosts() {
        return posts;
    }

    public void setPosts(List<PostDTO> posts) {
        this.posts = posts;
    }

    public Integer getFrameworksAmount() {
        return frameworksAmount;
    }

    public void setFrameworksAmount(Integer frameworksAmount) {
        this.frameworksAmount = frameworksAmount;
    }

    public Integer getPostsAmount() {
        return postsAmount;
    }

    public void setPostsAmount(Integer postsAmount) {
        this.postsAmount = postsAmount;
    }

    public String getToSearch() {
        return toSearch;
    }

    public void setToSearch(String toSearch) {
        this.toSearch = toSearch;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public Integer getStarsLeft() {
        return starsLeft;
    }

    public void setStarsLeft(Integer starsLeft) {
        this.starsLeft = starsLeft;
    }

    public Integer getStarsRight() {
        return starsRight;
    }

    public void setStarsRight(Integer starsRight) {
        this.starsRight = starsRight;
    }

    public boolean isNameFlag() {
        return nameFlag;
    }

    public void setNameFlag(boolean nameFlag) {
        this.nameFlag = nameFlag;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getLastComment() {
        return lastComment;
    }

    public void setLastComment(Integer lastComment) {
        this.lastComment = lastComment;
    }

    public Integer getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Integer lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public Long getPostsPage() {
        return postsPage;
    }

    public void setPostsPage(Long postsPage) {
        this.postsPage = postsPage;
    }

    public boolean isPost() {
        return isPost;
    }

    public void setPost(boolean post) {
        isPost = post;
    }
}
