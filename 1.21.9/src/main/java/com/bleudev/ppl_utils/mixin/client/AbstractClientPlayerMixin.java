package com.bleudev.ppl_utils.mixin.client;

import com.bleudev.ppl_utils.feature.state.SkinHelper;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin {
    @ModifyReturnValue(method = "getSkin", at = @At("RETURN"))
    private PlayerSkin modifyPlayerSkin(PlayerSkin original) {
        String playerName = ((AbstractClientPlayer) (Object) this).getGameProfile().name();
        return SkinHelper.getPlayerSkin(original, playerName);
    }
}
