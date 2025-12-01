package com.bleudev.ppl_utils.mixin.client;

import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin {
    // Disabled - counter is only shown in containers, not in player inventory
}
