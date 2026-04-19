package com.bleudev.ppl_utils;

import com.bleudev.ppl_utils.config.PplUtilsConfig;
import com.bleudev.ppl_utils.custom.PepelandUtilsDebugHudEntries;
import com.bleudev.ppl_utils.custom.PepelandUtilsKeyBindings;
import com.bleudev.ppl_utils.feature.rp.RpHelper;
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
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.NotNull;

import static com.bleudev.ppl_utils.ClientCallbacks.*;
import static com.bleudev.ppl_utils.PplUtilsConst.*;
import static com.bleudev.ppl_utils.config.PplUtilsConfig.do_rp_update;
import static com.bleudev.ppl_utils.config.PplUtilsConfig.rp_update_mins;
import static com.bleudev.ppl_utils.util.RegistryUtils.getIdentifier;
import static com.bleudev.ppl_utils.util.ServerUtils.getPing;
import static com.bleudev.ppl_utils.util.ServerUtils.isGlobalChatWorking;
import static com.bleudev.ppl_utils.util.TextUtils.link;
import static net.minecraft.SharedConstants.TICKS_PER_MINUTE;

public class PepelandUtils implements ClientModInitializer {
    private static int beta_mode_message_ticks;
    private static boolean last_do_rp_update = do_rp_update;
    private static int last_rp_update_mins = rp_update_mins;
    private static int rp_updater_ticks = 0;
    private static RestartHelper restartHelper;
    private static float globalChatEnabledAnim = 0f;

    public static final ResourceLocation AFTER_CHAT_OVERLAY = getIdentifier("after_chat_overlay");
    public static final ResourceLocation OVERLAY = getIdentifier("overlay");

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
                client.player.displayClientMessage(
                    Component.translatable("chat.message.join.beta")
                        .append("\n")
                        .append(link(ISSUES_PAGE))
                        .withStyle(ChatFormatting.GOLD),
                    false);
                beta_mode_message_ticks = 24 * 60 * TICKS_PER_MINUTE;
                LOGGER.info("Successfully sent beta mode message");
            }
            Minecraft.getInstance().gui.getChat().rescaleChat();
        });
        ClientPlayConnectionEvents.DISCONNECT.register((a1, a2) ->
            GlobalChatHelper.INSTANCE.turnOff());
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (do_rp_update && rp_updater_ticks % (1200 * rp_update_mins) == 0)
                RpHelper.asyncCheckUpdates();
            rp_updater_ticks++;
            if (beta_mode_message_ticks > 0) beta_mode_message_ticks--;

            while (PepelandUtilsKeyBindings.LOBBY_KEY.consumeClick()) executeLobby(client);
            while (PepelandUtilsKeyBindings.SIT_KEY.consumeClick()) executeSit(client);
            while (PepelandUtilsKeyBindings.LAY_KEY.consumeClick()) executeLay(client);
            while (PepelandUtilsKeyBindings.SEND_TO_GLOBAL_CHAT_KEY.consumeClick())
                if (isGlobalChatWorking(client))
                    client.setScreen(new ChatScreen("/" + GLOBAL_CHAT_COMMAND + " ", false));
            while (PepelandUtilsKeyBindings.TOGGLE_GLOBAL_CHAT_KEY.consumeClick()) {
                if (isGlobalChatWorking(client)) {
                    GlobalChatHelper.INSTANCE.toggle();
                    GlobalChatHelper.INSTANCE.sendToggleMessage(client);
                } else GlobalChatHelper.INSTANCE.sendToggleErrorMessage(client);
            }
            if (client.player == null) return;
            while (PepelandUtilsKeyBindings.SHOW_PING.consumeClick()) {
                var p = ServerUtils.getPing(client);
                if (p == -1) client.player.displayClientMessage(Component.translatable("ppl_utils.text.show_ping.failure").withStyle(ChatFormatting.RED), true);
                else client.player.displayClientMessage(Component.translatable("ppl_utils.text.show_ping.success").append(Component.translatable("ppl_utils.text.general.ping", p).withStyle(ChatFormatting.GREEN)), true);
            }
            restartHelper.update(client);
            ErrorScreenHelper.INSTANCE.tick();

            if (GlobalChatHelper.INSTANCE.isEnabled()) {
                if (client.gui.getChat().isChatFocused())
                    globalChatEnabledAnim = Math.min(globalChatEnabledAnim + 0.1f, 1f);
                else
                    globalChatEnabledAnim = Math.max(globalChatEnabledAnim - 0.1f, 0f);
            } else globalChatEnabledAnim = 0f;
        });

        // Hud
        HudElementRegistry.attachElementAfter(VanillaHudElements.CHAT, AFTER_CHAT_OVERLAY, this::renderAfterChatOverlay);
        HudElementRegistry.addLast(OVERLAY, this::renderOverlay);
    }

    private void renderAfterChatOverlay(@NotNull GuiGraphics ctx, DeltaTracker tickCounter) {
        int h = ctx.guiHeight();
        int w = ctx.guiWidth();
        var client = Minecraft.getInstance();
        var vignette_texture = ResourceLocation.withDefaultNamespace("textures/misc/vignette.png");

        // Global chat indicator
        int globalColor = ARGB.color(globalChatEnabledAnim, 0x69b3ff);
        int vignetteColor = ARGB.colorFromFloat(globalChatEnabledAnim, globalChatEnabledAnim / 2, globalChatEnabledAnim / 2, 0);
        ctx.blit(RenderPipelines.VIGNETTE, vignette_texture, 0, 0, 0, 0, w, h, w, h, vignetteColor);
        ctx.drawString(client.font, Component.translatable("ppl_utils.text.overlay.global_chat_enabled"), 10, 10, globalColor, true);
        // Ping indicator
        if (PplUtilsConfig.render_ping_indicator) {
            int ping = getPing(client);
            if (ping != -1) {
                Component ping_text = Component.translatable("ppl_utils.text.general.ping", ping);
                int ping_color = 0xff00ff00;
                ctx.drawString(client.font, ping_text, w - client.font.width(ping_text) - 10, 10, ping_color, true);
            }
        }
        // Diamond counter
        if (!(client.screen instanceof AbstractContainerScreen))
            DiamondHelper.renderCounter(ctx, false);
    }

    private void renderOverlay(@NotNull GuiGraphics ctx, DeltaTracker tickCounter) {
        int h = ctx.guiHeight();
        int w = ctx.guiWidth();

        int redColor = ARGB.color(ErrorScreenHelper.INSTANCE.getRedness(), 0xff0000);
        if (PplUtilsConfig.render_error_screen)
            ctx.fill(0, 0, w, h, redColor);
    }

    public static void onConfigUpdate() {
        if (last_do_rp_update != do_rp_update || rp_update_mins != last_rp_update_mins) {
            rp_updater_ticks = 1;
        }
        last_do_rp_update = do_rp_update;
        last_rp_update_mins = rp_update_mins;
    }
}
