package com.ghostkilltracker.mixin;

import com.ghostkilltracker.client.DropNotification;
import com.ghostkilltracker.client.GhostKillTrackerClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.regex.Pattern;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    private static final Pattern SORROW  = Pattern.compile("RARE DROP!.*?Sorrow",         Pattern.CASE_INSENSITIVE);
    private static final Pattern VOLTA   = Pattern.compile("RARE DROP!.*?Volta",           Pattern.CASE_INSENSITIVE);
    private static final Pattern PLASMA  = Pattern.compile("RARE DROP!.*?Plasma",          Pattern.CASE_INSENSITIVE);
    private static final Pattern BOOTS   = Pattern.compile("RARE DROP!.*?Ghostly Boots",   Pattern.CASE_INSENSITIVE);
    private static final Pattern MILLION = Pattern.compile("materialized 1,000,000 coins", Pattern.CASE_INSENSITIVE);

    private long lastSorrowTime  = 0;
    private long lastVoltaTime   = 0;
    private long lastPlasmaTime  = 0;
    private long lastBootsTime   = 0;
    private long lastMillionTime = 0;

    @Inject(method = "onGameMessage", at = @At("HEAD"))
    private void onChat(GameMessageS2CPacket packet, CallbackInfo ci) {
        Text msg = packet.content();
        if (msg == null) return;
        String raw = msg.getString();
        long now = System.currentTimeMillis();

        if (SORROW.matcher(raw).find() && now - lastSorrowTime > 1000) {
            lastSorrowTime = now;
            GhostKillTrackerClient.SESSION.addSorrow();
            if (GhostKillTrackerClient.dropsEnabled) DropNotification.show(msg);
            return;
        }
        if (VOLTA.matcher(raw).find() && now - lastVoltaTime > 1000) {
            lastVoltaTime = now;
            GhostKillTrackerClient.SESSION.addVolta();
            if (GhostKillTrackerClient.dropsEnabled) DropNotification.show(msg);
            return;
        }
        if (PLASMA.matcher(raw).find() && now - lastPlasmaTime > 1000) {
            lastPlasmaTime = now;
            GhostKillTrackerClient.SESSION.addPlasma();
            if (GhostKillTrackerClient.dropsEnabled) DropNotification.show(msg);
            return;
        }
        if (BOOTS.matcher(raw).find() && now - lastBootsTime > 1000) {
            lastBootsTime = now;
            GhostKillTrackerClient.SESSION.addBoots();
            if (GhostKillTrackerClient.dropsEnabled) DropNotification.show(msg);
            return;
        }
        if (MILLION.matcher(raw).find() && now - lastMillionTime > 1000) {
            lastMillionTime = now;
            GhostKillTrackerClient.SESSION.addMillionCoins();
            if (GhostKillTrackerClient.dropsEnabled) DropNotification.show(msg);
        }
    }
}
