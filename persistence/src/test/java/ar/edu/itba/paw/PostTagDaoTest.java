package ar.edu.itba.paw;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.ContentDao;
import ar.edu.itba.paw.persistence.PostTagDao;
import org.junit.Before;
import org.junit.BeforeClass;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Rollback
@Transactional
public class PostTagDaoTest {
    private Long USER_ID = 1L;
    private long POST_ID = 1;
    private final static String TAG_1 = "TAG";

    @Autowired
    private DataSource ds;
    @Autowired
    private PostTagDao postTagDao;
    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);

        User user = new User("user","mail",null,true,"",true);
        em.persist(user);
        USER_ID = user.getId();
        for (int i = 0; i < 2; i++) {
            Post post = new Post();
            post.setUser(user);
            post.setDescription("description1");
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            post.setTimestamp(ts);
            post.setTitle("title1");
            em.persist(post);
            em.flush();
            POST_ID = post.getPostId();
        }
    }

   @Test
    public void testCreate() {

       Optional<PostTag> tagOptional = postTagDao.insert(TAG_1,POST_ID, PostTagType.tech_name);

        em.flush();
        assertTrue(tagOptional.isPresent());
        assertEquals(USER_ID, tagOptional.get().getPost().getUser().getId());
        assertEquals(POST_ID, tagOptional.get().getPost().getPostId());
        assertEquals(TAG_1,tagOptional.get().getTagName());
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "post_tags","tag_id ="+tagOptional.get().getTagId()));
    }

    @Test(expected = Exception.class)
    public void testCreateWithoutPost() {

        Optional<PostTag> tagOptional = postTagDao.insert(TAG_1,POST_ID+1,PostTagType.tech_name);
        em.flush();
    }
    @Test
    public void testDelete() {

        PostTag postTag = new PostTag();
        postTag.setPost(em.find(Post.class,POST_ID));
        postTag.setTagName(TAG_1);
        postTag.setType(PostTagType.tech_name);
        em.persist(postTag);
        em.flush();

        postTagDao.delete(postTag.getTagId());
        em.flush();
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, "post_tags"));
    }
    @Test(expected = Exception.class)
    public void testDeleteWithoutTag() {

        postTagDao.delete(1);
        em.flush();
    }
    @Test
    public void testGetByPost(){

        for (int i = 0; i < 4; i++) {
            PostTag postTag = new PostTag();
            postTag.setPost(em.find(Post.class,POST_ID));
            postTag.setTagName(TAG_1+i);
            postTag.setType(PostTagType.tech_name);
            em.persist(postTag);
            em.flush();

        }
        PostTag postTag = new PostTag();
        postTag.setPost(em.find(Post.class,POST_ID-1));
        postTag.setTagName(TAG_1);
        postTag.setType(PostTagType.tech_name);
        em.persist(postTag);
        em.flush();
        List<PostTag> tags = postTagDao.getByPost(POST_ID);

        assertEquals(4,tags.size());
        for (PostTag tag:tags) {
            assertEquals(POST_ID,tag.getPost().getPostId());
        }
    }
    @Test
    public void testGetAll(){
        for (int i = 0; i < 4; i++) {
            PostTag postTag = new PostTag();
            postTag.setPost(em.find(Post.class,POST_ID));
            postTag.setTagName(TAG_1+i);
            postTag.setType(PostTagType.tech_name);
            em.persist(postTag);
            em.flush();

        }
        em.flush();
        List<PostTag> tags = postTagDao.getAll();

        assertEquals(4,tags.size());
    }
}
