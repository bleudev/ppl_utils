package com.bleudev.ppl_utils.mixin.client;

import com.bleudev.ppl_utils.ClientTempData;
import com.bleudev.ppl_utils.util.helper.DiamondHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin extends Screen {
    @Shadow
    @Final
    protected AbstractContainerMenu menu;

    protected AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void getInventoryAndRenderCounter(GuiGraphics context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (title.getContents() instanceof TranslatableContents ttc && ttc.getKey().equals("container.enderchest")) {
            ClientTempData.setCachedEnderChestCount(DiamondHelper.count(this.menu.getItems().subList(0, 27)));
            ClientTempData.save();
            ClientTempData.currentScreenInventory = ClientTempData.inventoryDefault;
        } else if (!(Minecraft.getInstance().screen instanceof CreativeModeInventoryScreen))
            ClientTempData.currentScreenInventory = this.menu.getItems().subList(0, 27);
        DiamondHelper.renderCounter(context, true);
    }

    @Inject(method = "onClose", at = @At("HEAD"))
    private void removeInventory(CallbackInfo ci) {
        ClientTempData.currentScreenInventory = ClientTempData.inventoryDefault;
    }
}
