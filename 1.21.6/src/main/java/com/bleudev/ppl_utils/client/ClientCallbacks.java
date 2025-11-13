package com.bleudev.ppl_utils.client;

import com.bleudev.ppl_utils.client.compat.modmenu.PplUtilsConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.bleudev.ppl_utils.util.ServerUtils.executeCommand;
import static com.bleudev.ppl_utils.util.ServerUtils.isLobbyCommandWorking;

public class ClientCallbacks {
    public static void executeLobby(@NotNull MinecraftClient client) {
        if (isLobbyCommandWorking(client))
            executeCommand(client, "lobby");
    }

    @Nullable
    private static String extractPlayer(@NotNull String content) {
        String e = content;

        final var NICKNAME = "[^ \\[\\]]+";
        e = e.replaceAll(String.format("(<%1$2s>)* *\\[\\+] *\\[%1$2s head] *", NICKNAME), "");
        e = e.replaceAll(String.format("(<%1$2s>)* *\\[-] *\\[%1$2s head] *", NICKNAME), "");
        if (content.equals(e)) {
            e = e.replaceAll(String.format("(<%1$2s>)* *\\[\\+] *", NICKNAME), "");
            e = e.replaceAll(String.format("(<%1$2s>)* *\\[-] *", NICKNAME), "");
        }
        System.out.println(e);
        if (e.matches(NICKNAME))
            return e;
        return null;
    }

    public static boolean shouldRenderChatMessage(ChatHudLine message) {
        if (PplUtilsConfig.do_join_leave_messages_rendering) return true;

        String user;
        if ((user = extractPlayer(message.content().getString())) != null)
            return PplUtilsConfig
                    .always_show_join_leave_messages_by.stream()
                    .map(String::toLowerCase).toList()
                    .contains(user);
        return true;
    }

    public static boolean shouldRenderLobbyButton(@NotNull MinecraftClient client) {
        return PplUtilsConfig.lobby_button_enabled && isLobbyCommandWorking(client);
    }
}
