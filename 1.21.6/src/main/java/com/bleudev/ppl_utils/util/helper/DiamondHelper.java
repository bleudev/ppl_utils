package com.bleudev.ppl_utils.util.helper;

import com.bleudev.ppl_utils.ClientTempData;
import com.bleudev.ppl_utils.config.PplUtilsConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.bleudev.ppl_utils.util.ServerUtils.isClientOnPepeland;

public class DiamondHelper {
    public static int count(@NotNull Iterable<ItemStack> inventory) {
        int result = 0;
        for (ItemStack itemStack: inventory) {
            if (itemStack.isOf(Items.DIAMOND)) result += itemStack.getCount();
            else if (itemStack.isOf(Items.DIAMOND_BLOCK)) result += 9 * itemStack.getCount();
            else if (itemStack.getComponents().contains(DataComponentTypes.CONTAINER))
                result += count(Objects.requireNonNull(itemStack.get(DataComponentTypes.CONTAINER)).stream().toList());
            else if (itemStack.getComponents().contains(DataComponentTypes.BUNDLE_CONTENTS))
                result += count(Objects.requireNonNull(itemStack.get(DataComponentTypes.BUNDLE_CONTENTS)).iterateCopy());
        }
        return result;
    }
    public static int count(@NotNull PlayerEntity player) {
        if (!PplUtilsConfig.diamond_counter_count_in_ender_chest) return count(player.getInventory());
        return count(player.getInventory()) + ClientTempData.getNormalCachedEnderChestCount();
    }
    public static int countWithCurrentContainer(@NotNull PlayerEntity player) {
        if (!PplUtilsConfig.diamond_counter_count_in_containers) return count(player);
        return count(player) + count(ClientTempData.currentScreenInventory);
    }

    public static void renderCounter(DrawContext context, boolean isContainer) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || !PplUtilsConfig.render_diamond_counter || !isClientOnPepeland(client)) return;
        String count = PplUtilsConfig.diamond_counter_count_format.format(isContainer ? countWithCurrentContainer(client.player) : count(client.player));
        int color = ClientTempData.isEnderChestSynced() ? ColorHelper.withAlpha(255, PplUtilsConfig.diamond_counter_color) : ColorHelper.withAlpha(255, PplUtilsConfig.diamond_counter_not_synced_color);
        context.getMatrices().pushMatrix();
        context.getMatrices().translate(context.getScaledWindowWidth() - 25 - client.textRenderer.getWidth(count), context.getScaledWindowHeight() - 20);
        context.fill(-2, -2, context.getScaledWindowWidth(), context.getScaledWindowHeight(), 0x66000000);
        context.drawItem(new ItemStack(Items.DIAMOND), 0, 0);
        context.drawText(client.textRenderer, count, 17, 5, color, true);
        context.getMatrices().popMatrix();
    }
}
