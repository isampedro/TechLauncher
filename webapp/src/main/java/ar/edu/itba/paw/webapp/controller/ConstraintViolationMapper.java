package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.dto.ConstraintViolationDTO;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
@Component
@Provider
public class ConstraintViolationMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException e) {
        List<ConstraintViolationDTO> messages = e.getConstraintViolations().stream()
                .map((constrainViolation) -> new ConstraintViolationDTO(
                        getKeyFromPath(constrainViolation.getPropertyPath().toString()),
                        constrainViolation.getMessage()))
                .collect(Collectors.toList());

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new GenericEntity<List<ConstraintViolationDTO>>(messages) {})
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private String getKeyFromPath (String constraintViolation) {
        return constraintViolation.substring(constraintViolation.lastIndexOf('.') + 1);
    }

}