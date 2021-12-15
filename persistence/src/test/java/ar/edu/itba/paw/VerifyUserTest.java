package ar.edu.itba.paw;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.VerifyUserDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static junit.framework.Assert.*;

@Rollback
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class VerifyUserTest {
    private final User[] USERS = new User[5];
    private Framework fw = null;

    @Autowired
    private DataSource ds;

    @Autowired
    private VerifyUserDao verifyUserDao;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);



        for (int i = 1; i < 6; i++) {
            User user = new User("user"+i,"mail"+i,null,true,"",true);
            em.persist(user);

            em.flush();
            USERS[i-1] = user;
        }
        fw = new Framework();
        fw.setAuthor(USERS[0]);
        fw.setCategory(FrameworkCategories.Artificial_Intelligence);
        fw.setType(FrameworkType.Framework);
        fw.setDescription("test");
        fw.setIntroduction("test");
        fw.setName("test");
        em.persist(fw);
        em.flush();
    }

    @Test
    public void testCreate(){

        VerifyUser user = new VerifyUser();
        user.setComment(null);
        user.setPending(true);
        user.setFramework(fw);
        user.setUser(USERS[1]);
        em.persist(user);
        em.flush();

        Optional<VerifyUser> verifyUser = verifyUserDao.getById(user.getVerificationId());
        assertTrue(verifyUser.isPresent());
        assertEquals(verifyUser.get().getCategory(), FrameworkCategories.Artificial_Intelligence.toString());
        assertEquals(verifyUser.get().getFramework(), fw);
        assertEquals(verifyUser.get().getVerificationId(), user.getVerificationId());
        assertEquals(verifyUser.get().getFrameworkName(),"test");
        assertNull(verifyUser.get().getComment());
        assertTrue(verifyUser.get().isPending());
    }

    @Test
    public void testVerify(){

        VerifyUser user = new VerifyUser();
        user.setComment(null);
        user.setPending(true);
        user.setFramework(fw);
        user.setUser(USERS[1]);
        em.persist(user);
        em.flush();

        Optional<VerifyUser> verifyUser = verifyUserDao.getById(user.getVerificationId());
        assertTrue(verifyUser.isPresent());
        verifyUserDao.verify(verifyUser.get().getVerificationId());
        em.flush();
        assertFalse(verifyUser.get().isPending());
    }

    @Test
    public void testGetApplicantsByPending(){

        VerifyUser user = new VerifyUser();
        user.setComment(null);
        user.setPending(true);
        user.setFramework(fw);
        user.setUser(USERS[1]);
        em.persist(user);
        em.flush();

        List<VerifyUser> applicants = verifyUserDao.getApplicantsByPending(true, 1, 5);
        assertEquals(1, applicants.size());
        Optional<Integer> amount = verifyUserDao.getApplicantsByPendingAmount(true);
        assertTrue(amount.isPresent());
        assertEquals(1, amount.get().intValue());
        VerifyUser vu = applicants.get(0);
        assertEquals(USERS[1].getId(), vu.getUser().getId());
        assertEquals(fw.getId(), vu.getFramework().getId());
        assertTrue(vu.isPending());
    }

    @Test
    public void testDelete(){

        VerifyUser user = new VerifyUser();
        user.setComment(null);
        user.setPending(true);
        user.setFramework(fw);
        user.setUser(USERS[1]);
        em.persist(user);
        em.flush();

        Optional<VerifyUser> verifyUser = verifyUserDao.getById(user.getVerificationId());
        assertTrue(verifyUser.isPresent());

        verifyUserDao.delete(verifyUser.get().getVerificationId());
        verifyUser = verifyUserDao.getById(user.getVerificationId());
        assertFalse(verifyUser.isPresent());
        em.flush();
    }

    @Test
    public void testDeleteVerificationByUser(){

        VerifyUser user = new VerifyUser();
        user.setComment(null);
        user.setPending(true);
        user.setFramework(fw);
        user.setUser(USERS[1]);
        em.persist(user);
        em.flush();

        Optional<VerifyUser> verifyUser = verifyUserDao.getById(user.getVerificationId());
        assertTrue(verifyUser.isPresent());

        verifyUserDao.deleteVerificationByUser(USERS[1].getId());

        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, "verify_users"));
    }


}
