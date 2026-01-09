package com.bleudev.ppl_utils.util.helper;

import com.bleudev.ppl_utils.config.PplUtilsConfig;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

import static com.bleudev.ppl_utils.util.ServerUtils.isClientOnPepeland;

public class ChatFilterHelper {
    private static final String PSTR_NICKNAME = "[^ \\[\\]+-]+";
    private static final Pattern NICKNAME = Pattern.compile(PSTR_NICKNAME);
    private static final Pattern CHAT_HEADS_HEAD = Pattern.compile("\\[%1$2s head]".formatted(PSTR_NICKNAME));
    private static final Pattern CHAT_MESSAGE_AUTHOR = Pattern.compile("<%1$2s> *".formatted(PSTR_NICKNAME));

    private static final Pattern JOIN = Pattern.compile("\\[\\+] *%1$2s".formatted(PSTR_NICKNAME));
    private static final Pattern LEAVE = Pattern.compile("\\[-] *%1$2s".formatted(PSTR_NICKNAME));

    private static final Pattern LOWER_WORD_PATTERN = Pattern.compile("[a-zа-я]+");

    private static class JoinLeaveMessages {
        private static String formatJoinLeaveMessage(String message) {
            message = CHAT_MESSAGE_AUTHOR.matcher(message).replaceAll("");
            message = CHAT_HEADS_HEAD.matcher(message).replaceAll("");
            return message;
        }

        private static boolean isJoinMessage(String message) {
            return JOIN.asPredicate().test(formatJoinLeaveMessage(message));
        }
        private static boolean isLeaveMessage(String message) {
            return LEAVE.asPredicate().test(formatJoinLeaveMessage(message));
        }

        private static boolean ignoreJoinLeaveMessage(String message) {
            String player = null;
            var matcher = NICKNAME.matcher(formatJoinLeaveMessage(message)
                .replace("[+]", "")
                .replace("[-]", ""));
            while (matcher.find() && player == null) player = matcher.group();
            if (player == null) return true;
            return PplUtilsConfig.always_show_join_leave_messages_by.contains(player);
        }
    }

    private static class ChatFilter {
        private static boolean isAuthorInWhitelist(@NotNull String message) {
            message = message.toLowerCase(Locale.ROOT);
            if (message.startsWith("[мир: ")) message = message.replaceFirst("\\[мир: .+]", "");
            if (message.startsWith("[g] ")) message = message.substring(4);
            var m = NICKNAME.matcher(message);

            String author = null;
            while (m.find() && author == null) author = m.group().replaceAll("[<>:]", "");
            if (author == null) return true;

            MinecraftClient client = MinecraftClient.getInstance();
            ArrayList<String> wl = new ArrayList<>();
            if (client.player != null) wl.add(client.player.getGameProfile().getName());
            if (PplUtilsConfig.enable_chat_filter_whitelist) wl.addAll(PplUtilsConfig.chat_filter_whitelist);
            for (String pl: wl) if (pl.toLowerCase(Locale.ROOT).equals(author)) return true;
            return false;
        }

        private static boolean hasBanwords(@NotNull String message) {
            var m = LOWER_WORD_PATTERN.matcher(message.toLowerCase(Locale.ROOT));
            while (m.find()) {
                String word = m.group();
                if (word == null) continue;
                for (String banword: PplUtilsConfig.chat_filter_banwords)
                    if (banword.toLowerCase(Locale.ROOT).equals(word)) return true;
            }
            return false;
        }
    }

    public static boolean shouldRenderChatMessage(String message) {
        if (!isClientOnPepeland()) return true;
        if (!PplUtilsConfig.do_join_leave_messages_rendering && (JoinLeaveMessages.isJoinMessage(message) || JoinLeaveMessages.isLeaveMessage(message)))
            return JoinLeaveMessages.ignoreJoinLeaveMessage(message);
        if (PplUtilsConfig.enable_chat_filter && ChatFilter.hasBanwords(message))
            return ChatFilter.isAuthorInWhitelist(message);
        return true;
    }
}
