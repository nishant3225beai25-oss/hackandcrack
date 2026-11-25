package com.hackncrack.exceptions;

public class HackException extends Exception {
    private final String message;
    private final String code;

    public HackException(String message) {
        super(message);
        this.message = message;
        this.code = "UNKNOWN";
    }

    public HackException(String message, String code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public String getMessage() { return message; }
    public String getCode() { return code; }

    @Override
    public String toString() {
        return "[" + code + "] " + message;
    }
}