package com.bleudev.ppl_utils.custom.debug.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

import static com.bleudev.ppl_utils.util.LangUtils.round;

public class WorldBorderDebugHudEntry implements DebugHudEntry {
    @Override
    public void render(DebugHudLines lines, @Nullable World world, @Nullable WorldChunk clientChunk, @Nullable WorldChunk chunk) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world != null)
            lines.addLine("World border: " + round(client.world.getWorldBorder().getSize(), 3));
    }
}
