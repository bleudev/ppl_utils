package com.bleudev.ppl_utils.mixin.client;

import com.bleudev.ppl_utils.util.helper.DiamondHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void renderCounter(GuiGraphics context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        DiamondHelper.renderCounter(context, false);
    }
}
