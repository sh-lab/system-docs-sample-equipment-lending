package net.shlab.hogefugapiyo.framework.i18n;

import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class I18nMessageResolver {

    private final MessageSource messageSource;

    public I18nMessageResolver(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String get(String code) {
        return messageSource.getMessage(code, null, currentLocale());
    }

    public String getBusinessMessage(String messageId) {
        return "[" + messageId + "]" + get(messageId);
    }

    private Locale currentLocale() {
        return LocaleContextHolder.getLocale();
    }
}
