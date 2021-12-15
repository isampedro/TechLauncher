package ar.edu.itba.paw.webapp.dto.validatedDTOs;

import ar.edu.itba.paw.webapp.dto.custom_constraints.Email;
import ar.edu.itba.paw.webapp.dto.custom_constraints.RegistrationEmail;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class ValidatedEmail {
    @NotNull
    @NotEmpty
    @Email
    @RegistrationEmail
    private String mail;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
