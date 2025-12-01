package com.bleudev.ppl_utils.util.helper;

import com.bleudev.ppl_utils.DataStorageHelper;
import com.bleudev.ppl_utils.PplUtilsConst;
import com.bleudev.ppl_utils.config.PplUtilsConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static com.bleudev.ppl_utils.util.ServerCache.isOnPepeland;

public class RestartHelper {
    private static final UUID rtUuid = UUID.randomUUID();
    private static final long UPDATE_INTERVAL_MS = 1000; // Update once per second
    private static final long SOUND_20_SECONDS_INTERVAL_MS = 20_000; // Every 20 seconds
    private static final long SOUND_2_SECONDS_THRESHOLD_MS = 2_000; // 2 seconds before restart

    private static long restartTime = 0;
    private static long startRestartTime = 0;
    
    // Cache for data from DataStorageHelper
    private static long cachedRestartTime = 0;
    private static long cachedStartRestartTime = 0;
    private static boolean dataDirty = true;
    
    // Sound playback tracking (optimization: prevent duplicate sounds)
    private boolean hasPlayedAppearanceSound = false;
    private long last20SecondsSoundTime = 0;
    private boolean hasPlayed2SecondsSound = false;

    public static void runRestartBar(long restartTime) {
        if (restartTime <= 0) {
            // Reset restart bar if time is 0 or negative
            RestartHelper.restartTime = 0;
            startRestartTime = 0;
            cachedRestartTime = 0;
            cachedStartRestartTime = 0;
            dataDirty = false;
            DataStorageHelper.save(new DataStorageHelper.StorageData(0, 0));
            PplUtilsConst.LOGGER.info("Restart bar reset");
            return;
        }
        
        RestartHelper.restartTime = restartTime;
        startRestartTime = System.currentTimeMillis();
        cachedRestartTime = restartTime;
        cachedStartRestartTime = startRestartTime;
        dataDirty = false;
        DataStorageHelper.save(new DataStorageHelper.StorageData(startRestartTime, restartTime));
        PplUtilsConst.LOGGER.info("Restart bar started: {} ms from now", restartTime);
    }
    
    /**
     * Resets sound tracking when restart bar is reset or disconnected.
     */
    private void resetSoundTracking() {
        hasPlayedAppearanceSound = false;
        last20SecondsSoundTime = 0;
        hasPlayed2SecondsSound = false;
    }

    private boolean added_boss_bar = false;
    private Text cachedTimeText = null;
    private long lastUpdateTime = 0;
    private float lastProgress = -1;
    
    public void update(@NotNull MinecraftClient client) {
        // Read from DataStorageHelper only if data is dirty (changed)
        if (dataDirty) {
            cachedRestartTime = DataStorageHelper.getData().restartTime();
            cachedStartRestartTime = DataStorageHelper.getData().startRestartTime();
            restartTime = cachedRestartTime;
            startRestartTime = cachedStartRestartTime;
            dataDirty = false;
            // Invalidate cached text when data changes
            cachedTimeText = null;
        }
        
        var bossBar = getBossBar(client);
        var hud = client.inGameHud.getBossBarHud();

        if (bossBar == null) {
            if (added_boss_bar) {
                hud.handlePacket(BossBarS2CPacket.remove(rtUuid));
                added_boss_bar = false;
                cachedTimeText = null;
                lastProgress = -1;
                resetSoundTracking();
            }
            return;
        }

        // Play appearance sound when boss bar is first added
        if (!added_boss_bar) {
            hud.handlePacket(BossBarS2CPacket.add(bossBar));
            added_boss_bar = true;
            lastProgress = 1f - (float) (restartTime - System.currentTimeMillis() + startRestartTime) / restartTime;
            playAppearanceSound(client);
        } else {
            // Only update if progress or text changed significantly
            float currentProgress = 1f - (float) (restartTime - System.currentTimeMillis() + startRestartTime) / restartTime;
            if (Math.abs(currentProgress - lastProgress) > 0.01f || cachedTimeText == null) {
                hud.handlePacket(BossBarS2CPacket.updateName(bossBar));
                hud.handlePacket(BossBarS2CPacket.updateProgress(bossBar));
                lastProgress = currentProgress;
            }
            // Style rarely changes, so we can skip it
        }
        
        // Handle sound playback (optimized: check only when needed)
        long currentTime = System.currentTimeMillis();
        long remainingTime = restartTime - (currentTime - startRestartTime);
        checkAndPlaySounds(client, remainingTime, currentTime);
    }
    
