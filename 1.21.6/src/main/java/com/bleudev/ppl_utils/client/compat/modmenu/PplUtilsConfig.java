package com.bleudev.ppl_utils.client.compat.modmenu;

import com.google.common.collect.Lists;
import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraft.client.MinecraftClient;

import java.util.List;

import static com.bleudev.ppl_utils.PplUtilsConst.MOD_ID;

public class PplUtilsConfig extends MidnightConfig {
    @Entry
    public static boolean lobby_button_enabled = true;
    @Condition(requiredOption = "lobby_button_enabled")
    @Entry
    public static boolean lobby_button_tooltip_enabled = true;
    @Entry
    public static boolean do_join_leave_messages_rendering = true;

    @Condition(requiredOption = "do_join_leave_messages_rendering", requiredValue = "false")
    @Entry
    public static List<String> always_show_join_leave_messages_by = Lists.newArrayList();

    @Override
    public void writeChanges(String modid) {
        super.writeChanges(modid);
        MinecraftClient.getInstance().inGameHud.getChatHud().reset();
    }

    public static void initialize() {
        MidnightConfig.init(MOD_ID, PplUtilsConfig.class);
    }
}
