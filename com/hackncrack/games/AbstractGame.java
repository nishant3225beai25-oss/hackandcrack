package com.hackncrack.games;

import com.hackncrack.concurrency.TimerThread;
import com.hackncrack.exceptions.InvalidHackInputException;
import com.hackncrack.exceptions.TimeExpiredException;
import com.hackncrack.models.Score;
import com.hackncrack.ui.AnsiColors;
import com.hackncrack.ui.ConsoleUI;

public abstract class AbstractGame implements MiniGame {
    protected static final int MAX_LIVES = 3;
    protected static final int BLITZ_SECONDS = 180; // 3 minutes

    protected static class RoundChallenge {
        public final String promptBlock;
        public final String inputLabel;
        public final String expectedAnswer;
        public final String correctFeedback;
        public final String wrongReveal;
        public final boolean caseSensitive;

        public RoundChallenge(String promptBlock, String inputLabel, String expectedAnswer,
                              String correctFeedback, String wrongReveal, boolean caseSensitive) {
            this.promptBlock = promptBlock;
            this.inputLabel = inputLabel;
            this.expectedAnswer = expectedAnswer;
            this.correctFeedback = correctFeedback;
            this.wrongReveal = wrongReveal;
            this.caseSensitive = caseSensitive;
        }
    }

    private final String name;
    private final String description;
    private final int difficulty;
    private TimerThread timer;
    private boolean completed;
    private boolean survivalMode = true;
    private int extraPenalty = 0;

    protected AbstractGame(String name, String description, int difficulty, int timeLimitSeconds) {
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
    }

    public void setSurvivalMode(boolean survivalMode) {
        this.survivalMode = survivalMode;
    }

    public boolean isSurvivalMode() {
        return survivalMode;
    }

