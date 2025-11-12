package com.bleudev.ppl_utils.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static com.bleudev.ppl_utils.util.LangUtils.unmodifiableUnion;

public class ServerUtils {
    public static final List<String> PEPELAND_IPS = List.of(
        "play.pepeland.net",
        "issues.pepeland.net",
        "issues2.pepeland.net"
    );

    private static final List<String> SUPPORTS_LOBBY_COMMAND_IPS = unmodifiableUnion(
        PEPELAND_IPS, List.of(
            // Maybe in future there will be more servers
        )
    );

    public static boolean isClientOn(@NotNull MinecraftClient client, String serverIp) {
        final var server = client.getCurrentServerEntry();
        if (server == null) return false;
        return Objects.equals(server.address, serverIp);
    }

    public static boolean isClientOnServerSupportsLobbyCommand(@NotNull MinecraftClient client) {
        return SUPPORTS_LOBBY_COMMAND_IPS.stream().anyMatch(n -> isClientOn(client, n));
    }

    public static boolean isLobbyCommandWorking(@NotNull MinecraftClient client) {
        if (!isClientOnServerSupportsLobbyCommand(client)) return false;

        if (isClientOnPepeland(client)) return !PepelandWorlds.isInLobby(client);
        return true;
    }

    public static boolean isClientOnPepeland(@NotNull MinecraftClient client) {
        return PEPELAND_IPS.stream().anyMatch(n -> isClientOn(client, n));
    }

    public static class PepelandWorlds {
        private static boolean isIn(@NotNull MinecraftClient client, Identifier dimension) {
            if (client.world != null)
                return client.world.getRegistryKey().getValue().equals(dimension);
            return false;
        }

        // TODO: Absolutely new system with player list hud
        public static boolean isInLobby(@NotNull MinecraftClient client) {
            return isIn(client, Identifier.ofVanilla("lobby")); // Doesn't work, sorry
        }
    }

    public static void executeCommand(@NotNull MinecraftClient client, @NotNull String command) {
        Objects.requireNonNull(client.getNetworkHandler()).sendChatCommand(command);
    }
    public static void executeCommand(String command) {
        executeCommand(MinecraftClient.getInstance(), command);
    }
}
