package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.PostComment;

import java.util.List;
import java.util.Optional;

public interface PostCommentDao {

    Optional<PostComment> findById(long postCommentId);
    List<PostComment> getByPost(long postId, long page, long pageSize);

    PostComment insertPostComment(long postId, long userId, String description, Long reference);
    void deletePostComment(long postCommentId);

}
