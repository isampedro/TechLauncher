package ar.edu.itba.paw.webapp.event;

import ar.edu.itba.paw.models.User;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private String appUrl;
    private Locale locale;
    private User user;
    private boolean resend;
    public OnRegistrationCompleteEvent(User user, Locale locale, String appUrl,Boolean resend) {
        super(user);
        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
        this.resend=resend;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public Locale getLocale() {
        return locale;
    }

    public User getUser() {
        return user;
    }

    public boolean isResend() {
        return resend;
    }

    public void setResend(boolean resend) {
        this.resend = resend;
    }
}
