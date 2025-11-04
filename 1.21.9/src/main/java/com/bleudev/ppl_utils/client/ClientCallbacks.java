package com.bleudev.ppl_utils.client;

import com.bleudev.ppl_utils.util.ServerUtils;
import net.minecraft.client.MinecraftClient;

public class ClientCallbacks {
    public static void executeLobby() {
        var client = MinecraftClient.getInstance();
        if (ServerUtils.isClientOnServerSupportsLobbyCommand(client))
            ServerUtils.executeCommand(client, "lobby");
    }
}
