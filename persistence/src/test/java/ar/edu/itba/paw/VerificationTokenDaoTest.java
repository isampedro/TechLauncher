package ar.edu.itba.paw;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.VerificationToken;
import ar.edu.itba.paw.persistence.VerificationTokenDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static junit.framework.Assert.*;

@Rollback
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class VerificationTokenDaoTest {
    private static final int USERS = 8;
    private static final User[] users_ids = new User[USERS];
    private static final String TOKEN = "token";
    private static final String TOKEN_2 = "token2";

    @Autowired
    private DataSource ds;
    @Autowired
    private VerificationTokenDao verificationTokenDao;

    @PersistenceContext
    private EntityManager em;


    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("verification_token")
                .usingGeneratedKeyColumns("token_id");

        for (int i = 0; i < USERS; i++) {
            User user = new User("user"+i,"mail"+i,null,true,"",true);
            em.persist(user);

            em.flush();
            users_ids[i] = user;
        }
    }

    //<editor-fold desc="Content Methods">
    @Test
    public void testCreate() {
          final VerificationToken token = verificationTokenDao.insert(users_ids[0].getId(),TOKEN);
        em.flush();
        assertEquals(token.getToken(), TOKEN);
        assertEquals(token.getUser().getId(), users_ids[0].getId());
    }

    @Test(expected = Exception.class)
    public void testCreateOnExisting() {

        Timestamp ts = new Timestamp(System.currentTimeMillis());
        final Map<String, Object> args = new HashMap<>();
        args.put("user_id",users_ids[0]);
        args.put("token", TOKEN);
        args.put("exp_date",ts);
        jdbcInsert.execute(args);

        verificationTokenDao.insert(users_ids[0].getId(),TOKEN);
        em.flush();
    }
    @Test(expected = Exception.class)
    public void testCreateWithoutUser() {

        verificationTokenDao.insert(users_ids[0].getId()+10,TOKEN);
        em.flush();
    }

    @Test
    public void testChange() {

        Timestamp ts = new Timestamp(System.currentTimeMillis());

        VerificationToken newVt = new VerificationToken();
        newVt.setUser(users_ids[0]);
        newVt.setToken(TOKEN);
        newVt.setExpiryDay(ts);

        em.persist(newVt);
        em.flush();

        verificationTokenDao.change(newVt,TOKEN_2);
        em.flush();
        assertEquals(newVt.getToken(), TOKEN_2);
    }
    //</editor-fold>
    //<editor-fold desc="Getters">

    @Test
    public void testGetByToken() {

        Timestamp ts = new Timestamp(System.currentTimeMillis());
        VerificationToken token = new VerificationToken();
        token.setUser(users_ids[0]);
        token.setToken(TOKEN);
        token.setExpiryDay(ts);
        em.persist(token);
        em.flush();

        Optional<VerificationToken> vt = verificationTokenDao.getByToken(TOKEN);
        em.flush();
        assertTrue(vt.isPresent());
        assertEquals(TOKEN, vt.get().getToken());
        assertEquals(users_ids[0].getId(),vt.get().getUser().getId());
    }
    @Test
    public void testGetByTokenNotExisting() {

        Optional<VerificationToken> vt = verificationTokenDao.getByToken(TOKEN);
        em.flush();
        assertFalse(vt.isPresent());
    }


}
