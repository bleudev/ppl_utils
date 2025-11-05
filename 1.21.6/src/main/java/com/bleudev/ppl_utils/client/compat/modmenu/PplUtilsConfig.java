package com.bleudev.ppl_utils.client.compat.modmenu;

import eu.midnightdust.lib.config.MidnightConfig;

import static com.bleudev.ppl_utils.PplUtilsConst.MOD_ID;

public class PplUtilsConfig extends MidnightConfig {
    @Entry
    public static boolean lobby_button_enabled = true;
    @Condition(requiredOption = "lobby_button_enabled")
    @Entry
    public static boolean lobby_button_tooltip_enabled = true;
    @Entry
    public static boolean do_join_leave_messages_rendering = true;

    public static void initialize() {
        MidnightConfig.init(MOD_ID, PplUtilsConfig.class);
    }
}
