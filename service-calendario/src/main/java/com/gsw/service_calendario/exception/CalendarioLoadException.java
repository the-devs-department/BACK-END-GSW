package com.gsw.service_calendario.exception;

public class CalendarioLoadException extends RuntimeException {
    public CalendarioLoadException(String message) {
        super(message);
    }

    public CalendarioLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}

