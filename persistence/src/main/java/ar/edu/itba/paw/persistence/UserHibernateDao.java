package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Blob;
import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Optional;

@Repository
public class UserHibernateDao implements UserDao {
    static private final String DESC_DEFAULT ="";
    static private final boolean ENABLED_DEFAULT =false;
    static private final byte[] PICTURE_DEFAULT = null;
    static private final boolean ALLOW_DEFAULT =true;

    @PersistenceContext
    private EntityManager em;

    @Override
    public User create(String username, String mail, String password) {
        final User user = new User(username,mail,password,ENABLED_DEFAULT,DESC_DEFAULT,ALLOW_DEFAULT);
        em.persist(user);
        return user;
    }

    @Override
    public void delete(long userId) {
        em.remove(em.getReference(User.class,userId));
    }

    @Override
    public Optional<User> update(long userId, String username, String mail, String password) {
        User user = em.find(User.class,userId);
        user.setUsername(username);
        user.setMail(mail);
        user.setPassword(password);
        em.merge(user);
        return Optional.ofNullable(user);
    }

    @Override
    public void setEnable(long id) {
        User user = em.find(User.class,id);
        user.setEnable(true);
        em.merge(user);
    }

    @Override
    public void updateInformation(Long userId, String description, byte[] picture, boolean updatePicture) {
        User user = em.find(User.class, userId);
        if (updatePicture) {
            if (user.getPictureId() != User.DEFAULT_PICTURE_ID) {
                em.remove(em.getReference(Blob.class, user.getPictureId()));
            }
            Blob blob = new Blob(picture);
            em.persist(blob);
            user.setPicture(blob);
        }
        user.setDescription(description);
        em.merge(user);
    }

    @Override
    public void updatePicture(long id, byte[] picture) {
        User user = em.find(User.class,id);
        if (user.getPictureId() != User.DEFAULT_PICTURE_ID) {
            Blob blob = em.find(Blob.class, user.getPictureId());
            blob.setPicture(picture);
            em.merge(blob);
        } else {
            Blob blob = new Blob(picture);
            em.persist(blob);
            user.setPicture(blob);
        }
        em.merge(user);
    }

    @Override
    public void updatePassword(long userId, String password) {
        User user = em.find(User.class,userId);
        user.setPassword(password);
        em.merge(user);
    }

    @Override
    public void updateModAllow(long userId, boolean allow) {
        User user = em.find(User.class,userId);
        user.setAllowMod(allow);
        em.merge(user);
    }

    @Override
    public Optional<User> findByUsername(final String username) {
        final TypedQuery<User> query = em.createQuery("from User as u where u.username = :username", User.class);
        query.setParameter("username", username);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public Optional<User> findByUsernameOrMail(String username, String mail) {
        final TypedQuery<User> query = em.createQuery("from User as u where u.username = :username or u.mail = :mail", User.class);
        query.setParameter("username", username);
        query.setParameter("mail", mail);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public Optional<User> findByMail(String mail) {
        final TypedQuery<User> query = em.createQuery("from User as u where u.mail = :mail", User.class);
        query.setParameter("mail", mail);
        return query.getResultList().stream().findFirst();
    }


    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }
}

