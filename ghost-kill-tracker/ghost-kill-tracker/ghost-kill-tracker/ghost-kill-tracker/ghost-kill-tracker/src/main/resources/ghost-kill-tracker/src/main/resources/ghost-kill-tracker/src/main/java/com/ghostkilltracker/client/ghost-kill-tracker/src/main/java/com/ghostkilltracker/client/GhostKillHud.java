package com.ghostkilltracker.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import java.text.DecimalFormat;

public class GhostKillHud {
    private static final int BG     = 0xBB000000;
    private static final int CYAN   = 0xFF55FFFF;
    private static final int GREEN  = 0xFF55FF55;
    private static final int BLUE   = 0xFF5555FF;
    private static final int PINK   = 0xFFFF55FF;
    private static final int YELLOW = 0xFFFFFF55;
    private static final int GOLD   = 0xFFFFAA00;
    private static final int WHITE  = 0xFFFFFFFF;
    private static final int GRAY   = 0xFFAAAAAA;
    private static final int RED    = 0xFFFF5555;

    private static final DecimalFormat DF0 = new DecimalFormat("#,##0");
    private static final DecimalFormat DF1 = new DecimalFormat("#,##0.0");
    private static final DecimalFormat PCT = new DecimalFormat("+#,##0.00;-#,##0.00");

    private static final int W   = 210;
    private static final int PAD = 5;
    private static final int LH  = 10;

    public static void render(DrawContext ctx, MinecraftClient client) {
        if (client.player == null) return;
        KillSession s = GhostKillTrackerClient.SESSION;
        int x = GhostKillTrackerClient.hudX;
        int y = GhostKillTrackerClient.hudY;

        int totalKills  = s.getTotalKills();
        int totalSorrow = s.getTotalSorrow();
        int totalVolta  = s.getTotalVolta();
        int totalPlasma = s.getTotalPlasma();
        int totalBoots  = s.getTotalBoots();
        int totalMil    = s.getTotalMillionCoins();

        int h = PAD * 2 + 19 * LH;
        ctx.fill(x, y, x + W, y + h, BG);

        int tx = x + PAD;
        int ty = y + PAD;

        // Status
        String status = !s.isRunning() ? "§7STOPPED" : s.isPaused() ? "§cPAUSED" : "§aRUNNING";
        ctx.drawTextWithShadow(client.textRenderer, Text.literal("§fGhost Tracker [" + status + "§f]"), tx, ty, WHITE);
        ty += LH + 2;

        // Kills
        ctx.drawTextWithShadow(client.textRenderer, Text.literal("§6Kills: §c" + DF0.format(totalKills)), tx, ty, WHITE);
        ty += LH;

        // Drops with %
        drawDrop(ctx, client, tx, ty, "Sorrows: ", totalSorrow, s.getSorrowPct(), CYAN);   ty += LH;
        drawDrop(ctx, client, tx, ty, "Voltas: ",  totalVolta,  s.getVoltaPct(),  GREEN);  ty += LH;
        drawDrop(ctx, client, tx, ty, "Plasmas: ", totalPlasma, s.getPlasmaPct(), BLUE);   ty += LH;
        drawDrop(ctx, client, tx, ty, "Ghostly boots: ", totalBoots, s.getBootsPct(), PINK); ty += LH;
        drawDrop(ctx, client, tx, ty, "1m coins: ", totalMil, s.getMillionPct(), YELLOW);  ty += LH;

        ty += 3;

        // Per hour
        double kph = s.getTotalKillsPerHour();
        ctx.drawTextWithShadow(client.textRenderer, Text.literal("§6Kills per hour: §e" + (kph == 0 ? "-" : DF1.format(kph))), tx, ty, WHITE); ty += LH;
        drawPh(ctx, client, tx, ty, "Sorrows per hour: ",       s.perHour(totalSorrow, false), CYAN);   ty += LH;
        drawPh(ctx, client, tx, ty, "Voltas per hour: ",        s.perHour(totalVolta,  false), GREEN);  ty += LH;
        drawPh(ctx, client, tx, ty, "Plasmas per hour: ",       s.perHour(totalPlasma, false), BLUE);   ty += LH;
        drawPh(ctx, client, tx, ty, "Ghostly boots per hour: ", s.perHour(totalBoots,  false), PINK);   ty += LH;
        drawPh(ctx, client, tx, ty, "1m coins per hour: ",      s.perHour(totalMil,    false), YELLOW); ty += LH;

        ty += 3;

        ctx.drawTextWithShadow(client.textRenderer, Text.literal("§6Time: §e" + s.getTotalUptime()), tx, ty, WHITE); ty += LH;
        ctx.drawTextWithShadow(client.textRenderer, Text.literal("§7Session: §e" + s.getSessionUptime()), tx, ty, GRAY); ty += LH;
        ctx.drawTextWithShadow(client.textRenderer, Text.literal("§7/ghostTrackerCommands for help"), tx, ty, GRAY);
    }

    private static void drawDrop(DrawContext ctx, MinecraftClient client,
                                  int x, int y, String label, int count, double pct, int color) {
        String main = label + DF0.format(count);
        ctx.drawTextWithShadow(client.textRenderer, Text.literal(main), x, y, color);
        if (count > 0) {
            int mainW = client.textRenderer.getWidth(main);
            int pctColor = pct >= 0 ? 0xFF55FF55 : 0xFFFF5555;
            ctx.drawTextWithShadow(client.textRenderer,
                Text.literal(" (" + PCT.format(pct) + "%)"), x + mainW, y, pctColor);
        }
    }

    private static void drawPh(DrawContext ctx, MinecraftClient client,
                                int x, int y, String label, double val, int color) {
        ctx.drawTextWithShadow(client.textRenderer,
            Text.literal(label + (val == 0 ? "-" : DF1.format(val))), x, y, color);
    }
}
