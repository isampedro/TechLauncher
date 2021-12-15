package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Content;
import ar.edu.itba.paw.models.ContentTypes;

import java.util.List;
import java.util.Optional;

public interface ContentDao {
    /***Getters***/
    Optional<Content> getById(long contentId);
    List<Content> getContentByFrameworkAndType(long frameworkId, ContentTypes type, long page, long pageSize);
    List<Content> getContentByUser(long userId, long page, long pagesize);
    Optional<Long> getContentCountByUser(long userId);
    List<Content> getContentByFrameworkAndTypeAndTitle(long frameworkId, ContentTypes type, String title);

    /***Content Methods***/
    Content insertContent(long frameworkId, long userId, String title, String link, ContentTypes type);
    int deleteContent(long contentId);
    Optional<Content> changeContent(long contentId, String title, String link, ContentTypes types);

}
