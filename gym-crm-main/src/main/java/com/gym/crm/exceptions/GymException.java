package com.gym.crm.exceptions;

import org.springframework.http.HttpStatus;

public class GymException extends RuntimeException{
    private final String error;
    private final HttpStatus status;

    public GymException(HttpStatus status, String error, String message) {
        super(message);
        this.error = error;
        this.status = status;
    }

    public String getError() { return error; }
    public HttpStatus getStatus() { return status; }
}
