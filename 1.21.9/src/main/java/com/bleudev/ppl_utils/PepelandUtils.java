package com.bleudev.ppl_utils;

import com.bleudev.ppl_utils.config.PplUtilsConfig;
import com.bleudev.ppl_utils.custom.PepelandUtilsDebugHudEntries;
import com.bleudev.ppl_utils.custom.PepelandUtilsKeyBindings;
import com.bleudev.ppl_utils.util.ServerUtils;
import com.bleudev.ppl_utils.util.helper.DiamondHelper;
import com.bleudev.ppl_utils.util.helper.ErrorScreenHelper;
import com.bleudev.ppl_utils.util.helper.GlobalChatHelper;
import com.bleudev.ppl_utils.util.helper.RestartHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.NotNull;

import static com.bleudev.ppl_utils.ClientCallbacks.*;
import static com.bleudev.ppl_utils.PplUtilsConst.*;
import static com.bleudev.ppl_utils.util.RegistryUtils.getIdentifier;
import static com.bleudev.ppl_utils.util.ServerUtils.getPing;
import static com.bleudev.ppl_utils.util.ServerUtils.isGlobalChatWorking;
import static com.bleudev.ppl_utils.util.TextUtils.link;
import static net.minecraft.SharedConstants.TICKS_PER_MINUTE;

public class PepelandUtils implements ClientModInitializer {
    int beta_mode_message_ticks;
    private RestartHelper restartHelper;

    public static final Identifier AFTER_CHAT_OVERLAY = getIdentifier("after_chat_overlay");
    public static final Identifier OVERLAY = getIdentifier("overlay");

    private float globalChatEnabledAnim = 0f;

    @Override
    public void onInitializeClient() {
        PplUtilsConfig.initialize();
        PepelandUtilsKeyBindings.initialize();
        PepelandUtilsDebugHudEntries.initialize();

        // Initialize data storage
        DataStorageHelper.load();
        DataStorageHelper.save();
        ClientTempData.load();
        ClientTempData.save();

        // Initialize base values
        beta_mode_message_ticks = 0;
        restartHelper = new RestartHelper();
        GlobalChatHelper.INSTANCE = new GlobalChatHelper(false);
        ErrorScreenHelper.INSTANCE = new ErrorScreenHelper();

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
        ClientPlayConnectionEvents.DISCONNECT.register((a1, a2) ->
            GlobalChatHelper.INSTANCE.turnOff());
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (beta_mode_message_ticks > 0) beta_mode_message_ticks--;

            while (PepelandUtilsKeyBindings.LOBBY_KEY.wasPressed()) executeLobby(client);
            while (PepelandUtilsKeyBindings.SIT_KEY.wasPressed()) executeSit(client);
            while (PepelandUtilsKeyBindings.LAY_KEY.wasPressed()) executeLay(client);
            while (PepelandUtilsKeyBindings.SEND_TO_GLOBAL_CHAT_KEY.wasPressed())
                if (isGlobalChatWorking(client))
                    client.setScreen(new ChatScreen("/" + GLOBAL_CHAT_COMMAND + " ", false));
            while (PepelandUtilsKeyBindings.TOGGLE_GLOBAL_CHAT_KEY.wasPressed()) {
                if (isGlobalChatWorking(client)) {
                    GlobalChatHelper.INSTANCE.toggle();
                    GlobalChatHelper.INSTANCE.sendToggleMessage(client);
                } else GlobalChatHelper.INSTANCE.sendToggleErrorMessage(client);
            }
            if (client.player == null) return;
            while (PepelandUtilsKeyBindings.SHOW_PING.wasPressed()) {
                var p = ServerUtils.getPing(client);
                if (p == -1) client.player.sendMessage(Text.translatable("ppl_utils.text.show_ping.failure").formatted(Formatting.RED), true);
                else client.player.sendMessage(Text.translatable("ppl_utils.text.show_ping.success").append(Text.translatable("ppl_utils.text.general.ping", p).formatted(Formatting.GREEN)), true);
            }
            restartHelper.update(client);
            ErrorScreenHelper.INSTANCE.tick();

            if (GlobalChatHelper.INSTANCE.isEnabled()) {
                if (client.inGameHud.getChatHud().isChatFocused())
                    globalChatEnabledAnim = Math.min(globalChatEnabledAnim + 0.1f, 1f);
                else
                    globalChatEnabledAnim = Math.max(globalChatEnabledAnim - 0.1f, 0f);
            } else globalChatEnabledAnim = 0f;
        });

        // Hud
        HudElementRegistry.attachElementAfter(VanillaHudElements.CHAT, AFTER_CHAT_OVERLAY, this::renderAfterChatOverlay);
        HudElementRegistry.addLast(OVERLAY, this::renderOverlay);
    }

    private void renderAfterChatOverlay(@NotNull DrawContext ctx, RenderTickCounter tickCounter) {
        int h = ctx.getScaledWindowHeight();
        int w = ctx.getScaledWindowWidth();
        var client = MinecraftClient.getInstance();
        var vignette_texture = Identifier.ofVanilla("textures/misc/vignette.png");

        // Global chat indicator
        int globalColor = ColorHelper.withAlpha(globalChatEnabledAnim, 0x69b3ff);
        int vignetteColor = ColorHelper.fromFloats(globalChatEnabledAnim, globalChatEnabledAnim / 2, globalChatEnabledAnim / 2, 0);
        ctx.drawTexture(RenderPipelines.VIGNETTE, vignette_texture, 0, 0, 0, 0, w, h, w, h, vignetteColor);
        ctx.drawText(client.textRenderer, Text.translatable("ppl_utils.text.overlay.global_chat_enabled"), 10, 10, globalColor, true);
        // Ping indicator
        if (PplUtilsConfig.render_ping_indicator) {
            Text ping_text = Text.translatable("ppl_utils.text.general.ping", getPing(client));
            int ping_color = 0xff00ff00;
            ctx.drawText(client.textRenderer, ping_text, w - client.textRenderer.getWidth(ping_text) - 10, 10, ping_color, true);
        }
        // Diamond counter
        if (!(client.currentScreen instanceof HandledScreen))
            DiamondHelper.renderCounter(ctx, false);
    }

    private void renderOverlay(@NotNull DrawContext ctx, RenderTickCounter tickCounter) {
        int h = ctx.getScaledWindowHeight();
        int w = ctx.getScaledWindowWidth();

        int redColor = ColorHelper.withAlpha(ErrorScreenHelper.INSTANCE.getRedness(), 0xff0000);
        if (PplUtilsConfig.render_error_screen)
            ctx.fill(0, 0, w, h, redColor);
    }
}
