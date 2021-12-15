package ar.edu.itba.paw.service;

import ar.edu.itba.paw.models.Framework;
import ar.edu.itba.paw.models.FrameworkVote;

import java.util.List;
import java.util.Optional;

public interface FrameworkVoteService {
    List<FrameworkVote> getAllByUser(long userId, long page);

    Optional<Integer> getAllCountByUser(long userId);

    FrameworkVote insert(Framework framework, long userId, int stars);
    void delete(long voteId);
    Optional<FrameworkVote> update(long voteId, int stars);
}
