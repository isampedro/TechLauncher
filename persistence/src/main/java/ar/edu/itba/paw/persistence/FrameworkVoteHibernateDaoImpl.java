package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Framework;
import ar.edu.itba.paw.models.FrameworkVote;
import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class FrameworkVoteHibernateDaoImpl implements FrameworkVoteDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<FrameworkVote> getAllByUser(long userId, long page, long pageSize) {

        Query pagingQuery = em.createNativeQuery("SELECT vote_id from framework_votes as fv where fv.user_id = " +  String.valueOf(userId) + " LIMIT " + String.valueOf(pageSize) + " OFFSET " + String.valueOf((page-1)*pageSize));

        @SuppressWarnings("unchecked")
        List<Long> resultList = ((List<Number>)pagingQuery.getResultList()).stream().map(Number::longValue).collect(Collectors.toList());

        if(!resultList.isEmpty()) {
            TypedQuery<FrameworkVote> query = em.createQuery("from FrameworkVote as fv where fv.id in (:resultList)", FrameworkVote.class);
            query.setParameter("resultList", resultList);
            return query.getResultList();
        }else{
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Integer> getAllCountByUser(long userId) {
        final TypedQuery<FrameworkVote> query = em.createQuery("select fv from FrameworkVote fv where fv.user.id= :userId", FrameworkVote.class);
        query.setParameter("userId", userId);
        return Optional.of(query.getResultList().size());
    }

    @Override
    public FrameworkVote insert(long frameworkId, long userId, int stars) {
        final FrameworkVote frameworkVote = new FrameworkVote( em.getReference(Framework.class, frameworkId), em.getReference(User.class, userId), stars);
        em.persist(frameworkVote);
        return frameworkVote;
    }

    @Override
    public void delete(long voteId) { em.remove(em.getReference(FrameworkVote.class,voteId)); }

    @Override
    public Optional<FrameworkVote> update(long voteId, int stars) {
        FrameworkVote frameworkVote = em.find(FrameworkVote.class, voteId);
        frameworkVote.setStars(stars);
        em.merge(frameworkVote);
        return Optional.of(frameworkVote);
    }
}
