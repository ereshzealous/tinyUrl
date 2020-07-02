package com.tetrasoft.shorturl.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApplicationException extends Exception {

    private HttpStatus status;

    public ApplicationException(Throwable e, String message, HttpStatus status) {
        super(message, e);
        this.status = status;
    }
}
