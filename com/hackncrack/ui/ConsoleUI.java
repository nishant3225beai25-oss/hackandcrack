package com.hackncrack.ui;

import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner;

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
    }

    public void printBanner() {
        System.out.println(AnsiColors.BRIGHT_CYAN + "+==========================================================================+" + AnsiColors.RESET);
        System.out.println(AnsiColors.BRIGHT_GREEN + "|  _   _    _     ____ _  __    _   _      ____ ____     _     ____ _  __  |" + AnsiColors.RESET);
        System.out.println(AnsiColors.BRIGHT_GREEN + "| | | | |  / \\   / ___| |/ /   | \\ | |    / ___|  _ \\   / \\   / ___| |/ /  |" + AnsiColors.RESET);
        System.out.println(AnsiColors.BRIGHT_GREEN + "| | |_| | / _ \\ | |   | ' /    |  \\| |   | |   | |_) | / _ \\ | |   | ' /   |" + AnsiColors.RESET);
        System.out.println(AnsiColors.BRIGHT_GREEN + "| |  _  |/ ___ \\| |___| . \\    | |\\  |   | |___|  _ < / ___ \\| |___| . \\   |" + AnsiColors.RESET);
        System.out.println(AnsiColors.BRIGHT_GREEN + "| |_| |_|_/   \\_ \\____|_|\\_\\   |_| \\_|    \\____|_| \\_\\ _/   \\_ \\____|_|\\_\\ |" + AnsiColors.RESET);
        System.out.println(AnsiColors.BRIGHT_CYAN + "|                                                                          |" + AnsiColors.RESET);
        System.out.println(AnsiColors.BRIGHT_YELLOW + "|                   >> HACK N CRACK // TERMINAL GAME <<                    |" + AnsiColors.RESET);
        System.out.println(AnsiColors.BRIGHT_CYAN + "+==========================================================================+" + AnsiColors.RESET);
    }

    public void printHeader(String title) {
        String cleanTitle = title.replace("=", "").trim();
        System.out.println();
        System.out.println(AnsiColors.BRIGHT_CYAN + "===========================================================================" + AnsiColors.RESET);
        System.out.println(AnsiColors.BRIGHT_GREEN + "  [ " + cleanTitle + " ]" + AnsiColors.RESET);
        System.out.println(AnsiColors.BRIGHT_CYAN + "===========================================================================" + AnsiColors.RESET);
    }

    public void printBox(String title, String... lines) {
        System.out.println();
        System.out.println(AnsiColors.BRIGHT_CYAN + "+--- " + title + " ---+" + AnsiColors.RESET);
        for (String line : lines) {
            System.out.println(AnsiColors.BRIGHT_CYAN + "| " + AnsiColors.RESET + line);
        }
        System.out.println(AnsiColors.BRIGHT_CYAN + "+-------------------------------------------------------------------------+" + AnsiColors.RESET);
    }

    public void printDivider() {
        System.out.println(AnsiColors.CYAN + "---------------------------------------------------------------------------" + AnsiColors.RESET);
    }

    public void printSuccess(String message) {
        System.out.println(AnsiColors.success(message));
    }

    public void printError(String message) {
        System.out.println(AnsiColors.error(message));
    }

    public void printWarning(String message) {
        System.out.println(AnsiColors.warning(message));
    }

    public void printInfo(String message) {
        System.out.println(AnsiColors.info(message));
    }

    public void println(String message) {
        System.out.println(message);
    }

    public void println() {
        System.out.println();
    }

    public void print(String message) {
        System.out.print(message);
    }

    public void printf(String format, Object... args) {
        System.out.printf(format, args);
    }

    public String readLine() {
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return "";
    }

    public String readLine(String prompt) {
        System.out.print(AnsiColors.BRIGHT_GREEN + "> " + prompt + AnsiColors.RESET);
        return readLine();
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(AnsiColors.BRIGHT_GREEN + "> " + prompt + AnsiColors.RESET);
            String input = readLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // Ignore and ask again
            }
            printError("Please enter a number between " + min + " and " + max + ".");
        }
    }

    public void typewriter(String text, int delayMillis) {
        for (char c : text.toCharArray()) {
            System.out.print(c);
            pause(delayMillis);
        }
        System.out.println();
    }

    public void pause(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void clearScreen() {
        System.out.print("\u001B[H\u001B[2J");
        System.out.flush();
    }
}