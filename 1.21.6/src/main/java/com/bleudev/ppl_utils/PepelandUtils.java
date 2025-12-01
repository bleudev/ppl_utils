package com.bleudev.ppl_utils;

import com.bleudev.ppl_utils.config.PplUtilsConfig;
import com.bleudev.ppl_utils.custom.Keys;
import com.bleudev.ppl_utils.util.helper.GlobalChatHelper;
import com.bleudev.ppl_utils.util.helper.RestartHelper;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.NotNull;

import static com.bleudev.ppl_utils.ClientCallbacks.executeLobby;
import static com.bleudev.ppl_utils.PplUtilsConst.*;
import static com.bleudev.ppl_utils.util.RegistryUtils.getIdentifier;
import static com.bleudev.ppl_utils.util.ServerCache.isGlobalChatWorking;
import static com.bleudev.ppl_utils.util.ServerCache.isLobbyCommandWorking;
import static com.bleudev.ppl_utils.util.ServerCache.isOnPepeland;
import static com.bleudev.ppl_utils.util.TextUtils.link;
import static com.bleudev.ppl_utils.util.UIConstants.ANIMATION_MAX;
import static com.bleudev.ppl_utils.util.UIConstants.ANIMATION_MIN;
import static com.bleudev.ppl_utils.util.UIConstants.ANIMATION_STEP;
import static com.bleudev.ppl_utils.util.UIConstants.COLOR_GLOBAL_CHAT_ENABLED;
import static com.bleudev.ppl_utils.util.UIConstants.PADDING_MEDIUM;
import static net.minecraft.SharedConstants.TICKS_PER_MINUTE;

public class PepelandUtils implements ClientModInitializer {
    private int beta_mode_message_ticks;
    private RestartHelper restartHelper;

    public static final Identifier AFTER_CHAT_OVERLAY = getIdentifier("after_chat_overlay");
    public static final Identifier INVENTORY_SLOT_COUNTER = getIdentifier("inventory_slot_counter");

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
            com.bleudev.ppl_utils.util.ServerCache.invalidate();
            LOGGER.info("Try send beta mode message");
            if (BETA_MODE_ENABLED && client.player != null && beta_mode_message_ticks == 0) {
                client.player.sendMessage(
                    Text.translatable("chat.message.join.beta")
                        .append("\n")
                        .append(link(ISSUES_PAGE))
                        .formatted(Formatting.GOLD),
                    false);
                beta_mode_message_ticks = BETA_MESSAGE_COOLDOWN_TICKS * TICKS_PER_MINUTE;
                LOGGER.info("Successfully sent beta mode message");
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register((a1, a2) -> {
            com.bleudev.ppl_utils.util.ServerCache.invalidate();
            restartHelper.onDisconnect();
        });
        ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {
            // Filter chat messages - if message should be filtered, prevent it from being added to chat
            if (!com.bleudev.ppl_utils.feature.chatfilter.ChatFilter.shouldDisplayMessage(message)) {
                // Prevent the message from being added to chat by clearing the chat hud
                // This is a workaround - the mixin will handle actual cancellation
                return;
            }
            // Process restart messages (always allow restart messages to be displayed)
            processRestartMessage(message);
        });
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
            while (Keys.EXECUTABLE_QUEUE_KEY.wasPressed()) {
                com.bleudev.ppl_utils.feature.executablequeue.ExecutableQueueManager.getInstance().executeQueue(client);
            }
            while (Keys.EXECUTABLE_QUEUE_CLIPBOARD_KEY.wasPressed()) {
                com.bleudev.ppl_utils.feature.executablequeue.ExecutableQueueManager.getInstance().executeQueueFromClipboard(client);
            }

            if (client.player == null) return;
            restartHelper.update(client);
            
            // Update Always With Me feature
            com.bleudev.ppl_utils.feature.alwayswithme.AlwaysWithMeManager.getInstance().tick(client);
            
            // Update Executable Queue feature
            com.bleudev.ppl_utils.feature.executablequeue.ExecutableQueueManager.getInstance().tick(client);

