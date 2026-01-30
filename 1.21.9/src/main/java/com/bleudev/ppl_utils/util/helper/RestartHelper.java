package com.bleudev.ppl_utils.util.helper;

import com.bleudev.ppl_utils.DataStorageHelper;
import com.bleudev.ppl_utils.config.PplUtilsConfig;
import com.bleudev.ppl_utils.mixin.client.accessor.BossHealthOverlayAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static com.bleudev.ppl_utils.util.ServerUtils.isClientOnPepeland;

public class RestartHelper {
    private static final UUID rtUuid = UUID.randomUUID();

    private static long restartTime = 0;
    private static long startRestartTime = 0;

    public static void runRestartBar(long restartTime) {
        RestartHelper.restartTime = restartTime;
        startRestartTime = System.currentTimeMillis();
        DataStorageHelper.save(DataStorageHelper.getData()
            .withStartRestartTime(startRestartTime)
            .withRestartTime(restartTime));
        // Play sound
        if (!PplUtilsConfig.play_restart_bar_appearing_sound) return;
        var client = Minecraft.getInstance();
        if (client.player != null) client.getSoundManager().play(new SimpleSoundInstance(
            SoundEvents.EXPERIENCE_ORB_PICKUP.location(), SoundSource.UI, 1f, 1f,
            client.player.getRandom(), false, 0, SoundInstance.Attenuation.LINEAR, 0, 1, 0, true
        ));
    }

    public void update(@NotNull Minecraft client) {
        restartTime = DataStorageHelper.getData().restartTime();
        startRestartTime = DataStorageHelper.getData().startRestartTime();
        
        var bossBar = getBossBar();
        var hud = client.gui.getBossOverlay();

        if (bossBar == null) {
            if (isBossBarThere())
                hud.update(ClientboundBossEventPacket.createRemovePacket(rtUuid));
            return;
        }

        if (isBossBarThere()) {
            hud.update(ClientboundBossEventPacket.createUpdateNamePacket(bossBar));
            hud.update(ClientboundBossEventPacket.createUpdateProgressPacket(bossBar));
            hud.update(ClientboundBossEventPacket.createUpdateStylePacket(bossBar));
        } else hud.update(ClientboundBossEventPacket.createAddPacket(bossBar));
    }

    private boolean isBossBarThere() {
        var bossBars = ((BossHealthOverlayAccessor) Minecraft.getInstance().gui.getBossOverlay()).ppl_utils$bossBars();
        return bossBars.containsKey(rtUuid);
    }

    @Contract(" -> new")
    @Nullable
    private LerpingBossEvent getBossBar() {
        long remainingTime = restartTime - System.currentTimeMillis() + startRestartTime;
        if (remainingTime <= 0 || !PplUtilsConfig.render_restart_bar || !isClientOnPepeland())
            return null;

        var text = Component
            .translatable("bossbar.ppl_utils.restart")
            .append(formatRemainingTime(remainingTime));
        return new LerpingBossEvent(
            rtUuid, text,
            1f - (float) remainingTime / restartTime,
            PplUtilsConfig.restart_bar_color, PplUtilsConfig.restart_bar_style,
            false, false, false);
    }

    @Contract(pure = true)
    private @NotNull Component formatRemainingTime(long millis) {
        long secs = (millis / 1000) % 60;
        long mins = (millis / 1000) / 60;

        MutableComponent ans = Component.empty();
        if (mins > 0) ans = ans.append(" " + mins + " ").append(getMinutesString(mins));
        if (secs > 0) ans = ans.append(" " + secs + " ").append(getSecondsString(secs));
        if (mins == 0 && secs == 0) return Component.literal(" ").append(Component.translatable("bossbar.ppl_utils.restart.now"));
        return ans;
    }

    private @NotNull Component getSecondsString(long secs) {
        if (secs == 1) return Component.translatable("bossbar.ppl_utils.restart.seconds.0");
        if (secs < 11 || secs > 14) {
            if (secs % 10 == 1) return Component.translatable("bossbar.ppl_utils.restart.seconds.1");
            if (secs % 10 >= 2 && secs % 10 <= 4) return Component.translatable("bossbar.ppl_utils.restart.seconds.2");
        }
        return Component.translatable("bossbar.ppl_utils.restart.seconds");
    }
    private @NotNull Component getMinutesString(long mins) {
        if (mins == 1) return Component.translatable("bossbar.ppl_utils.restart.minutes.0");
        if (mins < 11 || mins > 14) {
            if (mins % 10 == 1) return Component.translatable("bossbar.ppl_utils.restart.minutes.1");
            if (mins % 10 >= 2 && mins % 10 <= 4) return Component.translatable("bossbar.ppl_utils.restart.minutes.2");
        }
        return Component.translatable("bossbar.ppl_utils.restart.minutes");
    }
}
