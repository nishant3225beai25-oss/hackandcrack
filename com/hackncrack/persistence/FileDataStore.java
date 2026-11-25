package com.hackncrack.persistence;

import com.hackncrack.exceptions.SaveFileCorruptedException;
import com.hackncrack.models.Player;
import com.hackncrack.models.Score;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileDataStore {
    private final Path playersFile;
    private final Path scoresFile;
    private final Path saveFile;

    public FileDataStore() {
        this(Paths.get("data/players.txt"), Paths.get("data/scores.txt"),
                Paths.get("data/savegame.dat"));
    }

    public FileDataStore(Path playersFile, Path scoresFile, Path saveFile) {
        this.playersFile = playersFile;
        this.scoresFile = scoresFile;
        this.saveFile = saveFile;
        ensureDirectories();
    }

    private void ensureDirectories() {
        Path parent = playersFile.getParent();
        if (parent != null && !Files.exists(parent)) {
            try { Files.createDirectories(parent); } catch (IOException e) { /* ignore */ }
        }
    }

    public void savePlayers(List<Player> players) throws SaveFileCorruptedException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(playersFile.toFile()))) {
            for (Player p : players) {
                bw.write(p.toRecord());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new SaveFileCorruptedException("Failed to save players: " + e.getMessage());
        }
    }

    public List<Player> loadPlayers() throws SaveFileCorruptedException {
        List<Player> players = new ArrayList<>();
        if (!Files.exists(playersFile)) { return players; }
        try (BufferedReader br = new BufferedReader(new FileReader(playersFile.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) { continue; }
                try {
                    players.add(Player.fromRecord(line));
                } catch (IllegalArgumentException e) {
                    throw new SaveFileCorruptedException("Corrupted player line: " + line);
                }
            }
        } catch (IOException e) {
            throw new SaveFileCorruptedException("Failed to load players: " + e.getMessage());
        }
        return players;
    }

    public void saveScores(List<Score> scores) throws SaveFileCorruptedException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(scoresFile.toFile()))) {
            for (Score s : scores) {
                bw.write(s.toRecord());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new SaveFileCorruptedException("Failed to save scores: " + e.getMessage());
        }
    }

    public List<Score> loadScores() throws SaveFileCorruptedException {
        List<Score> scores = new ArrayList<>();
        if (!Files.exists(scoresFile)) { return scores; }
        try (BufferedReader br = new BufferedReader(new FileReader(scoresFile.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) { continue; }
                try {
                    scores.add(Score.fromRecord(line));
                } catch (IllegalArgumentException e) {
                    throw new SaveFileCorruptedException("Corrupted score line: " + line);
                }
            }
        } catch (IOException e) {
            throw new SaveFileCorruptedException("Failed to load scores: " + e.getMessage());
        }
        return scores;
    }

    public void saveGame(String data) throws SaveFileCorruptedException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(saveFile.toFile()))) {
            bw.write(data);
        } catch (IOException e) {
            throw new SaveFileCorruptedException("Failed to save game: " + e.getMessage());
        }
    }

    public String loadGame() throws SaveFileCorruptedException {
        if (!Files.exists(saveFile)) { return null; }
        try (BufferedReader br = new BufferedReader(new FileReader(saveFile.toFile()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            throw new SaveFileCorruptedException("Failed to load game: " + e.getMessage());
        }
    }

    public boolean hasSaveFile() { return Files.exists(saveFile); }
    public Path getSaveFilePath() { return saveFile; }
}