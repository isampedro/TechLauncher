package ar.edu.itba.paw.webapp.config;

import org.springframework.context.i18n.LocaleContextHolder;

import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import java.util.Locale;

public class LocaleSpecificMessageInterpolator implements MessageInterpolator {

    private final MessageInterpolator defaultInterpolator;

    public LocaleSpecificMessageInterpolator() {
        defaultInterpolator = Validation.byDefaultProvider().configure().getDefaultMessageInterpolator();
    }

    @Override
    public String interpolate(String messageTemplate, Context context) {
        return defaultInterpolator.interpolate(messageTemplate, context, LocaleContextHolder.getLocale());
    }

    @Override
    public String interpolate(String messageTemplate, Context context, Locale locale) {
        return defaultInterpolator.interpolate(messageTemplate, context, LocaleContextHolder.getLocale());
    }
}
