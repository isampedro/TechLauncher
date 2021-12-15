package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Blob;

import java.util.Optional;

public interface PictureDao {
    Blob uploadPicture(byte[] picture);
    Optional<Blob> findPictureById(long pictureId);
}
