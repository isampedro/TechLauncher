package ar.edu.itba.paw;

import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.PostComment;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.PostCommentDao;
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

@Rollback
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class PostCommentDaoTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private PostCommentDao postCommentDao;

    @PersistenceContext
    private EntityManager em;

    private Long USER_ID;
    private long POST_ID;
    private final int PAGE = 1;
    private final int PAGE_SIZE = 5;
    private final String TEST_DESCRIPTION="Somebody once told me the world is gonna roll me";


    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("post_comments")
                .usingGeneratedKeyColumns("post_comment_id");


        User user = new User("user","mail",null,true,"",true);
        em.persist(user);
        em.flush();
        USER_ID = user.getId();

        // Adding some posts
        for (int i = 1; i < 3; i++) {
            Post post = new Post();
            post.setUser(user);
            post.setDescription("description");
            post.setTimestamp(new Timestamp(System.currentTimeMillis()));
            post.setTitle("title");
            em.persist(post);
            em.flush();
            POST_ID = post.getPostId();
        }
    }

    @Test
    public void getByPostTest() {
        // Arrange
        int commentsAmount = 4;
        for (int i = 0; i < commentsAmount; i++) {
            PostComment postComment = new PostComment();
            postComment.setUser(em.find(User.class, USER_ID));
            postComment.setPost(em.find(Post.class, POST_ID));
            postComment.setDescription(TEST_DESCRIPTION);
            postComment.setReference(POST_ID);
            postComment.setTimestamp(new Timestamp(System.currentTimeMillis()));
            em.persist(postComment);
        }

        // Act
        List<PostComment> postComments = postCommentDao.getByPost(POST_ID, PAGE, PAGE_SIZE);
        em.flush();

        // Assert
        Assert.assertFalse(postComments.isEmpty());
        Assert.assertEquals(commentsAmount, postComments.size());
    }

    @Test
    public void insertPostCommentTest() {
        // Arrange

        // Act
        PostComment postComment = postCommentDao.insertPostComment(POST_ID, USER_ID, TEST_DESCRIPTION, null);
        em.flush();

        // Assert
        Assert.assertEquals(USER_ID, postComment.getUser().getId());
        Assert.assertEquals(POST_ID, postComment.getPost().getPostId());
        Assert.assertEquals(TEST_DESCRIPTION, postComment.getDescription());
        Assert.assertNull(postComment.getReference());
    }

    @Test
    public void deletePostCommentTest() {
        // Arrange
        PostComment postComment = new PostComment();
        postComment.setUser(em.find(User.class, USER_ID));
        postComment.setPost(em.find(Post.class, POST_ID));
        postComment.setDescription(TEST_DESCRIPTION);
        postComment.setReference(POST_ID);
        postComment.setTimestamp(new Timestamp(System.currentTimeMillis()));
        em.persist(postComment);

        // Act
        postCommentDao.deletePostComment(postComment.getPostCommentId());
        em.flush();

        // Assert
        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate,
                "post_comments", "post_comment_id="+postComment.getPostCommentId()));

    }

}
