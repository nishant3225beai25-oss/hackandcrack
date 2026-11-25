package com.hackncrack.exceptions;

public class SaveFileCorruptedException extends HackException {
    public SaveFileCorruptedException(String message) {
        super(message, "SAVE_FILE_CORRUPT");
    }
}