package com.hackncrack.ui;

public class AnsiColors {
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String DIM = "\u001B[2m";
    public static final String ITALIC = "\u001B[3m";
    public static final String UNDERLINE = "\u001B[4m";
    public static final String BLINK = "\u001B[5m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    public static final String BRIGHT_BLACK = "\u001B[90m";
    public static final String BRIGHT_RED = "\u001B[91m";
    public static final String BRIGHT_GREEN = "\u001B[92m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_BLUE = "\u001B[94m";
    public static final String BRIGHT_MAGENTA = "\u001B[95m";
    public static final String BRIGHT_CYAN = "\u001B[96m";
    public static final String BRIGHT_WHITE = "\u001B[97m";
    public static final String BG_RED = "\u001B[41m";
    public static final String BG_GREEN = "\u001B[42m";
    public static final String BG_BLUE = "\u001B[44m";

    public static String banner(String text) {
        return BRIGHT_CYAN + "+==================================================+" + RESET + "\n"
                + BRIGHT_CYAN + "|" + RESET + " " + BRIGHT_MAGENTA + text + RESET
                + " " + BRIGHT_CYAN + "|" + RESET + "\n"
                + BRIGHT_CYAN + "+==================================================+" + RESET;
    }

    public static String separator() {
        return BRIGHT_BLACK + "========================================================" + RESET;
    }

    public static String status(String label, String value) {
        return DIM + label + ": " + RESET + value;
    }

    public static String success(String msg) { return BRIGHT_GREEN + "[OK] " + msg + RESET; }
    public static String error(String msg) { return BRIGHT_RED + "[!!] " + msg + RESET; }
    public static String warning(String msg) { return BRIGHT_YELLOW + "[!] " + msg + RESET; }
    public static String info(String msg) { return BRIGHT_CYAN + "[i] " + msg + RESET; }
}