package ar.edu.itba.paw.service;

import ar.edu.itba.paw.models.Framework;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.VerificationToken;
import ar.edu.itba.paw.models.VerifyUser;
import javassist.NotFoundException;

import java.util.List;
import java.util.Optional;

public interface UserService {
    /** user **/
    Optional<User> findById(long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByMail(String mail);
    Optional<User> findByToken(String token);
    String responseOnLogin(String token, String username);
    User create(String username,String mail,String password) throws UserAlreadyExistException;
    void delete(long userId);
    boolean quitModdingFromTech(User user, long frameworkId);
    void updatePassword(long userId,String password);
    int updateModAllow(long userId, boolean allow);
    void updateInformation(Long userId, String description, byte[] picture);

    /** register **/
    void createVerificationToken(User user, String token,String appUrl);
    Optional<VerificationToken> getVerificationToken(String token);
    void saveRegisteredUser(User user);
    void generateNewVerificationToken(User user, String token, String appUrl);
    User register(String username,String mail,String password) throws UserAlreadyExistException;
    void confirmRegistration(String token) throws TokenExpiredException, NotFoundException;

    /** moderator **/
    VerifyUser createVerify(User user, Framework framework);
    Optional<VerifyUser> getVerifyById(long verificationId);
    List<VerifyUser> getVerifyByPending(boolean pending, long page);
    Optional<Integer> getVerifyByPendingAmount(boolean pending);
    Optional<Integer> getVerifyByFrameworkAmount(List<Long> frameworksIds,boolean pending);
    Optional<Integer> getApplicantsByPendingAmount(boolean pending);
    Optional<Integer> getApplicantsByFrameworkAmount(List<Long> frameworksIds,boolean pending);
    void deleteVerification(long verificationId);
    void deleteVerificationByUser(long userId);
    void verify(long verificationId);
    void passwordMailing(User user, String appUrl);
    void modMailing(User user,String frameworkName);
    List<VerifyUser> getApplicantsByPending( boolean pending, long page);
    List<VerifyUser> getApplicantsByFrameworks( List<Long> frameworksIds, long page);
    List<VerifyUser> getVerifyByPendingAndFrameworks( boolean pending, List<Long> frameworkIds, long page );
    List<Long> getOwnedFrameworks(User user );
    List<Long> getVerifiedFrameworks( User user );

    Integer getVerifyByPendingAndFrameworksAmount(boolean pending, List<Long> frameworkIds);

    long getPagesInt(Optional<Integer> count,long size);
    long getPagesLong(Optional<Long> count,long size);
}
