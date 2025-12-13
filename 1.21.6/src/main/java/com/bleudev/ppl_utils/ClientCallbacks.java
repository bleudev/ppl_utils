package com.bleudev.ppl_utils;

import com.bleudev.ppl_utils.config.PplUtilsConfig;
import com.bleudev.ppl_utils.util.helper.ErrorScreenHelper;
import com.bleudev.ppl_utils.util.helper.GlobalChatHelper;
import com.bleudev.ppl_utils.util.helper.RestartHelper;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;

import static com.bleudev.ppl_utils.PplUtilsConst.LOGGER;
import static com.bleudev.ppl_utils.util.LangUtils.anySubstringMatches;
import static com.bleudev.ppl_utils.util.ServerUtils.*;

public class ClientCallbacks {
    public static void executeLobby(@NotNull MinecraftClient client) {
        if (isLobbyCommandWorking(client)) executeCommand(client, "lobby");
    }
    public static void executeSit(@NotNull MinecraftClient client) {
        if (isGSitWorking(client)) executeCommand(client, "sit");
    }
    public static void executeLay(@NotNull MinecraftClient client) {
        if (isGSitWorking(client)) executeCommand(client, "lay");
    }

    public static boolean shouldRenderLobbyButton(@NotNull MinecraftClient client) {
        return PplUtilsConfig.lobby_button_enabled && isLobbyCommandWorking(client);
    }

    public static boolean shouldSendMessagesToGlobalChat(@NotNull MinecraftClient client) {
        return isGlobalChatWorking(client) && GlobalChatHelper.INSTANCE.isEnabled();
    }

    public static void tryStartWithMessage(@NotNull String message) {
        if (!isClientOnPepeland()) return;
        message = message
                .replaceAll("<[^< >]+> *", "");
        if (tryStartRestartBar(message)) return;
        if (tryStartErrorScreen(message)) return;
    }

    private static boolean tryStartRestartBar(@NotNull String restartMessage) {
        var content = restartMessage
                .replaceAll("\\[PPL[0-9]*]: ", ""); // Ignore Pepeland prefixes
        try {
            if (content.contains("Рестарт через")) {
                var time = Long.parseLong(content.replaceAll("[^0-9]", ""));
                RestartHelper.runRestartBar(time * (anySubstringMatches(content, "минут[а-я]*") ? 60_000 : 1_000));
                return true;
            }
        } catch (NumberFormatException ignored) {
            LOGGER.error("Unexpected number format exception while parsing \"{}\" string. Please report about it.", content);
        }
        return false;
    }

    private static boolean tryStartErrorScreen(@NotNull String errorMessage) {
        if (errorMessage.startsWith("Вы еще не можете зайти на сервер")) {
            ErrorScreenHelper.INSTANCE.cause(ErrorScreenHelper.ErrorScreenReason.WORLD_JOIN);
            return true;
        }
        return false;
    }
}
