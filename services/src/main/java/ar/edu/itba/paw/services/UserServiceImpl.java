package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Framework;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.VerificationToken;
import ar.edu.itba.paw.models.VerifyUser;
import ar.edu.itba.paw.persistence.UserDao;
import ar.edu.itba.paw.persistence.VerificationTokenDao;
import ar.edu.itba.paw.persistence.VerifyUserDao;
import ar.edu.itba.paw.service.TokenExpiredException;
import ar.edu.itba.paw.service.UserAlreadyExistException;
import ar.edu.itba.paw.service.UserService;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private static final int DELETE_VERIFICATIONS = -1;
    private static final int ALLOW_MOD = 1;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final long PAGE_SIZE = 5;
    @Autowired
    private UserDao userDao;

    @Autowired
    private VerificationTokenDao tokenDao;

    @Autowired
    private VerifyUserDao verifyUserDao;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SpringTemplateEngine thymeleafTemplateEngine;

    @Autowired
    private AuthenticationManager authenticationManager;


    @Transactional(readOnly = true)
    @Override
    public Optional<User> findById(long id) {
        return userDao.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findByMail(String mail) {
        return userDao.findByMail(mail);
    }

    @Override
    public Optional<User> findByToken(String token) {
        String[] strings = token.split("-a_d-ss-");
        long userId = Long.parseLong(strings[strings.length - 1]);

        return userDao.findById(userId);
    }

    @Transactional
    @Override
    public String responseOnLogin(String token, String username) {
        return "{\"token\":\""+token+"\",\"location\":\"/users/"+findByUsername(username).get().getId()+"\"}";
    }

    private Optional<User> findByUsernameOrMail(String username, String mail) {
        return userDao.findByUsernameOrMail(username, mail);
    }

    private boolean userMustBeUpdated(User user, String mail){
        return user.getMail().equals(mail) && user.getPassword() == null;
    }

    @Transactional
    @Override
    public User create(String username, String mail, String password) throws UserAlreadyExistException {
        Optional<User> user = findByUsernameOrMail(username, mail);
        String psw = passwordEncoder.encode(password);

        if (!user.isPresent()) {
            return userDao.create(username, mail, psw);
        }

        if (userMustBeUpdated(user.get(), mail)) {
            return update(user.get().getId(), username, mail, psw).get();
        }

        if (user.get().getMail().equals(mail)) {
            throw new UserAlreadyExistException(messageSource.getMessage("uae.email", new Object[]{user.get().getMail()}, LocaleContextHolder.getLocale()));
        }
        throw new UserAlreadyExistException(messageSource.getMessage("uae.username", new Object[]{user.get().getUsername()}, LocaleContextHolder.getLocale()));
    }

    @Transactional
    @Override
    public User register(String username, String mail, String password) throws UserAlreadyExistException, DisabledException, BadCredentialsException {
        User registeredUser = create(username, mail, password);
        authenticate(username, password);
        return registeredUser;
    }

    private void authenticate(String username, String password) throws DisabledException, BadCredentialsException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    @Transactional
    @Override
    public void confirmRegistration(String token) throws TokenExpiredException, NotFoundException {

        Optional<VerificationToken> verificationToken = getVerificationToken(token);
        if (!verificationToken.isPresent()) throw new NotFoundException("Token not found");
        Optional<User> user = findById((int) verificationToken.get().getUserId());

        if (user.isPresent()) {
            if (user.get().isEnable()) {
                LOGGER.info("Register: User {} was already enabled", user.get().getId());

            }
            Calendar cal = Calendar.getInstance();

            if ((verificationToken.get().getExpiryDay().getTime() - cal.getTime().getTime()) <= 0) {

                LOGGER.error("Register: Verification token for user {} expired", user.get().getId());
                throw new TokenExpiredException("Token expired");
            }

            user.get().setEnable(true);
            saveRegisteredUser(user.get());
            LOGGER.info("Register: User {} is now verified", user.get().getId());
            return;
        }
        LOGGER.error("Register: User {} does not exist", verificationToken.get().getUserId());
        throw new NotFoundException("User not found");
    }

    @Transactional
    @Override
    public void delete(long userId) {
        userDao.delete(userId);
    }

    @Transactional
    @Override
    public void updatePassword(long userId, String password) {
        String psw = passwordEncoder.encode(password);
        userDao.updatePassword(userId, psw);
    }

    @Transactional
    @Override
    public int updateModAllow(long userId, boolean allow) {
        userDao.updateModAllow(userId, allow);
        if (!allow) {
            deleteVerificationByUser(userId);
            return DELETE_VERIFICATIONS;
        }
        return ALLOW_MOD;
    }

    private Optional<User> update(long userId, String username, String mail, String password) {
        return userDao.update(userId, username, mail, password);
    }

    @Transactional
    @Override
    public void createVerificationToken(User user, String token, String appUrl) {
        String confirmationUrl = appUrl + "/confirm/" + token;
        tokenDao.insert(user.getId(), token);
        sendEmail(user.getMail(), messageSource.getMessage("email.subject", new Object[]{}, LocaleContextHolder.getLocale()), confirmationUrl, "confirm_registration.html");
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<VerificationToken> getVerificationToken(String token) {
        return tokenDao.getByToken(token);
    }

    @Transactional
    @Override
    public void saveRegisteredUser(User user) {
        userDao.setEnable(user.getId());
    }

    @Transactional
    @Override
    public void generateNewVerificationToken(User user, String token, String appUrl) {
        Optional<VerificationToken> verificationToken = user.getVerificationToken();
        verificationToken.ifPresent(value -> tokenDao.change(value, token));
        String verificationUrl = appUrl + "/confirm/" + token;
        sendEmail(user.getMail(), messageSource.getMessage("email.subject", new Object[]{}, LocaleContextHolder.getLocale()), verificationUrl, "confirm_registration.html");
    }

    @Transactional
    @Override
    public boolean quitModdingFromTech(User user, long frameworkId) {
        for (VerifyUser verifyUser : user.getVerifications()) {
            if (verifyUser.getFrameworkId() == frameworkId) {
                deleteVerification(verifyUser.getVerificationId());
                return true;
            }
        }
        return false;
    }

    @Transactional
    @Override
    public VerifyUser createVerify(User user, Framework framework) {
        return verifyUserDao.create(user, framework, null);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<VerifyUser> getVerifyById(long verificationId) {
        return verifyUserDao.getById(verificationId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<VerifyUser> getVerifyByPending(boolean pending, long page) {
        return verifyUserDao.getVerifyForCommentByPending(pending, page, PAGE_SIZE);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Integer> getVerifyByPendingAmount(boolean pending) {
        return verifyUserDao.getVerifyForCommentByPendingAmount(pending);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Integer> getVerifyByFrameworkAmount(List<Long> frameworksIds, boolean pending) {
        return verifyUserDao.getVerifyForCommentByFrameworkAmount(frameworksIds, pending);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Integer> getApplicantsByPendingAmount(boolean pending) {
        return verifyUserDao.getApplicantsByPendingAmount(pending);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Integer> getApplicantsByFrameworkAmount(List<Long> frameworksIds, boolean pending) {
        return verifyUserDao.getApplicantsByFrameworkAmount(frameworksIds, pending);
    }

    @Transactional
    @Override
    public void deleteVerification(long verificationId) {
        verifyUserDao.delete(verificationId);
    }

    @Transactional
    @Override
    public void deleteVerificationByUser(long userId) {
        verifyUserDao.deleteVerificationByUser(userId);
    }

    @Transactional
    @Override
    public void verify(long verificationId) {
        verifyUserDao.verify(verificationId);
    }

    @Override
    public void passwordMailing(User user, String appUrl) {

        String recipientAddress = user.getMail();

        String token = UUID.randomUUID().toString() + "-a_d-ss-" + user.getId();

        String subject = messageSource.getMessage("email.recovery.subject", new Object[]{}, LocaleContextHolder.getLocale());
        String recoveryPasswordUrl = appUrl + "/forgot_password/" + token;

        sendEmail(recipientAddress, subject, recoveryPasswordUrl, "recovery_password.html");
    }

    @Override
    public void modMailing(User user, String frameworkName) {
        String subject = messageSource.getMessage("email.moderator.subject", new Object[]{frameworkName}, LocaleContextHolder.getLocale());
        sendEmail(user.getMail(), subject, "", "moderator_acceptance.html");
    }

    private void sendEmail(String recipientAddress, String subject, String message, String template) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        mailSender.setJavaMailProperties(prop);
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("confirmemailonly", "pawserver");
            }
        });
        mailSender.setSession(session);

        Context thymeleafContext = new Context();
        thymeleafContext.setVariable("message", message);
        String htmlBody = thymeleafTemplateEngine.process(template, thymeleafContext);

        final MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            final MimeMessageHelper email =
                    new MimeMessageHelper(mimeMessage, true, "UTF-8");

            email.setSubject(subject);
            email.setFrom("confirmemailonly@gmail.com");
            email.setTo(recipientAddress);

            email.setText(htmlBody, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<VerifyUser> getApplicantsByPending(boolean pending, long page) {
        return verifyUserDao.getApplicantsByPending(true, page, PAGE_SIZE);
    }

    @Transactional(readOnly = true)
    @Override
    public List<VerifyUser> getApplicantsByFrameworks(List<Long> frameworksIds, long page) {
        return verifyUserDao.getApplicantsByFrameworks(frameworksIds, page, PAGE_SIZE);
    }

    @Transactional
    @Override
    public void updateInformation(Long userId, String description, byte[] picture) {
        userDao.updateInformation(userId, description, picture, picture != null );
    }

    @Transactional(readOnly = true)
    @Override
    public List<VerifyUser> getVerifyByPendingAndFrameworks(boolean pending, List<Long> frameworkIds, long page) {
        return verifyUserDao.getVerifyByPendingAndFramework(pending, frameworkIds, page, PAGE_SIZE);
    }

    @Transactional(readOnly = true)
    @Override
    public Integer getVerifyByPendingAndFrameworksAmount(boolean pending, List<Long> frameworkIds) {
        return verifyUserDao.getVerifyByPendingAndFrameworksAmount(pending, frameworkIds);
    }

    @Override
    public long getPagesInt(Optional<Integer> count, long size) {
        return count.map(integer -> (long) Math.ceil((double) integer / size)).orElse(0L);
    }
    @Override
    public long getPagesLong(Optional<Long> count, long size) {
        return count.map(integer -> (long) Math.ceil((double) integer / size)).orElse(0L);
    }

    @Override
    public List<Long> getOwnedFrameworks(User user ) {
        List<Long> frameworkIdsForReportedComments = new LinkedList<>();
        user.getOwnedFrameworks().forEach(framework -> {
            frameworkIdsForReportedComments.add(framework.getId());
        });
        return frameworkIdsForReportedComments;
    }

    @Override
    public List<Long> getVerifiedFrameworks( User user ) {
        List<Long> frameworkIds = new LinkedList<>();
        user.getVerifications().forEach(verifyUser -> {
            if (!verifyUser.isPending())
                frameworkIds.add(verifyUser.getFrameworkId());
        });

        user.getOwnedFrameworks().forEach(framework -> {
            frameworkIds.add(framework.getId());
        });

        return frameworkIds;
    }
}
