package com.bleudev.ppl_utils.config;

import com.google.common.collect.Lists;
import eu.midnightdust.lib.config.MidnightConfig;
import eu.midnightdust.lib.config.MidnightConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
        PEPE_MONO("lobby/pepe_mono"),
        ARROW("lobby/arrow");

        private final ResourceLocation sprite;

        LobbyButtonStyle(String sprite) {
            this.sprite = getIdentifier(sprite);
        }

        public ResourceLocation getSprite() {
            return this.sprite;
        }
    }
    @Condition(requiredOption = "lobby_button_enabled", visibleButLocked = true)
    @Entry
    public static LobbyButtonStyle lobby_button_style = LobbyButtonStyle.PEPE;

    @Comment(centered = true)
    public static Comment restart_bar_comment;
    @Entry
    public static boolean render_restart_bar = true;
    @Condition(requiredOption = "render_restart_bar")
    @Entry
    public static BossBarColor restart_bar_color = BossBarColor.GREEN;
    @Condition(requiredOption = "render_restart_bar")
    @Entry
    public static BossBarOverlay restart_bar_style = BossBarOverlay.PROGRESS;
    @Condition(requiredOption = "render_restart_bar")
    @Entry
    public static boolean play_restart_bar_appearing_sound = true;

    @Comment(centered = true)
    public static Comment misc_comment;
    @Entry
    public static boolean render_error_screen = true;
    @Entry
    public static boolean render_ping_indicator = false;

    // Chat filter category
    private static final String CHAT_FILTER_CATEGORY = "chat_filter";
    @Comment(category = CHAT_FILTER_CATEGORY, centered = true)
    public static Comment join_leave_messages_rendering_comment;
    @Entry(category = CHAT_FILTER_CATEGORY)
    public static boolean do_join_leave_messages_rendering = true;
    @Condition(requiredOption = "do_join_leave_messages_rendering", requiredValue = "false", visibleButLocked = true)
    @Entry(category = CHAT_FILTER_CATEGORY)
    public static List<String> always_show_join_leave_messages_by = Lists.newArrayList();

    private static final String CHAT_FILTER_CONDITION = "enable_chat_filter";
    @Comment(category = CHAT_FILTER_CATEGORY, centered = true)
    public static Comment chat_filter_comment;
    @Entry(category = CHAT_FILTER_CATEGORY)
    public static boolean enable_chat_filter = false;
    @Condition(requiredOption = CHAT_FILTER_CONDITION, visibleButLocked = true)
    @Entry(category = CHAT_FILTER_CATEGORY)
    public static List<String> chat_filter_banwords = Lists.newArrayList();
    @Condition(requiredOption = CHAT_FILTER_CONDITION, visibleButLocked = true)
    @Entry(category = CHAT_FILTER_CATEGORY)
    public static boolean enable_chat_filter_whitelist = false;
    @Condition(requiredOption = "enable_chat_filter_whitelist", visibleButLocked = true)
    @Entry(category = CHAT_FILTER_CATEGORY)
    public static List<String> chat_filter_whitelist = Lists.newArrayList();

    // Diamond Counter category
    private static final String DIAMOND_COUNTER_CATEGORY = "diamond_counter";
    private static final String DIAMOND_COUNTER_CONDITION = "render_diamond_counter";
    @Entry(category = DIAMOND_COUNTER_CATEGORY)
    public static boolean render_diamond_counter = false;
    public enum CountFormat {
        INTEGER, STACKS;

        public String format(int count) {
            switch (this) {
                case INTEGER -> {
                    return String.valueOf(count);
                }
                case STACKS -> {
                    var parts = new ArrayList<String>();

                    if (count / 64 > 0) parts.add((count / 64) + " x 64");
                    if (count % 64 > 0) parts.add(String.valueOf(count % 64));

                    if (parts.isEmpty()) return "0";
                    return String.join(" + ", parts);
                }
                default -> throw new UnsupportedOperationException("Unknown count format!");
            }
        }
    }
    @Condition(requiredOption = DIAMOND_COUNTER_CONDITION, visibleButLocked = true)
    @Entry(category = DIAMOND_COUNTER_CATEGORY)
    public static CountFormat diamond_counter_count_format = CountFormat.INTEGER;

    @Comment(category = DIAMOND_COUNTER_CATEGORY, centered = true)
    public static Comment diamond_counter_count_in_comment;
    @Condition(requiredOption = DIAMOND_COUNTER_CONDITION, visibleButLocked = true)
    @Entry(category = DIAMOND_COUNTER_CATEGORY)
    public static boolean diamond_counter_count_in_containers = true;
    @Condition(requiredOption = DIAMOND_COUNTER_CONDITION, visibleButLocked = true)
    @Entry(category = DIAMOND_COUNTER_CATEGORY)
    public static boolean diamond_counter_count_in_ender_chest = true;

    @Condition(requiredOption = DIAMOND_COUNTER_CONDITION, visibleButLocked = true)
    @Comment(category = DIAMOND_COUNTER_CATEGORY, centered = true)
    public static Comment diamond_counter_count_colors_comment;
    @Condition(requiredOption = DIAMOND_COUNTER_CONDITION, visibleButLocked = true)
    @Entry(category = DIAMOND_COUNTER_CATEGORY, isColor = true)
    public static String diamond_counter_not_synced_color = "#ff0000";
    @Condition(requiredOption = DIAMOND_COUNTER_CONDITION, visibleButLocked = true)
    @Entry(category = DIAMOND_COUNTER_CATEGORY, isColor = true)
    public static String diamond_counter_color = "#ffffff";

    // States category
    private static final String STATES_CATEGORY = "states";
    private static final String STATES_CONDITION = "render_states";
    @Entry(category = STATES_CATEGORY)
    public static boolean render_states = true;

    @Comment(category = STATES_CATEGORY, centered = true)
    public static Comment render_state_features_comment;
    @Condition(requiredOption = STATES_CONDITION, visibleButLocked = true)
    @Entry(category = STATES_CATEGORY)
    public static boolean render_state_capes = true;
    @Condition(requiredOption = STATES_CONDITION, visibleButLocked = true)
    @Entry(category = STATES_CATEGORY)
    public static boolean render_state_tab_icons = true;

    @Comment(category = STATES_CATEGORY, centered = true)
    public static Comment render_states_comment;
    @Condition(requiredOption = STATES_CONDITION, visibleButLocked = true)
    @Entry(category = STATES_CATEGORY)
    public static boolean render_gdr = true;

    @Override
    public void writeChanges() {
        super.writeChanges();
        Minecraft.getInstance().gui.getChat().rescaleChat();
    }

    public static void initialize() {
        MidnightConfig.init(MOD_ID, PplUtilsConfig.class);
    }
    @Contract("_ -> new")
    public static @NotNull Screen getConfigScreen(Screen parent) {
        return new MidnightConfigScreen(parent, MOD_ID);
    }
}
