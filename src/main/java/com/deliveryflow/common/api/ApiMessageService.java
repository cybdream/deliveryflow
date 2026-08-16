package com.deliveryflow.common.api;

import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Resolves API response messages from the active request locale.
 */
@Component
public class ApiMessageService {
    private final MessageSource messageSource;

    public ApiMessageService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String get(String key, Object... arguments) {
        return get(key, LocaleContextHolder.getLocale(), arguments);
    }

    public String get(String key, Locale locale, Object... arguments) {
        return messageSource.getMessage(key, arguments, key, locale);
    }
}