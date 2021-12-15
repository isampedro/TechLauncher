package ar.edu.itba.paw;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.UserDao;
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
import java.util.Optional;

import static junit.framework.Assert.*;

@Rollback
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class UserDaoTest {
    @Autowired
    private DataSource ds;

    @Autowired
    private UserDao userDao;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    private final String USER1 = "testUser1";
    private final String USER_MAIL = "testMail";
    private final String PASSWORD1 = "password1";
    private final String PASSWORD2 = "password2";
    private final String TEST_DESCRIPTION = "this is a test";

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");

    }

    @Test
    public void testCreate(){

        User testUser = new User();
        testUser.setUsername(USER1);
        testUser.setMail(USER_MAIL);
        testUser.setPassword(PASSWORD1);
        testUser.setEnable(false);
        testUser.setDescription("");
        testUser.setAllowMod(true);

        em.persist(testUser);
        em.flush();

        Optional<User> user = userDao.findByUsername(USER1);
        assertTrue(user.isPresent());
        assertEquals(user.get().getMail(), USER_MAIL);
        assertEquals(user.get().getUsername(), USER1);
        assertEquals(user.get().getPassword(), PASSWORD1);

        user = userDao.findByMail(USER_MAIL);
        assertTrue(user.isPresent());
        assertEquals(user.get().getMail(), USER_MAIL);
        assertEquals(user.get().getUsername(), USER1);
        assertEquals(user.get().getPassword(), PASSWORD1);

        user = userDao.findById(user.get().getId());
        assertTrue(user.isPresent());
        assertEquals(user.get().getMail(), USER_MAIL);
        assertEquals(user.get().getUsername(), USER1);
        assertEquals(user.get().getPassword(), PASSWORD1);

        user = userDao.findByUsernameOrMail(USER1, USER_MAIL);
        assertTrue(user.isPresent());
        assertEquals(user.get().getMail(), USER_MAIL);
        assertEquals(user.get().getUsername(), USER1);
        assertEquals(user.get().getPassword(), PASSWORD1);
        em.flush();
    }

    @Test
    public void testChangeDescription(){

        User testUser = new User();
        testUser.setUsername(USER1);
        testUser.setMail(USER_MAIL);
        testUser.setPassword(PASSWORD1);
        testUser.setEnable(false);
        testUser.setDescription("");
        testUser.setAllowMod(true);

        em.persist(testUser);
        em.flush();

        Optional<User> user = userDao.findByMail(USER_MAIL);
        assertTrue(user.isPresent());
        assertEquals(user.get().getDescription(), "");
        userDao.updateInformation(user.get().getId(), TEST_DESCRIPTION, null, false);
        em.flush();
        assertEquals(user.get().getDescription(), TEST_DESCRIPTION );

    }

    @Test
    public void testChangePassword(){

        User testUser = new User();
        testUser.setUsername(USER1);
        testUser.setMail(USER_MAIL);
        testUser.setPassword(PASSWORD1);
        testUser.setEnable(false);
        testUser.setDescription("");
        testUser.setAllowMod(true);

        em.persist(testUser);
        em.flush();

        Optional<User> user = userDao.findById(testUser.getId());
        assertTrue(user.isPresent());
        userDao.updatePassword(testUser.getId(), PASSWORD2);
        assertEquals(user.get().getPassword(), PASSWORD2);
        em.flush();
    }

    @Test
    public void testChangeAllowMod(){

        User testUser = new User();
        testUser.setUsername(USER1);
        testUser.setMail(USER_MAIL);
        testUser.setPassword(PASSWORD1);
        testUser.setEnable(false);
        testUser.setDescription("");
        testUser.setAllowMod(true);

        em.persist(testUser);
        em.flush();

        Optional<User> user = userDao.findById(testUser.getId());
        assertTrue(user.isPresent());

        userDao.updateModAllow(user.get().getId(), false);
        assertFalse(user.get().isAllowMod());
        em.flush();
    }

    @Test
    public void testDeleteUser(){

        User testUser = new User();
        testUser.setUsername(USER1);
        testUser.setMail(USER_MAIL);
        testUser.setPassword(PASSWORD1);
        testUser.setEnable(false);
        testUser.setDescription("");
        testUser.setAllowMod(true);

        em.persist(testUser);
        em.flush();

        Optional<User> user = userDao.findById(testUser.getId());
        assertTrue(user.isPresent());

        userDao.delete(user.get().getId());
        user = userDao.findById(testUser.getId());
        assertFalse(user.isPresent());
        em.flush();
    }


    @Test(expected = Exception.class)
    public void testCreateOnExisting() {

        final Map<String, Object> args = new HashMap<>();
        args.put("user_name", USER1);
        args.put("mail",USER_MAIL);
        args.put("password", PASSWORD1);
        jdbcInsert.execute(args);


        User user = userDao.create(USER1, USER_MAIL, PASSWORD2);
    }

    @Test
    public  void testChangeEnable(){

        User testUser = new User();
        testUser.setUsername(USER1);
        testUser.setMail(USER_MAIL);
        testUser.setPassword(PASSWORD1);
        testUser.setEnable(false);
        testUser.setDescription("");
        testUser.setAllowMod(true);

        em.persist(testUser);
        em.flush();

        Optional<User> user = userDao.findById(testUser.getId());
        assertTrue(user.isPresent());

        userDao.setEnable(user.get().getId());
        assertTrue(user.get().isEnable());
        em.flush();
    }

    @Test
    public void testModAllow(){

        User testUser = new User();
        testUser.setUsername(USER1);
        testUser.setMail(USER_MAIL);
        testUser.setPassword(PASSWORD1);
        testUser.setEnable(false);
        testUser.setDescription("");
        testUser.setAllowMod(true);

        em.persist(testUser);
        em.flush();

        Optional<User> user = userDao.findById(testUser.getId());
        assertTrue(user.isPresent());

        userDao.updateModAllow(user.get().getId(), false);
        assertFalse(user.get().isAllowMod());
        em.flush();
    }
}
