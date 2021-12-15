package ar.edu.itba.paw;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.ReportCommentDao;
import org.junit.Assert;
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
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Rollback
@Transactional
public class ReportCommentDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private ReportCommentDao reportCommentDao;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    private Long USER_ID;
    private long COMMENT_ID;
    private long FRAMEWORK_ID;
    private final String DESCRIPTION = "MOCKMOCKMOCKMOCKMOCKMOCKMOCKMOCKMOCKMOCKMOCKMOCKMOCKMOCK";
    public final String REPORT_DESCRIPTION = "Dolor sit amet";
    private final long INVALID_ID=99;
    private final long SIZE = 5;
    private final int VOTE_UP=1;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("comment_report")
                .usingGeneratedKeyColumns("report_id");

        for (int i = 1; i < SIZE+1; i++) {
            User user = new User("user"+i, "mail"+i, null, true, DESCRIPTION, false);
            em.persist(user);

            Framework framework = new Framework();
            framework.setName("framework"+i);
            framework.setCategory(FrameworkCategories.Media);
            framework.setDescription("description");
            framework.setIntroduction("introduction");
            framework.setType(FrameworkType.Framework);
            framework.setAuthor(user);
            em.persist(framework);

            USER_ID = user.getId();
            FRAMEWORK_ID = framework.getId();

            Comment comment = new Comment();
            comment.setFramework(em.find(Framework.class, FRAMEWORK_ID));
            comment.setUser(em.find(User.class, USER_ID));
            comment.setDescription(DESCRIPTION);
            comment.setTimestamp(new Timestamp(System.currentTimeMillis()));
            comment.setReference(null);

            em.persist(comment);
            em.flush();
            COMMENT_ID = comment.getCommentId();
        }

    }

    @Test
    public void testCreate() {

        reportCommentDao.insert(COMMENT_ID,USER_ID,DESCRIPTION);
        em.flush();

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "comment_report","comment_id="+COMMENT_ID+" and user_id="+USER_ID));
    }
    @Test(expected = Exception.class)
    public void testCreateOnExisting() {
        ReportComment reportComment = new ReportComment();
        reportComment.setUser(em.find(User.class, USER_ID));
        reportComment.setComment(em.find(Comment.class, COMMENT_ID));
        reportComment.setReportDescription(REPORT_DESCRIPTION);
        em.persist(reportComment);

        reportCommentDao.insert(COMMENT_ID,USER_ID,REPORT_DESCRIPTION);
        em.flush();
    }

    @Test(expected = Exception.class)
    public void testCreateWithoutUser() {
        reportCommentDao.insert(COMMENT_ID,INVALID_ID,DESCRIPTION);
        em.flush();
    }
    @Test(expected = Exception.class)
    public void testCreateWithoutContent() {
        reportCommentDao.insert(INVALID_ID,USER_ID,DESCRIPTION);
        em.flush();
    }

    @Test
    public void testDelete() {
        ReportComment reportComment = new ReportComment();
        reportComment.setUser(em.find(User.class, USER_ID));
        reportComment.setComment(em.find(Comment.class, COMMENT_ID));
        reportComment.setReportDescription(REPORT_DESCRIPTION);
        em.persist(reportComment);

        reportCommentDao.delete(reportComment.getReportId());
        em.flush();

        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "comment_report","report_id ="+reportComment.getReportId()));
    }

    @Test
    public void testDeleteByComment() {

        for (int i = 0; i < 2; i++) {
            ReportComment reportComment = new ReportComment();
            reportComment.setUser(em.find(User.class, USER_ID-i));
            reportComment.setComment(em.find(Comment.class, COMMENT_ID));
            reportComment.setReportDescription(REPORT_DESCRIPTION);
            em.persist(reportComment);
        }

        reportCommentDao.deleteReportByComment(COMMENT_ID);
        em.flush();

        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "comment_report","comment_id ="+COMMENT_ID));
    }
    //</editor-fold>

    //<editor-fold desc="Getters">


    @Test
    public void testGetAll() {

        for (int i = 0; i < 4; i++) {
            ReportComment reportComment = new ReportComment();
            reportComment.setUser(em.find(User.class, USER_ID-i));
            reportComment.setComment(em.find(Comment.class, COMMENT_ID-i));
            reportComment.setReportDescription(REPORT_DESCRIPTION);
            em.persist(reportComment);
        }

        final List<ReportComment> comments = reportCommentDao.getAll(1, 5);
        em.flush();

        Assert.assertFalse(comments.isEmpty());
        Assert.assertEquals(4,comments.size());
    }

}
