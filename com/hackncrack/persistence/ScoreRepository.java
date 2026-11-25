package com.hackncrack.persistence;

import com.hackncrack.models.Score;
import java.util.*;

public class ScoreRepository implements Repository<Score> {
    private final List<Score> scores = new ArrayList<>();

    @Override
    public void save(Score score) {
        scores.add(score);
    }

    @Override
    public Optional<Score> findById(String id) {
        return scores.stream().filter(s -> s.getCodename().equals(id)).findFirst();
    }

    @Override
    public List<Score> findAll() {
        return new ArrayList<>(scores);
    }

    @Override
    public boolean deleteById(String id) {
        return scores.removeIf(s -> s.getCodename().equals(id));
    }

    @Override
    public int count() {
        return scores.size();
    }

    public List<Score> getTopScores(int limit) {
        return scores.stream().sorted(Comparator.comparingInt(Score::getPoints).reversed())
                .limit(limit).toList();
    }

    public double getAverageScore() {
        return scores.stream().mapToInt(Score::getPoints).average().orElse(0.0);
    }
}