package com.bleudev.ppl_utils.feature.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import static com.bleudev.ppl_utils.util.UIConstants.*;

/**
 * Renders the inventory slot counter HUD element.
 * Displays occupied/total slots with color coding based on usage.
 */
public class InventorySlotCounterRenderer {
    private final MinecraftClient client;
    
    public InventorySlotCounterRenderer(@NotNull MinecraftClient client) {
        this.client = client;
    }
    
    /**
     * Renders the inventory slot counter next to the hotbar.
     * 
     * @param context The draw context
     * @param occupiedSlots Number of occupied inventory slots
     * @param screenWidth Screen width
     * @param screenHeight Screen height
     */
    public void render(@NotNull DrawContext context, int occupiedSlots, int screenWidth, int screenHeight) {
        // Calculate hotbar position
        int hotbarX = (screenWidth / 2) - HOTBAR_OFFSET_X;
        int hotbarY = screenHeight - HOTBAR_OFFSET_Y;
        
        // Position icon and text to the right of hotbar
        int iconX = hotbarX + HOTBAR_WIDTH + PADDING_LARGE;
        int iconY = hotbarY + ICON_VERTICAL_OFFSET;
        
        // Create text with format: "occupied/total"
        String slotTextStr = occupiedSlots + "/" + TOTAL_INVENTORY_SLOTS;
        Text slotText = Text.literal(slotTextStr);
        
        // Calculate text dimensions and position
        int textWidth = client.textRenderer.getWidth(slotText);
        int textHeight = client.textRenderer.fontHeight;
        int textX = iconX + ICON_SIZE + TEXT_OFFSET_FROM_ICON;
        int textY = iconY + (ICON_SIZE / 2) - (textHeight / 2);
        
        // Calculate background dimensions
        int bgX = iconX - PADDING_SMALL;
        int bgY = iconY - PADDING_SMALL;
        // Background width: from bgX to end of text + padding on the right
        int bgWidth = (textX + textWidth) - bgX + PADDING_SMALL;
        int bgHeight = Math.max(ICON_SIZE, textHeight) + (PADDING_SMALL * 2);
        
        // Draw background
        context.fill(bgX, bgY, bgX + bgWidth, bgY + bgHeight, COLOR_BACKGROUND_SEMI_TRANSPARENT);
        
        // Draw chest item icon
        ItemStack chestStack = new ItemStack(Items.CHEST);
        context.drawItemWithoutEntity(chestStack, iconX, iconY);
        
        // Determine text color based on occupied slots
        int textColor = getTextColor(occupiedSlots);
        
        // Draw text
        context.drawText(client.textRenderer, slotText, textX, textY, textColor, true);
    }
    
    /**
     * Gets the text color based on the number of occupied slots.
     * 
     * @param occupiedSlots Number of occupied slots
     * @return Color code for the text
     */
    private int getTextColor(int occupiedSlots) {
        if (occupiedSlots <= SLOT_THRESHOLD_MEDIUM) {
            // 0-23: beige (same as 0-17)
            return COLOR_TEXT_BEIGE;
        } else if (occupiedSlots <= SLOT_THRESHOLD_HIGH) {
            // 24-34: yellow
            return COLOR_TEXT_YELLOW;
        } else {
            // 35-36: red
            return COLOR_TEXT_RED;
        }
    }
}


