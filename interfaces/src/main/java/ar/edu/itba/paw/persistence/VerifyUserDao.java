package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Framework;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.VerifyUser;

import java.util.List;
import java.util.Optional;

public interface VerifyUserDao {
    VerifyUser create(User user, Framework framework, Comment comment);
    Optional<VerifyUser> getById(long verificationId);
    List<VerifyUser> getVerifyForCommentByPending(boolean pending, long page, long pageSize);
    List<VerifyUser> getApplicantsByPending(boolean pending, long page, long pageSize);
    List<VerifyUser> getApplicantsByFrameworks( List<Long> frameworksIds, long page, long pageSize );
    Optional<Integer> getVerifyForCommentByPendingAmount(boolean pending);
    Optional<Integer> getVerifyForCommentByFrameworkAmount(List<Long> frameworksIds, boolean pending);
    Optional<Integer> getApplicantsByPendingAmount(boolean pending);
    Optional<Integer> getApplicantsByFrameworkAmount(List<Long> frameworksIds,boolean pending);
    void delete(long verificationId);
    void deleteVerificationByUser(long userId);
    void verify(long verificationId);
    List<VerifyUser> getVerifyByPendingAndFramework( boolean pending, List<Long> frameworkIds, long page, long pageSize);

    Integer getVerifyByPendingAndFrameworksAmount(boolean pending, List<Long> frameworkIds);
}
