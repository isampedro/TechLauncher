package ar.edu.itba.paw.service;

import ar.edu.itba.paw.models.Blob;

import java.util.Optional;

public interface PictureService {
    Blob uploadPicture(byte[] picture);
    Optional<byte []> findPictureById(long pictureId);
}
