package com.bleudev.ppl_utils.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.bleudev.ppl_utils.config.PepelandUtilsYaclConfig.getConfig;
import static com.bleudev.ppl_utils.config.PepelandUtilsYaclConfig.getDefaults;

public class ConfigManager {
    private static <T> Binding<T> simpleBinding(
        Function<PepelandUtilsYaclConfig, T> getter,
        BiConsumer<PepelandUtilsYaclConfig, T> setter
    ) {
        return Binding.generic(
            getter.apply(getDefaults()), () -> getter.apply(getConfig()),
            v -> { setter.accept(getConfig(), v); PepelandUtilsYaclConfig.HANDLER.save(); }
        );
    }

    public static Screen buildConfigScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
            .title(Text.literal("My Mod Settings"))
            .category(ConfigCategory.createBuilder()
                .name(Text.literal("General"))
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("ppl_utils.yacl.group.lobby_button"))
                    .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("ppl_utils.yacl.lobby_button_enabled"))
                        .binding(simpleBinding(
                            c -> c.lobby_button_enabled,
                            (c, v) -> c.lobby_button_enabled = v
                        ))
                        .controller(opt -> BooleanControllerBuilder.create(opt)
                            .yesNoFormatter()
                            .coloured(true))
                        .build())
                    .build())

                .build()
            )
            .build()
            .generateScreen(parent);
    }
}


