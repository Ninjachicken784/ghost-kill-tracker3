package com.ghostkilltracker.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GhostKillTrackerClient implements ClientModInitializer {
    public static final String MOD_ID = "ghostkilltracker";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final KillSession SESSION = new KillSession();
    public static boolean hudVisible = true;
    public static boolean dropsEnabled = true;
    public static int hudX = -1;
    public static int hudY = 5;

    private int lastGauntletKills = -1;
    private int tickCounter = 0;
    private static final Pattern KILLS_PATTERN = Pattern.compile("Kills:\\s*([\\d,]+)");

    @Override
    public void onInitializeClient() {
        LOGGER.info("Ghost Kill Tracker initialized!");

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {

            dispatcher.register(ClientCommandManager.literal("ghosttracker")
                .then(ClientCommandManager.literal("start")
                    .executes(ctx -> {
                        SESSION.start();
                        lastGauntletKills = -1;
                        ctx.getSource().sendFeedback(Text.literal("§aTracker STARTED!"));
                        return 1;
                    })
                )
                .then(ClientCommandManager.literal("pause")
                    .executes(ctx -> {
                        SESSION.pause();
                        ctx.getSource().sendFeedback(Text.literal("§eTracker PAUSED!"));
                        return 1;
                    })
                )
                .then(ClientCommandManager.literal("stop")
                    .executes(ctx -> {
                        SESSION.pause();
                        ctx.getSource().sendFeedback(Text.literal("§cTracker STOPPED!"));
                        return 1;
                    })
                )
                .then(ClientCommandManager.literal("reset")
                    .executes(ctx -> {
                        SESSION.resetSession();
                        lastGauntletKills = -1;
                        ctx.getSource().sendFeedback(Text.literal("§cSession RESET!"));
                        return 1;
                    })
                )
                .then(ClientCommandManager.literal("toggle")
                    .executes(ctx -> {
                        hudVisible = !hudVisible;
                        ctx.getSource().sendFeedback(Text.literal("Ghost Tracker HUD: " + (hudVisible ? "§aON" : "§cOFF")));
                        return 1;
                    })
                )
            );

            dispatcher.register(ClientCommandManager.literal("editGhostTracker")
                .executes(ctx -> {
                    MinecraftClient.getInstance().send(() ->
                        MinecraftClient.getInstance().setScreen(new GhostTrackerEditScreen()));
                    return 1;
                })
            );

            dispatcher.register(ClientCommandManager.literal("ghostdrops")
                .then(ClientCommandManager.literal("enable")
                    .executes(ctx -> {
                        dropsEnabled = true;
                        ctx.getSource().sendFeedback(Text.literal("§aDrop notifications ENABLED!"));
                        return 1;
                    })
                )
                .then(ClientCommandManager.literal("disable")
                    .executes(ctx -> {
                        dropsEnabled = false;
                        ctx.getSource().sendFeedback(Text.literal("§cDrop notifications DISABLED!"));
                        return 1;
                    })
                )
            );

            dispatcher.register(ClientCommandManager.literal("ghostTrackerCommands")
                .executes(ctx -> {
                    ctx.getSource().sendFeedback(Text.literal("§e--- Ghost Tracker Commands ---"));
                    ctx.getSource().sendFeedback(Text.literal("§a/ghosttracker start §7- Start tracker"));
                    ctx.getSource().sendFeedback(Text.literal("§a/ghosttracker pause §7- Pause tracker"));
                    ctx.getSource().sendFeedback(Text.literal("§a/ghosttracker stop §7- Stop tracker"));
                    ctx.getSource().sendFeedback(Text.literal("§a/ghosttracker reset §7- Reset session"));
                    ctx.getSource().sendFeedback(Text.literal("§a/ghosttracker toggle §7- Toggle HUD on/off"));
                    ctx.getSource().sendFeedback(Text.literal("§a/editGhostTracker §7- Drag HUD to reposition"));
                    ctx.getSource().sendFeedback(Text.literal("§a/ghostdrops enable §7- Enable drop popups"));
                    ctx.getSource().sendFeedback(Text.literal("§a/ghostdrops disable §7- Disable drop popups"));
                    ctx.getSource().sendFeedback(Text.literal("§a/ghostTrackerCommands §7- Show this list"));
                    return 1;
                })
            );
        });

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (hudX == -1) hudX = client.getWindow().getScaledWidth() - 215;
            if (hudVisible) GhostKillHud.render(drawContext, client);
            DropNotification.render(drawContext, client);
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.currentScreen != null) return;

            tickCounter++;
            if (tickCounter >= 20) {
                tickCounter = 0;
                ItemStack held = client.player.getMainHandStack();
                if (!held.isEmpty()) {
                    var lore = held.get(DataComponentTypes.LORE);
                    if (lore != null) {
                        for (Text line : lore.lines()) {
                            Matcher m = KILLS_PATTERN.matcher(line.getString());
                            if (m.find()) {
                                try {
                                    int kills = Integer.parseInt(m.group(1).replace(",", ""));
                                    if (lastGauntletKills >= 0 && kills > lastGauntletKills) {
                                        int diff = kills - lastGauntletKills;
                                        if (diff <= 20) {
                                            for (int i = 0; i < diff; i++) SESSION.addKill();
                                        }
                                    }
                                    lastGauntletKills = kills;
                                } catch (NumberFormatException ignored) {}
                                break;
                            }
                        }
                    }
                }
            }
        });
    }
}
