package com.bleudev.ppl_utils.util.helper;

import com.bleudev.ppl_utils.config.PplUtilsConfig;

import java.util.regex.Pattern;

public class ChatFilterHelper {
    private static final String PSTR_NICKNAME = "[^ \\[\\]+-]+";
    private static final Pattern NICKNAME = Pattern.compile(PSTR_NICKNAME);
    private static final Pattern CHAT_HEADS_HEAD = Pattern.compile("\\[%1$2s head]".formatted(PSTR_NICKNAME));
    private static final Pattern CHAT_MESSAGE_AUTHOR = Pattern.compile("<%1$2s> *".formatted(PSTR_NICKNAME));

    private static final Pattern JOIN = Pattern.compile("\\[\\+] *%1$2s".formatted(PSTR_NICKNAME));
    private static final Pattern LEAVE = Pattern.compile("\\[-] *%1$2s".formatted(PSTR_NICKNAME));

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

    public static boolean shouldRenderChatMessage(String message) {
        if (!PplUtilsConfig.do_join_leave_messages_rendering && (JoinLeaveMessages.isJoinMessage(message) || JoinLeaveMessages.isLeaveMessage(message)))
            return JoinLeaveMessages.ignoreJoinLeaveMessage(message);
        // In the future, there will be chat filter
        return true;
    }
}
