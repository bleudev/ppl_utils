package com.bleudev.ppl_utils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import static com.bleudev.ppl_utils.util.ServerUtils.isClientOnPepeland;

public class ClientTempData {
    public static final Iterable<ItemStack> inventoryDefault = DefaultedList.ofSize(27, ItemStack.EMPTY);
    public static Iterable<ItemStack> currentScreenInventory = inventoryDefault;
    public static boolean isInEnderChest = false;
    private static int cachedEnderChestCount = -1;

    public static void setCachedEnderChestCount(int newCount) {
        if (!isClientOnPepeland()) return;
        cachedEnderChestCount = newCount;
    }
    public static int getNormalCachedEnderChestCount() {
        return Math.max(cachedEnderChestCount, 0);
    }
    public static boolean isEnderChestSynced() {
        return cachedEnderChestCount != -1;
    }

    public static void save() {
        DataStorageHelper.save(DataStorageHelper.getData()
            .withCachedEnderChestCount(cachedEnderChestCount));
    }

    public static void load() {
        cachedEnderChestCount = DataStorageHelper.getData().cachedEnderChestCount();
    }
}
