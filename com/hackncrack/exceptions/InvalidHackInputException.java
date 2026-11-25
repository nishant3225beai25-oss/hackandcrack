package com.hackncrack.exceptions;

public class InvalidHackInputException extends HackException {
    public InvalidHackInputException(String message) {
        super(message, "INVALID_INPUT");
    }
}