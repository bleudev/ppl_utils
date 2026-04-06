package com.bleudev.ppl_utils.feature.state;

import com.bleudev.ppl_utils.config.PplUtilsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

import static com.bleudev.ppl_utils.PplUtilsConst.resourceLocation;

public enum States {
    GDR(
        List.of(
            Minecraft.getInstance().getGameProfile().name()
        ),
        "player/skin/cape/gdr",
        () -> PplUtilsConfig.render_gdr
    );

    private final List<String> playerNames;
    private final @Nullable ResourceLocation cape;
    private final Supplier<Boolean> renderSupplier;
    States(List<String> playerNames, @Nullable ResourceLocation cape, Supplier<Boolean> renderSupplier) {
        this.playerNames = playerNames;
        this.cape = cape;
        this.renderSupplier = renderSupplier;
    }
    States(List<String> playerNames, String cape, Supplier<Boolean> renderSupplier) {
        this(playerNames, resourceLocation(cape), renderSupplier);
    }

    public List<String> getPlayerNames() {
        return playerNames;
    }
    public @Nullable ResourceLocation getCapeTexture() {
        return cape;
    }
    public boolean shouldRender() {
        return renderSupplier.get();
    }
}
