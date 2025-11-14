package com.bleudev.ppl_utils.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.bleudev.ppl_utils.config.YaclConfig.getConfig;
import static com.bleudev.ppl_utils.config.YaclConfig.getDefaults;

public class ConfigManager {
    private static <T> Binding<T> simpleBinding(
        Function<YaclConfig, T> getter,
        BiConsumer<YaclConfig, T> setter
    ) {
        return Binding.generic(
            getter.apply(getDefaults()), () -> getter.apply(getConfig()),
            v -> { setter.accept(getConfig(), v); YaclConfig.HANDLER.save(); }
        );
    }

    public static Screen buildConfigScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
            .title(Text.literal("My Mod Settings"))
            .category(ConfigCategory.createBuilder()
                .name(Text.literal("General"))
                .option(Option.<Boolean>createBuilder()
                    .name(Text.literal("Enable Feature"))
                    .description(OptionDescription.createBuilder()
                        .text(Text.literal("Enable feature description"))
                        .image(Identifier.ofVanilla("textures/painting/alban.png"), 8, 8)
                        .build()
                    )
                    .binding(simpleBinding(
                        c -> c.enableFeature,
                        (c, v) -> c.enableFeature = v
                    ))
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


