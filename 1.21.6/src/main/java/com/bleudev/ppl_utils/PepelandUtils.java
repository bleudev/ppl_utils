package com.bleudev.ppl_utils;

import com.bleudev.ppl_utils.config.PplUtilsConfig;
import com.bleudev.ppl_utils.custom.Keys;
import com.bleudev.ppl_utils.util.helper.GlobalChatHelper;
import com.bleudev.ppl_utils.util.helper.RestartHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.NotNull;

import static com.bleudev.ppl_utils.ClientCallbacks.executeLobby;
import static com.bleudev.ppl_utils.PplUtilsConst.*;
import static com.bleudev.ppl_utils.util.LangUtils.anySubstringMatches;
import static com.bleudev.ppl_utils.util.RegistryUtils.getIdentifier;
import static com.bleudev.ppl_utils.util.ServerUtils.isClientOnPepeland;
import static com.bleudev.ppl_utils.util.ServerUtils.isGlobalChatWorking;
import static com.bleudev.ppl_utils.util.TextUtils.link;
import static net.minecraft.SharedConstants.TICKS_PER_MINUTE;

public class PepelandUtils implements ClientModInitializer {
    private int beta_mode_message_ticks;
    private RestartHelper restartHelper;

    public static final Identifier AFTER_CHAT_OVERLAY = getIdentifier("after_chat_overlay");

    private float globalChatEnabledAnim = 0f;

    @Override
    public void onInitializeClient() {
        PplUtilsConfig.initialize();
        Keys.initialize();

        // Initialize data storage
        DataStorageHelper.save();
        DataStorageHelper.load();

        beta_mode_message_ticks = 0;
        restartHelper = new RestartHelper();
        GlobalChatHelper.INSTANCE = new GlobalChatHelper(false);

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
        ClientPlayConnectionEvents.DISCONNECT.register((a1, a2) -> restartHelper.onDisconnect());
        ClientReceiveMessageEvents.CHAT.register((t, a1, a2, a3, a4) -> tryStartRestartBar(t));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (beta_mode_message_ticks > 0) beta_mode_message_ticks--;

            while (Keys.LOBBY_KEY.wasPressed()) executeLobby(client);
            while (Keys.SEND_TO_GLOBAL_CHAT_KEY.wasPressed())
                if (isGlobalChatWorking(client))
                    client.setScreen(new ChatScreen("/" + GLOBAL_CHAT_COMMAND + " "));
            while (Keys.TOGGLE_GLOBAL_CHAT_KEY.wasPressed()) {
                if (isGlobalChatWorking(client)) {
                    GlobalChatHelper.INSTANCE.toggle();
                    GlobalChatHelper.INSTANCE.sendToggleMessage(client);
                } else GlobalChatHelper.INSTANCE.sendToggleErrorMessage(client);
            }

            if (client.player == null) return;
            restartHelper.update(client);

            if (GlobalChatHelper.INSTANCE.isEnabled()) {
                if (client.inGameHud.getChatHud().isChatFocused())
                    globalChatEnabledAnim = Math.min(globalChatEnabledAnim + 0.1f, 1f);
                else
                    globalChatEnabledAnim = Math.max(globalChatEnabledAnim - 0.1f, 0f);
            } else globalChatEnabledAnim = 0f;
        });

        // Hud
        HudElementRegistry.attachElementAfter(VanillaHudElements.CHAT, AFTER_CHAT_OVERLAY, this::renderAfterChatOverlay);
    }

    private void tryStartRestartBar(Text restartMessage) {
        if (!isClientOnPepeland()) return;

        var content = restartMessage.getString()
            .replaceAll("<[^< >]+> *", "")
            .replaceAll("\\[PPL[0-9]*]: ", ""); // Ignore Pepeland prefixes
        try {
            if (content.contains("Рестарт через")) {
                LOGGER.info("Got restart message: {}", content);
                var time = Long.parseLong(content.replaceAll("[^0-9]", ""));
                RestartHelper.runRestartBar(time * (anySubstringMatches(content, "минут[а-я]*") ? 60_000 : 1_000));
            }
        } catch (NumberFormatException ignored) {
            LOGGER.error("Unexpected number format exception while parsing \"{}\" string. Please report about it.", content);
        }
    }

    private void renderAfterChatOverlay(@NotNull DrawContext ctx, RenderTickCounter tickCounter) {
        int h = ctx.getScaledWindowHeight();
        int w = ctx.getScaledWindowWidth();
        var client = MinecraftClient.getInstance();
        var vignette_texture = Identifier.ofVanilla("textures/misc/vignette.png");

        int globalColor = ColorHelper.withAlpha(globalChatEnabledAnim, 0x69b3ff);
        int vignetteColor = ColorHelper.fromFloats(globalChatEnabledAnim, globalChatEnabledAnim / 2, globalChatEnabledAnim / 2, 0);
        ctx.drawTexture(RenderPipelines.VIGNETTE, vignette_texture, 0, 0, 0, 0, w, h, w, h, vignetteColor);
        ctx.drawText(client.textRenderer, Text.translatable("ppl_utils.text.overlay.global_chat_enabled"), 10, 10, globalColor, true);
    }
}
