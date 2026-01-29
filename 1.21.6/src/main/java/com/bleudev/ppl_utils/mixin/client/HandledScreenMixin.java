package com.bleudev.ppl_utils.mixin.client;

import com.bleudev.ppl_utils.ClientTempData;
import com.bleudev.ppl_utils.util.helper.DiamondHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class HandledScreenMixin extends Screen {
    @Shadow
    @Final
    protected ScreenHandler handler;

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void getInventoryAndRenderCounter(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (title.getContent() instanceof TranslatableTextContent ttc && ttc.getKey().equals("container.enderchest")) {
            ClientTempData.setCachedEnderChestCount(DiamondHelper.count(this.handler.getStacks().subList(0, 27)));
            ClientTempData.save();
            ClientTempData.currentScreenInventory = ClientTempData.inventoryDefault;
        } else if (!(MinecraftClient.getInstance().currentScreen instanceof CreativeInventoryScreen))
            ClientTempData.currentScreenInventory = this.handler.getStacks().subList(0, 27);
        DiamondHelper.renderCounter(context, true);
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void removeInventory(CallbackInfo ci) {
        ClientTempData.currentScreenInventory = ClientTempData.inventoryDefault;
    }
}
