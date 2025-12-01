package com.bleudev.ppl_utils.mixin.client;

import com.bleudev.ppl_utils.config.PplUtilsConfig;
import com.bleudev.ppl_utils.util.DiamondCounter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {
    @Shadow
    protected abstract Slot getSlotAt(double x, double y);

    @Inject(method = "render", at = @At("TAIL"))
    private void renderDiamondCounter(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!PplUtilsConfig.show_diamond_counter) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;

        HandledScreen<?> screen = (HandledScreen<?>) (Object) this;
        if (screen.getScreenHandler() == null) return;
        
        // Get all slots from screen handler (includes both player inventory and container)
        var slots = screen.getScreenHandler().slots;
        
        // Skip if this is InventoryScreen or player inventory screen (36 slots or less)
        // Check by class name first (more reliable than instanceof due to obfuscation)
        String screenClassName = screen.getClass().getName();
        if (screenClassName.contains("InventoryScreen")) return;
        
        // Skip player inventory screens (36 slots or less)
        if (slots == null || slots.size() <= 36) return;
        
        // Get player inventory - always use this for counting player's items
        var playerInventory = client.player.getInventory();
        
        // Determine screen type: container screens typically have more than 36 slots
        // Player inventory has exactly 36 slots (9 hotbar + 27 main inventory)
        // But we should always count from player inventory to ensure survival mode works
        boolean isContainerScreen = slots != null && slots.size() > 36;
        
        int diamondBlocks = 0;
        int diamonds = 0;
        
        // Count from container slots
        if (isContainerScreen && slots != null) {
            diamondBlocks = DiamondCounter.countDiamondBlocksFromSlots(slots);
            diamonds = DiamondCounter.countDiamondsFromSlots(slots);
        } else {
            // Fallback: count from player inventory
            diamondBlocks = DiamondCounter.countDiamondBlocks(playerInventory);
            diamonds = DiamondCounter.countDiamonds(playerInventory);
        }

        // Get hovered slot to show item count
        Item hoveredItem = null;
        int hoveredItemCount = 0;
        try {
            Slot hoveredSlot = getSlotAt(mouseX, mouseY);
            if (hoveredSlot != null && hoveredSlot.hasStack()) {
                ItemStack stack = hoveredSlot.getStack();
                hoveredItem = stack.getItem();
                
                // Count based on screen type
                if (isContainerScreen && slots != null) {
                    hoveredItemCount = DiamondCounter.countItemsFromSlots(slots, hoveredItem);
                } else {
                    hoveredItemCount = DiamondCounter.countItems(playerInventory, hoveredItem);
                }
            }
        } catch (Exception e) {
            // Ignore errors when getting hovered slot
        }

        // Calculate panel dimensions
        int panelWidth = 120;
        int panelX = 10;
        int panelY = 10;
        int lineHeight = 12;
        int padding = 6;
        int panelHeight = padding * 2 + lineHeight * (hoveredItem != null ? 3 : 2);

        // Draw semi-transparent black background panel
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0x80000000);

        // Draw text
        int textX = panelX + padding;
        int textY = panelY + padding;
        int textColor = 0xFFFFFFFF; // White

        // Кол-во АБ
        Text abText = Text.translatable("ppl_utils.diamond_counter.diamond_blocks").append(" " + diamondBlocks);
        context.drawText(client.textRenderer, abText, textX, textY, textColor, false);
        textY += lineHeight;

        // Всего алмазов
        Text diamondsText = Text.translatable("ppl_utils.diamond_counter.diamonds").append(" " + diamonds);
        context.drawText(client.textRenderer, diamondsText, textX, textY, textColor, false);
        textY += lineHeight;

        // Кол-во предметов (only if hovering over an item)
        if (hoveredItem != null) {
            Text itemCountText = Text.translatable("ppl_utils.diamond_counter.item_count").append(" " + hoveredItemCount);
            context.drawText(client.textRenderer, itemCountText, textX, textY, textColor, false);
        }
    }
}

