package ar.edu.itba.paw;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.ContentDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static junit.framework.Assert.*;

@Rollback
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ContentDaoTest {
    private long FRAMEWORK_ID = 1;
    private long USER_ID = 1;
    private static final String TITLE = "title";
    private static final String TITLE_2 = "title2";
    private static final String LINK = "link";
    private static final ContentTypes TYPE = Enum.valueOf(ContentTypes.class,"book");

    @Autowired
    private DataSource ds;
    @Autowired
    private ContentDao contentDao;
    @PersistenceContext
    private EntityManager em;

    private  JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("content")
                .usingGeneratedKeyColumns("content_id");

        for (int i = 1; i < 6; i++) {
            User user = new User("user"+i,"mail"+i,null,true,"",true);
            em.persist(user);
            Framework framework = new Framework();
            framework.setName("framework"+i);
            framework.setCategory(FrameworkCategories.Media);
            framework.setDescription("description");
            framework.setIntroduction("introduction");
            framework.setType(FrameworkType.Framework);
            framework.setAuthor(user);
            em.persist(framework);
            em.flush();
            USER_ID = user.getId();
            FRAMEWORK_ID = framework.getId();
        }

    }

    //<editor-fold desc="Content Methods">
    @Test
    public void testCreate() {


        final Content content = contentDao.insertContent(FRAMEWORK_ID,USER_ID,TITLE,LINK,TYPE);

        em.flush();
        assertNotNull(content);
        assertEquals(FRAMEWORK_ID, content.getFrameworkId());
        assertEquals(USER_ID,content.getUserId());
        assertEquals(TITLE, content.getTitle());
        assertEquals(TYPE,content.getType());
        assertEquals(LINK,content.getLink());
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "content","content_id ="+content.getContentId()));
    }

    @Test(expected = Exception.class)
    public void testCreateOnExisting() {

        Timestamp ts = new Timestamp(System.currentTimeMillis());
        final Map<String, Object> args = new HashMap<>();
        args.put("framework_id", FRAMEWORK_ID);
        args.put("user_id",USER_ID);
        args.put("title", TITLE);
        args.put("tstamp", ts);
        args.put("link", LINK);
        args.put("type", TYPE.name());
        jdbcInsert.execute(args);

        final Content content = contentDao.insertContent(FRAMEWORK_ID,USER_ID,TITLE,LINK,TYPE);
    }

    @Test(expected = Exception.class)
    public void testCreateWithoutUser() {
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate,"content","title = ",TITLE);
        final Content content = contentDao.insertContent(FRAMEWORK_ID,USER_ID+1,TITLE,LINK,TYPE);
    }
    @Test(expected = Exception.class)
    public void testCreateWithoutFramework() {
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate,"content","title = ",TITLE);
        final Content content = contentDao.insertContent(FRAMEWORK_ID+1,USER_ID,TITLE,LINK,TYPE);
    }
    @Test
    public void testChange() {

        Timestamp ts = new Timestamp(System.currentTimeMillis());
        final Map<String, Object> args = new HashMap<>();
        Content content = new Content();
        content.setFramework(em.find(Framework.class,FRAMEWORK_ID));
        content.setLink(LINK);
        content.setUser(em.find(User.class,USER_ID));
        content.setTitle(TITLE);
        content.setType(TYPE);
        content.setTimestamp(ts);
        em.persist(content);

        Optional<Content> content2 = contentDao.changeContent(content.getContentId(),TITLE_2,LINK,TYPE);

        em.flush();
        assertTrue(content2.isPresent());
        assertEquals(FRAMEWORK_ID, content2.get().getFrameworkId());
        assertEquals(USER_ID,content2.get().getUserId());
        assertEquals(TITLE_2, content2.get().getTitle());
        assertEquals(TYPE,content2.get().getType());
        assertEquals(LINK,content2.get().getLink());
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "content","content_id ="+content2.get().getContentId()));
    }
    @Test
    public void testDelete() {

        Timestamp ts = new Timestamp(System.currentTimeMillis());
        Content content = new Content();
        content.setFramework(em.find(Framework.class,FRAMEWORK_ID));
        content.setLink(LINK);
        content.setUser(em.find(User.class,USER_ID));
        content.setTitle(TITLE);
        content.setType(TYPE);
        content.setTimestamp(ts);
        em.persist(content);

        int i = contentDao.deleteContent(content.getContentId());

        em.flush();
        assertEquals(1,i);
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "content","content_id ="+content.getContentId()));
    }

    //</editor-fold>
    //<editor-fold desc="Getters">
    @Test
    public void testGetById() {

        Timestamp ts = new Timestamp(System.currentTimeMillis());
        Content content = new Content();
        content.setFramework(em.find(Framework.class,FRAMEWORK_ID));
        content.setLink(LINK);
        content.setUser(em.find(User.class,USER_ID));
        content.setTitle(TITLE);
        content.setType(TYPE);
        content.setTimestamp(ts);
        em.persist(content);

        Optional<Content> content2 = contentDao.getById(content.getContentId());

        em.flush();
        assertTrue(content2.isPresent());
        assertEquals(FRAMEWORK_ID, content2.get().getFrameworkId());
        assertEquals(USER_ID,content2.get().getUserId());
        assertEquals(TITLE, content2.get().getTitle());
        assertEquals(TYPE,content2.get().getType());
        assertEquals(LINK,content2.get().getLink());
    }
    @Test()
    public void testGetByIdNotExisting() {


        Optional<Content> content = contentDao.getById(1);

        em.flush();
        assertFalse(content.isPresent());
    }

