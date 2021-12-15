package ar.edu.itba.paw;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.CommentDao;
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
import java.util.Optional;

@Rollback
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CommentDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private CommentDao commentDao;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    private long FRAMEWORK_ID;
    private Long USER_ID;
    private final long INVALID_ID=99;
    private final long SIZE = 5;
    private final String DESCRIPTION = "Un Anillo para gobernarlos a todos. Un Anillo para encontrarlos, un Anillo para atraerlos a todos y atarlos en las tinieblas";
    private final String DESCRIPTION_NEW = "Ash Nazg durbatulûk, ash Nazg gimbatul, ash Nazg thrakatulûk agh burzum-ishi krimpatul.";

    @Before
    public void setUp() {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("comments")
                .usingGeneratedKeyColumns("comment_id");
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
            em.flush();
            USER_ID = user.getId();
            FRAMEWORK_ID = framework.getId();}
    }

    @Test
    public void testInsertComment() {
        // Arrange delegated

        // Act
        final Comment comment = commentDao.insertComment(FRAMEWORK_ID, USER_ID, DESCRIPTION, null);
        em.flush();

        // Assert
        Assert.assertEquals(FRAMEWORK_ID, comment.getFrameworkId());
        Assert.assertEquals(USER_ID, comment.getUser().getId());
        Assert.assertEquals(DESCRIPTION, comment.getDescription());
        Assert.assertNull(comment.getReference());
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "comments", "comment_id =" + comment.getCommentId()));
    }

        @Test
        public void testReplyComment() {
            // Arrange
            Comment comment = new Comment();
            comment.setFramework(em.find(Framework.class, FRAMEWORK_ID));
            comment.setUser(em.find(User.class, USER_ID));
            comment.setDescription(DESCRIPTION);
            comment.setTimestamp(new Timestamp(System.currentTimeMillis()));
            comment.setReference(null);
            em.persist(comment);

            // Act
            final Comment reply = commentDao.insertComment(FRAMEWORK_ID, USER_ID, DESCRIPTION, comment.getCommentId());
            em.flush();

            // Assert
            Assert.assertNotNull(reply.getReference());
            Assert.assertEquals(reply.getReference().longValue() , comment.getCommentId());
            Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "comments", "reference =" + comment.getCommentId()));
        }

    @Test(expected = RuntimeException.class)
    public void testInsertWithInvalidUser() {
        // Arrange delegated

        // Act
        commentDao.insertComment(FRAMEWORK_ID, INVALID_ID, DESCRIPTION, null);
        em.flush();

        // Assert on Exception
    }

    @Test(expected = RuntimeException.class)
    public void testInsertWithInvalidFramework() {
        // Arrange delegated

        // Act
        commentDao.insertComment(INVALID_ID, USER_ID, DESCRIPTION, null);
        em.flush();

        // Assert on Exception
    }

    @Test
    public void testDeleteComment() {
        // Arrange
        Comment comment = new Comment();
        comment.setFramework(em.find(Framework.class, FRAMEWORK_ID));
        comment.setUser(em.find(User.class, USER_ID));
        comment.setDescription(DESCRIPTION);
        comment.setTimestamp(new Timestamp(System.currentTimeMillis()));
        comment.setReference(null);
        em.persist(comment);

        // Act
        commentDao.deleteComment(comment.getCommentId());
        em.flush();

        // Assert
        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "comments", "comment_id =" + comment.getCommentId()));
    }

    @Test
    public void testChangeComment() {
        // Arrange
        Comment comment = new Comment();
        comment.setFramework(em.find(Framework.class, FRAMEWORK_ID));
        comment.setUser(em.find(User.class, USER_ID));
        comment.setDescription(DESCRIPTION);
        comment.setTimestamp(new Timestamp(System.currentTimeMillis()));
        comment.setReference(null);
        em.persist(comment);

        // Act
        Optional<Comment> updatedComment = commentDao.changeComment(comment.getCommentId(), DESCRIPTION_NEW);
        em.flush();

        // Assert
        Assert.assertTrue(updatedComment.isPresent());
        Assert.assertNotEquals(DESCRIPTION, updatedComment.get().getDescription());
        Assert.assertEquals(DESCRIPTION_NEW, updatedComment.get().getDescription());
        Assert.assertEquals(comment.getCommentId(), updatedComment.get().getCommentId());
    }

    @Test
    public void testGetCommentsByFramework() {
        // Arrange
        for (int i = 0; i < SIZE; i++) {
            Comment comment = new Comment();
            comment.setFramework(em.find(Framework.class, FRAMEWORK_ID));
            comment.setUser(em.find(User.class, USER_ID));
            comment.setDescription(DESCRIPTION);
            comment.setTimestamp(new Timestamp(System.currentTimeMillis()));
            comment.setReference(null);
            em.persist(comment);
        }

        // Act
        final List<Comment> commentList = commentDao.getCommentsByFramework(FRAMEWORK_ID, null);
        em.flush();

        // Assert
        Assert.assertFalse(commentList.isEmpty());
        Assert.assertEquals(SIZE, commentList.size());
    }

    @Test
    public void testGetCommentsByUser() {
        // Arrange
        for (int i = 0; i < SIZE; i++) {
            Comment comment = new Comment();
            comment.setFramework(em.find(Framework.class, FRAMEWORK_ID));
            comment.setUser(em.find(User.class, i < 2 ? USER_ID : USER_ID - 1));
            comment.setDescription(DESCRIPTION);
            comment.setTimestamp(new Timestamp(System.currentTimeMillis()));
            comment.setReference(null);
            em.persist(comment);
        }

        // Act
        final List<Comment> commentList = commentDao.getCommentsByUser(USER_ID, 1, 5);
        em.flush();

        // Assert
        Assert.assertFalse(commentList.isEmpty());
        Assert.assertEquals(2, commentList.size());
    }


    @Test
    public void testGetCommentsWithoutReferenceByFrameworkWithUser() {
        // Arrange
        Comment comment = new Comment();
        comment.setFramework(em.find(Framework.class, FRAMEWORK_ID));
        comment.setUser(em.find(User.class, USER_ID));
        comment.setDescription(DESCRIPTION);
        comment.setTimestamp(new Timestamp(System.currentTimeMillis()));
        comment.setReference(null);
        em.persist(comment);

        for (int i = 0; i < 3; i++) {
            Comment reply = new Comment();
            reply.setFramework(em.find(Framework.class, FRAMEWORK_ID));
            reply.setUser(em.find(User.class, USER_ID));
            reply.setDescription(DESCRIPTION);
            reply.setTimestamp(new Timestamp(System.currentTimeMillis()));
            reply.setReference(comment.getCommentId());
            em.persist(reply);
        }

        // Act
        final List<Comment> returnValue = commentDao.getCommentsWithoutReferenceByFramework(FRAMEWORK_ID, 1, 5);
        em.flush();
        
        // Assert
        Assert.assertFalse(returnValue.isEmpty());
        Assert.assertEquals(1, returnValue.size());
    }
}
