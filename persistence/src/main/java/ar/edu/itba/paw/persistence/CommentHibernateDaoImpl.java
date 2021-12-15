package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Framework;
import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class CommentHibernateDaoImpl implements CommentDao {
    static private final Integer VOTES_DEFAULT = 0;

    @PersistenceContext
    private EntityManager em;


    @Override
    public Comment insertComment(long frameworkId, long userId, String description, Long reference) {
        Date ts = new Date(System.currentTimeMillis());
        final Comment comment = new Comment();
        comment.setFramework(em.getReference(Framework.class, frameworkId));
        comment.setUser(em.getReference(User.class, userId));
        comment.setDescription(description);
        comment.setTimestamp(ts);
        comment.setReference(reference);
        em.persist(comment);
        return comment;
    }

    @Override
    public Optional<Comment> getById(long commentId) {
        return Optional.ofNullable(em.find(Comment.class, commentId));
    }

    @Override
    public List<Comment> getCommentsByFramework(long frameworkId,Long userId) {

        if(userId!=null) {
            final TypedQuery<Comment> query = em.createQuery("from Comment as c where c.user.id = :userId and c.framework.id = :frameworkId ", Comment.class);
            query.setParameter("userId", userId);
            query.setParameter("frameworkId", frameworkId);
            return query.getResultList();
        }
        final TypedQuery<Comment> query = em.createQuery("from Comment as c where c.framework.id = :frameworkId ", Comment.class);
        query.setParameter("frameworkId", frameworkId);

        return query.getResultList();
    }

    @Override
    public List<Comment> getCommentsWithoutReferenceByFramework(long frameworkId, long page, long pageSize) {

        Query pagingQuery = em.createNativeQuery("SELECT comment_id from comments as c where c.framework_id = " +  String.valueOf(frameworkId) + "and c.reference is null LIMIT " + String.valueOf(pageSize) + " OFFSET " + String.valueOf((page-1)*pageSize));

        @SuppressWarnings("unchecked")
        List<Long> resultList = ((List<Number>)pagingQuery.getResultList()).stream().map(Number::longValue).collect(Collectors.toList());

        if(!resultList.isEmpty()) {
            TypedQuery<Comment> query = em.createQuery("from Comment as c where c.commentId in (:resultList)", Comment.class);
            query.setParameter("resultList", resultList);
            return query.getResultList();
        }else{
            return Collections.emptyList();
        }

    }

    @Override
    public List<Comment> getCommentsByUser(long userId, long page, long pageSize ) {
        Query pagingQuery = em.createNativeQuery("SELECT comment_id FROM comments WHERE user_id = " + String.valueOf(userId)+ " LIMIT " + String.valueOf(pageSize) + " OFFSET " + String.valueOf((page-1)*pageSize));
        @SuppressWarnings("unchecked")
        List<Long> resultList = ((List<Number>)pagingQuery.getResultList()).stream().map(Number::longValue).collect(Collectors.toList());

        if(!resultList.isEmpty()) {
            TypedQuery<Comment> query = em.createQuery("from Comment as c where c.commentId in (:resultList)", Comment.class);
            query.setParameter("resultList", resultList);
            return query.getResultList();
        }else{
            return Collections.emptyList();
        }



    }

    @Override
    public Optional<Integer> getCommentsCountByUser(long userId){
        final TypedQuery<Comment> query = em.createQuery("from Comment as c where c.user.id = :userId", Comment.class);
        query.setParameter("userId", userId);
        return Optional.of(query.getResultList().size());
    }

    @Override
    public void deleteComment(long commentId) {
        em.remove(em.getReference(Comment.class,commentId));
    }

    @Override
    public Optional<Comment> changeComment(long commentId, String description) {

        Comment comment = em.find(Comment.class, commentId);
        comment.setDescription(description);
        em.merge(comment);
        return Optional.ofNullable(comment);
    }

}
