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

    public static boolean shouldCancelMessageRendering(ChatHudLine message) {
        if (PplUtilsConfig.do_join_leave_messages_rendering) return false;

        var content = message.content().getString();
        final int i;
        if ((i = content.indexOf(">")) != -1)
            content = content.substring(i+2);

        return content.startsWith("[+]") || content.startsWith("[-]");
    }
}
