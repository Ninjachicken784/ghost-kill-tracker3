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
    public int getSessionSorrow()       { return sessionSorrow; }
    public int getSessionVolta()        { return sessionVolta; }
    public int getSessionPlasma()       { return sessionPlasma; }
    public int getSessionBoots()        { return sessionBoots; }
    public int getSessionMillionCoins() { return sessionMillionCoins; }

    private long getTotalActiveMs() {
        if (!running || totalStartTime < 0) return 0;
        long now = System.currentTimeMillis();
        long e = now - totalStartTime - totalPausedElapsed;
        if (paused && pauseStartTime != -1) e -= (now - pauseStartTime);
        return Math.max(e, 0);
    }

    private long getSessionActiveMs() {
        if (!running || sessionStartTime < 0) return 0;
        long now = System.currentTimeMillis();
        long e = now - sessionStartTime - sessionPausedElapsed;
        if (paused && pauseStartTime != -1) e -= (now - pauseStartTime);
        return Math.max(e, 0);
    }

    public double getTotalKillsPerHour()   { long ms = getTotalActiveMs();   return ms < 1000 ? 0 : totalKills   / (ms / 3600000.0); }
    public double getSessionKillsPerHour() { long ms = getSessionActiveMs(); return ms < 1000 ? 0 : sessionKills / (ms / 3600000.0); }

    public double perHour(int count, boolean session) {
        long ms = session ? getSessionActiveMs() : getTotalActiveMs();
        return ms < 1000 ? 0 : count / (ms / 3600000.0);
    }

    public String getTotalUptime()   { return formatTime(getTotalActiveMs()); }
    public String getSessionUptime() { return formatTime(getSessionActiveMs()); }

    private String formatTime(long ms) {
        long s = ms / 1000, h = s / 3600, m = (s % 3600) / 60, sec = s % 60;
        if (h > 0) return h + "h " + String.format("%02d", m) + "m";
        return m + "m " + String.format("%02d", sec) + "s";
    }

    private static final double EXP_SORROW = 1.0 / 270;
    private static final double EXP_VOLTA  = 1.0 / 100;
    private static final double EXP_PLASMA = 1.0 / 2000;
    private static final double EXP_BOOTS  = 1.0 / 10000;
    private static final double EXP_1M     = 1.0 / 150;

    public double getPct(int actual, int kills, double expected) {
        if (kills == 0) return 0;
        return ((actual - kills * expected) / (kills * expected)) * 100.0;
    }

    public double getSorrowPct()  { return getPct(totalSorrow, totalKills, EXP_SORROW); }
    public double getVoltaPct()   { return getPct(totalVolta,  totalKills, EXP_VOLTA); }
    public double getPlasmaPct()  { return getPct(totalPlasma, totalKills, EXP_PLASMA); }
    public double getBootsPct()   { return getPct(totalBoots,  totalKills, EXP_BOOTS); }
    public double getMillionPct() { return getPct(totalMillionCoins, totalKills, EXP_1M); }
}
