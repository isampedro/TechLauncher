package ar.edu.itba.paw;

import ar.edu.itba.paw.models.Post;
import ar.edu.itba.paw.models.PostTag;
import ar.edu.itba.paw.models.PostVote;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.PostTagDao;
import ar.edu.itba.paw.persistence.PostVoteDao;
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
import javax.swing.text.html.Option;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Rollback
@Transactional
public class PostVoteDaoTest {
    private Long USER_ID = 1L;
    private long POST_ID = 1;
    private final static Integer POSITIVE = 1;
    private final static Integer NEGATIVE = -1;

    @Autowired
    private DataSource ds;
    @Autowired
    private PostVoteDao postVoteDao;
    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);

        for (int i = 0; i < 2; i++) {
            User user = new User("user" + i, "mail" + i, null, true, "", true);
            em.persist(user);
            Post post = new Post();
            post.setUser(user);
            post.setDescription("description1");
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            post.setTimestamp(ts);
            post.setTitle("title1");
            em.persist(post);
            em.flush();
            POST_ID = post.getPostId();
            USER_ID = user.getId();
            System.out.println(user.getId());
        }
    }

    @Test
    public void testCreate() {

        PostVote vote = postVoteDao.insert(POST_ID,USER_ID,POSITIVE);

        em.flush();
        assertEquals(USER_ID, vote.getUser().getId());
        assertEquals(POST_ID, vote.getPost().getPostId());
        assertEquals(POSITIVE, vote.getVote());
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "post_votes","post_vote_id ="+vote.getPostVoteId()));
    }

    @Test(expected = Exception.class)
    public void testCreateWithoutUser() {


        postVoteDao.insert(POST_ID,USER_ID+1,POSITIVE);
        em.flush();
    }
    @Test(expected = Exception.class)
    public void testCreateWithoutPost() {


        postVoteDao.insert(POST_ID+1,USER_ID,POSITIVE);
        em.flush();
    }

    @Test
    public void testDelete() {

        PostVote vote = new PostVote();
        vote.setPost(em.find(Post.class,POST_ID));
        vote.setUser(em.find(User.class,USER_ID));
        vote.setVote(POSITIVE);
        em.persist(vote);
        em.flush();

        postVoteDao.delete(vote.getPostVoteId());
        em.flush();
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, "post_votes"));
    }
    @Test(expected = Exception.class)
    public void testDeleteWithoutTag() {

        postVoteDao.delete(1);
        em.flush();
    }
    @Test
    public void testUpdate(){

        PostVote vote = new PostVote();
        vote.setPost(em.find(Post.class,POST_ID));
        vote.setUser(em.find(User.class,USER_ID));
        vote.setVote(POSITIVE);
        em.persist(vote);
        em.flush();

        Optional<PostVote> optionalPostVote = postVoteDao.update(vote.getPostVoteId(),NEGATIVE);

        em.flush();
        assertTrue(optionalPostVote.isPresent());
        assertEquals(USER_ID, optionalPostVote.get().getUser().getId());
        assertEquals(POST_ID, optionalPostVote.get().getPost().getPostId());
        assertEquals(NEGATIVE, optionalPostVote.get().getVote());
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "post_votes","post_vote_id ="+optionalPostVote.get().getPostVoteId()));
    }
    @Test
    public void testGetByUser(){
        for (int i = 0; i < 2; i++) {
            PostVote vote = new PostVote();
            vote.setPost(em.find(Post.class,POST_ID-i));
            vote.setUser(em.find(User.class,USER_ID));
            vote.setVote(POSITIVE);
            em.persist(vote);
            PostVote vote2 = new PostVote();
            vote2.setPost(em.find(Post.class,POST_ID-i));
            vote2.setUser(em.find(User.class,USER_ID-1));
            vote2.setVote(POSITIVE);
            em.persist(vote2);
            em.flush();
        }

        List<PostVote> voteList = postVoteDao.getByUser(USER_ID,1,5);

        em.flush();
        assertEquals(2,voteList.size());
        for (PostVote vote: voteList) {
            assertEquals(USER_ID, vote.getUser().getId());

        }
    }
    @Test
    public void testGetByUserAndPost(){

        for (int i = 0; i < 2; i++) {
            PostVote vote = new PostVote();
            vote.setPost(em.find(Post.class,POST_ID-i));
            vote.setUser(em.find(User.class,USER_ID));
            vote.setVote(POSITIVE);
            em.persist(vote);
            PostVote vote2 = new PostVote();
            vote2.setPost(em.find(Post.class,POST_ID-i));
            vote2.setUser(em.find(User.class,USER_ID-1));
            vote2.setVote(POSITIVE);
            em.persist(vote2);
            em.flush();
        }

        Optional<PostVote> vote = postVoteDao.getByPostAndUser(POST_ID,USER_ID);

        em.flush();
        assertTrue(vote.isPresent());
        assertEquals(USER_ID, vote.get().getUser().getId());
        assertEquals(POST_ID, vote.get().getPost().getPostId());

    }
}
