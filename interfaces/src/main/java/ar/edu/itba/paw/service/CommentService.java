package ar.edu.itba.paw.service;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.CommentVote;
import ar.edu.itba.paw.models.ReportComment;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    /*** Getters ***/
    Optional<Comment> getById(long contentId);
    List<Comment> getCommentsWithoutReferenceByFramework(long frameworkId, long page);
    List<Comment> getCommentsByUser(long userId, long page);

    Optional<Integer> getCommentsCountByUser(long userId);

    /*** Comment Methods ***/
    Comment insertComment(long frameworkId, long userId, String description, Long reference);
    void deleteComment(long commentId);
    Optional<Comment> changeComment(long commentId, String description);

    /***Votes methods***/
    Optional<CommentVote> vote(long commentId, long userId, int voteSign);


    /***Reports***/
    List<ReportComment> getAllReport(long page);
    Optional<Integer> getAllReportsAmount();
    void addReport(long commentId,long userId,String description);
    void acceptReport(long commentId);
    void denyReport(long commentId);
    void deleteReport(long reportId);
    List<ReportComment> getReportsByFrameworks( List<Long> frameworksIds, long page);

    Integer getReportsAmountByFrameworks(List<Long> frameworksIds);
}
