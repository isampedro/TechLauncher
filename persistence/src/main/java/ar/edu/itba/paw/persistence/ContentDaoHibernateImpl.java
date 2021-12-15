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
public class ContentDaoHibernateImpl implements  ContentDao{
    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Content> getById(long contentId) {
        return Optional.ofNullable(em.find(Content.class, contentId));
    }

    @Override
    public List<Content> getContentByFrameworkAndType(long frameworkId, ContentTypes type, long page, long pageSize) {

        Query pagingQuery = em.createNativeQuery("SELECT content_id FROM content c WHERE c.framework_id = "+ String.valueOf(frameworkId) + " and c.type ='" + type + "' LIMIT " + String.valueOf(pageSize) + " OFFSET " + String.valueOf((page-1)*pageSize));
        @SuppressWarnings("unchecked")
        List<Long> resultList = ((List<Number>)pagingQuery.getResultList()).stream().map(Number::longValue).collect(Collectors.toList());

        if(!resultList.isEmpty()) {
            TypedQuery<Content> query = em.createQuery("from Content as c where c.contentId in (:resultList)", Content.class);
            query.setParameter("resultList", resultList);
            return query.getResultList();
        }else{
            return Collections.emptyList();
        }

    }

    @Override
    public List<Content> getContentByUser(long userId, long page, long pagesize) {

        Query pagingQuery = em.createNativeQuery("SELECT content_id FROM content c WHERE c.user_id = "+ String.valueOf(userId) + " LIMIT " + String.valueOf(pagesize) + " OFFSET " + String.valueOf((page-1)*pagesize));
        @SuppressWarnings("unchecked")
        List<Long> resultList = ((List<Number>)pagingQuery.getResultList()).stream().map(Number::longValue).collect(Collectors.toList());

        if(!resultList.isEmpty()) {
            TypedQuery<Content> query = em.createQuery("from Content as c where c.contentId in (:resultList)", Content.class);
            query.setParameter("resultList", resultList);
            return query.getResultList();
        }else{
            return Collections.emptyList();
        }

    }

    @Override
    public Optional<Long> getContentCountByUser(long userId) {
        final TypedQuery<Long> query = em.createQuery("SELECT count(c.contentId) FROM Content c WHERE c.user.id = :userId", Long.class );
        query.setParameter("userId",userId);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public List<Content> getContentByFrameworkAndTypeAndTitle(long frameworkId, ContentTypes type, String title) {
        final TypedQuery<Content> query = em.createQuery("FROM Content c WHERE c.framework.id = :frameworkId and c.type = :type and c.title = :title", Content.class);
        query.setParameter("frameworkId", frameworkId);
        query.setParameter("type", type);
        query.setParameter("title", title);
        return query.getResultList();
    }

    @Override
    public Content insertContent(long frameworkId, long userId, String title, String link, ContentTypes type) {
        Content content = new Content(em.getReference(Framework.class,frameworkId),em.getReference(User.class,userId),title,link,type);
        Date ts = new Date(System.currentTimeMillis());
        content.setTimestamp(ts);
        em.persist(content);
        return content;
    }

    @Override
    public int deleteContent(long contentId) {
        em.remove(em.find(Content.class, contentId));
        return 1;
    }

    @Override
    public Optional<Content> changeContent(long contentId, String title, String link, ContentTypes type) {
        Content content = em.find(Content.class, contentId);
        content.setTitle(title);
        content.setLink(link);
        content.setType(type);
        em.merge(content);
        return Optional.of(content);
    }
}
