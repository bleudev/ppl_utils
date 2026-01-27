package com.bleudev.ppl_utils.custom.debug.hud;

import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

import static com.bleudev.ppl_utils.util.LangUtils.round;

public class WorldBorderDebugHudEntry implements DebugHudEntry {
    @Override
    public void render(DebugHudLines lines, @Nullable World world, @Nullable WorldChunk clientChunk, @Nullable WorldChunk chunk) {
        if (world != null) {
            StringBuilder border = new StringBuilder(Double.toString(round(world.getWorldBorder().getSize(), 3)));
            while (border.toString().split("\\.", 2)[1].length() < 3) border.append("0");
            lines.addLine("World border: " + border);
        }
    }
}
