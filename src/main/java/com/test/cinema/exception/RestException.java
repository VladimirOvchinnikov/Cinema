package com.test.cinema.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RestException extends RuntimeException {


    public RestException(String message) {
        super(message);
    }
}
