package com.bleudev.ppl_utils.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.nio.file.Path;

public class ConfigManager {
    private static YaclConfig CONFIG;

    public static void init(Path configDir) {
        Path configFile = configDir.resolve("mymod-config.json");
        CONFIG = new YaclConfig();
    }

    public static YaclConfig getConfig() {
        return CONFIG;
    }

    public static Screen buildConfigScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
            .title(Text.literal("My Mod Settings"))
            .category(
                ConfigCategory.createBuilder()
                    .name(Text.literal("General"))
                    .option(
                        Option.<Boolean>createBuilder()
                            .name(Text.literal("Enable Feature"))
                            .description(OptionDescription.createBuilder()
                                .text(Text.literal("Enable feature description"))
                                .image(Identifier.ofVanilla("textures/painting/alban.png"), 8, 8)
                                .build()
                            )
                            .binding(
                                getConfig().enableFeature,
                                () -> getConfig().enableFeature,
                                val -> getConfig().enableFeature = val
                            )
                            .controller(opt -> BooleanControllerBuilder.create(opt)
                                .yesNoFormatter()
                                .coloured(true))
                            .build()
                    )
                    .build()
                )
                .build()
                .generateScreen(parent);
    }
}


