package com.hackncrack.concurrency;

public class TraceTrackerThread extends Thread {
    private final SecurityMonitor monitor;
    private volatile boolean running;
    private int traceLevel;

    public TraceTrackerThread(SecurityMonitor monitor) {
        this.monitor = monitor;
        this.running = true;
        this.traceLevel = 0;
    }

    @Override
    public void run() {
        try {
            while (running) {
                Thread.sleep(500);
                this.traceLevel = monitor.incrementTraceLevel();
                if (this.traceLevel >= 100) {
                    monitor.setCritical(true);
                    System.out.println("CRITICAL: Trace level exceeded 100");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void stopTracking() { running = false; interrupt(); }
}