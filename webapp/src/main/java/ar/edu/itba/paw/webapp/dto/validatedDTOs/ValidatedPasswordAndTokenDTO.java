package ar.edu.itba.paw.webapp.dto.validatedDTOs;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ValidatedPasswordAndTokenDTO {
    @NotNull
    @Size(min = 44, max = 52)
    private String token;

    @NotNull
    @Size(min = 6, max = 100)
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
