
 package com.hackncrack.games;

import com.hackncrack.exceptions.InvalidHackInputException;
import com.hackncrack.exceptions.TimeExpiredException;
import com.hackncrack.models.Score;
import com.hackncrack.ui.AnsiColors;
import com.hackncrack.ui.ConsoleUI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/** Unscramble the letters to find the word. */
public class WordScrambleGame extends AbstractGame {
    private static final String[] WORD_ARRAY = {
        "java", "thread", "string", "object", "method",
        "binary", "puzzle", "logic", "array", "exception",
        "compiler", "variable", "package", "interface", "boolean",
        "integer", "abstract", "override", "network", "protocol",
        "cipher", "matrix", "pointer", "socket", "stream"
    };

    private final ConsoleUI ui;
    private final Queue<String> wordQueue;

    public WordScrambleGame(ConsoleUI ui) {
        this(ui, 2);
    }

    public WordScrambleGame(ConsoleUI ui, int difficulty) {
        super("WordScramble", "Unscramble the mixed-up letters to find the word", difficulty, 60);
        this.ui = ui;
        this.wordQueue = new LinkedList<>();
    }

    @Override
    protected ConsoleUI getUI() {
        return ui;
    }

    @Override
    protected void onSessionStart() {
        wordQueue.clear();
        refillWords();
    }

    @Override
    protected RoundChallenge nextChallenge() {
        return null;
    }

    private void refillWords() {
        List<String> words = new ArrayList<>();
        Collections.addAll(words, WORD_ARRAY);
        Collections.shuffle(words);
        wordQueue.addAll(words);
    }

    /** Mixes the letters of a word. */
    private String scramble(String word) {
        List<Character> letters = new ArrayList<>();
        for (char c : word.toCharArray()) {
            letters.add(c);
        }
        String result;
        do {
            Collections.shuffle(letters);
            StringBuilder sb = new StringBuilder();
            for (char c : letters) {
                sb.append(c);
            }
            result = sb.toString();
        } while (result.equals(word));
        return result;
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

        int diff = getDifficulty();
        int firstTryPoints = 10 * diff;
        int secondTryPoints = 5 * diff;
        int penaltyPerWrong = isSurvivalMode() ? 5 : 10;
        long blitzEndTime = System.currentTimeMillis() + (BLITZ_SECONDS * 1000L);

        String modeTag = isSurvivalMode() ? "SURVIVAL - 3 LIVES" : "3-MINUTE BLITZ";
        ui.printHeader(getName().toUpperCase() + " [" + modeTag + "]");
        ui.println(AnsiColors.BRIGHT_YELLOW + "Unscramble the mixed letters. You get 2 tries per word." + AnsiColors.RESET);
        ui.println(AnsiColors.BRIGHT_YELLOW + "1st try = +" + firstTryPoints + " pts | 2nd try = +" + secondTryPoints + " pts (10/5 pts x difficulty " + diff + ")." + AnsiColors.RESET);

        if (isSurvivalMode()) {
            ui.println(AnsiColors.BRIGHT_CYAN + "Mode: Survival (3 Lives) | Solve as many words as you can before 3 missed words!" + AnsiColors.RESET);
            ui.println(AnsiColors.DIM + "-" + penaltyPerWrong + " pts & -1 life if you miss both tries" + AnsiColors.RESET);
        } else {
            ui.println(AnsiColors.BRIGHT_CYAN + "Mode: 3-Minute Blitz (180s) | Unlimited lives — missed words deduct points!" + AnsiColors.RESET);
            ui.println(AnsiColors.DIM + "-" + penaltyPerWrong + " pts if you miss both tries" + AnsiColors.RESET);
        }

        while (canContinue(lives, blitzEndTime)) {
            if (wordQueue.isEmpty()) {
                refillWords();
            }
            String word = wordQueue.poll();
            String mixed = scramble(word).toUpperCase();
            int wordNumber = attempted + 1;
            boolean wordSolved = false;

            String statusBar = isSurvivalMode()
                    ? String.format("\n[Word #%d | Solved: %d | Lives: %d/%d | Score: %d pts]",
                            wordNumber, correct, lives, MAX_LIVES, currentPoints)
                    : String.format("\n[Word #%d | Solved: %d | Missed: %d | Score: %d pts | Time: %s]",
                            wordNumber, correct, wrong, currentPoints, formatClock(getBlitzRemaining(blitzEndTime)));

            ui.println(AnsiColors.BRIGHT_YELLOW + statusBar + AnsiColors.RESET);
            ui.println(AnsiColors.BRIGHT_CYAN + "Word " + wordNumber + ": " + mixed + AnsiColors.RESET);

            for (int attempt = 1; attempt <= 2 && canContinue(lives, blitzEndTime); attempt++) {
                String answer = "";
                while (true) {
                    if (!isSurvivalMode() && isBlitzExpired(blitzEndTime)) {
                        break;
                    }
                    ui.print(AnsiColors.BRIGHT_GREEN + "Your answer (Try " + attempt + "/2): " + AnsiColors.RESET);
                    answer = ui.readLine().trim();

                    if (!isSurvivalMode() && isBlitzExpired(blitzEndTime)) {
                        break;
                    }
                    if (answer.isEmpty()) {
                        ui.println(AnsiColors.RED + "Answer cannot be empty." + AnsiColors.RESET);
                        continue;
                    }
                    break;
                }

                if (!isSurvivalMode() && isBlitzExpired(blitzEndTime)) {
                    ui.println(AnsiColors.YELLOW + "\n[!] 3 minutes are up! Final input was after the buzzer." + AnsiColors.RESET);
                    break;
                }

                if (answer.equalsIgnoreCase(word)) {
                    attempted++;
                    correct++;
                    wordSolved = true;
                    int earned = (attempt == 1) ? firstTryPoints : secondTryPoints;
                    currentPoints += earned;
                    ui.println(AnsiColors.BRIGHT_GREEN + "[OK] Correct! (+" + earned + " pts)" + AnsiColors.RESET);
                    break;
                }

                if (attempt == 1) {
                    ui.println(AnsiColors.YELLOW + "[!] Wrong. Hint: it starts with '" + word.substring(0, 1).toUpperCase() + "'" + AnsiColors.RESET);
                }
            }

            if (!wordSolved && canContinue(lives, blitzEndTime)) {
                attempted++;
                wrong++;
                currentPoints = Math.max(0, currentPoints - penaltyPerWrong);
                if (isSurvivalMode()) {
                    lives--;
                    ui.println(AnsiColors.BRIGHT_RED + "[X] Wrong. The word was: " + word.toUpperCase()
                            + " (-" + penaltyPerWrong + " pts | Lives left: " + lives + ")" + AnsiColors.RESET);
                } else {
                    ui.println(AnsiColors.BRIGHT_RED + "[X] Wrong. The word was: " + word.toUpperCase()
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
                + " | Missed: " + wrong
                + " | Accuracy: " + accuracy + "%"
                + " | Final Score: " + finalPoints + " pts" + AnsiColors.RESET);

        return new Score("TEMP", finalPoints, accuracy, remaining, getName());
    }
}