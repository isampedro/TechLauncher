package ar.edu.itba.paw.webapp.dto;

import java.util.List;

public class PostsDTO {
    private List<PostDTO> posts;
    private Integer amount;
    private Integer currentPage;
    private Integer pageSize;
    public List<PostDTO> getPosts() {
        return posts;
    }
    public void setPosts(List<PostDTO> posts) {
        this.posts = posts;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
