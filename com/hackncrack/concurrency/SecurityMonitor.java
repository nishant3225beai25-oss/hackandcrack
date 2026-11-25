package com.hackncrack.concurrency;

public class SecurityMonitor {
    private int traceLevel;
    private boolean critical;
    private final Object lock = new Object();

    public SecurityMonitor() {
        this.traceLevel = 0;
        this.critical = false;
    }

    public int getTraceLevel() {
        synchronized (lock) {
            return traceLevel;
        }
    }

    public void setTraceLevel(int level) {
        synchronized (lock) {
            this.traceLevel = level;
            lock.notifyAll();
        }
    }

    public int incrementTraceLevel() {
        synchronized (lock) {
            this.traceLevel++;
            return this.traceLevel;
        }
    }

    public boolean isCritical() {
        synchronized (lock) {
            return critical;
        }
    }

    public void setCritical(boolean critical) {
        synchronized (lock) {
            this.critical = critical;
            lock.notifyAll();
        }
    }

    public void reset() {
        synchronized (lock) {
            this.traceLevel = 0;
            this.critical = false;
            lock.notifyAll();
        }
    }

    public Object getLock() { return lock; }
}