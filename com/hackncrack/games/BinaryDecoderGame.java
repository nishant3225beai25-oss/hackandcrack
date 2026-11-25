package com.hackncrack.games;

import com.hackncrack.ui.AnsiColors;
import com.hackncrack.ui.ConsoleUI;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class BinaryDecoderGame extends AbstractGame {
    private final ConsoleUI ui;
    private final Random random;
    private final Set<Integer> usedValues;

    public BinaryDecoderGame(ConsoleUI ui) {
        this(ui, 2);
    }

    public BinaryDecoderGame(ConsoleUI ui, int difficulty) {
        super("Binary Decoder", "Convert binary numbers to decimal", difficulty, 60);
        this.ui = ui;
        this.random = new Random();
        this.usedValues = new HashSet<>();
    }

    @Override
    protected ConsoleUI getUI() {
        return ui;
    }

    @Override
    protected void onSessionStart() {
        usedValues.clear();
    }

    @Override
    protected RoundChallenge nextChallenge() {
        int bitWidth = switch (getDifficulty()) {
            case 1 -> 6;
            case 3 -> 10;
            default -> 8;
        };
        int maxVal = (1 << bitWidth) - 1;

        if (usedValues.size() >= maxVal - 5) {
            usedValues.clear();
        }

        int targetDecimal;
        do {
            targetDecimal = 1 + random.nextInt(maxVal);
        } while (!usedValues.add(targetDecimal));

        String rawBin = Integer.toBinaryString(targetDecimal);
        String paddedBin = "0".repeat(Math.max(0, bitWidth - rawBin.length())) + rawBin;
        String expected = String.valueOf(targetDecimal);

        return new RoundChallenge(
                AnsiColors.BRIGHT_CYAN + "Binary (" + bitWidth + "-bit): " + paddedBin + AnsiColors.RESET,
                "Enter decimal equivalent: ",
                expected,
                "",
                "Decimal value was: " + expected,
                false
        );
    }

    @Override
    protected String validateFormat(String rawInput) {
        try {
            Integer.parseInt(rawInput);
            return null;
        } catch (NumberFormatException e) {
            return "Invalid number. Enter a whole decimal integer.";
        }
    }
}