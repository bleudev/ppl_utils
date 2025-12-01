package com.bleudev.ppl_utils;

import com.bleudev.ppl_utils.config.PplUtilsConfig;
import com.bleudev.ppl_utils.util.helper.GlobalChatHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

import static com.bleudev.ppl_utils.util.ServerCache.isGlobalChatWorking;
import static com.bleudev.ppl_utils.util.ServerCache.isLobbyCommandWorking;
import static com.bleudev.ppl_utils.util.ServerUtils.executeCommand;

public class ClientCallbacks {
    // Pre-compiled regex patterns for better performance
    private static final String NICKNAME_PATTERN = "[^ \\[\\]]+";
    private static final Pattern JOIN_WITH_HEAD = Pattern.compile(
        String.format("(<%1$s>)* *\\[\\+] *\\[%1$s head] *", NICKNAME_PATTERN));
    private static final Pattern LEAVE_WITH_HEAD = Pattern.compile(
        String.format("(<%1$s>)* *\\[-] *\\[%1$s head] *", NICKNAME_PATTERN));
    private static final Pattern JOIN_SIMPLE = Pattern.compile(
        String.format("(<%1$s>)* *\\[\\+] *", NICKNAME_PATTERN));
    private static final Pattern LEAVE_SIMPLE = Pattern.compile(
        String.format("(<%1$s>)* *\\[-] *", NICKNAME_PATTERN));
    private static final Pattern NICKNAME_MATCH = Pattern.compile(NICKNAME_PATTERN);
    
    public static void executeLobby(@NotNull MinecraftClient client) {
        if (isLobbyCommandWorking(client))
            executeCommand(client, "lobby");
    }

    @Nullable
    private static String extractPlayer(@NotNull String content) {
        String e = JOIN_WITH_HEAD.matcher(content).replaceAll("");
        e = LEAVE_WITH_HEAD.matcher(e).replaceAll("");
        if (content.equals(e)) {
            e = JOIN_SIMPLE.matcher(e).replaceAll("");
            e = LEAVE_SIMPLE.matcher(e).replaceAll("");
        }
        if (NICKNAME_MATCH.matcher(e).matches())
            return e;
        return null;
    }

    public static boolean shouldRenderChatMessage(ChatHudLine message) {
        // Always show restart messages (they are important system messages)
        if (com.bleudev.ppl_utils.util.helper.RestartMessageParser.isRestartMessage(message.content())) {
            return true;
        }
        
        // First check chat filter - this should always be checked
        if (!com.bleudev.ppl_utils.feature.chatfilter.ChatFilter.shouldDisplayMessage(message.content())) {
            return false;
        }
        
        // Then check join/leave messages (only applies if filter allows the message)
        if (PplUtilsConfig.do_join_leave_messages_rendering) {
            return true;
        }

        // Check if this is a join/leave message for specific players
        String user;
        if ((user = extractPlayer(message.content().getString())) != null) {
            return PplUtilsConfig
                    .always_show_join_leave_messages_by.stream()
                    .map(String::toLowerCase).toList()
                    .contains(user.toLowerCase());
        }
        return true;
    }

    public static boolean shouldRenderLobbyButton(@NotNull MinecraftClient client) {
        if (!PplUtilsConfig.lobby_button_enabled) return false;
        if (PplUtilsConfig.lobby_button_ignore_server_check) return true;
        return isLobbyCommandWorking(client);
    }

    public static boolean shouldSendMessagesToGlobalChat(@NotNull MinecraftClient client) {
        return isGlobalChatWorking(client) && GlobalChatHelper.INSTANCE.isEnabled();
    }
}