            if (GlobalChatHelper.INSTANCE.isEnabled()) {
                if (client.inGameHud.getChatHud().isChatFocused())
                    globalChatEnabledAnim = Math.min(globalChatEnabledAnim + ANIMATION_STEP, ANIMATION_MAX);
                else
                    globalChatEnabledAnim = Math.max(globalChatEnabledAnim - ANIMATION_STEP, ANIMATION_MIN);
            } else globalChatEnabledAnim = ANIMATION_MIN;
        });

        // Hud
        HudElementRegistry.attachElementAfter(VanillaHudElements.CHAT, AFTER_CHAT_OVERLAY, this::renderAfterChatOverlay);
        // Note: Using mixin instead of HUD element for inventory slot counter
        
        // Register chat filter command
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            com.bleudev.ppl_utils.feature.chatfilter.command.ChatFilterCommand.register(dispatcher);
        });
    }

    private void processRestartMessage(Text message) {
        MinecraftClient client = MinecraftClient.getInstance();
        // Check server only if not ignoring server check
        boolean shouldProcess = PplUtilsConfig.restart_bar_ignore_server_check 
            || isOnPepeland(client);
        
        if (!shouldProcess) return;
        
        // Use the new parser to extract restart time
        Long restartTime = com.bleudev.ppl_utils.util.helper.RestartMessageParser.parseRestartTime(message);
        
        if (restartTime != null) {
            RestartHelper.runRestartBar(restartTime);
        }
    }

    private void renderAfterChatOverlay(@NotNull DrawContext ctx, RenderTickCounter tickCounter) {
        int h = ctx.getScaledWindowHeight();
        int w = ctx.getScaledWindowWidth();
        var client = MinecraftClient.getInstance();
        var vignette_texture = Identifier.ofVanilla("textures/misc/vignette.png");

        int globalColor = ColorHelper.withAlpha(globalChatEnabledAnim, COLOR_GLOBAL_CHAT_ENABLED);
        int vignetteColor = ColorHelper.fromFloats(globalChatEnabledAnim, globalChatEnabledAnim / 2, globalChatEnabledAnim / 2, 0);
        ctx.drawTexture(RenderPipelines.VIGNETTE, vignette_texture, 0, 0, 0, 0, w, h, w, h, vignetteColor);
        ctx.drawText(client.textRenderer, Text.translatable("ppl_utils.text.overlay.global_chat_enabled"), PADDING_MEDIUM, PADDING_MEDIUM, globalColor, true);
    }

    private void renderInventorySlotCounter(@NotNull DrawContext ctx, RenderTickCounter tickCounter) {
        if (!PplUtilsConfig.show_inventory_slot_count) return;
        
        var client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // Count occupied slots in inventory (27 slots) and hotbar (9 slots)
        int occupiedSlots = 0;
        var inventory = client.player.getInventory();
        
        // Count hotbar slots (0-8)
        for (int i = 0; i < 9; i++) {
            if (!inventory.getStack(i).isEmpty()) {
                occupiedSlots++;
            }
        }
        
        // Count main inventory slots (9-35, total 27 slots)
        for (int i = 9; i < 36; i++) {
            if (!inventory.getStack(i).isEmpty()) {
                occupiedSlots++;
            }
        }

        int totalSlots = 36; // 9 hotbar + 27 inventory
        
        // Get screen dimensions
        int width = ctx.getScaledWindowWidth();
        int height = ctx.getScaledWindowHeight();
        
        // Hotbar is centered, positioned at: x = (width / 2) - 91, y = height - 22
        // We'll render to the right of the hotbar
        int hotbarX = (width / 2) - 91;
        int hotbarY = height - 22;
        int hotbarWidth = 182; // Standard hotbar width
        
        // Position icon and text to the right of hotbar with some padding
        int iconSize = 16;
        int iconX = hotbarX + hotbarWidth + 8;
        int iconY = hotbarY + 3; // Align with hotbar items (hotbar items are at y + 3)
        
        // Draw chest item icon
        ItemStack chestStack = new ItemStack(Items.CHEST);
        ctx.drawItemWithoutEntity(chestStack, iconX, iconY);
        
        // Create text with format: "occupied/total"
        String slotTextStr = occupiedSlots + "/" + totalSlots;
        Text slotText = Text.literal(slotTextStr);
        
        // Draw text next to icon (icon is 16x16, add 4px padding)
        // Position text to align with center of icon vertically
        int textX = iconX + iconSize + 4;
        int textY = iconY + 8 - (client.textRenderer.fontHeight / 2); // Center vertically with icon
        
        // Draw text next to icon
        ctx.drawText(client.textRenderer, slotText, textX, textY, 0xFFFFFF, true);
    }
}
