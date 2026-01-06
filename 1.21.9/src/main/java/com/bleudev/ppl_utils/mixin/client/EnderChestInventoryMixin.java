package com.bleudev.ppl_utils.mixin.client;

import com.bleudev.ppl_utils.ClientTempData;
import net.minecraft.entity.ContainerUser;
import net.minecraft.inventory.EnderChestInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderChestInventory.class)
public class EnderChestInventoryMixin {
    @Inject(method = "onOpen", at = @At("HEAD"))
    private void setIsInEnderChest(ContainerUser user, CallbackInfo ci) {
        ClientTempData.isInEnderChest = true;
    }
    @Inject(method = "onClose", at = @At("HEAD"))
    private void setIsNotInEnderChest(ContainerUser user, CallbackInfo ci) {
        ClientTempData.isInEnderChest = false;
    }
}
