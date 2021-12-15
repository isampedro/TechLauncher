package ar.edu.itba.paw.webapp.config;

import org.springframework.context.i18n.LocaleContextHolder;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class CustomHeaderFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if(!requestContext.getAcceptableLanguages().isEmpty()) {
            LocaleContextHolder.setLocale(requestContext.getAcceptableLanguages().get(0));
        }
    }
}