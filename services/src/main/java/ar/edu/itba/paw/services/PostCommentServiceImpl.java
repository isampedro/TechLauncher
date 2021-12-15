package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.PostCommentDao;
import ar.edu.itba.paw.persistence.PostCommentVoteDao;
import ar.edu.itba.paw.service.PostCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostCommentServiceImpl implements PostCommentService {
    private final int PAGE_SIZE = 5;

    @Autowired
    private PostCommentDao postCommentDao;

    @Autowired
    private PostCommentVoteDao postCommentVoteDao;

    @Transactional(readOnly = true)
    @Override
    public Optional<PostComment> getById(long postCommentId) {
        return postCommentDao.findById(postCommentId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PostComment> getByPost(long postId, long page) {
        return postCommentDao.getByPost(postId, page, PAGE_SIZE);
    }

    @Transactional
    @Override
    public PostComment insertPostComment(long postId, long userId, String description, Long reference) {
        return postCommentDao.insertPostComment(postId, userId, description, reference);
    }

    @Transactional
    @Override
    public void deletePostComment(long postCommentId) {
        postCommentDao.deletePostComment(postCommentId);

    }

    @Transactional
    @Override
    public void vote(long postCommentId, long userId, int voteSign) {
        Optional<PostCommentVote> vote = postCommentVoteDao.getByPostCommentAndUser(postCommentId, userId);
        if (vote.isPresent()) {
            if (vote.get().getVote() == voteSign)
                postCommentVoteDao.delete(vote.get().getPostCommentVoteId());
            else
                postCommentVoteDao.update(vote.get().getPostCommentVoteId(), voteSign);
        } else {
            postCommentVoteDao.insert(postCommentId, userId, voteSign);
        }
    }
}
