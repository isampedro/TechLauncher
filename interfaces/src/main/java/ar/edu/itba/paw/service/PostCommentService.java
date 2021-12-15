package ar.edu.itba.paw.service;

import ar.edu.itba.paw.models.PostComment;

import java.util.List;
import java.util.Optional;

public interface PostCommentService {

    Optional<PostComment> getById(long postCommentId);
    List<PostComment> getByPost(long postId, long page);

    PostComment insertPostComment(long postId, long userId, String description, Long reference);
    void deletePostComment(long postCommentId);

    void vote(long postCommentId, long userId, int voteSign);

}
