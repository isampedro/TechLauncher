package ar.edu.itba.paw.service;

import ar.edu.itba.paw.models.CommentVote;
import ar.edu.itba.paw.models.PostVote;

import java.util.List;
import java.util.Optional;

public interface PostVoteService {

    List<PostVote> getByUser(long userId, long page, long pageSize);
    Optional<PostVote> getByPostAndUser(long postId, long userId);

    PostVote insert(long postId, long userId, int vote);
    void delete(long postVoteId);
    Optional<PostVote> update(long postVoteId, int vote);

}
