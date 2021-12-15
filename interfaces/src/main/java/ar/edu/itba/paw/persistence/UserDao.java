package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface UserDao {
    Optional<User> findById(long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameOrMail(String username,String Mail);
    Optional<User> findByMail(String mail);
    User create(String username,String mail,String password);
    void delete(long userId);
    Optional<User> update(long userId, String username, String mail, String password);
    void setEnable(long id);
    void updateInformation(Long userId, String description, byte[] picture, boolean updatePicture);

    void updatePicture(long id, byte[] picture);

    void updatePassword(long userId, String password);
    void updateModAllow(long userId,boolean allow);
}
