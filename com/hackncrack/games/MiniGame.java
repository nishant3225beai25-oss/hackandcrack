package com.hackncrack.games;

import com.hackncrack.concurrency.TimerThread;
import com.hackncrack.exceptions.HackException;
import com.hackncrack.models.Score;

public interface MiniGame {
    String getName();
    String getDescription();
    int getDifficulty();
    int getBasePoints();
    Score play() throws HackException;
    TimerThread getTimer();
    void setTimer(TimerThread timer);
    void reset();
    boolean isCompleted();
}