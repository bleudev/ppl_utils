package com.bleudev.ppl_utils.mixin.client;

import com.bleudev.ppl_utils.config.PplUtilsConfig;
import com.bleudev.ppl_utils.feature.cache.InventorySlotCache;
import com.bleudev.ppl_utils.feature.renderer.InventorySlotCounterRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "render", at = @At("TAIL"))
    private void renderInventorySlotCount(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (!PplUtilsConfig.show_inventory_slot_count || client.player == null) return;

        // Get occupied slots from cache (updates only when inventory changes)
        int occupiedSlots = InventorySlotCache.getOccupiedSlots(client);
        
        // Render using dedicated renderer
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();
        InventorySlotCounterRenderer renderer = new InventorySlotCounterRenderer(client);
        renderer.render(context, occupiedSlots, width, height);
    }
    
}

