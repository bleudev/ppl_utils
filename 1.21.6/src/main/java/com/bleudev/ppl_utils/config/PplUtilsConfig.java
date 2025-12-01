package com.bleudev.ppl_utils.config;

import com.google.common.collect.Lists;
import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.boss.BossBar.Color;
import net.minecraft.entity.boss.BossBar.Style;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.bleudev.ppl_utils.PplUtilsConst.LOGGER;
import static com.bleudev.ppl_utils.PplUtilsConst.MOD_ID;
import static com.bleudev.ppl_utils.util.RegistryUtils.getIdentifier;

public class PplUtilsConfig extends MidnightConfig {
    @Comment(centered = true)
    public static Comment lobby_button_comment;
    @Entry
    public static boolean lobby_button_enabled = true;
    @Condition(requiredOption = "lobby_button_enabled", visibleButLocked = true)
    @Entry
    public static boolean lobby_button_tooltip_enabled = true;
    public enum LobbyButtonStyle {
        PEPE("lobby/pepe"),
        PEPE_MONO("lobby/pepe_mono");

        private final Identifier sprite;

        LobbyButtonStyle(String sprite) {
            this.sprite = getIdentifier(sprite);
        }

        public Identifier getSprite() {
            return this.sprite;
        }
    }
    
    public enum LobbyButtonPosition {
        FIRST,
        SECOND,
        THIRD
    }
    @Condition(requiredOption = "lobby_button_enabled", visibleButLocked = true)
    @Entry
    public static LobbyButtonStyle lobby_button_style = LobbyButtonStyle.PEPE;
    @Condition(requiredOption = "lobby_button_enabled", visibleButLocked = true)
    @Entry
    public static LobbyButtonPosition lobby_button_position = LobbyButtonPosition.FIRST;
    @Condition(requiredOption = "lobby_button_enabled", visibleButLocked = true)
    @Entry
    public static boolean lobby_button_ignore_server_check = false;

    @Comment(centered = true)
    public static Comment join_leave_messages_rendering_comment;
    @Entry
    public static boolean do_join_leave_messages_rendering = true;
    @Condition(requiredOption = "do_join_leave_messages_rendering", requiredValue = "false", visibleButLocked = true)
    @Entry
    public static List<String> always_show_join_leave_messages_by = Lists.newArrayList();

    @Comment(centered = true)
    public static Comment restart_bar_comment;
    @Entry
    public static boolean render_restart_bar = true;
    @Condition(requiredOption = "render_restart_bar")
    @Entry
    public static Color restart_bar_color = Color.GREEN;
    @Condition(requiredOption = "render_restart_bar")
    @Entry
    public static Style restart_bar_style = Style.PROGRESS;
    @Condition(requiredOption = "render_restart_bar", visibleButLocked = true)
    @Entry
    public static boolean restart_bar_ignore_server_check = false;
    @Entry
    public static boolean restart_bar_sound_start = true;
    @Entry
    public static boolean restart_bar_sound_interval = true;
    @Entry
    public static boolean restart_bar_sound_end = true;

    @Comment(centered = true)
    public static Comment inventory_slot_count_comment;
    @Entry
    public static boolean show_inventory_slot_count = true;

    @Comment(centered = true)
    public static Comment diamond_counter_comment;
    @Entry
    public static boolean show_diamond_counter = true;

    @Comment(centered = true)
    public static Comment chat_filter_comment;
    @Entry
    public static boolean chat_filter_enabled = false;
    @Condition(requiredOption = "chat_filter_enabled", visibleButLocked = true)
    @Entry
    public static ChatFilterMode chat_filter_mode = ChatFilterMode.BLACKLIST;
    @Condition(requiredOption = "chat_filter_enabled", visibleButLocked = true)
    @Entry
    public static List<String> chat_filter_words = Lists.newArrayList();

    public enum ChatFilterMode {
        WHITELIST,
        BLACKLIST
    }

    @Comment(centered = true)
    public static Comment always_with_me_comment;
    @Entry
    public static boolean always_with_me_enabled = false;
    @Condition(requiredOption = "always_with_me_enabled", visibleButLocked = true)
    @Entry
    public static String always_with_me_mappings = "";

    @Comment(centered = true)
    public static Comment executable_queue_comment;
    @Entry
    public static String executable_queue_commands = "";

    @Override
    public void writeChanges(String modid) {
        super.writeChanges(modid);
        MinecraftClient.getInstance().inGameHud.getChatHud().reset();
    }

    public static void initialize() {
        try {
            // Initialize config
            MidnightConfig.init(MOD_ID, PplUtilsConfig.class);
        } catch (Exception e) {
            // If initialization fails (e.g., due to removed COMPASS enum value),
            // reset to default and try again
            lobby_button_style = LobbyButtonStyle.PEPE;
            try {
                MidnightConfig.init(MOD_ID, PplUtilsConfig.class);
            } catch (Exception e2) {
                // If it still fails, log and use defaults
                com.bleudev.ppl_utils.PplUtilsConst.LOGGER.error("Failed to load config, using defaults", e2);
            }
        }
        
        // Validate config
        validateConfig();
    }
    
    /**
     * Validates all config values and resets invalid ones to defaults.
     */
    private static void validateConfig() {
        // Validate lobby_button_style
        if (lobby_button_style == null) {
            LOGGER.warn("Invalid lobby_button_style, resetting to default");
            lobby_button_style = LobbyButtonStyle.PEPE;
        }
        
        // Validate lobby_button_position
        if (lobby_button_position == null) {
            LOGGER.warn("Invalid lobby_button_position, resetting to default");
            lobby_button_position = LobbyButtonPosition.FIRST;
        }
        
        // Validate restart_bar_color
        if (restart_bar_color == null) {
            LOGGER.warn("Invalid restart_bar_color, resetting to default");
            restart_bar_color = Color.GREEN;
        }
        
        // Validate restart_bar_style
        if (restart_bar_style == null) {
            LOGGER.warn("Invalid restart_bar_style, resetting to default");
            restart_bar_style = Style.PROGRESS;
        }
        
        // Validate always_show_join_leave_messages_by list
        if (always_show_join_leave_messages_by == null) {
            LOGGER.warn("Invalid always_show_join_leave_messages_by, resetting to empty list");
            always_show_join_leave_messages_by = Lists.newArrayList();
        }
        
        // Validate chat_filter_mode
        if (chat_filter_mode == null) {
            LOGGER.warn("Invalid chat_filter_mode, resetting to default");
            chat_filter_mode = ChatFilterMode.BLACKLIST;
        }
        
        // Validate chat_filter_words list
        if (chat_filter_words == null) {
            LOGGER.warn("Invalid chat_filter_words, resetting to empty list");
            chat_filter_words = Lists.newArrayList();
        }
    }
    @Contract("_ -> new")
    public static @NotNull Screen getConfigScreen(Screen parent) {
        return MidnightConfig.getScreen(parent, MOD_ID);
    }
    
    /**
     * Saves the current config to disk.
     * This is a static helper method for saving config from other classes.
     */
    public static void saveConfig() {
        try {
            // Create a temporary instance to call writeChanges
            PplUtilsConfig instance = new PplUtilsConfig();
            instance.writeChanges(MOD_ID);
        } catch (Exception e) {
            LOGGER.error("Failed to save config", e);
        }
    }
}
