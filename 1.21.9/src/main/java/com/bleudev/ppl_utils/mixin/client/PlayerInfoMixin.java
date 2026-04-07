package com.bleudev.ppl_utils.mixin.client;

import com.bleudev.ppl_utils.feature.state.StateHelper;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.objects.AtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(PlayerInfo.class)
public abstract class PlayerInfoMixin {
    @Shadow
    public abstract GameProfile getProfile();

    @ModifyReturnValue(method = "getTabListDisplayName", at = @At("RETURN"))
    private Component addStateTabIcon(Component original) {
        String name = getProfile().name();
        Optional<ResourceLocation> icon = StateHelper.getTabIcon(name);
        if (icon.isEmpty()) return original;
        return original.copy().append(" ").append(Component.object(new AtlasSprite(AtlasIds.GUI, icon.get())));
    }
}
