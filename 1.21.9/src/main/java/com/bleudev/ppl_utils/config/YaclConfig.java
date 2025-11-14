package com.bleudev.ppl_utils.config;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;

import static com.bleudev.ppl_utils.util.RegistryUtils.getIdentifier;

public class YaclConfig {
    public static ConfigClassHandler<YaclConfig> HANDLER = ConfigClassHandler.createBuilder(YaclConfig.class)
        .id(getIdentifier("config_handler"))
        .serializer(config -> GsonConfigSerializerBuilder.create(config)
            .setPath(FabricLoader.getInstance().getConfigDir().resolve("ppl_utils_config.json5"))
            .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
            .setJson5(true)
            .build())
        .build();

    public static YaclConfig getDefaults() {
        return YaclConfig.HANDLER.defaults();
    }
    public static YaclConfig getConfig() {
        return YaclConfig.HANDLER.instance();
    }

    @SerialEntry
    public boolean enableFeature = true;
    @SerialEntry
    public int maxCount = 10;
}
