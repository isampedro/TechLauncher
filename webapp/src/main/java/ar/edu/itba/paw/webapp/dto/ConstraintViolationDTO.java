package ar.edu.itba.paw.webapp.dto;

public class ConstraintViolationDTO {
    private String key, message;

    public ConstraintViolationDTO() {
    }

    public ConstraintViolationDTO(String key, String message) {
        this.key = key;
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
