package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
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
public class VerifyUserHibernateDao implements VerifyUserDao {
    static private final boolean PENDING_DEFAULT = true;

    @PersistenceContext
    private EntityManager em;

    @Override
    public VerifyUser create(User user, Framework framework, Comment comment) {
        final VerifyUser verifyUser = new VerifyUser(user, framework, comment, PENDING_DEFAULT);
        em.persist(verifyUser);
        return verifyUser;
    }

    @Override
    public Optional<VerifyUser> getById(long verificationId) {
        return Optional.ofNullable(em.find(VerifyUser.class, verificationId));
    }

    @Override
    public List<VerifyUser> getVerifyForCommentByPending(boolean pending, long page, long pageSize) {

        Query pagingQuery;

        if (!pending) {
            pagingQuery = em.createNativeQuery("SELECT verification_id from verify_users as vu where vu.pending = " + String.valueOf(pending) + " LIMIT " + String.valueOf(pageSize) + " OFFSET " + String.valueOf((page - 1) * pageSize));

        } else {
            pagingQuery = em.createNativeQuery("SELECT verification_id from verify_users as vu where vu.pending = " + String.valueOf(pending) + " and vu.comment_id is not null LIMIT " + String.valueOf(pageSize) + " OFFSET " + String.valueOf((page - 1) * pageSize));
        }
        @SuppressWarnings("unchecked")
        List<Long> resultList = ((List<Number>) pagingQuery.getResultList()).stream().map(Number::longValue).collect(Collectors.toList());

        if (!resultList.isEmpty()) {
            TypedQuery<VerifyUser> query = em.createQuery("from VerifyUser as vu where vu.verificationId in (:resultList)", VerifyUser.class);
            query.setParameter("resultList", resultList);
            return query.getResultList();
        } else {
            return Collections.emptyList();
        }

    }

