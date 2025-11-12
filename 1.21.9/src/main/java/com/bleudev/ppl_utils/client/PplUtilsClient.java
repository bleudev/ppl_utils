package com.bleudev.ppl_utils.client;

import com.bleudev.ppl_utils.client.compat.modmenu.PplUtilsConfig;
import com.bleudev.ppl_utils.client.custom.Keys;
import com.bleudev.ppl_utils.client.custom.debug.WorldBorderDebugHudEntry;
import com.bleudev.ppl_utils.util.ServerUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
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

    @Override
    public void onInitializeClient() {
        PplUtilsConfig.initialize();
        Keys.initialize();

        beta_mode_message_ticks = 0;

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
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (beta_mode_message_ticks > 0) beta_mode_message_ticks--;

            while (Keys.LOBBY_KEY.wasPressed()) executeLobby(client);

            if (client.world != null)
                LOGGER.info("World: {}\nHeader: {}",
                    client.world.getRegistryKey().getValue().toString(),
                    ServerUtils.PepelandWorlds.getCurrentWorld(client)
                );
        });
    }
}
