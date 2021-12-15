package ar.edu.itba.paw;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.ReportContentDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@Rollback
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ReportContentDaoTest {
    private long CONTENT_ID = 1;
    private long FRAMEWORK_ID = 1;
    private long USER_ID = 1;
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String LINK = "link";
    private static final ContentTypes TYPE = Enum.valueOf(ContentTypes.class,"book");

    @Autowired
    private DataSource ds;
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private ReportContentDao reportContentDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);

        Timestamp ts = new Timestamp(System.currentTimeMillis());

        for (int i = 1; i < 7; i++) {
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
            Content content = new Content();
            content.setFramework(framework);
            content.setLink(LINK);
            content.setUser(user);
            content.setTitle(TITLE);
            content.setType(TYPE);
            content.setTimestamp(ts);
            em.persist(content);
            em.flush();
            USER_ID = user.getId();
            FRAMEWORK_ID = framework.getId();
            CONTENT_ID = content.getContentId();
        }
    }

    //<editor-fold desc="reportContent methods">
    @Test
    public void testCreate() {

        reportContentDao.add(CONTENT_ID,USER_ID,DESCRIPTION);
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "content_report"));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "content_report","content_id="+CONTENT_ID+" and user_id="+USER_ID));
    }
    @Test(expected = Exception.class)
    public void testCreateOnExisting() {

        ReportContent rp = new ReportContent();
        rp.setContent(em.find(Content.class,CONTENT_ID));
        rp.setDescription(DESCRIPTION);
        rp.setUser(em.find(User.class,USER_ID));
        em.persist(rp);

        reportContentDao.add(CONTENT_ID,USER_ID,DESCRIPTION);
        em.flush();
    }

    @Test(expected = Exception.class)
    public void testCreateWithoutUser() {

        reportContentDao.add(CONTENT_ID,USER_ID+7,DESCRIPTION);
        em.flush();
    }
    @Test(expected = Exception.class)
    public void testCreateWithoutContent() {

        reportContentDao.add(CONTENT_ID+7,USER_ID,DESCRIPTION);
        em.flush();
    }

    @Test
    public void testDelete() {


        ReportContent rp = new ReportContent();
        rp.setContent(em.find(Content.class,CONTENT_ID));
        rp.setDescription(DESCRIPTION);
        rp.setUser(em.find(User.class,USER_ID));
        em.persist(rp);


        reportContentDao.delete(rp.getReportId());
        em.flush();
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "content_report","report_id ="+rp.getReportId()));
    }
    @Test
    public void testDeleteByContent() {

        for (int i = 0; i < 2; i++) {
            ReportContent rp = new ReportContent();
            rp.setContent(em.find(Content.class,CONTENT_ID));
            rp.setDescription(DESCRIPTION);
            rp.setUser(em.find(User.class,USER_ID-i));
            em.persist(rp);
        }

        reportContentDao.deleteByContent(CONTENT_ID);
        em.flush();
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "content_report","content_id ="+CONTENT_ID));
    }
    //</editor-fold>

    //<editor-fold desc="Getters">


    @Test
    public void testGetAll() {

        for (int i = 0; i < 5; i++) {
            ReportContent rp = new ReportContent();
            rp.setContent(em.find(Content.class,CONTENT_ID));
            rp.setDescription(DESCRIPTION);
            rp.setUser(em.find(User.class,USER_ID-i));
            em.persist(rp);
        }

        final List<ReportContent> content = reportContentDao.getAll(1, 10);
        em.flush();
        assertEquals(false,content.isEmpty());
        assertEquals(5,content.size());
    }




    //</editor-fold>
}
