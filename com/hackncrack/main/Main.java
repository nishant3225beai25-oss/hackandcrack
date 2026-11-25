package com.hackncrack.main;

import com.hackncrack.ui.AnsiColors;
import com.hackncrack.ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        ConsoleUI ui = new ConsoleUI();
        try {
            HackNCrackApp app = new HackNCrackApp(ui);
            app.start();
        } catch (Exception e) {
            System.err.println(AnsiColors.error("Fatal error: " + e.getMessage()));
            e.printStackTrace();
        }
    }
}