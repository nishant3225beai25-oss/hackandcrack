package com.hackncrack.main;

import com.hackncrack.concurrency.TimerThread;
import com.hackncrack.games.AbstractGame;
import com.hackncrack.games.BinaryDecoderGame;
import com.hackncrack.games.WordScrambleGame;
import com.hackncrack.games.LogicGateGame;
import com.hackncrack.games.NumberGuessGame;
import com.hackncrack.models.Player;
import com.hackncrack.models.Score;
import com.hackncrack.persistence.PlayerRepository;
import com.hackncrack.persistence.ScoreRepository;

import com.hackncrack.ui.AnsiColors;
import com.hackncrack.ui.ConsoleUI;

public class GameEngine {
    private final ConsoleUI ui;

    private final ScoreRepository scoreRepository;
    private final PlayerRepository playerRepository;
    private int difficulty;

    public GameEngine(ConsoleUI ui, ScoreRepository scoreRepository, PlayerRepository playerRepository) {
        this.ui = ui;
        
        this.scoreRepository = scoreRepository;
        this.playerRepository = playerRepository;
    }

    public void runSession(Player player) {
        ui.print("Choose difficulty [1=Easy  2=Medium  3=Hard]: ");
        String diffInput = ui.readLine();
        difficulty = 2;
        try {
            difficulty = Integer.parseInt(diffInput.trim());
        } catch (Exception ignored) {
        }
        if (difficulty < 1) difficulty = 1;
        if (difficulty > 3) difficulty = 3;

        ui.println("\nChoose Game Mode:");
        ui.println("1. Survival Mode (3 Lives - play until 3 wrong answers)");
        ui.println("2. 3-Minute Blitz (180s - unlimited lives, wrong answers deduct score)");
        ui.print("Select mode [1-2]: ");
        boolean survivalMode = !"2".equals(ui.readLine().trim());

        ui.clearScreen();
        ui.printHeader("GAME SESSION - SELECT MINI-GAME");
        ui.println("1. Number Guesser");
        ui.println("2. Binary Decoder");
        ui.println("3. Logic Gate");
        ui.println("4. Word Scramble");
        ui.println("5. Return to Main Menu");
        ui.print("Select game [1-5]: ");
        String input = ui.readLine();

        AbstractGame selectedGame = switch (input) {
            case "1" -> new NumberGuessGame(ui, difficulty);
            case "2" -> new BinaryDecoderGame(ui, difficulty);
            case "3" -> new LogicGateGame(ui, difficulty);
            case "4" -> new WordScrambleGame(ui, difficulty);
            default -> null;
        };

        if (selectedGame != null) {
            selectedGame.setSurvivalMode(survivalMode);
            playGame(player, selectedGame);
        } else if ("5".equals(input)) {
            ui.println(AnsiColors.info("Returning..."));
        } else {
            ui.println(AnsiColors.error("Invalid selection"));
        }
    }

    private void playGame(Player player, AbstractGame game) {
        ui.println(AnsiColors.info("Starting " + game.getName() + "..."));

        TimerThread localTimer = null;
        if (!game.isSurvivalMode()) {
            localTimer = new TimerThread(game.getTimeLimitSeconds());
            localTimer.start();
            game.setTimer(localTimer);
        }

        try {
            Score raw = game.play();

            if (localTimer != null) {
                localTimer.stopTimer();
                localTimer.interrupt();
            }

            Score finalScore = new Score(player.getCodename(), raw.getPoints(),
                    raw.getAccuracy(), raw.getTimeRemaining(), raw.getGameType());

            player.registerGameResult(finalScore.getPoints(), finalScore.getAccuracy());
            playerRepository.refreshRanking(player);
            scoreRepository.save(finalScore);

            ui.println(AnsiColors.success("Session recorded! Total Points: " + finalScore.getPoints()));
            ui.print(AnsiColors.DIM + "Press Enter to return to Hacker Panel..." + AnsiColors.RESET);
            ui.readLine();

        } catch (Exception e) {
            ui.println(AnsiColors.error("Game error: " + e.getMessage()));
            if (localTimer != null) {
                localTimer.stopTimer();
            }
            ui.print(AnsiColors.DIM + "Press Enter to return to Hacker Panel..." + AnsiColors.RESET);
            ui.readLine();
        }
    }
}