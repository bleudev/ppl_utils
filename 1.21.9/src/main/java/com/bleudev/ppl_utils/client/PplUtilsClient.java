package com.bleudev.ppl_utils.client;

import com.bleudev.ppl_utils.client.compat.modmenu.PplUtilsConfig;
import com.bleudev.ppl_utils.client.custom.Keys;
import com.bleudev.ppl_utils.client.custom.debug.WorldBorderDebugHudEntry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.gui.hud.debug.DebugHudEntries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.bleudev.ppl_utils.PplUtilsConst.*;
import static com.bleudev.ppl_utils.client.ClientCallbacks.executeLobby;
import static com.bleudev.ppl_utils.util.RegistryUtils.getIdentifier;
import static com.bleudev.ppl_utils.util.TextUtils.link;
import static net.minecraft.SharedConstants.TICKS_PER_MINUTE;

public class PplUtilsClient implements ClientModInitializer {
    int beta_mode_message_ticks;
    private RestartHelper restartHelper;

    @Override
    public void onInitializeClient() {
        PplUtilsConfig.initialize();
        Keys.initialize();

        beta_mode_message_ticks = 0;
        restartHelper = new RestartHelper();

        LOGGER.debug("Register {} debug hud entry", getIdentifier("world_border"));
        DebugHudEntries.register(getIdentifier("world_border"), new WorldBorderDebugHudEntry());

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            LOGGER.info("Try send beta mode message");
            if (BETA_MODE_ENABLED && client.player != null && beta_mode_message_ticks == 0) {
                client.player.sendMessage(
                    Text.translatable("chat.message.join.beta")
                        .append("\n")
                        .append(link(ISSUES_PAGE))
                        .formatted(Formatting.GOLD),
                    false);
                beta_mode_message_ticks = 10 * TICKS_PER_MINUTE;
                LOGGER.info("Successfully sent beta mode message");
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            restartHelper.onDisconnect();
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (beta_mode_message_ticks > 0) beta_mode_message_ticks--;

            while (Keys.LOBBY_KEY.wasPressed()) executeLobby(client);

            if (client.player == null) return;
            restartHelper.update(client);
        });
        ClientReceiveMessageEvents.CHAT.register((text, signedMessage, gameProfile, parameters, instant) -> {
            var content = text.getString().replaceAll("<[^< >]+> *", "");
            content = content.replaceAll("\\[PPL[0-9]*]: ", ""); // Ignore Pepeland prefixes
            try {
                if (content.contains("Рестарт через")) {
                    var time = Long.parseLong(content.replaceAll("[^0-9]", ""));
                    RestartHelper.runRestartBar(time * (content.contains("минут") ? 60_000 : 1_000));
                }
            } catch (NumberFormatException ignored) {
                LOGGER.error("Unexpected number format exception while parsing \"{}\" string. Please report about it.", content);
            }
        });
    }
}
