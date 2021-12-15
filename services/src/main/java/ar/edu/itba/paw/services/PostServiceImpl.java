package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.PostVote;
import ar.edu.itba.paw.persistence.PostDao;
import ar.edu.itba.paw.persistence.PostVoteDao;
import ar.edu.itba.paw.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

    private final long PAGE_SIZE_USER_PROFILE = 5;
    final private Integer MIN_TITLE_LEN = 3, MIN_DESCRIPTION_LEN = 0, MAX_TITLE_LEN = 200, MAX_DESCRIPTION_LEN = 5000;

    @Autowired
    private PostDao postDao;

    @Autowired
    private PostVoteDao postVoteDao;

    @Transactional(readOnly = true)
    @Override
    public Optional<Post> findById(long postId) {
        return postDao.findById(postId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Post> getAll(long page, long pageSize) {
        return postDao.getAllByPage(page, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Post> getPostsByUser(long userId, long page) {
        return postDao.getPostsByUser(userId, page, PAGE_SIZE_USER_PROFILE);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Integer> getPostsCountByUser( long userId ){
        return postDao.getPostsCountByUser(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Post> getByTagName(String category, long page, long pageSize) {
        return postDao.getByTagName(category,page, pageSize);
    }

    @Transactional
    @Override
    public Post insertPost( long userId, String title, String description) {
        return postDao.insertPost(userId, title, description);
    }

    @Transactional
    @Override
    public void deletePost(long postId) {
        postDao.deletePost(postId);
    }

    @Transactional
    @Override
    public Optional<Post> update(long postId, String title, String description) {
        return postDao.update(postId, title, description);
    }

    @Transactional
    @Override
    public void vote(long postId, long userId, int voteSign) {

        Optional<PostVote> vote = postVoteDao.getByPostAndUser(postId,userId);
        if(vote.isPresent()){
            if(vote.get().getVote()==voteSign)
                postVoteDao.delete(vote.get().getPostVoteId());
            else
                postVoteDao.update(vote.get().getPostVoteId(),voteSign);
        }else {
            postVoteDao.insert(postId, userId, voteSign);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public int getPostsAmount() {
        return postDao.getAmount();
    }

    @Override
    public boolean isPostInvalid(String title, String description, List<String> names, List<String> categories, List<String> types) {
        if (title == null) {
            return true;
        }
        if (title.length() < MIN_TITLE_LEN || title.length() > MAX_TITLE_LEN) {
            return true;
        }

        if (description == null) {
            return true;
        }

        if (description.length() < MIN_DESCRIPTION_LEN || description.length() > MAX_DESCRIPTION_LEN) {
            return true;
        }

        return types.isEmpty() && categories.isEmpty() && names.isEmpty();
    }
}
