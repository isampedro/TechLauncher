package ar.edu.itba.paw;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.FrameworkVoteHibernateDaoImpl;
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
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Rollback
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class FrameworkVoteDaoImplTest {

    private static final long VOTE_ID = 1L;
    private static final int STARS = 0;

    private long FRAMEWORK_ID = 1;
    private long USER_ID = 1;

    @Autowired
    private DataSource ds;

    @Autowired
    private FrameworkVoteHibernateDaoImpl frameworkVoteDao;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("framework_votes")
                .usingGeneratedKeyColumns("vote_id");
        for (int i = 1; i < 4; i++) {
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

    @Test
    public void testCreate() {
        //Preconditions

        //Class under test
        final FrameworkVote frameworkVote = frameworkVoteDao.insert(FRAMEWORK_ID,USER_ID,STARS);

        em.flush();
        //Asserts
        assertNotNull(frameworkVote);
        Assert.assertEquals(FRAMEWORK_ID, frameworkVote.getFrameworkId());
        Assert.assertEquals(USER_ID, frameworkVote.getUserId());
        Assert.assertEquals(STARS, frameworkVote.getStars());

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "framework_votes","vote_id ="+frameworkVote.getVoteId()));
    }

    @Test(expected = Exception.class)
    public void testCreateOnExisting() {

         final Map<String, Object> args = new HashMap<>();
        args.put("framework_id", FRAMEWORK_ID);
        args.put("user_id",USER_ID);
        args.put("stars", STARS);
        jdbcInsert.execute(args);

        final FrameworkVote frameworkVote = frameworkVoteDao.insert(FRAMEWORK_ID, USER_ID, STARS);
    }

    @Test
    public void testDelete() {

        final FrameworkVote frameworkVote  = new FrameworkVote();
        frameworkVote.setStars(STARS);
        frameworkVote.setUser(em.find(User.class,USER_ID));
        frameworkVote.setFramework(em.find(Framework.class, FRAMEWORK_ID));

        em.persist(frameworkVote);

        frameworkVoteDao.delete(frameworkVote.getVoteId());

        em.flush();
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "framework_votes","vote_id ="+frameworkVote.getVoteId()));
    }


}


