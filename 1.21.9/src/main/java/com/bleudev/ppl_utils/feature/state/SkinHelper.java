package com.bleudev.ppl_utils.feature.state;

import net.minecraft.world.entity.player.PlayerSkin;

import java.util.Optional;

public class SkinHelper {
    public static PlayerSkin getPlayerSkin(PlayerSkin original, String playerName) {
        return original.with(PlayerSkin.Patch.create(
            Optional.empty(),
            StateHelper.getCustomCapeTexture(playerName),
            Optional.empty(),
            Optional.empty()
        ));
    }
}
