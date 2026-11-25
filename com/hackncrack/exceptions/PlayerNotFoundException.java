package com.hackncrack.exceptions;

public class PlayerNotFoundException extends HackException {
    public PlayerNotFoundException(String message) {
        super(message, "PLAYER_NOT_FOUND");
    }
}