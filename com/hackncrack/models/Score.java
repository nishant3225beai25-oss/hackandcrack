package com.hackncrack.models;

import java.time.LocalDateTime;

public class Score {
    private final String codename;
    private final int points;
    private final int accuracy;
    private final int timeRemaining;
    private final String gameType;
    private final LocalDateTime timestamp;

    public Score(String codename, int points, int accuracy, int timeRemaining, String gameType) {
        this.codename = codename;
        this.points = points;
        this.accuracy = accuracy;
        this.timeRemaining = timeRemaining;
        this.gameType = gameType;
        this.timestamp = LocalDateTime.now();
    }

    public String getCodename() { return codename; }
    public int getPoints() { return points; }
    public int getAccuracy() { return accuracy; }
    public int getTimeRemaining() { return timeRemaining; }
    public String getGameType() { return gameType; }
    public LocalDateTime getTimestamp() { return timestamp; }

    /** Pipe-delimited record for FileDataStore. */
    public String toRecord() {
        return String.join("|", codename, String.valueOf(points), String.valueOf(accuracy),
                String.valueOf(timeRemaining), gameType);
    }

    public static Score fromRecord(String record) {
        String[] parts = record.split("\\|", -1);
        if (parts.length < 5) {
            throw new IllegalArgumentException("Corrupted score record: " + record);
        }
        return new Score(parts[0], Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), parts[4]);
    }

    @Override
    public String toString() {
        return "Score{codename='" + codename + "', points=" + points
                + ", accuracy=" + accuracy + "%, timeLeft=" + timeRemaining + "s"
                + ", game='" + gameType + "', at=" + timestamp + "}";
    }
}