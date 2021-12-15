package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.ReportContent;

import java.util.List;
import java.util.Optional;

public interface ReportContentDao {
    List<ReportContent> getAll(long page, long pageSize);
    List<ReportContent> getByFrameworks( List<Long> frameworksIds, long page, long pageSize);
    Optional<Integer> getAllReportsAmount();
    Optional<Integer> getReportsAmount(List<Long> frameworksIds);
    void add(long contentId,long userId,String description);
    void delete(long reportId);
    void deleteByContent(long contentId);

}
