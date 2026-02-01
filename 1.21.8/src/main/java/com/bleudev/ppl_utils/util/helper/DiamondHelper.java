package com.bleudev.ppl_utils.util.helper;

import com.bleudev.ppl_utils.ClientTempData;
import com.bleudev.ppl_utils.config.PplUtilsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.bleudev.ppl_utils.util.ServerUtils.isClientOnPepeland;

public class DiamondHelper {
    public static int count(@NotNull Iterable<ItemStack> inventory) {
        int result = 0;
        for (ItemStack itemStack: inventory) {
            if (itemStack.is(Items.DIAMOND)) result += itemStack.getCount();
            else if (itemStack.is(Items.DIAMOND_BLOCK)) result += 9 * itemStack.getCount();
            else if (itemStack.getComponents().has(DataComponents.CONTAINER))
                result += count(Objects.requireNonNull(itemStack.get(DataComponents.CONTAINER)).stream().toList());
            else if (itemStack.getComponents().has(DataComponents.BUNDLE_CONTENTS))
                result += count(Objects.requireNonNull(itemStack.get(DataComponents.BUNDLE_CONTENTS)).itemsCopy());
        }
        return result;
    }
    public static int count(@NotNull Player player) {
        if (!PplUtilsConfig.diamond_counter_count_in_ender_chest) return count(player.getInventory());
        return count(player.getInventory()) + ClientTempData.getNormalCachedEnderChestCount();
    }
    public static int countWithCurrentContainer(@NotNull Player player) {
        if (!PplUtilsConfig.diamond_counter_count_in_containers) return count(player);
        return count(player) + count(ClientTempData.currentScreenInventory);
    }

    public static void renderCounter(GuiGraphics context, boolean isContainer) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || !PplUtilsConfig.render_diamond_counter || !isClientOnPepeland(client)) return;
        String count = PplUtilsConfig.diamond_counter_count_format.format(isContainer ? countWithCurrentContainer(client.player) : count(client.player));
        int iColor;
        try {
            iColor = Integer.parseInt(PplUtilsConfig.diamond_counter_color.substring(1), 16);
        } catch (NumberFormatException ignored) {
            iColor = 0xffffff; // Fallback
        }
        int iNotSyncedColor;
        try {
            iNotSyncedColor = Integer.parseInt(PplUtilsConfig.diamond_counter_not_synced_color.substring(1), 16);
        } catch (NumberFormatException ignored) {
            iNotSyncedColor = 0xff0000; // Fallback
        }
        int color = ClientTempData.isEnderChestSynced() ? ARGB.color(255, iColor) : ARGB.color(255, iNotSyncedColor);

        context.pose().pushMatrix();
        context.pose().translate(context.guiWidth() - 25 - client.font.width(count), context.guiHeight() - 20);
        context.fill(-2, -2, context.guiWidth(), context.guiHeight(), 0x66000000);
        context.renderItem(new ItemStack(Items.DIAMOND), 0, 0);
        context.drawString(client.font, count, 17, 5, color, true);
        context.pose().popMatrix();
    }
}
