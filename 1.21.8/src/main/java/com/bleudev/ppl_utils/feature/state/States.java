package com.bleudev.ppl_utils.feature.state;

import com.bleudev.ppl_utils.config.PplUtilsConfig;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

import static com.bleudev.ppl_utils.util.RegistryUtils.getIdentifier;

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
        "textures/player/skin/cape/gdr.png",
        "藶",
        () -> PplUtilsConfig.render_gdr
    );

    private final List<String> playerNames;
    private final @Nullable ResourceLocation cape;
    private final @Nullable String tabIcon;
    private final Supplier<Boolean> renderSupplier;
    States(List<String> playerNames, @Nullable ResourceLocation cape, @Nullable String tabIcon, Supplier<Boolean> renderSupplier) {
        this.playerNames = playerNames;
        this.cape = cape;
        this.tabIcon = tabIcon;
        this.renderSupplier = renderSupplier;
    }
    States(List<String> playerNames, String cape, String tabIcon, Supplier<Boolean> renderSupplier) {
        this(playerNames, getIdentifier(cape), tabIcon, renderSupplier);
    }

    public List<String> getPlayerNames() {
        return playerNames;
    }
    public @Nullable ResourceLocation getCapeTexture() {
        return cape;
    }
    public @Nullable String getTabIcon() {
        return tabIcon;
    }
    public boolean shouldRender() {
        return renderSupplier.get();
    }
}
