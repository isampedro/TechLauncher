package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Blob;
import ar.edu.itba.paw.persistence.PictureDao;
import ar.edu.itba.paw.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PictureServiceImpl implements PictureService {
    @Autowired
    private PictureDao pictureDao;

    @Transactional
    @Override
    public Blob uploadPicture(byte[] picture) {
        return pictureDao.uploadPicture(picture);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<byte[]> findPictureById(long pictureId) {
        return pictureDao.findPictureById(pictureId).map(Blob::getPicture);
    }

}
