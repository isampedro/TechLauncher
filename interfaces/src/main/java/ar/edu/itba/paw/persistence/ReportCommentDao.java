package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.ReportComment;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface ReportCommentDao {
    List<ReportComment> getAll(long page, long pageSize);
    Optional<Integer> getAllReportsAmount();
    void insert(long commentId, long userId, String description);
    void delete(long reportId);
    void deleteReportByComment(long commentId);
    List<ReportComment> getByFrameworks( List<Long> frameworksIds, long page, long pageSize);

    Integer getReportsAmountByFrameworks(List<Long> frameworksIds);
}
