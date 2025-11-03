package com.bleudev.ppl_utils.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Nullables;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class ServerUtils {
    public static final List<String> SUPPORTS_LOBBY_COMMAND_IPS = List.of(
        "play.pepeland.net",
        "issues.pepeland.net",
        "issues2.pepeland.net"
    );

    public static boolean isClientOnServerSupportsLobbyCommand(MinecraftClient client) {
        final var server = client.getCurrentServerEntry();
        if (server == null) return false;
        return Nullables.mapOrElse(server.address, SUPPORTS_LOBBY_COMMAND_IPS::contains, false);
    }
    public static boolean isClientOnServerSupportsLobbyCommand() {
        return isClientOnServerSupportsLobbyCommand(MinecraftClient.getInstance());
    }

    public static void executeCommand(@NotNull MinecraftClient client, @NotNull String command) {
        Objects.requireNonNull(client.getNetworkHandler()).sendChatCommand(command);
    }
    public static void executeCommand(String command) {
        executeCommand(MinecraftClient.getInstance(), command);
    }
}
