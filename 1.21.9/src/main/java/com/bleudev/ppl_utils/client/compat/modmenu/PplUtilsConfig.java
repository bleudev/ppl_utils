package com.bleudev.ppl_utils.client.compat.modmenu;

import com.google.common.collect.Lists;
import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.boss.BossBar.Color;
import net.minecraft.entity.boss.BossBar.Style;
import net.minecraft.util.Identifier;

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
        PEPE_MONO("lobby/pepe_mono");

        private final Identifier sprite;

        LobbyButtonStyle(String sprite) {
            this.sprite = getIdentifier(sprite);
        }

        public Identifier getSprite() {
            return this.sprite;
        }
    }
    @Condition(requiredOption = "lobby_button_enabled", visibleButLocked = true)
    @Entry
    public static LobbyButtonStyle lobby_button_style = LobbyButtonStyle.PEPE;

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

    @Override
    public void writeChanges() {
        super.writeChanges();
        MinecraftClient.getInstance().inGameHud.getChatHud().reset();
    }

    public static void initialize() {
        MidnightConfig.init(MOD_ID, PplUtilsConfig.class);
    }
}
