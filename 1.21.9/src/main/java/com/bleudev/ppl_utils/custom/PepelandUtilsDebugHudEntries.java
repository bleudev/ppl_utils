package com.bleudev.ppl_utils.custom;

import com.bleudev.ppl_utils.custom.debug.hud.WorldBorderDebugHudEntry;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;

import static com.bleudev.ppl_utils.PplUtilsConst.LOGGER;
import static com.bleudev.ppl_utils.util.RegistryUtils.getIdentifier;

public class PepelandUtilsDebugHudEntries {
    public static WorldBorderDebugHudEntry WORLD_BORDER = new WorldBorderDebugHudEntry();

    private static void registerHudEntry(String path, DebugScreenEntry debugHudEntry) {
        LOGGER.info("Registering ppl_utils:{} debug hud entry", path);
        DebugScreenEntries.register(getIdentifier(path), debugHudEntry);
        LOGGER.info("Registered ppl_utils:{} debug hud entry", path);
    }

    public static void initialize() {
        registerHudEntry("world_border", WORLD_BORDER);
    }
}