//    @Test
//    public void testGetByFrameworkAndType() {
//
//        Timestamp ts = new Timestamp(System.currentTimeMillis());
//        for (int i = 0; i < 3; i++) {
//            Content content = new Content();
//            content.setFramework(em.find(Framework.class,FRAMEWORK_ID));
//            content.setLink(LINK);
//            content.setUser(em.find(User.class,USER_ID-i));
//            content.setTitle(TITLE+i);
//            content.setType(TYPE);
//            content.setTimestamp(ts);
//            em.persist(content);
//        }
//        Content content = new Content();
//        content.setFramework(em.find(Framework.class,FRAMEWORK_ID-4));
//        content.setLink(LINK);
//        content.setUser(em.find(User.class,USER_ID-4));
//        content.setTitle(TITLE);
//        content.setType(TYPE);
//        content.setTimestamp(ts);
//        em.persist(content);
//
//        List<Content> contents = contentDao.getContentByFrameworkAndType(FRAMEWORK_ID,TYPE, 1, 5);
//
//        em.flush();
//        assertFalse(contents.isEmpty());
//        assertEquals(3,contents.size());
//        for (Content c:contents) {
//            assertEquals(FRAMEWORK_ID, c.getFrameworkId());
//            assertEquals(TYPE,c.getType());
//        }
//    }
    @Test
    public void testGetByFrameworkAndTypeAndTitle() {

        Timestamp ts = new Timestamp(System.currentTimeMillis());
        for (int i = 0; i < 3; i++) {
            Content content = new Content();
            content.setFramework(em.find(Framework.class,FRAMEWORK_ID));
            content.setLink(LINK);
            content.setUser(em.find(User.class,USER_ID-i));
            content.setTitle(TITLE+i);
            content.setType(TYPE);
            content.setTimestamp(ts);
            em.persist(content);
        }
        Content content = new Content();
        content.setFramework(em.find(Framework.class,FRAMEWORK_ID-4));
        content.setLink(LINK);
        content.setUser(em.find(User.class,USER_ID-4));
        content.setTitle(TITLE);
        content.setType(TYPE);
        content.setTimestamp(ts);
        em.persist(content);

        List<Content> contents = contentDao.getContentByFrameworkAndTypeAndTitle(FRAMEWORK_ID,TYPE,TITLE+0);

        em.flush();
        assertFalse(contents.isEmpty());
        assertEquals(1,contents.size());
        for (Content c:contents) {
            assertEquals(FRAMEWORK_ID, c.getFrameworkId());
            assertEquals(TYPE,c.getType());
            assertEquals(TITLE+0,c.getTitle());
        }
    }
    @Test
    public void testGetByUser() {

        Timestamp ts = new Timestamp(System.currentTimeMillis());
        for (int i = 0; i < 3; i++) {
            Content content = new Content();
            content.setFramework(em.find(Framework.class,FRAMEWORK_ID-i));
            content.setLink(LINK);
            content.setUser(em.find(User.class,USER_ID));
            content.setTitle(TITLE);
            content.setType(TYPE);
            content.setTimestamp(ts);
            em.persist(content);
        }
        Content content = new Content();
        content.setFramework(em.find(Framework.class,FRAMEWORK_ID));
        content.setLink(LINK);
        content.setUser(em.find(User.class,USER_ID-1));
        content.setTitle(TITLE);
        content.setType(TYPE);
        content.setTimestamp(ts);
        em.persist(content);

        List<Content> contents = contentDao.getContentByUser(USER_ID, 1, 5);

        em.flush();
        assertFalse(contents.isEmpty());
        assertEquals(3,contents.size());
        for (Content c:contents) {
            assertEquals(USER_ID,c.getUserId());
        }
    }

    //</editor-fold>
}
