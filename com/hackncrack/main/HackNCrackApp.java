 package com.hackncrack.main;

import com.hackncrack.exceptions.InvalidHackInputException;
import com.hackncrack.exceptions.SaveFileCorruptedException;
import com.hackncrack.models.Player;
import com.hackncrack.models.Score;
import com.hackncrack.persistence.FileDataStore;
import com.hackncrack.persistence.PlayerRepository;
import com.hackncrack.persistence.ScoreRepository;
import com.hackncrack.services.PlayerService;
import com.hackncrack.ui.AnsiColors;
import com.hackncrack.ui.ConsoleUI;
import com.hackncrack.ui.InputValidator;
import java.util.List;

public class HackNCrackApp {
    private final ConsoleUI ui;
    private final FileDataStore dataStore;
    private final PlayerRepository playerRepository;
    private final ScoreRepository scoreRepository;
    private final PlayerService playerService;
    private GameEngine gameEngine;

    public HackNCrackApp(ConsoleUI ui) {
        this.ui = ui;
        this.dataStore = new FileDataStore();
        this.playerRepository = new PlayerRepository();
        this.scoreRepository = new ScoreRepository();
        this.playerService = new PlayerService(playerRepository);
    }

    public void start() {
        showSplash();
        initApp();
        authLoop();
    }

    private void showSplash() {
        ui.clearScreen();
        ui.printBanner();
        ui.typewriter(AnsiColors.info("Booting secure shell..."), 15);
        ui.typewriter(AnsiColors.info("Loading core modules..."), 10);
        ui.pause(600);
    }

    private void initApp() {
        ui.printDivider();
        loadPersistedData();
        gameEngine = new GameEngine(ui, scoreRepository, playerRepository);
        ui.println(AnsiColors.success("All systems nominal. GameEngine ready."));
    }

    private void loadPersistedData() {
        try {
            List<Player> players = dataStore.loadPlayers();
            players.forEach(playerRepository::save);
            ui.println(AnsiColors.info("Loaded " + players.size() + " operative profile(s) from disk."));
        } catch (SaveFileCorruptedException e) {
            ui.println(AnsiColors.warning("Player database skipped: " + e.getMessage()));
        }
        try {
            List<Score> scores = dataStore.loadScores();
            scores.forEach(scoreRepository::save);
            ui.println(AnsiColors.info("Loaded " + scores.size() + " score record(s) from disk."));
        } catch (SaveFileCorruptedException e) {
            ui.println(AnsiColors.warning("Score database skipped: " + e.getMessage()));
        }
    }

    private void authLoop() {
        while (true) {
            ui.clearScreen();
            ui.printHeader("AUTHENTICATION");
            ui.println("1. Register New Hacker");
            ui.println("2. Login");
            ui.println("3. View Leaderboard");
            ui.println("4. Exit");
            ui.print("Select option [1-4]: ");
            String input = ui.readLine();
            switch (input) {
                case "1" -> register();
                case "2" -> login();
                case "3" -> viewLeaderboard();
                case "4" -> { ui.println(AnsiColors.info("Exiting...")); saveAndExit(); return; }
                default -> ui.println(AnsiColors.error("Invalid option"));
            }
            ui.pause(1000);
        }
    }

    private void register() {
        ui.clearScreen();
        ui.printHeader("REGISTER");
        String codename = ui.readLine("Enter hacker alias (3-15 alnum/underscore): ");
        String username = ui.readLine("Enter username: ");
        try {
            codename = InputValidator.validateCodename(codename);
            username = InputValidator.validateUsername(username);
            Player p = new Player(codename, username);
            playerService.register(p);
            ui.println(AnsiColors.success("Hacker registered successfully!"));
        } catch (InvalidHackInputException e) {
            ui.println(AnsiColors.error(e.getMessage()));
        }
    }

    private void login() {
        ui.clearScreen();
        ui.printHeader("LOGIN");
        String codename = ui.readLine("Enter hacker alias (codename): ");
        try {
            codename = InputValidator.validateCodename(codename);
            var optPlayer = playerService.getPlayer(codename);
            if (optPlayer.isPresent()) {
                ui.println(AnsiColors.success("Welcome back, " + optPlayer.get().getUsername() + "!"));
                sessionLoop(optPlayer.get());
            } else {
                ui.println(AnsiColors.error("Hacker not found."));
            }
        } catch (InvalidHackInputException e) {
            ui.println(AnsiColors.error(e.getMessage()));
        }
    }

   private void sessionLoop(Player player) {
        boolean active = true;
        while (active) {
            ui.clearScreen();
            ui.printHeader("HACKER PANEL");
            ui.println(AnsiColors.success("Welcome, " + player.getUsername() + " (Level " + player.getLevel() + ")"));
            ui.println("XP: " + player.getXp() + " | Total Score: " + player.getTotalScore() + " | Best: " + player.getBestScore());
            ui.println("Games Played: " + player.getGamesPlayed());
            ui.printDivider();
            ui.println("1. Play Mini-Games");
            ui.println("2. Save Game");
            ui.println("3. Logout");
            ui.print("Choose: ");
            String input = ui.readLine();
            try {
                switch (input) {
                    case "1" -> {
                        gameEngine.runSession(player);
                        persistData(); // Auto-save right after each game
                    }
                    case "2" -> {
                        persistData();
                        ui.println(AnsiColors.info("Game saved."));
                        ui.pause(700);
                    }
                    case "3" -> {
                        persistData();
                        active = false;
                    }
                    default -> ui.println(AnsiColors.error("Invalid option."));
                }
            } catch (SaveFileCorruptedException e) {
                ui.println(AnsiColors.error("Save failed: " + e.getMessage()));
            }
        }
    }

    private void viewLeaderboard() {
        ui.clearScreen();
        ui.printHeader("LEADERBOARD");
        List<Player> leaderboard = playerService.getPlayersSortedByScore();
        if (leaderboard.isEmpty()) {
            ui.println(AnsiColors.info("No hackers registered yet."));
        } else {
            int rank = 1;
            for (Player p : leaderboard) {
                ui.println(rank + ". " + p.getCodename() + " - Score " + p.getTotalScore());
                rank++;
            }
            List<Score> topScores = scoreRepository.getTopScores(5);
            if (!topScores.isEmpty()) {
                ui.println("\nTop Scores:");
                for (int i = 0; i < topScores.size(); i++) {
                    Score s = topScores.get(i);
                    ui.println((i + 1) + ". " + s.getCodename() + ": " + s.getPoints() + " pts (" + s.getAccuracy() + "%)");
                }
            }
        }
        ui.println();
        ui.print(AnsiColors.DIM + "Press Enter to return to Main Menu..." + AnsiColors.RESET);
        ui.readLine();
    }

    private void saveAndExit() {
        try {
            ui.println(AnsiColors.info("Saving data..."));
            persistData();
            ui.println(AnsiColors.success("Data saved."));
        } catch (SaveFileCorruptedException e) {
            ui.println(AnsiColors.error("Save failed: " + e.getMessage()));
        }
    }

    private void persistData() throws SaveFileCorruptedException {
        dataStore.savePlayers(playerService.getAllPlayers());
        dataStore.saveScores(scoreRepository.findAll());
    }
}