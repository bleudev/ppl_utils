package com.bleudev.ppl_utils.client.compat.modmenu;

import eu.midnightdust.lib.config.MidnightConfig;

import static com.bleudev.ppl_utils.client.compat.modmenu.PplUtilsConst.MOD_ID;

public class PplUtilsConfig extends MidnightConfig {
    @Entry
    public static boolean lobby_button_enabled = true;

    public static void initialize() {
        MidnightConfig.init(MOD_ID, PplUtilsConfig.class);
    }
}
