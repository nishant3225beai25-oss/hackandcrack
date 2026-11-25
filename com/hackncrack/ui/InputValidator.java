package com.hackncrack.ui;

import com.hackncrack.exceptions.InvalidHackInputException;

public class InputValidator {
    public static final String HACKER_ALIAS_REGEX = "^[A-Za-z0-9_]{3,15}$";
    public static final String USERNAME_REGEX = "^[A-Za-z][A-Za-z0-9_]{1,23}$";

    public static String validateCodename(String input) throws InvalidHackInputException {
        if (input == null || !input.trim().matches(HACKER_ALIAS_REGEX)) {
            throw new InvalidHackInputException("Codename must be 3-15 chars (A-Z, 0-9, _).");
        }
        return input.trim();
    }

    public static String validateUsername(String input) throws InvalidHackInputException {
        if (input == null || !input.trim().matches(USERNAME_REGEX)) {
            throw new InvalidHackInputException("Username must start with a letter, 2-24 chars.");
        }
        return input.trim();
    }

    public static int validateRange(String input, int min, int max) throws InvalidHackInputException {
        int value;
        try {
            value = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            throw new InvalidHackInputException("Expected an integer between " + min + " and " + max);
        }
        if (value < min || value > max) {
            throw new InvalidHackInputException("Value must be between " + min + " and " + max);
        }
        return value;
    }

    public static boolean isValidPassword(String input) {
        if (input == null || input.length() < 3 || input.length() > 12) { return false; }
        return input.matches("^[A-Za-z0-9]+$");
    }

    public static boolean isValidBinary(String input) {
        if (input == null || input.isEmpty() || input.length() > 16) { return false; }
        return input.matches("^[01]+$");
    }

    public static boolean parseBoolean(String input) throws InvalidHackInputException {
        String v = input == null ? "" : input.trim().toLowerCase();
        if (v.equals("true") || v.equals("t") || v.equals("yes") || v.equals("y")) { return true; }
        if (v.equals("false") || v.equals("f") || v.equals("no") || v.equals("n")) { return false; }
        throw new InvalidHackInputException("Invalid boolean input: '" + input + "'. Expected true/false/yes/no/t/f/y/n.");
    }
}
