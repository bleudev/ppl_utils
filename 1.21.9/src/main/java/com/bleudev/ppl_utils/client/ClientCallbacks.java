package com.bleudev.ppl_utils.client;

import com.bleudev.ppl_utils.client.compat.modmenu.PplUtilsConfig;
import com.bleudev.ppl_utils.util.ServerUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.jetbrains.annotations.Nullable;

public class ClientCallbacks {
    public static void executeLobby() {
        var client = MinecraftClient.getInstance();
        if (ServerUtils.isClientOnServerSupportsLobbyCommand(client))
            ServerUtils.executeCommand(client, "lobby");
    }

    @Nullable
    private static String extractPlayer(String content) {
        content = content.toLowerCase();
        if (content.contains(" ")) return extractPlayer(content.replace(" ", ""));

        final int i, j;
        if (content.startsWith("<") && (i = content.indexOf(">")) != -1 && content.length() >= i + 1)
            return extractPlayer(content.substring(i+1));

        if (content.startsWith("[+]") || content.startsWith("[-]")) {
            var user = content.substring(3);

            if (user.startsWith("[")) {
                if ((j = user.indexOf("]")) != -1 && content.length() >= j + 1) {
                    var ans = user.substring(j+1);
                    if (user.startsWith("[" + ans + "head]")) return ans;
                }
            } else return user;
        }

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
}
