package com.bleudev.ppl_utils.util.helper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

public class GlobalChatHelper {
    public static GlobalChatHelper INSTANCE = null;

    private static final Text TOGGLE_ENABLED_SUCESS = Text.translatable("ppl_utils.text.toggle_global_chat.start").formatted(Formatting.WHITE)
        .append(Text.translatable("ppl_utils.text.toggle_global_chat.enabled").formatted(Formatting.GREEN));
    private static final Text TOGGLE_DISABLED_SUCESS = Text.translatable("ppl_utils.text.toggle_global_chat.start").formatted(Formatting.WHITE)
        .append(Text.translatable("ppl_utils.text.toggle_global_chat.disabled").formatted(Formatting.RED));
    private static final Text TOGGLE_ERROR_DOESNT_SUPPORT = Text.translatable("ppl_utils.text.toggle_global_chat.fail.doesnt_support").formatted(Formatting.RED);

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

    public void sendToggleMessage(@NotNull MinecraftClient client) {
        if (client.player != null)
            client.player.sendMessage(isEnabled() ? TOGGLE_ENABLED_SUCESS : TOGGLE_DISABLED_SUCESS, true);
    }

    public void sendToggleErrorMessage(@NotNull MinecraftClient client) {
        if (client.player != null)
            client.player.sendMessage(TOGGLE_ERROR_DOESNT_SUPPORT, true);
    }
}
