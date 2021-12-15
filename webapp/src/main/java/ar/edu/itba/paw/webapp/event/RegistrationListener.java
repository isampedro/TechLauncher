package ar.edu.itba.paw.webapp.event;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    @Autowired
    private UserService service;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        if(!event.isResend()){
            service.createVerificationToken(user, token,reformatURL(event.getAppUrl()));
        }else{
            service.generateNewVerificationToken(user,token,reformatURL(event.getAppUrl()));
        }
    }

    private String reformatURL(String url){
        int startIndex;

        startIndex = url.indexOf("/api");
        url = url.substring(0, startIndex).concat("/#/register");
        return url;
    }
}