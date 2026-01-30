package com.bleudev.ppl_utils.custom.debug.hud;

import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

import static com.bleudev.ppl_utils.util.LangUtils.round;

public class WorldBorderDebugHudEntry implements DebugScreenEntry {
    @Override
    public void display(DebugScreenDisplayer lines, @Nullable Level world, @Nullable LevelChunk clientChunk, @Nullable LevelChunk chunk) {
        if (world != null) {
            StringBuilder border = new StringBuilder(Double.toString(round(world.getWorldBorder().getSize(), 3)));
            while (border.toString().split("\\.", 2)[1].length() < 3) border.append("0");
            lines.addLine("World border: " + border);
        }
    }
}
