package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.VerificationToken;
import ar.edu.itba.paw.service.TokenExpiredException;
import ar.edu.itba.paw.service.UserAlreadyExistException;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.auth.PawUserDetailsService;
import ar.edu.itba.paw.webapp.dto.JwtResponseDTO;
import ar.edu.itba.paw.webapp.dto.validatedDTOs.ValidatedEmail;
import ar.edu.itba.paw.webapp.dto.validatedDTOs.ValidatedUserRegistrationDTO;
import ar.edu.itba.paw.webapp.event.OnRegistrationCompleteEvent;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Optional;

@Path("register")
@Component
public class RegisterController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private UserService us;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private PawUserDetailsService userDetailsService;

    @Context
    private UriInfo uriInfo;

    @POST
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response register(@Valid final ValidatedUserRegistrationDTO newUser) {
        try {
            User registeredUser = us.register(newUser.getUsername(), newUser.getMail(), newUser.getPassword());
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registeredUser, LocaleContextHolder.getLocale(), uriInfo.getRequestUri().toString(), false));
            LOGGER.info("Register: User '{}' registered successfully with id '{}'", registeredUser.getUsername(), registeredUser.getId());

            String token = userDetailsService.generateToken(newUser.getUsername());
            return Response.ok(new JwtResponseDTO(token, registeredUser, uriInfo)).build();

        } catch (UserAlreadyExistException uaeEx) {
            return Response.status(Response.Status.CONFLICT).build();
        } catch (DisabledException | BadCredentialsException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/confirm")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response confirmRegistration(@QueryParam("token") String token) {
        try {
            us.confirmRegistration(token);
            return Response.ok().build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (TokenExpiredException e) {
            return Response.status(Response.Status.GONE).build();
        }
    }

    @POST
    @Path("resend_token")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response resendToken() {
        Optional<User> user = us.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        Optional<VerificationToken> verificationToken = us.getVerificationToken(user.get().getVerificationToken().get().getToken());

        if (verificationToken.isPresent()) {
            Optional<User> registered = Optional.ofNullable(verificationToken.get().getUser());

            if (registered.isPresent()) {
                eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered.get(), LocaleContextHolder.getLocale(), uriInfo.getBaseUri().toString() + "register", true));
                LOGGER.info("Register: Resending verification token for user {}", registered.get().getId());
                return Response.ok().build();
            }

            LOGGER.error("Register: User {} does not exist", verificationToken.get().getUserId());
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("forgot_password")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response forgotPassword(@Valid final ValidatedEmail userDTO) {
        Optional<User> optionalUser = us.findByMail(userDTO.getMail());
        if (!optionalUser.isPresent()) {
            LOGGER.error("Register: No user found with mail {}", userDTO.getMail());
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        us.passwordMailing(optionalUser.get(), reformatURL(uriInfo.getBaseUri().toString()));
        LOGGER.info("Register: Successfully sent recovery mail to {}", userDTO.getMail());
        return Response.ok().build();
    }

    private String reformatURL(String url) {
        int startIndex;

        startIndex = url.indexOf("/api");
        url = url.substring(0, startIndex).concat("/#/register");
        return url;
    }

}

