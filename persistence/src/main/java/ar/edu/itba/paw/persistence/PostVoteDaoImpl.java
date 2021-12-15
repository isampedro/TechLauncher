package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class PostVoteDaoImpl implements PostVoteDao{

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<PostVote> getByUser(long userId, long page, long pageSize) {
        Query pagingQuery = em.createNativeQuery("SELECT post_vote_id FROM post_votes WHERE user_id = " + String.valueOf(userId)+ " LIMIT " + String.valueOf(pageSize) + " OFFSET " + String.valueOf((page-1)*pageSize));
        @SuppressWarnings("unchecked")
        List<Long> resultList = ((List<Number>)pagingQuery.getResultList()).stream().map(Number::longValue).collect(Collectors.toList());

        if(!resultList.isEmpty()) {
            TypedQuery<PostVote> query = em.createQuery("from PostVote as pv where pv.postVoteId in (:resultList)",PostVote.class);
            query.setParameter("resultList", resultList);
            return query.getResultList();
        }else{
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<PostVote> getByPostAndUser(long postId, long userId) {
        final TypedQuery<PostVote> query = em.createQuery("select pv from PostVote pv where pv.post.id = :postId and pv.user.id = :userId", PostVote.class);
        query.setParameter("postId", postId);
        query.setParameter("userId", userId);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public PostVote insert(long postId, long userId, int vote) {
        final PostVote postVote = new PostVote();
        postVote.setPost(em.getReference(Post.class, postId));
        postVote.setUser( em.getReference(User.class, userId));
        postVote.setVote(vote);
        em.persist(postVote);
        return postVote;
    }

    @Override
    public void delete(long postVoteId) {
        em.remove(em.getReference(PostVote.class, postVoteId));
    }

    @Override
    public Optional<PostVote> update(long postVoteId, int vote) {
        PostVote postVote = em.find(PostVote.class, postVoteId);
        postVote.setVote(vote);
        em.merge(postVote);
        return Optional.of(postVote);
    }
}
