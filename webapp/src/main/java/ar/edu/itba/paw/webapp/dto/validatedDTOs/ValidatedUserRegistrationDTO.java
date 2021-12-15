package ar.edu.itba.paw.webapp.dto.validatedDTOs;

import ar.edu.itba.paw.webapp.dto.custom_constraints.Email;
import ar.edu.itba.paw.webapp.dto.custom_constraints.RegistrationEmail;
import ar.edu.itba.paw.webapp.dto.custom_constraints.RegistrationUsername;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class ValidatedUserRegistrationDTO {

    @NotNull
    @Size(min = 3, max = 100)
    @Pattern(regexp = "[a-zA-Z0-9]+")
    @RegistrationUsername
    private String username;

    @NotNull
    @NotEmpty
    @Email
    @RegistrationEmail
    private String mail;

    @NotNull
    @Size(min = 6, max = 100)
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
