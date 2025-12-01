package com.bleudev.ppl_utils.feature.chatfilter.command;

import com.bleudev.ppl_utils.feature.chatfilter.ChatFilter;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Command for managing chat filter settings.
 * /pplchat mode - show current mode
 * /pplchat mode blacklist - set blacklist mode
 * /pplchat mode whitelist - set whitelist mode
 * /pplchat enable - turn on filters
 * /pplchat disable - turn off filters
 * /pplchat add [word] - add word to filter
 * /pplchat remove [word] - remove word from filter
 * /pplchat list - show current words
 */
public class ChatFilterCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("pplchat")
            .then(ClientCommandManager.literal("mode")
                .executes(context -> {
                    ChatFilter.FilterMode mode = ChatFilter.getMode();
                    String modeName = mode == ChatFilter.FilterMode.WHITELIST ? "whitelist" : "blacklist";
                    context.getSource().sendFeedback(
                        Text.translatable("ppl_utils.chat_filter.mode.current", modeName)
                            .formatted(Formatting.GREEN)
                    );
                    return 1;
                })
                .then(ClientCommandManager.literal("blacklist")
                    .executes(context -> {
                        ChatFilter.setMode(ChatFilter.FilterMode.BLACKLIST);
                        context.getSource().sendFeedback(
                            Text.translatable("ppl_utils.chat_filter.mode.set", "blacklist")
                                .formatted(Formatting.GREEN)
                        );
                        return 1;
                    })
                )
                .then(ClientCommandManager.literal("whitelist")
                    .executes(context -> {
                        ChatFilter.setMode(ChatFilter.FilterMode.WHITELIST);
                        context.getSource().sendFeedback(
                            Text.translatable("ppl_utils.chat_filter.mode.set", "whitelist")
                                .formatted(Formatting.GREEN)
                        );
                        return 1;
                    })
                )
            )
            .then(ClientCommandManager.literal("enable")
                .executes(context -> {
                    ChatFilter.setEnabled(true);
                    context.getSource().sendFeedback(
                        Text.translatable("ppl_utils.chat_filter.enabled")
                            .formatted(Formatting.GREEN)
                    );
                    return 1;
                })
            )
            .then(ClientCommandManager.literal("disable")
                .executes(context -> {
                    ChatFilter.setEnabled(false);
                    context.getSource().sendFeedback(
                        Text.translatable("ppl_utils.chat_filter.disabled")
                            .formatted(Formatting.YELLOW)
                    );
                    return 1;
                })
            )
            .then(ClientCommandManager.literal("add")
                .then(ClientCommandManager.argument("word", StringArgumentType.greedyString())
                    .executes(context -> {
                        String word = StringArgumentType.getString(context, "word");
                        boolean wasEnabled = ChatFilter.isEnabled();
                        if (ChatFilter.addWord(word)) {
                            MutableText feedback = Text.translatable("ppl_utils.chat_filter.added", word)
                                .formatted(Formatting.GREEN).copy();
                            // If filter was auto-enabled, add note
                            if (!wasEnabled && ChatFilter.isEnabled()) {
                                feedback.append("\n")
                                    .append(Text.translatable("ppl_utils.chat_filter.auto_enabled")
                                        .formatted(Formatting.YELLOW));
                            }
                            context.getSource().sendFeedback(feedback);
                        } else {
                            context.getSource().sendError(
                                Text.translatable("ppl_utils.chat_filter.add.failed", word)
                            );
                        }
                        return 1;
                    })
                )
            )
            .then(ClientCommandManager.literal("remove")
                .then(ClientCommandManager.argument("word", StringArgumentType.greedyString())
                    .executes(context -> {
                        String word = StringArgumentType.getString(context, "word");
                        if (ChatFilter.removeWord(word)) {
                            context.getSource().sendFeedback(
                                Text.translatable("ppl_utils.chat_filter.removed", word)
                                    .formatted(Formatting.GREEN)
                            );
                        } else {
                            context.getSource().sendError(
                                Text.translatable("ppl_utils.chat_filter.remove.failed", word)
                            );
                        }
                        return 1;
                    })
                )
            )
            .then(ClientCommandManager.literal("list")
                .executes(context -> {
                    context.getSource().sendFeedback(
                        Text.translatable("ppl_utils.chat_filter.list.open_config")
                            .formatted(Formatting.YELLOW)
                    );
                    return 1;
                })
            )
        );
    }
}

