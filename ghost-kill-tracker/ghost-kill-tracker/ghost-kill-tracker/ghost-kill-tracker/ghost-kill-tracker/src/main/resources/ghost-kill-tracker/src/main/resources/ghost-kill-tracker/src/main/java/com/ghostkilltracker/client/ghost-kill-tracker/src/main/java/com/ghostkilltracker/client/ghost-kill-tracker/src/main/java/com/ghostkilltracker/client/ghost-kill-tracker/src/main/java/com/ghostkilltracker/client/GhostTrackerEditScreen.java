package com.ghostkilltracker.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class GhostTrackerEditScreen extends Screen {
    private boolean dragging = false;
    private int dragOffsetX, dragOffsetY;
    private boolean lastMouseDown = false;

    public GhostTrackerEditScreen() {
        super(Text.literal("Edit Ghost Tracker"));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);

        boolean mouseDown = GLFW.glfwGetMouseButton(
            client.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS;

        int hx = GhostKillTrackerClient.hudX;
        int hy = GhostKillTrackerClient.hudY;

        if (mouseDown && !lastMouseDown) {
            if (mouseX >= hx && mouseX <= hx + 210 && mouseY >= hy && mouseY <= hy + 220) {
                dragging = true;
                dragOffsetX = mouseX - hx;
                dragOffsetY = mouseY - hy;
            }
        }

        if (!mouseDown) dragging = false;

        if (dragging && mouseDown) {
            GhostKillTrackerClient.hudX = mouseX - dragOffsetX;
            GhostKillTrackerClient.hudY = mouseY - dragOffsetY;
        }

        lastMouseDown = mouseDown;

        GhostKillHud.render(ctx, client);

        ctx.fill(GhostKillTrackerClient.hudX - 1, GhostKillTrackerClient.hudY - 1,
                 GhostKillTrackerClient.hudX + 211, GhostKillTrackerClient.hudY + 1, 0xFFFFFF00);

        ctx.drawCenteredTextWithShadow(textRenderer,
            Text.literal("§eDrag the tracker. Press ESC to save."),
            width / 2, height - 20, 0xFFFFFF);
    }

    @Override
    public boolean shouldPause() { return false; }
}
