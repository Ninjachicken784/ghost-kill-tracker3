package com.ghostkilltracker.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class DropNotification {
    private static Text message = null;
    private static long showUntil = 0;

    public static void show(Text msg) {
        message = msg;
        showUntil = System.currentTimeMillis() + 2000;
    }

    public static void render(DrawContext ctx, MinecraftClient client) {
        if (message == null || System.currentTimeMillis() > showUntil) return;
        int screenW = client.getWindow().getScaledWidth();
        int screenH = client.getWindow().getScaledHeight();
        int textW = client.textRenderer.getWidth(message);
        ctx.drawTextWithShadow(client.textRenderer, message,
            (screenW - textW) / 2, screenH / 3, 0xFFFFFFFF);
    }
}
