package com.bleudev.ppl_utils.util;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static com.bleudev.ppl_utils.PplUtilsConst.MOD_ID;

public class RegistryUtils {
    @Contract("_ -> new")
    public static @NotNull ResourceLocation getIdentifier(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }
}
