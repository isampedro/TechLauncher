package ar.edu.itba.paw;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.UserDao;
import ar.edu.itba.paw.persistence.VerifyUserDao;
import ar.edu.itba.paw.service.UserAlreadyExistException;
import ar.edu.itba.paw.services.UserServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static junit.framework.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    private static final String PASSWORD = "passwordpassword";
    private static final String USERNAME = "username";
    private static final String MAIL = "username@mail.com";
    @InjectMocks
    private UserServiceImpl userService = new UserServiceImpl();
    @Mock
    private UserDao userDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private VerifyUserDao verifyUserDao;

    @Test()
    public void createNew() throws UserAlreadyExistException {
        Mockito.when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD);
        User u = new User();
        u.setMail(MAIL);
        u.setId(0L);
        u.setUsername(USERNAME);
        u.setPassword(PASSWORD);
        Mockito.when(userDao.create(USERNAME,MAIL,PASSWORD)).thenReturn(u);
        Mockito.when(userDao.findByUsernameOrMail(USERNAME,MAIL)).thenReturn(Optional.empty());

        User user = userService.create(USERNAME,MAIL,PASSWORD);

        assertEquals(USERNAME,user.getUsername());
        assertEquals(MAIL,user.getMail());
        assertEquals(PASSWORD,user.getPassword());
    }
    @Test()
    public void createNewNotActivated() throws UserAlreadyExistException {
        Mockito.when(passwordEncoder.encode(PASSWORD)).thenReturn(PASSWORD);
        User u = new User();
        u.setMail(MAIL);
        u.setId(0L);
        u.setUsername(USERNAME);
        u.setPassword(PASSWORD);
        User u2 = new User();
        u2.setMail(MAIL);
        u2.setId(0L);
        u2.setUsername(USERNAME);
        Mockito.when(userDao.update(0,USERNAME,MAIL,PASSWORD)).thenReturn(Optional.of(u));
        Mockito.when(userDao.findByUsernameOrMail(USERNAME,MAIL)).thenReturn(Optional.of(u2));

        User user = userService.create(USERNAME,MAIL,PASSWORD);

        assertEquals(USERNAME,user.getUsername());
        assertEquals(MAIL,user.getMail());
        assertEquals(PASSWORD,user.getPassword());
    }
    @Test(expected = Exception.class)
    public void createNewExisting() throws UserAlreadyExistException {
        User u = new User();
        u.setMail(MAIL);
        u.setId(0L);
        u.setUsername(USERNAME);
        u.setPassword(PASSWORD);
        Mockito.when(userDao.findByUsernameOrMail(USERNAME,MAIL)).thenReturn(Optional.of(u));

        userService.create(USERNAME,MAIL,PASSWORD);
    }
    @Test
    public void UpdateModeFalse(){
        int answer = userService.updateModAllow(0,false);
        assertEquals(-1,answer);
    }
    @Test
    public void UpdateModeTrue(){
        int answer = userService.updateModAllow(0,true);
        assertEquals(1,answer);
    }
}
