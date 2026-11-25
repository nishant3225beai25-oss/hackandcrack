package com.hackncrack.models;

import java.util.Objects;

public class Player implements Comparable<Player> {
    private final String codename;
    private String username;
    private int level;
    private int xp;
    private int totalScore;
    private int bestScore;
    private int gamesPlayed;
    private int missionsCompleted;

    public Player(String codename, String username) {
        this.codename = codename;
        this.username = username;
        this.level = 1;
    }

    public String getCodename() { return codename; }
    public String getUsername() { return username; }
    public int getLevel() { return level; }
    public int getXp() { return xp; }
    public int getTotalScore() { return totalScore; }
    public int getBestScore() { return bestScore; }
    public int getGamesPlayed() { return gamesPlayed; }
    public int getMissionsCompleted() { return missionsCompleted; }

    public void setUsername(String username) { this.username = username; }

    public void addXp(int amount) { this.xp += amount; recomputeLevel(); }

    public void registerGameResult(int points, int accuracy) {
        this.totalScore += points;
        if (points > this.bestScore) { this.bestScore = points; }
        this.gamesPlayed++;
        this.addXp(Math.max(5, points / 2));
    }

    private void recomputeLevel() {
        int newLevel = 1 + this.xp / 500;
        if (newLevel > 99) { newLevel = 99; }
        this.level = newLevel;
    }

    @Override
    public int compareTo(Player other) {
        int byScore = Integer.compare(other.totalScore, this.totalScore);
        if (byScore != 0) { return byScore; }
        return this.codename.compareTo(other.codename);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Player player = (Player) o;
        return Objects.equals(codename, player.codename);
    }

    @Override
    public int hashCode() { return Objects.hash(codename); }

    /** Pipe-delimited record used by FileDataStore. */
    public String toRecord() {
        return String.join("|", codename, username, String.valueOf(level), String.valueOf(xp),
                String.valueOf(totalScore), String.valueOf(bestScore),
                String.valueOf(gamesPlayed), String.valueOf(missionsCompleted));
    }

    public static Player fromRecord(String record) {
        String[] parts = record.split("\\|", -1);
        if (parts.length < 8) {
            throw new IllegalArgumentException("Corrupted player record: " + record);
        }
        Player p = new Player(parts[0], parts[1]);
        p.level = Integer.parseInt(parts[2]);
        p.xp = Integer.parseInt(parts[3]);
        p.totalScore = Integer.parseInt(parts[4]);
        p.bestScore = Integer.parseInt(parts[5]);
        p.gamesPlayed = Integer.parseInt(parts[6]);
        p.missionsCompleted = Integer.parseInt(parts[7]);
        return p;
    }

    @Override
    public String toString() {
        return "Player{codename='" + codename + "', username='" + username
                + "', level=" + level + ", xp=" + xp + ", totalScore=" + totalScore
                + ", bestScore=" + bestScore + "}";
    }
}