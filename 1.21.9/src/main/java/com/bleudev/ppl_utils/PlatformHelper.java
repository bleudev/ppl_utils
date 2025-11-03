package com.bleudev.ppl_utils;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class PlatformHelper {
    private static <T> Optional<T> getModInfo(String modId, Function<ModMetadata, T> getter) {
        try {
            return FabricLoader.getInstance().getModContainer(modId)
                    .map(c -> getter.apply(c.getMetadata()));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Get version string of mod which has specified {@code modId}
     *
     * @param modId ID of mod where to get version
     * @return Mod version
     * */
    public static String getModVersion(String modId) {
        return getModInfo(modId, m -> m.getVersion().getFriendlyString())
                .orElse("0.0.0");
    }
    /**
     * Get version substring before first occurrence of {@code before} of mod which has specified {@code modId}
     *
     * @param modId ID of mod where to get version
     * @param before String that is the end of a substring (exclusive).
     *               If it isn't in the string, the function will return the result of {@code getModVersion(modId)}
     * @return Mod version substring
     *
     * <pre>{@code
     * // Example
     * v1 = getModVersion(MOD_ID); // 1.0.0+1.21.9
     * v2 = getModVersion(MOD_ID, "+"); // 1.0.0
     *
     * // But
     * v3 = getModVersion(MOD_ID, "-"); // 1.0.0+1.21.9
     * }</pre>
     * */
    public static String getModVersion(String modId, String before) {
        var v = getModVersion(modId);
        var i = v.indexOf(before);
        return v.substring(0, (i == -1) ? v.length() : i);
    }
    /**
     * Get name (not ID!) of mod which has specified {@code modId}
     *
     * @param modId ID of mod where to get name
     * @return Mod name
     * */
    public static String getModName(String modId) {
        return getModInfo(modId, ModMetadata::getName)
                .orElse("Unknown");
    }
    /**
     * Get authors names of mod which has specified {@code modId}
     *
     * @param modId ID of mod where to get authors
     * @return Mod authors names
     * */
    public static List<String> getModAuthors(String modId) {
        return getModInfo(modId, m -> m.getAuthors().stream().map(Person::getName).toList())
                .orElse(List.of());
    }
}
