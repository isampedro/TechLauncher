package ar.edu.itba.paw;

import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.PostComment;
import ar.edu.itba.paw.models.PostCommentVote;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.PostCommentVoteDao;
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

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Rollback
@Transactional
public class PostCommentVoteDaoTest {
    @Autowired
    private DataSource ds;

    @Autowired
    private PostCommentVoteDao postCommentVoteDao;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    private Long USER_ID;
    private long POST_ID;
    private long POST_COMMENT_ID;
    private final int VOTEUP = 1, VOTEDOWN = -1;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("post_comment_votes")
                .usingGeneratedKeyColumns("post_comment_vote_id");


        for (int i = 0; i < 2; i++) {
            User user = new User("user" + i, "mail" + i, null, true, "", true);
            em.persist(user);
            Post post = new Post();
            post.setUser(user);
            post.setDescription("description"+i);
            post.setTimestamp(new Timestamp(System.currentTimeMillis()));
            post.setTitle("title"+i);
            em.persist(post);
            PostComment postComment = new PostComment();
            POST_ID = post.getPostId();
            USER_ID = user.getId();
            postComment.setUser(em.find(User.class, USER_ID));
            postComment.setPost(em.find(Post.class, POST_ID));
            postComment.setDescription("description");
            postComment.setReference(POST_ID);
            postComment.setTimestamp(new Timestamp(System.currentTimeMillis()));
            em.persist(postComment);
            em.flush();
            POST_COMMENT_ID = postComment.getPostCommentId();
        }
    }

    @Test
    public void testInsert() {

        PostCommentVote vote = postCommentVoteDao.insert(POST_COMMENT_ID,USER_ID,VOTEUP);
        em.flush();

        assertEquals(USER_ID, vote.getUser().getId());
        assertEquals(POST_COMMENT_ID, vote.getPostComment().getPostCommentId());
        assertEquals(VOTEUP, vote.getVote());
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "post_comment_votes","post_comment_vote_id ="+vote.getPostCommentVoteId()));
    }

    @Test(expected = Exception.class)
    public void testInsertWithoutUser() {

        postCommentVoteDao.insert(POST_COMMENT_ID,USER_ID+1,VOTEUP);
        em.flush();
    }

    @Test(expected = Exception.class)
    public void testInsertWithoutComment() {

        postCommentVoteDao.insert(POST_COMMENT_ID+1,USER_ID,VOTEDOWN);
        em.flush();
    }

    @Test
    public void testUpdate() {
        PostCommentVote postCommentVote = new PostCommentVote();
        postCommentVote.setPostComment(em.find(PostComment.class, POST_COMMENT_ID));
        postCommentVote.setUser(em.find(User.class, USER_ID));
        postCommentVote.setVote(VOTEUP);
        em.persist(postCommentVote);

        postCommentVoteDao.update(postCommentVote.getPostCommentVoteId(), VOTEDOWN);
        em.flush();

        Assert.assertEquals(VOTEDOWN, postCommentVote.getVote());
        Assert.assertEquals(POST_COMMENT_ID, postCommentVote.getPostComment().getPostCommentId());
        Assert.assertEquals(USER_ID, postCommentVote.getUser().getId());
    }

    @Test
    public void testDelete() {
        PostCommentVote postCommentVote = new PostCommentVote();
        postCommentVote.setPostComment(em.find(PostComment.class, POST_COMMENT_ID));
        postCommentVote.setUser(em.find(User.class, USER_ID));
        postCommentVote.setVote(VOTEUP);
        em.persist(postCommentVote);

        postCommentVoteDao.delete(postCommentVote.getPostCommentVoteId());
        em.flush();

        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "post_comment_votes", "post_comment_vote_id="+postCommentVote.getPostCommentVoteId()));
    }

    @Test
    public void testGetByPostCommentAndUser() {
        int votesAmount = 3;
        for (int i = 0; i < votesAmount; i++) {
            PostCommentVote postCommentVote = new PostCommentVote();
            postCommentVote.setPostComment(em.find(PostComment.class, POST_COMMENT_ID));
            postCommentVote.setUser(em.find(User.class, i > 0 ? USER_ID - 1 : USER_ID));
            postCommentVote.setVote(VOTEUP);
            em.persist(postCommentVote);
        }

        Optional<PostCommentVote> postCommentVote = postCommentVoteDao.getByPostCommentAndUser(POST_COMMENT_ID, USER_ID);
        em.flush();

        Assert.assertTrue(postCommentVote.isPresent());
        Assert.assertEquals(POST_COMMENT_ID, postCommentVote.get().getPostComment().getPostCommentId());
        Assert.assertEquals(USER_ID, postCommentVote.get().getUser().getId());
        Assert.assertEquals(VOTEUP, postCommentVote.get().getVote());
    }


}
