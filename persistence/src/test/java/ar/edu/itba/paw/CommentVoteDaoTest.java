package ar.edu.itba.paw;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.CommentVoteDao;
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
import java.util.Optional;

@Rollback
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CommentVoteDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private CommentVoteDao commentVoteDao;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    private Long USER_ID;
    private long COMMENT_ID;
    private long FRAMEWORK_ID;
    private final String DESCRIPTION = "MOCKMOCKMOCKMOCKMOCKMOCKMOCKMOCKMOCKMOCKMOCKMOCKMOCKMOCK";
    private final long INVALID_ID=99;
    private final long SIZE = 5;
    private final int VOTE_UP=1;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("comment_votes")
                .usingGeneratedKeyColumns("vote_id");

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
    public void testInsertVote() {
        // Arrange delegated

        // Act
        final CommentVote returnValue = commentVoteDao.insert(COMMENT_ID,USER_ID, VOTE_UP);
        em.flush();
        // Assert
        Assert.assertNotNull(returnValue);
        Assert.assertEquals(COMMENT_ID, returnValue.getComment().getCommentId());
        Assert.assertEquals(USER_ID, returnValue.getUser().getId());
        Assert.assertTrue(returnValue.isVoteUp());
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "comment_votes","vote_id ="+returnValue.getCommentVoteId()));
    }

    @Test(expected = RuntimeException.class)
    public void testInsertVoteInvalidUser() {
        // Arrange delegated

        // Act
        commentVoteDao.insert(COMMENT_ID,INVALID_ID, VOTE_UP);
        em.flush();

        // Assert on Exception
    }

    @Test(expected = RuntimeException.class)
    public void testInsertVoteInvalidComment() {
        // Arrange delegated

        // Act
        commentVoteDao.insert(INVALID_ID,USER_ID, VOTE_UP);
        em.flush();

        // Assert on Exception
    }

    @Test
    public void testChangeVote() {
        // Arrange
        CommentVote commentVote = new CommentVote();
        commentVote.setComment(em.find(Comment.class, COMMENT_ID));
        commentVote.setUser(em.find(User.class, USER_ID));
        commentVote.setVote(VOTE_UP);
        em.persist(commentVote);

        // Act
        int VOTE_DOWN = -VOTE_UP;
        final Optional<CommentVote> returnValue = commentVoteDao.update(commentVote.getCommentVoteId(), VOTE_DOWN);
        em.flush();

        // Assert
        Assert.assertTrue(returnValue.isPresent());
        Assert.assertEquals(commentVote.getCommentVoteId(), returnValue.get().getCommentVoteId());
        Assert.assertFalse(returnValue.get().isVoteUp());
    }

    @Test
    public void testDeleteVote() {
        // Arrange
        CommentVote commentVote = new CommentVote();
        commentVote.setComment(em.find(Comment.class, COMMENT_ID));
        commentVote.setUser(em.find(User.class, USER_ID));
        commentVote.setVote(VOTE_UP);
        em.persist(commentVote);

        // Act
        commentVoteDao.delete(commentVote.getCommentVoteId());
        em.flush();

        // Asserts
        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "comment_votes","vote_id ="+commentVote.getCommentVoteId()));
    }

    @Test
    public void testGetByCommentAndUser() {
        // Arrange

        CommentVote commentVote = new CommentVote();
        commentVote.setComment(em.find(Comment.class, COMMENT_ID));
        commentVote.setUser(em.find(User.class, USER_ID));
        commentVote.setVote(VOTE_UP);
        em.persist(commentVote);

        // Act
        final Optional<CommentVote> returnValue = commentVoteDao.getByCommentAndUser(COMMENT_ID, USER_ID);
        em.flush();

        // Assert
        Assert.assertTrue(returnValue.isPresent());
        Assert.assertEquals(COMMENT_ID, returnValue.get().getCommentId());
        Assert.assertEquals(USER_ID.longValue(), returnValue.get().getUserId());
        Assert.assertTrue(commentVote.isVoteUp());
    }

}
