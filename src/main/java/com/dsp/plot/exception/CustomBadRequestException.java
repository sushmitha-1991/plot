package com.dsp.plot.exception;

public class CustomBadRequestException extends RuntimeException {
    public CustomBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}

