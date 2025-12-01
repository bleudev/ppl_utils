package com.bleudev.ppl_utils.feature.cache;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import static com.bleudev.ppl_utils.util.UIConstants.TOTAL_INVENTORY_SLOTS;

/**
 * Caches inventory slot count to avoid recalculating every frame.
 * Updates only when inventory actually changes.
 */
public class InventorySlotCache {
    private static int cachedOccupiedSlots = 0;
    private static long lastInventoryHash = 0;
    
    /**
     * Gets the number of occupied slots, using cache if inventory hasn't changed.
     * 
     * @param client The Minecraft client
     * @return Number of occupied slots
     */
    public static int getOccupiedSlots(@NotNull MinecraftClient client) {
        if (client.player == null) {
            cachedOccupiedSlots = 0;
            return 0;
        }
        
        PlayerInventory inventory = client.player.getInventory();
        long currentHash = calculateInventoryHash(inventory);
        
        if (currentHash != lastInventoryHash) {
            cachedOccupiedSlots = countOccupiedSlots(inventory);
            lastInventoryHash = currentHash;
        }
        
        return cachedOccupiedSlots;
    }
    
    /**
     * Calculates a hash of the inventory state for change detection.
     * Uses position, item type, and count to ensure uniqueness even with duplicate items.
     * 
     * @param inventory The player inventory
     * @return Hash value representing inventory state
     */
    private static long calculateInventoryHash(@NotNull PlayerInventory inventory) {
        long hash = 0;
        for (int i = 0; i < TOTAL_INVENTORY_SLOTS; i++) {
            var stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                // Include slot position, item registry ID, and count in hash
                // This ensures different stacks of the same item produce different hashes
                long slotHash = (long) i * 31L;
                long itemHash = (long) net.minecraft.registry.Registries.ITEM.getRawId(stack.getItem()) * 31L;
                long countHash = (long) stack.getCount() * 31L;
                hash = hash * 31L + slotHash + itemHash + countHash;
            } else {
                // Include empty slots too to detect when items are moved
                hash = hash * 31L + (long) i;
            }
        }
        return hash;
    }
    
    /**
     * Counts the number of occupied slots in the inventory.
     * 
     * @param inventory The player inventory
     * @return Number of occupied slots
     */
    private static int countOccupiedSlots(@NotNull PlayerInventory inventory) {
        int count = 0;
        for (int i = 0; i < TOTAL_INVENTORY_SLOTS; i++) {
            if (!inventory.getStack(i).isEmpty()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Invalidates the cache. Should be called when inventory is known to have changed.
     */
    public static void invalidate() {
        lastInventoryHash = 0;
        cachedOccupiedSlots = 0;
    }
}


