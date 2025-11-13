package com.bleudev.ppl_utils.client;

import com.bleudev.ppl_utils.client.compat.modmenu.PplUtilsConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
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
        DataStorageHelper.save(new DataStorageHelper.StorageData(startRestartTime, restartTime));
    }

    private boolean added_boss_bar = false;
    public void update(@NotNull MinecraftClient client) {
        restartTime = DataStorageHelper.getData().restartTime();
        startRestartTime = DataStorageHelper.getData().startRestartTime();
        
        var bossBar = getBossBar();
        var hud = client.inGameHud.getBossBarHud();

        if (bossBar == null) {
            if (added_boss_bar) {
                hud.handlePacket(BossBarS2CPacket.remove(rtUuid));
                added_boss_bar = false;
            }
            return;
        }

        if (added_boss_bar) {
            hud.handlePacket(BossBarS2CPacket.updateName(bossBar));
            hud.handlePacket(BossBarS2CPacket.updateProgress(bossBar));
            hud.handlePacket(BossBarS2CPacket.updateStyle(bossBar));
        } else {
            hud.handlePacket(BossBarS2CPacket.add(bossBar));
            added_boss_bar = true;
        }
    }

    public void onDisconnect() {
        added_boss_bar = false;
    }

    @Contract(" -> new")
    @Nullable
    private BossBar getBossBar() {
        long remainingTime = restartTime - System.currentTimeMillis() + startRestartTime;
        if (remainingTime <= 0 || !PplUtilsConfig.render_restart_bar || !isClientOnPepeland())
            return null;

        var text = Text
            .translatable("bossbar.ppl_utils.restart")
            .append(formatRemainingTime(remainingTime));
        var bossBar = new BossBar(rtUuid, text, PplUtilsConfig.restart_bar_color, PplUtilsConfig.restart_bar_style) {};
        bossBar.setPercent(1f - (float) remainingTime / restartTime);
        return bossBar;
    }

    @Contract(pure = true)
    private @NotNull Text formatRemainingTime(long millis) {
        long secs = (millis / 1000) % 60;
        long mins = (millis / 1000) / 60;

        MutableText ans = Text.empty();
        if (mins > 0) ans = ans.append(" " + mins + " ").append(getMinutesString(mins));
        if (secs > 0) ans = ans.append(" " + secs + " ").append(getSecondsString(secs));
        if (mins == 0 && secs == 0) return Text.literal(" ").append(Text.translatable("bossbar.ppl_utils.restart.now"));
        return ans;
    }

    private @NotNull Text getSecondsString(long secs) {
        if (secs == 1) return Text.translatable("bossbar.ppl_utils.restart.seconds.0");
        if (secs < 11 || secs > 14) {
            if (secs % 10 == 1) return Text.translatable("bossbar.ppl_utils.restart.seconds.1");
            if (secs % 10 >= 2 && secs % 10 <= 4) return Text.translatable("bossbar.ppl_utils.restart.seconds.2");
        }
        return Text.translatable("bossbar.ppl_utils.restart.seconds");
    }
    private @NotNull Text getMinutesString(long mins) {
        if (mins == 1) return Text.translatable("bossbar.ppl_utils.restart.minutes.0");
        if (mins < 11 || mins > 14) {
            if (mins % 10 == 1) return Text.translatable("bossbar.ppl_utils.restart.minutes.1");
            if (mins % 10 >= 2 && mins % 10 <= 4) return Text.translatable("bossbar.ppl_utils.restart.minutes.2");
        }
        return Text.translatable("bossbar.ppl_utils.restart.minutes");
    }
}