    protected void addScorePenalty(int points) {
        this.extraPenalty += Math.max(0, points);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getDifficulty() {
        return difficulty;
    }

    @Override
    public int getBasePoints() {
        return 15 + (difficulty * 5);
    }

    public int getTimeLimitSeconds() {
        return survivalMode ? 3600 : BLITZ_SECONDS;
    }

    @Override
    public void setTimer(TimerThread timer) {
        this.timer = timer;
    }

    @Override
    public TimerThread getTimer() {
        return timer;
    }

    public boolean isCompleted() {
        return completed;
    }

    @Override
    public void reset() {
        this.completed = false;
        this.extraPenalty = 0;
    }

    protected void markCompleted(int accuracy) {
        this.completed = true;
    }

    protected int computePoints(int accuracy) {
        return (difficulty * 25) + accuracy;
    }

    protected abstract ConsoleUI getUI();

    protected void onSessionStart() {
    }

    protected abstract RoundChallenge nextChallenge();

    protected String validateFormat(String rawInput) {
        return rawInput.isEmpty() ? "Input cannot be empty." : null;
    }

    protected String handleSpecialCommand(String rawInput, RoundChallenge current) {
        return null;
    }

    @Override
    public Score play() throws TimeExpiredException, InvalidHackInputException {
        reset();
        onSessionStart();

        ConsoleUI ui = getUI();
        int correct = 0;
        int wrong = 0;
        int attempted = 0;
        int lives = MAX_LIVES;
        int currentPoints = 0;

        int pointsPerCorrect = getBasePoints();
        int penaltyPerWrong = survivalMode ? 5 : 10;
        long blitzEndTime = System.currentTimeMillis() + (BLITZ_SECONDS * 1000L);

        String modeTag = survivalMode ? "SURVIVAL - 3 LIVES" : "3-MINUTE BLITZ";
        ui.printHeader(name.toUpperCase() + " [" + modeTag + "]");
        if (survivalMode) {
            ui.println(AnsiColors.BRIGHT_CYAN + "Mode: Survival (3 Lives) | Solve as many as you can before 3 wrong answers!" + AnsiColors.RESET);
            ui.println(AnsiColors.DIM + "+" + pointsPerCorrect + " pts per correct | -" + penaltyPerWrong + " pts & -1 life per wrong answer" + AnsiColors.RESET);
        } else {
            ui.println(AnsiColors.BRIGHT_CYAN + "Mode: 3-Minute Blitz (180s) | Unlimited lives — wrong answers deduct points!" + AnsiColors.RESET);
            ui.println(AnsiColors.DIM + "+" + pointsPerCorrect + " pts per correct | -" + penaltyPerWrong + " pts per wrong answer" + AnsiColors.RESET);
        }

        while (canContinue(lives, blitzEndTime)) {
            RoundChallenge challenge = nextChallenge();
            int questionNumber = attempted + 1;

            while (canContinue(lives, blitzEndTime)) {
                String statusBar = survivalMode
                        ? String.format("\n[Q#%d | Solved: %d | Lives: %d/%d | Score: %d pts]",
                                questionNumber, correct, lives, MAX_LIVES, currentPoints)
                        : String.format("\n[Q#%d | Solved: %d | Wrong: %d | Score: %d pts | Time: %s]",
                                questionNumber, correct, wrong, currentPoints, formatClock(getBlitzRemaining(blitzEndTime)));

                ui.println(AnsiColors.BRIGHT_YELLOW + statusBar + AnsiColors.RESET);
                ui.println(challenge.promptBlock);
                ui.print(AnsiColors.BRIGHT_GREEN + challenge.inputLabel + AnsiColors.RESET);

                String rawInput = ui.readLine().trim();

                if (!survivalMode && isBlitzExpired(blitzEndTime)) {
                    ui.println(AnsiColors.YELLOW + "\n[!] 3 minutes are up! Final input was after the buzzer." + AnsiColors.RESET);
                    break;
                }

                String specialReply = handleSpecialCommand(rawInput, challenge);
                if (specialReply != null) {
                    currentPoints = Math.max(0, currentPoints - 10);
                    ui.println(specialReply);
                    continue;
                }

                String formatError = validateFormat(rawInput);
                if (formatError != null) {
                    ui.println(AnsiColors.RED + formatError + AnsiColors.RESET);
                    continue;
                }

                attempted++;
                boolean matched = challenge.caseSensitive
                        ? rawInput.equals(challenge.expectedAnswer)
                        : rawInput.equalsIgnoreCase(challenge.expectedAnswer);

                if (matched) {
                    correct++;
                    currentPoints += pointsPerCorrect;
                    String extra = (challenge.correctFeedback == null || challenge.correctFeedback.isEmpty())
                            ? "" : " " + challenge.correctFeedback;
                    ui.println(AnsiColors.BRIGHT_GREEN + "[OK] Correct! (+" + pointsPerCorrect + " pts)" + extra + AnsiColors.RESET);
                } else {
                    wrong++;
                    currentPoints = Math.max(0, currentPoints - penaltyPerWrong);
                    if (survivalMode) {
                        lives--;
                        ui.println(AnsiColors.BRIGHT_RED + "[X] Incorrect! " + challenge.wrongReveal
                                + " (-" + penaltyPerWrong + " pts | Lives left: " + lives + ")" + AnsiColors.RESET);
                    } else {
                        ui.println(AnsiColors.BRIGHT_RED + "[X] Incorrect! " + challenge.wrongReveal
                                + " (-" + penaltyPerWrong + " pts)" + AnsiColors.RESET);
                    }
                }
                break;
            }
        }

        if (survivalMode) {
            ui.println(AnsiColors.BRIGHT_RED + "\n[!!] All 3 lives used! Survival run ended." + AnsiColors.RESET);
        } else {
            ui.println(AnsiColors.BRIGHT_CYAN + "\n[!] 3-Minute Blitz complete! Tallying your score..." + AnsiColors.RESET);
        }

        int accuracy = (attempted == 0) ? 0 : (int) Math.round((correct * 100.0) / attempted);
        int accuracyBonus = (correct > 0) ? (accuracy / 2) : 0;
        int finalPoints = (correct == 0) ? 0 : Math.max(5, currentPoints + accuracyBonus - extraPenalty);
        int remaining = survivalMode ? 0 : getBlitzRemaining(blitzEndTime);

        if (correct > 0) {
            markCompleted(accuracy);
        }

        ui.println(AnsiColors.BRIGHT_CYAN + "Final Tally -> Solved: " + correct + "/" + attempted
                + " | Wrong: " + wrong
                + " | Accuracy: " + accuracy + "%"
                + " | Final Score: " + finalPoints + " pts" + AnsiColors.RESET);

        return new Score("TEMP", finalPoints, accuracy, remaining, getName());
    }

    protected boolean canContinue(int lives, long blitzEndTime) {
        if (survivalMode) {
            return lives > 0;
        }
        return !isBlitzExpired(blitzEndTime);
    }

    protected int getBlitzRemaining(long blitzEndTime) {
        if (timer != null) {
            return Math.max(0, timer.getSecondsRemaining());
        }
        return (int) Math.max(0, (blitzEndTime - System.currentTimeMillis()) / 1000L);
    }

    protected boolean isBlitzExpired(long blitzEndTime) {
        if (timer != null) {
            return timer.isTimeExpired();
        }
        return System.currentTimeMillis() >= blitzEndTime;
    }

    protected String formatClock(int totalSeconds) {
        int mins = totalSeconds / 60;
        int secs = totalSeconds % 60;
        return String.format("%d:%02d", mins, secs);
    }
}