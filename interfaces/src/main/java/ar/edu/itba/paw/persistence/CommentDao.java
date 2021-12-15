package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentDao {
    Optional<Comment> getById(long contentId);
    List<Comment> getCommentsByFramework(long frameworkId,Long userId);
    List<Comment> getCommentsWithoutReferenceByFramework(long frameworkId, long page, long pageSize);
    List<Comment> getCommentsByUser(long userId, long page, long pageSize);
    Optional<Integer> getCommentsCountByUser(long userId);
    Comment insertComment(long frameworkId, long userId, String description, Long reference);
    void deleteComment(long commentId);
    Optional<Comment> changeComment(long commentId, String description);

}
