package com.bleudev.ppl_utils;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import static com.bleudev.ppl_utils.util.ServerUtils.isClientOnPepeland;

public class ClientTempData {
    public static final Iterable<ItemStack> inventoryDefault = NonNullList.withSize(27, ItemStack.EMPTY);
    public static Iterable<ItemStack> currentScreenInventory = inventoryDefault;
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
