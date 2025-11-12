package com.bleudev.ppl_utils.util;

import com.bleudev.ppl_utils.PplUtilsConst;
import com.bleudev.ppl_utils.mixin.client.PlayerListHudAccessor;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        public enum PplWorld {
            LOBBY("Лобби");

            private final String tabName;
            PplWorld(String tabName) {
                this.tabName = tabName;
            }

            @Override
            public String toString() {
                return "pplWorld{"+this.tabName+"}";
            }

            private static Optional<PplWorld> from(String tabName) {
                PplUtilsConst.LOGGER.info("tried to find world {}", tabName);
                return Arrays.stream(PplWorld.values())
                    .filter(o -> o.tabName.equals(tabName))
                    .findFirst();
            }
        }

        @VisibleForTesting
        @Nullable
        public static ServerUtils.PepelandWorlds.PplWorld getCurrentWorld(@NotNull MinecraftClient client) {
            var header = ((PlayerListHudAccessor) client.inGameHud.getPlayerListHud()).ppl_utils$header();
            if (header == null) return null;
            PplUtilsConst.LOGGER.info("Header: {}", header);
            String worldPlayerListName = "Мир: ";
            for (String l : header.getString().split("\n")) {
                if (l.contains(worldPlayerListName)) {
                    return PplWorld.from(l
                        .replace(worldPlayerListName, "")
                        .replaceAll("[^A-Za-zА-Яа-я #0-9]", "")
                        .strip())
                        .orElse(null);
                }
            }
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
