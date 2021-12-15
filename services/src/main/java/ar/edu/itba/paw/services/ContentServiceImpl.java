package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.ContentDao;
import ar.edu.itba.paw.persistence.ReportContentDao;
import ar.edu.itba.paw.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentDao content;

    @Autowired
    private ReportContentDao rc;

    private final long PAGE_SIZE = 5;

    @Transactional(readOnly = true)
    @Override
    public Optional<Content> getById(long contentId) {
        return content.getById(contentId);
    }


    @Transactional(readOnly = true)
    @Override
    public List<Content> getContentByUser(long userId, long page) {
        return content.getContentByUser(userId, page, PAGE_SIZE );
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Long> getContentCountByUser( long userId ){
        return content.getContentCountByUser(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Content> getContentByFrameworkAndType(long frameworkId, ContentTypes type, long page) {
        return content.getContentByFrameworkAndType(frameworkId, type, page, PAGE_SIZE);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Content> getContentByFrameworkAndTypeAndTitle(long frameworkId, ContentTypes type, String title) {
        return content.getContentByFrameworkAndTypeAndTitle(frameworkId,type,title);
    }

    @Transactional
    @Override
    public Content insertContent(long frameworkId, long userId, String title, String link, ContentTypes type) {
        return content.insertContent(frameworkId, userId, title, link, type);
    }

    @Transactional
    @Override
    public int deleteContent(long contentId) {
        return content.deleteContent(contentId);
    }

    @Transactional
    @Override
    public Optional<Content> changeContent(long contentId, String title, String link, ContentTypes types) {
        return content.changeContent(contentId, title, link, types);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReportContent> getAllReports(long page) {
        return rc.getAll(page, PAGE_SIZE);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Integer> getAllReportsAmount() {
        return rc.getAllReportsAmount();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Integer> getReportsAmount(List<Long> frameworksIds) {
        return rc.getReportsAmount(frameworksIds);
    }

    @Transactional
    @Override
    public void addReport(long contentId, long userId, String description) {
        rc.add(contentId,userId,description);
    }

    @Transactional
    @Override
    public void acceptReport(long contentId) {
        content.deleteContent(contentId);
    }

    @Transactional
    @Override
    public void denyReport(long contentId) {
        rc.deleteByContent(contentId);
    }

    @Transactional
    @Override
    public void deleteReport(long reportId) {
        rc.delete(reportId);
    }

    @Transactional
    @Override
    public List<ReportContent> getReportsByFrameworks(List<Long> frameworksIds, long page) {
        return rc.getByFrameworks(frameworksIds, page, PAGE_SIZE);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean titleIsAvailable( long id, final String title, final String contentType) {
        if (title == null ) {
            return false;
        }
        ContentTypes type;
        try {
            type = Enum.valueOf(ContentTypes.class, contentType);
        } catch (IllegalArgumentException e) {
            return false;
        }
        List<Content> ls = getContentByFrameworkAndTypeAndTitle(id, type, title);
        return ls.isEmpty();

    }
}
