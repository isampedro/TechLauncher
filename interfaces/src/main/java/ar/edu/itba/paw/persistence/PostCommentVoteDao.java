package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.CommentVote;
import ar.edu.itba.paw.models.PostCommentVote;

import java.util.Optional;

public interface PostCommentVoteDao {
    Optional<PostCommentVote> getByPostCommentAndUser(long postCommentId, long userId);

    PostCommentVote insert(long postCommentId, long userId, int vote);
    void delete(long postCommentVoteId);
    Optional<PostCommentVote> update(long postCommentVoteId, int vote);
}
