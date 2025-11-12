package com.bleudev.ppl_utils.util;

import com.bleudev.ppl_utils.mixin.client.PlayerListHudAccessor;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import static com.bleudev.ppl_utils.PplUtilsConst.PEPELAND_IPS;
import static com.bleudev.ppl_utils.PplUtilsConst.SUPPORTS_LOBBY_COMMAND_IPS;

public class ServerUtils {
    public static boolean isClientOn(@NotNull MinecraftClient client, String serverIp) {
        final var server = client.getCurrentServerEntry();
        if (server == null) return false;
        return Objects.equals(server.address, serverIp);
    }
    public static boolean isClientOn(@NotNull MinecraftClient client, @NotNull Collection<String> serverIps) {
        return serverIps.stream().anyMatch(n -> isClientOn(client, n));
    }

    public static boolean isLobbyCommandWorking(@NotNull MinecraftClient client) {
        if (!isClientOn(client, SUPPORTS_LOBBY_COMMAND_IPS)) return false;
        if (isClientOn(client, PEPELAND_IPS)) return !PepelandWorlds.isInLobby(client);
        return true;
    }

    public static class PepelandWorlds {
        private enum PplWorld {
            LOBBY("Лобби");

            private final String tabName;
            PplWorld(String tabName) {
                this.tabName = tabName;
            }

            @NotNull
            private static Optional<PplWorld> from(String tabName) {
                return Arrays.stream(PplWorld.values())
                        .filter(o -> o.tabName.equals(tabName))
                        .findFirst();
            }
        }

        @Nullable
        private static PplWorld getCurrentWorld(@NotNull MinecraftClient client) {
            var header = ((PlayerListHudAccessor) client.inGameHud.getPlayerListHud()).ppl_utils$header();
            if (header == null) return null;
            var worldPlayerListName = "Мир: ";
            for (String l : header.getString().split("\n"))
                if (l.contains(worldPlayerListName))
                    return PplWorld.from(l
                            .replace(worldPlayerListName, "")
                            .replaceAll("[^A-Za-zА-Яа-я #0-9]", "")
                            .strip()).orElse(null);
            return null;
        }

        public static boolean isInLobby(@NotNull MinecraftClient client) {
            return getCurrentWorld(client) == PplWorld.LOBBY;
        }
    }

    public static void executeCommand(@NotNull MinecraftClient client, @NotNull String command) {
        Objects.requireNonNull(client.getNetworkHandler()).sendChatCommand(command);
    }
    public static void executeCommand(String command) {
        executeCommand(MinecraftClient.getInstance(), command);
    }
}
