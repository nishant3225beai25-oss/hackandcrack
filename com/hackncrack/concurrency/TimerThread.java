package com.hackncrack.concurrency;

public class TimerThread extends Thread {
    private volatile boolean running;
    private int secondsRemaining;
    private volatile boolean timeExpired;

    public TimerThread(int seconds) {
        this.secondsRemaining = seconds;
        this.running = false;
        this.timeExpired = false;
    }

    @Override
    public void run() {
        running = true;
        try {
            while (running && secondsRemaining > 0) {
                Thread.sleep(1000);
                secondsRemaining--;
            }
            if (running && secondsRemaining == 0) {
                timeExpired = true;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Keep timeExpired true if it already fired; only clear if we were interrupted
            // before the countdown reached zero (i.e., user stopped the timer early).
            if (secondsRemaining > 0) {
                timeExpired = false;
            }
        }
    }

    public void stopTimer() {
        running = false;
        interrupt();
    }

    public int getSecondsRemaining() { return secondsRemaining; }
    public boolean isRunning() { return running; }
    public boolean isTimeExpired() { return timeExpired; }
    public void setSecondsRemaining(int seconds) { this.secondsRemaining = seconds; }
}