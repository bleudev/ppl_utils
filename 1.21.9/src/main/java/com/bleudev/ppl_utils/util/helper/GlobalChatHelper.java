package com.bleudev.ppl_utils.util.helper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class GlobalChatHelper {
    public static GlobalChatHelper INSTANCE = null;

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
        Supplier<Formatting> enabledDisabledColor = () -> enabled ? Formatting.GREEN : Formatting.RED;

        if (client.player != null)
            client.player.sendMessage(Text.translatable("ppl_utils.text.action.bar.start")
                .formatted(Formatting.WHITE)
                .append((enabled ? Text.translatable("ppl_utils.text.action.bar.enabled") : Text.translatable("ppl_utils.text.action.bar.disabled"))
                    .formatted(enabledDisabledColor.get())
                ), true);
    }
}
