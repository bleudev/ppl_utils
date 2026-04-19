package com.bleudev.ppl_utils.feature.state;

import com.bleudev.ppl_utils.config.PplUtilsConfig;
import com.bleudev.ppl_utils.util.RegistryUtils;
import net.minecraft.Optionull;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public enum States {
    GDR(
        List.of(
            "bleugame",
            "CrockoMD",
            "Milkich22397",
            "waflla_kek",
            "Keksik_play_",
            "T3chW0rk",
            "g0fers",
            "IvanJan",
            "msqoweew"
        ),
        "player/skin/cape/gdr",
        "tab/state/gdr",
        () -> PplUtilsConfig.render_gdr
    ),
    SOSI(
        List.of(
            "MorgenStrudel"
            // TODO: Another members
        ),
        null,
        "tab/state/sosi",
        () -> PplUtilsConfig.render_sosi
    );

    private final List<String> playerNames;
    private final @Nullable ResourceLocation cape;
    private final @Nullable ResourceLocation tabIcon;
    private final Supplier<Boolean> renderSupplier;
    States(List<String> playerNames, @Nullable ResourceLocation cape, @Nullable ResourceLocation tabIcon, Supplier<Boolean> renderSupplier) {
        this.playerNames = playerNames;
        this.cape = cape;
        this.tabIcon = tabIcon;
        this.renderSupplier = renderSupplier;
    }
    States(List<String> playerNames, @Nullable String cape, @Nullable String tabIcon, Supplier<Boolean> renderSupplier) {
        this(playerNames,
             Optionull.mapOrDefault(cape, RegistryUtils::getIdentifier, null),
             Optionull.mapOrDefault(tabIcon, RegistryUtils::getIdentifier, null),
             renderSupplier
        );
    }

    public List<String> getPlayerNames() {
        return playerNames;
    }
    public @Nullable ResourceLocation getCapeTexture() {
        return cape;
    }
    public @Nullable ResourceLocation getTabIcon() {
        return tabIcon;
    }
    public boolean shouldRender() {
        return renderSupplier.get();
    }
}
