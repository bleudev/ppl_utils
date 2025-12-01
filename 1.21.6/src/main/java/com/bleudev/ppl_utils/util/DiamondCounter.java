package com.bleudev.ppl_utils.util;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DiamondCounter {
    /**
     * Counts total diamonds from all slots
     */
    public static int countDiamondsFromSlots(@NotNull List<Slot> slots) {
        return countItemsFromSlots(slots, Items.DIAMOND);
    }
    
    /**
     * Counts total diamond blocks from all slots
     */
    public static int countDiamondBlocksFromSlots(@NotNull List<Slot> slots) {
        return countItemsFromSlots(slots, Items.DIAMOND_BLOCK);
    }
    
    /**
     * Counts total items of specific type from all slots
     */
    public static int countItemsFromSlots(@NotNull List<Slot> slots, @NotNull Item item) {
        int total = 0;
        for (Slot slot : slots) {
            if (slot == null || !slot.hasStack()) continue;
            ItemStack stack = slot.getStack();
            
            if (stack.getItem() == item) {
                total += stack.getCount();
            }
        }
        return total;
    }
    
    /**
     * Counts total diamonds
     */
    public static int countDiamonds(@NotNull Inventory inventory) {
        return countItems(inventory, Items.DIAMOND);
    }
    
    /**
     * Counts total diamond blocks
     */
    public static int countDiamondBlocks(@NotNull Inventory inventory) {
        return countItems(inventory, Items.DIAMOND_BLOCK);
    }
    
    /**
     * Counts total items of specific type
     */
    public static int countItems(@NotNull Inventory inventory, @NotNull Item item) {
        int total = 0;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) continue;
            
            if (stack.getItem() == item) {
                total += stack.getCount();
            }
        }
        return total;
    }
}
