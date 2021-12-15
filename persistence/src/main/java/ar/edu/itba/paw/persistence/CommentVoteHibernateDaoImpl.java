package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.CommentVote;
import ar.edu.itba.paw.models.Framework;
import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class CommentVoteHibernateDaoImpl implements CommentVoteDao{

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<CommentVote> getByCommentAndUser(long commentId, long userId) {

        final TypedQuery<CommentVote> query = em.createQuery("from CommentVote as c where c.comment.id = :commentId and c.user.id = :userId", CommentVote.class);
        query.setParameter("commentId", commentId);
        query.setParameter("userId", userId);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public CommentVote insert(long commentId, long userId, int vote) {

        final CommentVote commentVote = new CommentVote( em.getReference(Comment.class, commentId), em.getReference(User.class, userId), vote);

        em.persist(commentVote);
        return commentVote;

    }

    @Override
    public void delete(long voteId) {
        em.remove(em.getReference(CommentVote.class,voteId));
    }

    @Override
    public Optional<CommentVote> update(long voteId, int vote) {

        CommentVote commentVote = em.find(CommentVote.class, voteId);
        commentVote.setVote(vote);
        em.merge(commentVote);

        return Optional.of(commentVote);
    }
}
