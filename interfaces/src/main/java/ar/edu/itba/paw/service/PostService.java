package ar.edu.itba.paw.service;

import ar.edu.itba.paw.models.Post;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PostService {
    Optional<Post> findById(long postId);
    List<Post> getAll(long page, long pageSize);
    List<Post> getPostsByUser(long userId, long page);
    Optional<Integer> getPostsCountByUser(long userId);
    List<Post> getByTagName(String tagName, long page, long pageSize);
    Post insertPost(long userId, String title, String description);
    void deletePost(long postId);
    Optional<Post> update(long postId, String title, String description);

    void vote(long postId, long userId, int voteSign);
    int getPostsAmount();

    public boolean isPostInvalid(String title, String description, List<String> names, List<String> categories, List<String> types);
}
