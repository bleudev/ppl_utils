package com.bleudev.ppl_utils.util;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static com.bleudev.ppl_utils.PplUtilsConst.MOD_ID;

public class RegistryUtils {
    @Contract("_ -> new")
    public static @NotNull Identifier getIdentifier(String name) {
        return Identifier.of(MOD_ID, name);
    }
}
