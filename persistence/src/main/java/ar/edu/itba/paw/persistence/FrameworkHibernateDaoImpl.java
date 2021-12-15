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
public class FrameworkHibernateDaoImpl implements FrameworkDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Framework> findById(long id) {
        return Optional.ofNullable(em.find(Framework.class, id));
    }

    @Override
    public List<String> getFrameworkNames() {
        final TypedQuery<String> query = em.createQuery("select f.name from Framework f", String.class);
        return query.getResultList();
    }


    @Override
    public List<Framework> getByCategory(FrameworkCategories category, long page, long pageSize) {

        Query pagingQuery = em.createNativeQuery("select framework_id from frameworks f where f.category= '"+ String.valueOf(category) + "' order by f.framework_id  LIMIT " + String.valueOf(pageSize) + " OFFSET " + String.valueOf((page-1)*pageSize));
        @SuppressWarnings("unchecked")
        List<Long> resultList = ((List<Number>)pagingQuery.getResultList()).stream().map(Number::longValue).collect(Collectors.toList());

        if(!resultList.isEmpty()) {
            TypedQuery<Framework> query = em.createQuery("from Framework as f where f.id in (:resultList)", Framework.class);
            query.setParameter("resultList", resultList);
            return query.getResultList();
        }else{
            return Collections.emptyList();
        }
    }

    @Override
    public List<Framework> getAll() {
        final TypedQuery<Framework> query = em.createQuery("select f from Framework f", Framework.class);
        return query.getResultList();
    }

    @Override
    public List<Framework> getUserInterests(long userId) {
        final TypedQuery<Framework> query = em.createQuery("select distinct f from Framework f inner join Content c on f.id=c.framework.id where c.user.id = :userId", Framework.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public List<Framework> getByMinStars(int stars) {
        final TypedQuery<Framework> query = em.createQuery("select f from Framework f left outer join FrameworkVote v on f.id = v.framework.id group by f having coalesce(avg(v.stars), 0) >= :stars", Framework.class);
        Double st = Double.valueOf(stars);
        query.setParameter("stars", st);
        return query.getResultList();
    }


    @Override
    public List<Framework> getByUser(long userId, long page, long pageSize) {

        Query pagingQuery = em.createNativeQuery("select framework_id from frameworks f where f.author= "+ String.valueOf(userId) + " order by f.framework_id LIMIT " + String.valueOf(pageSize) + " OFFSET " + String.valueOf((page-1)*pageSize));

        @SuppressWarnings("unchecked")
        List<Long> resultList = ((List<Number>)pagingQuery.getResultList()).stream().map(Number::longValue).collect(Collectors.toList());

        if(!resultList.isEmpty()) {
            TypedQuery<Framework> query = em.createQuery("from Framework as f where f.id in (:resultList)", Framework.class);
            query.setParameter("resultList", resultList);
            return query.getResultList();
        }else{
            return Collections.emptyList();
        }

    }

    @Override
    public Optional<Framework> getByName(String name) {
        final TypedQuery<Framework> query = em.createQuery("select f from Framework f where f.name = :name", Framework.class);
        query.setParameter("name", name);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public int getAmountByCategory(FrameworkCategories categories) {
        final TypedQuery<Framework> query = em.createQuery("from Framework f where category = :category", Framework.class);
        query.setParameter("category", categories);
        return query.getResultList().size();
    }

    @Override
    public Optional<Integer> getByUserCount(long userId) {
        final TypedQuery<Framework> query = em.createQuery("select f from Framework f left outer join User u on f.author.id = u.id where u.id = :userId", Framework.class);
        query.setParameter("userId", userId);
        return Optional.of(query.getResultList().size());
    }

    @Override
    public List<Framework> search(String toSearch, List<FrameworkCategories> categories, List<FrameworkType> types, Integer starsLeft, Integer starsRight, boolean nameFlag, Integer commentAmount, Date lastComment, Date lastUpdated, Integer order, long page, long pageSize) {

        if(toSearch == null && categories == null && types == null && starsLeft == 0 && starsRight == 5 && commentAmount == 0
                && lastComment == null && lastUpdated == null && (order == null || order == 0)) {

            if (page == -1 && pageSize == -1) {
                return getAll();
            }

            final TypedQuery<Framework> query = em.createQuery("select f from Framework f", Framework.class);
            query.setFirstResult((int) ((page-1) * pageSize));
            query.setMaxResults((int) pageSize);
            return query.getResultList();
        }

        StringBuilder sb = new StringBuilder(" select f.framework_id from frameworks f left outer join framework_votes v on f.framework_id = v.framework_id left outer join comments c on f.framework_id = c.framework_id");
        if(toSearch!=null || categories!=null || types != null || lastUpdated!=null) {
            sb.append(" where ");
        }

        String search = "";

        if(toSearch != null && !toSearch.isEmpty()){
            search = "'%"+toSearch.toLowerCase()+"%'";
            if(nameFlag || toSearch.length() < 3) {
                sb.append(" lower(f.framework_name) like "+search+" ");
            }
            else {
                sb.append(" lower(f.framework_name) like "+search+" ");
                sb.append(" or lower(f.description) like "+search+" ");
                sb.append(" or lower(f.introduction) like "+search+" ");
            }
        }

        if (categories != null) {
            String parsedCategories = categories.stream().map(String::valueOf).collect(Collectors.joining("','","'","'"));
            if(!sb.toString().substring(sb.length()-6).contains("where")){
                sb.append(" and ");
            }
            sb.append(" f.category in ("+parsedCategories+") ");
        }

        if (types != null) {
            String parsedTypes = types.stream().map(String::valueOf).collect(Collectors.joining("','","'","'"));

            if(!sb.toString().substring(sb.length()-6).contains("where")){
                sb.append(" and ");
            }
            sb.append(" f.type in ("+parsedTypes+") ");
        }

        if (lastUpdated != null) {
            if(!sb.toString().substring(sb.length()-6).contains("where")){
                sb.append(" and ");
            }
            sb.append(" f.date >= '"+lastUpdated+"' ");
        }

        sb.append(" group by f.framework_id having coalesce(avg(v.stars),0) >= "+starsLeft+" and coalesce(avg(v.stars),0) <= "+starsRight+" and count(distinct c.comment_id) >= "+commentAmount+" ");

        if(lastComment!=null) {
            sb.append(" and max(c.tstamp) >= '"+lastComment+"' ");
        }

        if(order != null && order != 0){
            sb.append(" order by ");

            switch (Math.abs(order)){
                case 1:
                    sb.append(" coalesce(avg(v.stars),0) ").append(order > 0 ?" desc ":" asc ");
                    break;
                case 2:
                    sb.append(" count(distinct c.comment_id) ").append(order > 0 ?" desc ":" asc ");
                    break;
                case 3:
                    sb.append(" f.date ").append(order > 0 ?" desc ":" asc ");
                    break;
                case 4:
                    sb.append(" max(c.tstamp) ").append(order > 0 ?" desc ":" asc ");
                    break;
            }
        }



        Query pagingQuery;
        if(pageSize > 0){
            pagingQuery = em.createNativeQuery(sb.toString() + " LIMIT " + String.valueOf(pageSize) + " OFFSET " + String.valueOf((page-1)*pageSize));
        } else {
            pagingQuery = em.createNativeQuery(sb.toString());
        }


        @SuppressWarnings("unchecked")
        List<Long> resultList = ((List<Number>)pagingQuery.getResultList()).stream().map(Number::longValue).collect(Collectors.toList());

        if(!resultList.isEmpty()) {
            TypedQuery<Framework> query = em.createQuery("from Framework as f where f.id in (:resultList)", Framework.class);
            query.setParameter("resultList", resultList);
            return query.getResultList();
        }else{
            return Collections.emptyList();
        }

    }

    @Override
    public Optional<Framework> create(String name, FrameworkCategories category, String description, String introduction, FrameworkType type, long userId, byte[] picture) {
        Date ts = new Date(System.currentTimeMillis());
        final Framework framework = new Framework();

        framework.setName(name);
        framework.setCategory(category);
        framework.setDescription(description);
        framework.setIntroduction(introduction);
        framework.setType(type);
        framework.setAuthor(em.getReference(User.class, userId));
        framework.setPublishDate(ts);

        Blob blob = new Blob(picture);
        em.persist(blob);
        framework.setPicture(blob);

        em.persist(framework);
        return Optional.of(framework);
    }

    @Override
    public Optional<Framework> update(long id, String name, FrameworkCategories category, String description, String introduction, FrameworkType type, byte[] picture) {
        Date ts = new Date(System.currentTimeMillis());
        Framework framework = em.find(Framework.class, id);

        framework.setName(name);
        framework.setCategory(category);
        framework.setDescription(description);
        framework.setIntroduction(introduction);
        framework.setType(type);
        framework.setPublishDate(ts);

        if (picture!=null && picture.length>0) {
            if (framework.getPictureId() != Framework.DEFAULT_PICTURE_ID) {
                Blob blob = em.find(Blob.class, framework.getPictureId());
                blob.setPicture(picture);
                em.merge(blob);
            } else {
                Blob blob = new Blob(picture);
                em.persist(blob);
                framework.setPicture(blob);
            }
        }
//        }  else {
//            framework.setPicture(framework.getPicture());
//        }

        em.merge(framework);
        return Optional.of(framework);
    }

    @Override
    public void delete(long id) {
        em.remove(em.getReference(Framework.class, id));
    }

    @Override
    public Integer searchResultsNumber(String toSearch, List<FrameworkCategories> categories, List<FrameworkType> types, Integer starsLeft, Integer starsRight, boolean nameFlag, Integer commentAmount, Date lastComment, Date lastUpdated) {
        final List<Framework> resultList = search(toSearch, categories, types, starsLeft, starsRight, nameFlag, commentAmount, lastComment, lastUpdated,0, -1, -1);
        return resultList.size();
    }
}
