package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.CommentVote;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface CommentVoteDao {
    Optional<CommentVote> getByCommentAndUser(long commentId, long userId);
    CommentVote insert(long commentId, long userId, int vote);
    void delete(long voteId);
    Optional<CommentVote> update(long voteId, int vote);
}
