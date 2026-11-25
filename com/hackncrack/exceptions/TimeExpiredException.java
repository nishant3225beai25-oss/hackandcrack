package com.hackncrack.exceptions;

public class TimeExpiredException extends HackException {
    public TimeExpiredException(String message) {
        super(message, "TIME_EXPIRED");
    }
}