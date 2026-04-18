package com.bleudev.ppl_utils.mixin.client;

import com.bleudev.ppl_utils.feature.tab.PepelandTabHelper;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerInfo.class)
public abstract class PlayerInfoMixin {
    @Shadow
    public abstract GameProfile getProfile();

    @ModifyReturnValue(method = "getTabListDisplayName", at = @At("RETURN"))
    private Component addStateTabIcon(Component original) {
        return PepelandTabHelper.getNewTabListDisplayName(getProfile().name(), original.copy());
    }
}
