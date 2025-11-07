package com.bleudev.ppl_utils.client;

import com.bleudev.ppl_utils.client.compat.modmenu.PplUtilsConfig;
import com.bleudev.ppl_utils.util.ServerUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;

public class ClientCallbacks {
    public static void executeLobby() {
        var client = MinecraftClient.getInstance();
        if (ServerUtils.isClientOnServerSupportsLobbyCommand(client))
            ServerUtils.executeCommand(client, "lobby");
    }

    public static boolean shouldRenderChatMessage(ChatHudLine message) {
        if (PplUtilsConfig.do_join_leave_messages_rendering) return true;

        var content = message.content().getString();
        final int i;
        if ((i = content.indexOf(">")) != -1)
            content = content.substring(i+2);

        if (content.startsWith("[+]") || content.startsWith("[-]"))
            if (content.length() >= 5) {
                final String user = content.substring(4);
                return PplUtilsConfig
                        .always_show_join_leave_messages_by
                        .contains(user);
            }

        return true;
    }
}