    public static void markDataDirty() {
        dataDirty = true;
    }

    public void onDisconnect() {
        added_boss_bar = false;
        cachedTimeText = null;
        lastProgress = -1;
        dataDirty = true; // Mark data as dirty to reload on next connection
        resetSoundTracking();
    }
    
    /**
     * Plays sound when restart bar appears.
     */
    private void playAppearanceSound(@NotNull MinecraftClient client) {
        if (hasPlayedAppearanceSound || client.player == null || !PplUtilsConfig.restart_bar_sound_start) {
            return;
        }
        
        // Use player's playSoundToPlayer method for client-side sounds
        SoundEvent soundEvent = SoundEvents.BLOCK_DECORATED_POT_BREAK;
        client.player.playSoundToPlayer(soundEvent, SoundCategory.MASTER, 1.0f, 2.0f);
        hasPlayedAppearanceSound = true;
        PplUtilsConst.LOGGER.debug("Played restart bar appearance sound");
    }
    
    /**
     * Checks and plays sounds based on remaining time.
     * Optimized to check only when needed and prevent duplicate playback.
     */
    private void checkAndPlaySounds(@NotNull MinecraftClient client, long remainingTimeMs, long currentTime) {
        if (client.player == null) {
            return;
        }
        
        long remainingSeconds = remainingTimeMs / 1000;
        
        // Sound 1: 2 seconds before restart
        if (remainingTimeMs <= SOUND_2_SECONDS_THRESHOLD_MS && remainingTimeMs > 0 && !hasPlayed2SecondsSound && PplUtilsConfig.restart_bar_sound_end) {
            SoundEvent soundEvent = SoundEvents.BLOCK_BEACON_DEACTIVATE;
            client.player.playSoundToPlayer(soundEvent, SoundCategory.MASTER, 1.0f, 1.0f);
            hasPlayed2SecondsSound = true;
            PplUtilsConst.LOGGER.debug("Played 2 seconds warning sound");
        }
        
        // Sound 2: Every 20 seconds, but NOT in last 20 seconds when remaining is 0-1
        // Only play if remaining time is more than 1 second (exclude 0-1 seconds)
        if (remainingSeconds > 1 && PplUtilsConfig.restart_bar_sound_interval) {
            // Check if 20 seconds have passed since last sound
            long timeSinceLastSound = currentTime - last20SecondsSoundTime;
            if (timeSinceLastSound >= SOUND_20_SECONDS_INTERVAL_MS) {
                // Additional check: don't play if we're in the last 20 seconds with 0-1 remaining
                // This prevents playing when remainingSeconds is 0 or 1
                if (remainingSeconds > 1) {
                    SoundEvent soundEvent = SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE;
                    client.player.playSoundToPlayer(soundEvent, SoundCategory.MASTER, 1.0f, 0.0f);
                    last20SecondsSoundTime = currentTime;
                    PplUtilsConst.LOGGER.debug("Played 20 seconds interval sound at {} seconds remaining", remainingSeconds);
                }
            }
        }
    }

    @Contract(" -> new")
    @Nullable
    private ClientBossBar getBossBar(@NotNull MinecraftClient client) {
        long currentTime = System.currentTimeMillis();
        long remainingTime = restartTime - (currentTime - startRestartTime);
        
        if (remainingTime <= 0 || !PplUtilsConfig.render_restart_bar)
            return null;
        
        // Check server if not ignoring server check (uses cached result from ServerCache)
        if (!PplUtilsConfig.restart_bar_ignore_server_check && !isOnPepeland(client))
            return null;

        // Update text only once per second to reduce allocations
        if (cachedTimeText == null || (currentTime - lastUpdateTime) >= UPDATE_INTERVAL_MS) {
            cachedTimeText = formatRemainingTime(remainingTime);
            lastUpdateTime = currentTime;
        }
        
        var text = Text
            .translatable("bossbar.ppl_utils.restart")
            .append(cachedTimeText);
        
        return new ClientBossBar(
            rtUuid, text,
            1f - (float) remainingTime / restartTime,
            PplUtilsConfig.restart_bar_color, PplUtilsConfig.restart_bar_style,
            false, false, true);
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
