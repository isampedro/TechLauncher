package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.CommentVote;
import ar.edu.itba.paw.models.PostComment;
import ar.edu.itba.paw.models.PostCommentVote;
import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Optional;

@Repository
public class PostCommentVoteDaoImpl implements PostCommentVoteDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<PostCommentVote> getByPostCommentAndUser(long postCommentId, long userId) {
        final TypedQuery<PostCommentVote> query = em.createQuery("from PostCommentVote as pcv where pcv.postComment.id = :postCommentId and pcv.user.id = :userId", PostCommentVote.class);
        query.setParameter("postCommentId", postCommentId);
        query.setParameter("userId", userId);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public PostCommentVote insert(long postCommentId, long userId, int vote) {

        PostCommentVote postCommentVote = new PostCommentVote();
        postCommentVote.setPostComment(em.getReference(PostComment.class,postCommentId));
        postCommentVote.setUser(em.getReference(User.class, userId));
        postCommentVote.setVote(vote);

        em.persist(postCommentVote);
        return postCommentVote;
                
    }

    @Override
    public void delete(long postCommentVoteId) {
        em.remove(em.getReference(PostCommentVote.class, postCommentVoteId));
    }

    @Override
    public Optional<PostCommentVote> update(long postCommentVoteId, int vote) {
        PostCommentVote postCommentVote = em.find(PostCommentVote.class, postCommentVoteId);
        postCommentVote.setVote(vote);
        em.merge(postCommentVote);

        return Optional.of(postCommentVote);
    }
}
