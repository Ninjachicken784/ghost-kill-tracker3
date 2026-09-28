package com.ghostkilltracker.client;

public class KillSession {
    private int totalKills = 0;
    private int totalSorrow = 0;
    private int totalVolta = 0;
    private int totalPlasma = 0;
    private int totalBoots = 0;
    private int totalMillionCoins = 0;
    private int sessionKills = 0;
    private int sessionSorrow = 0;
    private int sessionVolta = 0;
    private int sessionPlasma = 0;
    private int sessionBoots = 0;
    private int sessionMillionCoins = 0;
    private long totalStartTime = -1;
    private long totalPausedElapsed = 0;
    private long sessionStartTime = -1;
    private long sessionPausedElapsed = 0;
    private long pauseStartTime = -1;
    private boolean paused = false;
    private boolean running = false;

    public void start() {
        if (!running) {
            running = true; paused = false;
            long now = System.currentTimeMillis();
            totalStartTime = now; sessionStartTime = now;
            totalPausedElapsed = 0; sessionPausedElapsed = 0;
            pauseStartTime = -1;
        } else if (paused) {
            paused = false;
            if (pauseStartTime != -1) {
                long d = System.currentTimeMillis() - pauseStartTime;
                totalPausedElapsed += d; sessionPausedElapsed += d;
                pauseStartTime = -1;
            }
        }
    }

    public void pause() {
        if (running && !paused) { paused = true; pauseStartTime = System.currentTimeMillis(); }
    }

    public void resetSession() {
        sessionKills = 0; sessionSorrow = 0; sessionVolta = 0;
        sessionPlasma = 0; sessionBoots = 0; sessionMillionCoins = 0;
        sessionStartTime = System.currentTimeMillis();
        sessionPausedElapsed = 0;
        if (paused && pauseStartTime != -1) pauseStartTime = System.currentTimeMillis();
    }

    public void addKill()         { if (!running || paused) return; totalKills++; sessionKills++; }
    public void addSorrow()       { if (!running || paused) return; totalSorrow++; sessionSorrow++; }
    public void addVolta()        { if (!running || paused) return; totalVolta++; sessionVolta++; }
    public void addPlasma()       { if (!running || paused) return; totalPlasma++; sessionPlasma++; }
    public void addBoots()        { if (!running || paused) return; totalBoots++; sessionBoots++; }
    public void addMillionCoins() { if (!running || paused) return; totalMillionCoins++; sessionMillionCoins++; }

    public boolean isRunning() { return running; }
    public boolean isPaused()  { return paused; }

    public int getTotalKills()        { return totalKills; }
    public int getTotalSorrow()       { return totalSorrow; }
    public int getTotalVolta()        { return totalVolta; }
    public int getTotalPlasma()       { return totalPlasma; }
    public int getTotalBoots()        { return totalBoots; }
    public int getTotalMillionCoins() { return totalMillionCoins; }
    public int getSessionKills()        { return sessionKills; }
