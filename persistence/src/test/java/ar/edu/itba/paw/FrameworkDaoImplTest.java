package ar.edu.itba.paw;

import ar.edu.itba.paw.models.Framework;
import ar.edu.itba.paw.models.FrameworkCategories;
import ar.edu.itba.paw.models.FrameworkType;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.FrameworkHibernateDaoImpl;
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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;


@Rollback
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class FrameworkDaoImplTest {

    private static final long FRAMEWORK_ID = 1;
    private static final String FRAMEWORK_NAME = "name";
    private static final FrameworkCategories CATEGORY = Enum.valueOf(FrameworkCategories.class, "Platforms");
    private static final FrameworkCategories CATEGORY2 = Enum.valueOf(FrameworkCategories.class, "Business");
    private static final String DESCRIPTION = "description";
    private static final String INTRODUCTION = "introduction";
    private static final FrameworkType TYPE = Enum.valueOf(FrameworkType.class, "Framework");
    private static final FrameworkType TYPE2 = Enum.valueOf(FrameworkType.class, "Service");
    private static final long AUTHOR = 1;
    private static final String AUTHOR_NAME = "user1";
    private static final byte[] PICTURE = null;

    private static long USER_ID = 1;


    @Autowired
    private DataSource ds;

    @Autowired
    private FrameworkHibernateDaoImpl frameworkDao;

    @PersistenceContext
    private EntityManager em;

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("frameworks")
                .usingGeneratedKeyColumns("framework_id");

         for (int i = 1; i < 6; i++) {
            User user = new User("user"+i,"mail"+i,null,true,"",true);
            em.persist(user);
            em.flush();
            USER_ID = user.getId();
        }
    }

    @Test
    public void testFindById() throws SQLException {
        //Preconditions


        Date date = new Date(System.currentTimeMillis());

        Framework framework = new Framework();
        framework.setName(FRAMEWORK_NAME);
        framework.setCategory(CATEGORY);
        framework.setType(TYPE);
        framework.setDescription(DESCRIPTION);
        framework.setIntroduction(INTRODUCTION);
        framework.setAuthor(em.find(User.class, USER_ID));
        framework.setPublishDate(date);
        em.persist(framework);

        //Class under test
        Optional<Framework> framework2 = frameworkDao.findById(framework.getId());
        em.flush();

        //Asserts
        Assert.assertTrue(framework2.isPresent());
        Assert.assertEquals(FRAMEWORK_NAME, framework2.get().getName());
        Assert.assertEquals(CATEGORY, framework2.get().getCategory());
        Assert.assertEquals(DESCRIPTION, framework2.get().getDescription());
        Assert.assertEquals(INTRODUCTION, framework2.get().getIntroduction());
        Assert.assertEquals(TYPE, framework2.get().getType());
        Assert.assertEquals(USER_ID, framework2.get().getAuthor().getId().longValue());
        Assert.assertEquals(date, framework2.get().getPublishDate());

    }

   @Test
    public void tesFindByIdNotExists() {

        //Class under test
        Optional<Framework> framework = frameworkDao.findById(FRAMEWORK_ID);
        em.flush();

        //Asserts
        Assert.assertEquals(false,framework.isPresent());
    }

    @Test(expected = Exception.class)
    public void testCreateAlreadyExists() {


        Timestamp ts = new Timestamp(System.currentTimeMillis());
        final Map<String, Object> args = new HashMap<>();

        args.put("framework_id", FRAMEWORK_ID);
        args.put("framework_name", FRAMEWORK_NAME);
        args.put("category", CATEGORY.name());
        args.put("description", DESCRIPTION);
        args.put("introduction", INTRODUCTION);
        args.put("type", TYPE.name());
        args.put("date", ts);
        args.put("author", AUTHOR);

        jdbcInsert.execute(args);

        //Class under test
        Optional<Framework> framework = frameworkDao.create(FRAMEWORK_NAME,CATEGORY,DESCRIPTION, INTRODUCTION, TYPE,USER_ID,PICTURE);
    }


    @Test
    public void testGetFrameworkNames(){
        //Preconditions

        Date date = new Date(System.currentTimeMillis());
        List<String> names = new ArrayList<>();

        for (int i = 0; i < 4; i++) {

            Framework framework = new Framework();
            framework.setName(FRAMEWORK_NAME+String.valueOf(i));
            framework.setCategory(CATEGORY);
            framework.setType(TYPE);
            framework.setDescription(DESCRIPTION);
            framework.setIntroduction(INTRODUCTION);
            framework.setAuthor(em.find(User.class, USER_ID));
            framework.setPublishDate(date);
            em.persist(framework);

            names.add(FRAMEWORK_NAME+String.valueOf(i));
        }

        //Class under test

        List<String> frameworkNames = frameworkDao.getFrameworkNames();
        em.flush();

        //Asserts
        Assert.assertFalse(frameworkNames.isEmpty());
        Assert.assertEquals(names, frameworkNames);

    }


    @Test
    public void testSearchByCategory(){
        //Preconditions

        Date date = new Date(System.currentTimeMillis());

        List<Framework> results = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            Framework framework = new Framework();
            framework.setName("html"+i);
            framework.setDescription("html is great");

            framework.setType(TYPE);
            framework.setIntroduction(INTRODUCTION);
            framework.setAuthor(em.find(User.class, USER_ID));
            framework.setPublishDate(date);

            switch(i) {
                case 0:
                case 1:
                    framework.setCategory(CATEGORY);
                    results.add(framework);
                    break;
                case 2:
                case 3:
                    framework.setCategory(CATEGORY2);
                    break;

            }

            em.persist(framework);

        }



        em.flush();
        List<FrameworkCategories> categoriesList = new ArrayList<>();
        categoriesList.add(CATEGORY);
        //Class under test
        List<Framework> matchingFrameworks = frameworkDao.search(null, categoriesList, null,0,5,false,0,null,null,0,1,5);

        //Asserts
        Assert.assertFalse(matchingFrameworks.isEmpty());
        Assert.assertEquals(results, matchingFrameworks);
    }

    @Test
    public void testSearchByType(){
        //Preconditions


        Date date = new Date(System.currentTimeMillis());

        List<Framework> results = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            Framework framework = new Framework();
            framework.setName("html"+i);
            framework.setDescription("html is great");

            framework.setCategory(CATEGORY);
            framework.setIntroduction(INTRODUCTION);
            framework.setAuthor(em.find(User.class, USER_ID));
            framework.setPublishDate(date);

            switch(i) {
                case 0:
                case 1:
                    framework.setType(TYPE);
                    results.add(framework);
                    break;
                case 2:
                case 3:
                    framework.setType(TYPE2);
                    break;

            }

            em.persist(framework);

        }



        em.flush();
        List<FrameworkType> typesList = new ArrayList<>();
        typesList.add(TYPE);
        //Class under test
        List<Framework> matchingFrameworks = frameworkDao.search(null, null,typesList,0,5,false,0,null,null,0,1,5);

        //Asserts
        Assert.assertFalse(matchingFrameworks.isEmpty());
        Assert.assertEquals(results, matchingFrameworks);
    }



}
