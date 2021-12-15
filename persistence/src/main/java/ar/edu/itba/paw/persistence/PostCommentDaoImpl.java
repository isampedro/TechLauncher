package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class PostCommentDaoImpl implements PostCommentDao{

    @PersistenceContext
    private EntityManager em;


    @Override
    public Optional<PostComment> findById(long postCommentId) {
        return Optional.ofNullable(em.find(PostComment.class, postCommentId));
    }

    @Override
    public List<PostComment> getByPost(long postId, long page, long pageSize) {

        Query pagingQuery = em.createNativeQuery("SELECT post_comment_id from post_comments as pc where pc.post_id = " +  String.valueOf(postId) + " LIMIT " + String.valueOf(pageSize) + " OFFSET " + String.valueOf((page-1)*pageSize));

        @SuppressWarnings("unchecked")
        List<Long> resultList = ((List<Number>)pagingQuery.getResultList()).stream().map(Number::longValue).collect(Collectors.toList());

        if(!resultList.isEmpty()) {
            TypedQuery<PostComment> query = em.createQuery("from PostComment as pc where pc.id in (:resultList)", PostComment.class);
            query.setParameter("resultList", resultList);
            return query.getResultList();
        }else{
            return Collections.emptyList();
        }

    }

    @Override
    public PostComment insertPostComment(long postId, long userId, String description, Long reference) {
        final PostComment postComment = new PostComment();
        Date ts = new Date(System.currentTimeMillis());
        postComment.setPost(em.getReference(Post.class, postId));
        postComment.setUser(em.getReference(User.class, userId));
        postComment.setDescription(description);
        postComment.setReference(reference);
        postComment.setTimestamp(ts);
        em.persist(postComment);
        return postComment;

    }

    @Override
    public void deletePostComment(long postCommentId) {
        em.remove(em.getReference(PostComment.class,postCommentId));
    }
}
