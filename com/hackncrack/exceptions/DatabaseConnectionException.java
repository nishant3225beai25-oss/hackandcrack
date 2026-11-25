package com.hackncrack.exceptions;

public class DatabaseConnectionException extends HackException {
    public DatabaseConnectionException(String message) {
        super(message, "DB_CONN_FAIL");
    }
}