    @Override
    public List<VerifyUser> getVerifyByPendingAndFramework(boolean pending, List<Long> frameworkIds, long page, long pageSize) {

        String ids = frameworkIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        Query pagingQuery;
        if(!pending) {
            pagingQuery = em.createNativeQuery("SELECT verification_id from verify_users as vu WHERE vu.framework_id IN (" + ids + ") and vu.pending = " + pending + " LIMIT " + String.valueOf(pageSize) + " OFFSET " + String.valueOf((page - 1) * pageSize));
        } else {
            pagingQuery = em.createNativeQuery("SELECT verification_id from verify_users as vu WHERE vu.framework_id IN (" + ids + ") and vu.pending = " + pending + " and vu.comment_id is not null LIMIT " + String.valueOf(pageSize) + " OFFSET " + String.valueOf((page - 1) * pageSize));
        }

        @SuppressWarnings("unchecked")
        List<Long> resultList = ((List<Number>)pagingQuery.getResultList()).stream().map(Number::longValue).collect(Collectors.toList());

        if(!resultList.isEmpty()) {
            TypedQuery<VerifyUser> query = em.createQuery("from VerifyUser as vu where vu.verificationId in (:resultList)", VerifyUser.class);
            query.setParameter("resultList", resultList);
            return query.getResultList();
        }else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<VerifyUser> getApplicantsByPending(boolean pending, long page, long pageSize) {

        Query pagingQuery = em.createNativeQuery("SELECT verification_id from verify_users as vu where vu.pending = " + String.valueOf(pending) + " and vu.comment_id is null LIMIT " + String.valueOf(pageSize) + " OFFSET " + String.valueOf((page - 1) * pageSize));

        @SuppressWarnings("unchecked")
        List<Long> resultList = ((List<Number>) pagingQuery.getResultList()).stream().map(Number::longValue).collect(Collectors.toList());

        if (!resultList.isEmpty()) {
            TypedQuery<VerifyUser> query = em.createQuery("from VerifyUser as vu where vu.verificationId in (:resultList)", VerifyUser.class);
            query.setParameter("resultList", resultList);
            return query.getResultList();
        } else {
            return Collections.emptyList();
        }

    }

    @Override
    public List<VerifyUser> getApplicantsByFrameworks(List<Long> frameworksIds, long page, long pageSize) {

        String ids = frameworksIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        Query pagingQuery = em.createNativeQuery("SELECT verification_id from verify_users as vu WHERE vu.framework_id IN (" + ids + ") and vu.pending = true and vu.comment_id is null LIMIT " + pageSize + " OFFSET " + (page - 1) * pageSize);

        @SuppressWarnings("unchecked")
        List<Long> resultList = ((List<Number>) pagingQuery.getResultList()).stream().map(Number::longValue).collect(Collectors.toList());

        if (!resultList.isEmpty()) {
            TypedQuery<VerifyUser> query = em.createQuery("from VerifyUser as vu where vu.verificationId in (:resultList)", VerifyUser.class);
            query.setParameter("resultList", resultList);
            return query.getResultList();
        } else {
            return Collections.emptyList();
        }

    }

    @Override
    public Optional<Integer> getVerifyForCommentByPendingAmount(boolean pending) {
        final TypedQuery<VerifyUser> query;
        if (!pending)
            query = em.createQuery("from VerifyUser as vu where vu.pending = :pending", VerifyUser.class);
        else
            query = em.createQuery("from VerifyUser as vu where vu.pending = :pending and vu.comment is not null", VerifyUser.class);
        query.setParameter("pending", pending);
        return Optional.of(query.getResultList().size());
    }

    @Override
    public Optional<Integer> getVerifyForCommentByFrameworkAmount(List<Long> frameworksIds, boolean pending) {
        final TypedQuery<VerifyUser> query = em.createQuery("from VerifyUser as vu where vu.framework.id in (:frameworksIds) and vu.pending = :pending", VerifyUser.class);
        query.setParameter("frameworksIds", frameworksIds);
        query.setParameter("pending", pending);
        return Optional.of(query.getResultList().size());
    }

    @Override
    public Optional<Integer> getApplicantsByPendingAmount(boolean pending) {
        final TypedQuery<VerifyUser> query = em.createQuery("from VerifyUser as vu where vu.pending = :pending and vu.comment is null", VerifyUser.class);
        query.setParameter("pending", pending);
        return Optional.of(query.getResultList().size());
    }

    @Override
    public Optional<Integer> getApplicantsByFrameworkAmount(List<Long> frameworksIds, boolean pending) {
        final TypedQuery<VerifyUser> query = em.createQuery("from VerifyUser as vu where vu.framework.id in :frameworksIds and vu.comment is null and vu.pending = :pending", VerifyUser.class);
        query.setParameter("frameworksIds", frameworksIds);
        query.setParameter("pending", pending);
        return Optional.of(query.getResultList().size());
    }

    @Override
    public void delete(long verificationId) {
        em.remove(em.getReference(VerifyUser.class, verificationId));
    }

    @Override
    public void deleteVerificationByUser(long userId) {
        em.createQuery("DELETE FROM VerifyUser vu WHERE vu.user.id = :userId ")
                .setParameter("userId", userId)
                .executeUpdate();
    }

    @Override
    public void verify(long verificationId) {
        VerifyUser verifyUser = em.find(VerifyUser.class, verificationId);
        verifyUser.setPending(false);
        em.merge(verifyUser);
    }

    @Override
    public Integer getVerifyByPendingAndFrameworksAmount(boolean pending, List<Long> frameworkIds) {
        final TypedQuery<VerifyUser> query;
        if (!pending)
            query = em.createQuery("from VerifyUser as vu where vu.pending = :pending and vu.framework.id in (:frameworksIds)", VerifyUser.class);
        else
            query = em.createQuery("from VerifyUser as vu where vu.pending = :pending and vu.comment is not null and vu.framework.id in (:frameworksIds)", VerifyUser.class);
        query.setParameter("pending", pending);
        query.setParameter("frameworksIds", frameworkIds);
        return query.getResultList().size();
    }
}
