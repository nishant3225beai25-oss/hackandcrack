package com.hackncrack.games;

import com.hackncrack.exceptions.InvalidHackInputException;
import com.hackncrack.exceptions.TimeExpiredException;
import com.hackncrack.models.Score;
import com.hackncrack.ui.AnsiColors;
import com.hackncrack.ui.ConsoleUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Guess the secret number between 1 and 100. Fewer attempts = more points. */
public class NumberGuessGame extends AbstractGame {
    private final ConsoleUI ui;
    private final Random random;

    public NumberGuessGame(ConsoleUI ui) {
        this(ui, 2);
    }

    public NumberGuessGame(ConsoleUI ui, int difficulty) {
        super("Number Guesser", "Guess the secret number between 1 and 100", difficulty, 60);
        this.ui = ui;
        this.random = new Random();
    }

    private int getMaxAttempts() {
        return switch (getDifficulty()) {
            case 1 -> 10; // Easy
            case 3 -> 5;  // Hard
            default -> 7; // Medium
        };
    }

    @Override
    protected ConsoleUI getUI() {
        return ui;
    }

    @Override
    protected RoundChallenge nextChallenge() {
        return null;
    }

    @Override
    public Score play() throws TimeExpiredException, InvalidHackInputException {
        reset();
        onSessionStart();

        int correct = 0;
        int wrong = 0;
        int attempted = 0;
        int lives = MAX_LIVES;
        int currentPoints = 0;

        int maxAttempts = getMaxAttempts();
        int basePoints = getBasePoints();
        int penaltyPerWrong = isSurvivalMode() ? 5 : 10;
        long blitzEndTime = System.currentTimeMillis() + (BLITZ_SECONDS * 1000L);

        String modeTag = isSurvivalMode() ? "SURVIVAL - 3 LIVES" : "3-MINUTE BLITZ";
        ui.printHeader(getName().toUpperCase() + " [" + modeTag + "]");
        ui.println(AnsiColors.BRIGHT_YELLOW + "I am thinking of a number between 1 and 100." + AnsiColors.RESET);
        ui.println(AnsiColors.BRIGHT_YELLOW + "You have " + maxAttempts + " attempts per round. Fewer attempts = more points!" + AnsiColors.RESET);

        if (isSurvivalMode()) {
            ui.println(AnsiColors.BRIGHT_CYAN + "Mode: Survival (3 Lives) | Solve as many rounds as you can before 3 failed rounds!" + AnsiColors.RESET);
            ui.println(AnsiColors.DIM + "+" + basePoints + " base pts + (attempts left * 5) speed bonus | -" + penaltyPerWrong + " pts & -1 life if out of attempts" + AnsiColors.RESET);
        } else {
            ui.println(AnsiColors.BRIGHT_CYAN + "Mode: 3-Minute Blitz (180s) | Unlimited lives — failed rounds deduct points!" + AnsiColors.RESET);
            ui.println(AnsiColors.DIM + "+" + basePoints + " base pts + (attempts left * 5) speed bonus | -" + penaltyPerWrong + " pts if out of attempts" + AnsiColors.RESET);
        }

        while (canContinue(lives, blitzEndTime)) {
            int secret = 1 + random.nextInt(100);
            List<Integer> guesses = new ArrayList<>();
            int roundNumber = attempted + 1;
            boolean roundSolved = false;

            String statusBar = isSurvivalMode()
                    ? String.format("\n[Round #%d | Solved: %d | Lives: %d/%d | Score: %d pts]",
                            roundNumber, correct, lives, MAX_LIVES, currentPoints)
                    : String.format("\n[Round #%d | Solved: %d | Failed: %d | Score: %d pts | Time: %s]",
                            roundNumber, correct, wrong, currentPoints, formatClock(getBlitzRemaining(blitzEndTime)));

            ui.println(AnsiColors.BRIGHT_YELLOW + statusBar + AnsiColors.RESET);

            for (int attempt = 1; attempt <= maxAttempts && canContinue(lives, blitzEndTime); attempt++) {
                int guess = -1;
                while (true) {
                    if (!isSurvivalMode() && isBlitzExpired(blitzEndTime)) {
                        break;
                    }
                    String timeTag = isSurvivalMode() ? "" : " [Time: " + formatClock(getBlitzRemaining(blitzEndTime)) + "]";
                    ui.print(AnsiColors.BRIGHT_GREEN + "Attempt " + attempt + "/" + maxAttempts + timeTag + " (1-100): " + AnsiColors.RESET);
                    String raw = ui.readLine().trim();

                    if (!isSurvivalMode() && isBlitzExpired(blitzEndTime)) {
                        break;
                    }

                    try {
                        guess = Integer.parseInt(raw);
                        if (guess < 1 || guess > 100) {
                            ui.println(AnsiColors.RED + "Please enter a number between 1 and 100." + AnsiColors.RESET);
                            continue;
                        }
                        break;
                    } catch (NumberFormatException e) {
                        ui.println(AnsiColors.RED + "Invalid input. Enter a whole number between 1 and 100." + AnsiColors.RESET);
                    }
                }

                if (!isSurvivalMode() && isBlitzExpired(blitzEndTime)) {
                    ui.println(AnsiColors.YELLOW + "\n[!] 3 minutes are up! Final input was after the buzzer." + AnsiColors.RESET);
                    break;
                }

                guesses.add(guess);

                if (guess == secret) {
                    attempted++;
                    correct++;
                    roundSolved = true;
                    int speedBonus = (maxAttempts - attempt + 1) * 5;
                    int roundPoints = basePoints + speedBonus;
                    currentPoints += roundPoints;
                    ui.println(AnsiColors.BRIGHT_GREEN + "[OK] You found it in " + attempt + " attempt(s)! (+"
                            + roundPoints + " pts: " + basePoints + " base + " + speedBonus + " speed bonus)" + AnsiColors.RESET);
                    break;
                }

                if (attempt < maxAttempts) {
                    String hint = (guess < secret) ? "Too low!" : "Too high!";
                    ui.println(AnsiColors.BRIGHT_CYAN + hint + AnsiColors.DIM + "  | Your guesses: " + guesses + AnsiColors.RESET);
                }
            }

            if (!roundSolved && canContinue(lives, blitzEndTime)) {
                attempted++;
                wrong++;
                currentPoints = Math.max(0, currentPoints - penaltyPerWrong);
                if (isSurvivalMode()) {
                    lives--;
                    ui.println(AnsiColors.BRIGHT_RED + "[X] Out of attempts! The number was " + secret
                            + ". Your guesses: " + guesses
                            + " (-" + penaltyPerWrong + " pts | Lives left: " + lives + ")" + AnsiColors.RESET);
                } else {
                    ui.println(AnsiColors.BRIGHT_RED + "[X] Out of attempts! The number was " + secret
                            + ". Your guesses: " + guesses
                            + " (-" + penaltyPerWrong + " pts)" + AnsiColors.RESET);
                }
            }
        }

        if (isSurvivalMode()) {
            ui.println(AnsiColors.BRIGHT_RED + "\n[!!] All 3 lives used! Survival run ended." + AnsiColors.RESET);
        } else {
            ui.println(AnsiColors.BRIGHT_CYAN + "\n[!] 3-Minute Blitz complete! Tallying your score..." + AnsiColors.RESET);
        }

        int accuracy = (attempted == 0) ? 0 : (int) Math.round((correct * 100.0) / attempted);
        int accuracyBonus = (correct > 0) ? (accuracy / 2) : 0;
        int finalPoints = (correct == 0) ? 0 : Math.max(5, currentPoints + accuracyBonus);
        int remaining = isSurvivalMode() ? 0 : getBlitzRemaining(blitzEndTime);

        if (correct > 0) {
            markCompleted(accuracy);
        }

        ui.println(AnsiColors.BRIGHT_CYAN + "Final Tally -> Solved: " + correct + "/" + attempted
                + " | Failed: " + wrong
                + " | Accuracy: " + accuracy + "%"
                + " | Final Score: " + finalPoints + " pts" + AnsiColors.RESET);

        return new Score("TEMP", finalPoints, accuracy, remaining, getName());
    }
}