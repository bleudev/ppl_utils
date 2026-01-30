package com.bleudev.ppl_utils.util.helper;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class GlobalChatHelper {
    public static GlobalChatHelper INSTANCE = null;

    private static final Component TOGGLE_ENABLED_SUCESS = Component.translatable("ppl_utils.text.toggle_global_chat.start").withStyle(ChatFormatting.WHITE)
        .append(Component.translatable("ppl_utils.text.toggle_global_chat.enabled").withStyle(ChatFormatting.GREEN));
    private static final Component TOGGLE_DISABLED_SUCESS = Component.translatable("ppl_utils.text.toggle_global_chat.start").withStyle(ChatFormatting.WHITE)
        .append(Component.translatable("ppl_utils.text.toggle_global_chat.disabled").withStyle(ChatFormatting.RED));
    private static final Component TOGGLE_ERROR_DOESNT_SUPPORT = Component.translatable("ppl_utils.text.toggle_global_chat.fail.doesnt_support").withStyle(ChatFormatting.RED);

    private boolean enabled;
    public GlobalChatHelper(boolean initial) {
        enabled = initial;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void toggle() {
        enabled = !enabled;
    }
    public void turnOff() {
        enabled = false;
    }

    public void sendToggleMessage(@NotNull Minecraft client) {
        if (client.player != null)
            client.player.displayClientMessage(isEnabled() ? TOGGLE_ENABLED_SUCESS : TOGGLE_DISABLED_SUCESS, true);
    }

    public void sendToggleErrorMessage(@NotNull Minecraft client) {
        if (client.player != null)
            client.player.displayClientMessage(TOGGLE_ERROR_DOESNT_SUPPORT, true);
    }
}
