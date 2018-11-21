package com.test.cinema.service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageSource messageSource;

    public String getMessage(String id, Object... args) {
//        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(id, args, new Locale("ru"));
    }

}
