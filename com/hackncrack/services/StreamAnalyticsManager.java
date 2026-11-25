package com.hackncrack.services;

import com.hackncrack.models.Score;
import java.util.List;

public class StreamAnalyticsManager {
    private static final String PHASE_TWO = "Phase 2 feature: Lambda/Streams analytics";

    public double averageScore(List<Score> scores) {
        throw new UnsupportedOperationException(PHASE_TWO);
    }

    public List<Score> topPerformers(List<Score> scores, int limit) {
        throw new UnsupportedOperationException(PHASE_TWO);
    }

    public long countByGameType(List<Score> scores, String gameType) {
        throw new UnsupportedOperationException(PHASE_TWO);
    }

    public int maxStreak(List<Score> scores) {
        throw new UnsupportedOperationException(PHASE_TWO);
    }

    public String generateReport(List<Score> scores) {
        throw new UnsupportedOperationException(PHASE_TWO);
    }
}