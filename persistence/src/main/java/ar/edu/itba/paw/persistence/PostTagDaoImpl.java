package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.PostTag;
import ar.edu.itba.paw.models.PostTagType;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class PostTagDaoImpl implements PostTagDao{


    @PersistenceContext
    private EntityManager em;

    @Override
    public List<PostTag> getByPost(long postId) {
        final TypedQuery<PostTag> query = em.createQuery("select pt from PostTag pt where pt.post.id = :postId", PostTag.class);
        query.setParameter("postId", postId);
        return query.getResultList();
    }

    @Override
    public List<PostTag> getAll(){
        final TypedQuery<PostTag> query = em.createQuery("select pt from PostTag pt", PostTag.class);
        return query.getResultList();
    }

    @Override
    public Optional<PostTag> insert(String tagName, long postId, PostTagType type) {
        final PostTag postTag = new PostTag();
        postTag.setPost(em.getReference(Post.class, postId));
        postTag.setTagName(tagName);
        postTag.setType(type);

        em.persist(postTag);

        return Optional.of(postTag);
    }

    @Override
    public void delete(long tagId) {
        em.remove(em.getReference(PostTag.class, tagId));
    }
}
