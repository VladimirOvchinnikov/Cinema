package com.test.cinema.controller;

import com.test.cinema.exception.ExceptionResponse;
import com.test.cinema.exception.RestException;
import com.test.cinema.service.util.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.PersistenceException;

@ControllerAdvice
public class ExceptionController {

    @Autowired
    private MessageService messageService;

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ExceptionResponse handleRestException(RestException e){
        e.printStackTrace();
        ExceptionResponse response = new ExceptionResponse();
        response.setCode(HttpStatus.BAD_REQUEST.value());
        response.setMessage(e.getMessage());
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ExceptionResponse handlePersistenceException(PersistenceException e){
        e.printStackTrace();
        ExceptionResponse response = new ExceptionResponse();
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(messageService.getMessage("error.hibernate.bad-persistence"));
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ExceptionResponse handleException(Exception e){
        e.printStackTrace();
        ExceptionResponse response = new ExceptionResponse();
        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(messageService.getMessage("error.server.unknown-exception"));
        return response;
    }

}
