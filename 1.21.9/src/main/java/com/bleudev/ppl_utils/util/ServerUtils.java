package com.bleudev.ppl_utils.util;

import com.bleudev.ppl_utils.mixin.client.accessor.PlayerTabOverlayAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import static com.bleudev.ppl_utils.PplUtilsConst.*;

public class ServerUtils {
    public static boolean isClientOn(@NotNull Minecraft client, String serverIp) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) return true;
        final var server = client.getCurrentServer();
        if (server == null) return false;
        return Objects.equals(server.ip, serverIp);
    }
    public static boolean isClientOn(@NotNull Minecraft client, @NotNull Collection<String> serverIps) {
        return serverIps.stream().anyMatch(n -> isClientOn(client, n));
    }

    public static boolean isClientOnPepeland(@NotNull Minecraft client) {
        return isClientOn(client, PEPELAND_IPS);
    }
    public static boolean isClientOnPepeland() {
        return isClientOnPepeland(Minecraft.getInstance());
    }

    public static boolean isLobbyCommandWorking(@NotNull Minecraft client) {
        if (!isClientOn(client, SUPPORTS_LOBBY_COMMAND_IPS)) return false;
        if (isClientOnPepeland(client)) return !PepelandWorlds.isInLobby(client);
        return true;
    }
    public static boolean isGSitWorking(@NotNull Minecraft client) {
        return isClientOn(client, HAS_GSIT_IPS);
    }
    public static boolean isGlobalChatWorking(@NotNull Minecraft client) {
        return isClientOn(client, SUPPORTS_GLOBAL_CHAT_IPS);
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
        private static PplWorld getCurrentWorld(@NotNull Minecraft client) {
            var header = ((PlayerTabOverlayAccessor) client.gui.getTabList()).ppl_utils$header();
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

        public static boolean isInLobby(@NotNull Minecraft client) {
            return getCurrentWorld(client) == PplWorld.LOBBY;
        }
    }

    public static void executeCommand(@NotNull Minecraft client, @NotNull String command) {
        Objects.requireNonNull(client.getConnection()).sendCommand(command);
    }

    public static int getPing(@NotNull Minecraft client) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) return 666; // Debug value
        if (client.player == null || client.level == null || !isClientOnPepeland(client)) return -1;
        Scoreboard scoreboard = client.level.getScoreboard();
        var obj = scoreboard.getDisplayObjective(DisplaySlot.LIST);
        if (obj == null) return -1;
        var score = scoreboard.getPlayerScoreInfo(client.player, obj);
        if (score == null) return -1;
        return score.value();
    }
}
