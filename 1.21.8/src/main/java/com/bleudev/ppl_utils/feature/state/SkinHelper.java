package com.bleudev.ppl_utils.feature.state;

import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class SkinHelper {
    public static PlayerSkin getPlayerSkin(PlayerSkin original, String playerName) {
        Optional<ResourceLocation> cape = StateHelper.getCustomCapeTexture(playerName);
        if (cape.isPresent()) {
            original = new PlayerSkin(original.texture(), original.textureUrl(), cape.get(), cape.get(), original.model(), original.secure());
        }
        return original;
    }
}
