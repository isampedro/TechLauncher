package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Content;
import ar.edu.itba.paw.models.ReportComment;
import ar.edu.itba.paw.models.ReportContent;
import ar.edu.itba.paw.models.User;
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
public class ReportContentHibernateDaoImpl implements ReportContentDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<ReportContent> getAll(long page, long pageSize) {
        Query pagingQuery = em.createNativeQuery("SELECT report_id FROM content_report " + " LIMIT " + String.valueOf(pageSize) + " OFFSET " + String.valueOf((page-1)*pageSize));
        @SuppressWarnings("unchecked")
        List<Long> resultList = ((List<Number>)pagingQuery.getResultList()).stream().map(Number::longValue).collect(Collectors.toList());

        if(!resultList.isEmpty()) {
            TypedQuery<ReportContent> query = em.createQuery("from ReportContent as c where c.reportId in (:resultList)", ReportContent.class);
            query.setParameter("resultList", resultList);
            return query.getResultList();
        }else{
            return Collections.emptyList();
        }
    }

    @Override
    public List<ReportContent> getByFrameworks(List<Long> frameworksIds, long page, long pageSize) {

        String ids = frameworksIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        Query pagingQuery = em.createNativeQuery("SELECT report_id from content_report as cr join content as c ON c.content_id = cr.content_id WHERE c.framework_id IN (" + ids + ") LIMIT " + String.valueOf(pageSize) + " OFFSET " + String.valueOf((page-1)*pageSize));

        @SuppressWarnings("unchecked")
        List<Long> resultList = ((List<Number>)pagingQuery.getResultList()).stream().map(Number::longValue).collect(Collectors.toList());

        if(!resultList.isEmpty()) {
            TypedQuery<ReportContent> query = em.createQuery("from ReportContent as rc where rc.reportId in (:resultList)", ReportContent.class);
            query.setParameter("resultList", resultList);
            return query.getResultList();
        }else{
            return Collections.emptyList();
        }

    }

    @Override
    public Optional<Integer> getAllReportsAmount() {
        TypedQuery<ReportContent> query = em.createQuery("from ReportContent", ReportContent.class);
        return Optional.of(query.getResultList().size());
    }

    @Override
    public Optional<Integer> getReportsAmount(List<Long> frameworksIds) {
        final TypedQuery<ReportContent> query = em.createQuery("FROM ReportContent rc where rc.content.framework.id in :frameworksIds", ReportContent.class);
        query.setParameter("frameworksIds", frameworksIds);
        return Optional.of(query.getResultList().size());
    }

    @Override
    public void add(long contentId, long userId, String description) {
        final ReportContent rc = new ReportContent(em.getReference(Content.class,contentId), em.getReference(User.class,userId), description);
        em.persist(rc);
    }

    @Override
    public void delete(long reportId) {
        em.remove(em.getReference(ReportContent.class, reportId));
    }

    @Override
    public void deleteByContent(long contentId) {
        final TypedQuery<Long> query = em.createQuery("select rc.reportId from ReportContent rc where rc.content.contentId = :contentId", Long.class);
        query.setParameter("contentId", contentId);
        for (Long l : query.getResultList()) {
            delete(l);
        }
    }
}
