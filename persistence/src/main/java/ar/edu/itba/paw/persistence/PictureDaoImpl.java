package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.models.Blob;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class PictureDaoImpl implements PictureDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Blob uploadPicture(byte[] picture) {
        final Blob image = new Blob(picture);
        em.persist(image);
        return image;
    }

    @Override
    public Optional<Blob> findPictureById(long pictureId) {
        return Optional.ofNullable(em.find(Blob.class, pictureId));
    }
}

