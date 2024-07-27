package com.gettimhired.error;

import org.springframework.http.HttpStatus;

public class APIUpdateException extends RuntimeException{

    private final HttpStatus httpStatus;


    public APIUpdateException(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
