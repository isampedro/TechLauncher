package ar.edu.itba.paw.service;

import ar.edu.itba.paw.models.Content;
import ar.edu.itba.paw.models.Content;
import ar.edu.itba.paw.models.ContentTypes;
import ar.edu.itba.paw.models.ReportContent;

import java.net.URL;
import java.util.List;
import java.util.Optional;

public interface ContentService {
    /*** Getters ***/
    Optional<Content> getById(long contentId);
    List<Content> getContentByUser(long userId, long page);
    Optional<Long> getContentCountByUser(long userId);
    List<Content> getContentByFrameworkAndType(long frameworkId, ContentTypes type, long page);
    List<Content> getContentByFrameworkAndTypeAndTitle(long frameworkId, ContentTypes type,String title);
    /***Content methods***/
    Content insertContent(long frameworkId, long userId, String title, String link, ContentTypes type);
    int deleteContent(long contentId);
    Optional<Content> changeContent(long contentId, String title, String link, ContentTypes types);

    /*** Reports ***/
    List<ReportContent> getAllReports( long page);
    Optional<Integer> getAllReportsAmount();
    Optional<Integer> getReportsAmount(List<Long> frameworksIds);
    List<ReportContent> getReportsByFrameworks( List<Long> frameworksIds, long page);
    void addReport(long contentId,long userId,String description);
    void acceptReport(long contentId);
    void denyReport(long contentId);
    void deleteReport(long reportId);
    boolean titleIsAvailable(long id, String title, String contentType);
}
