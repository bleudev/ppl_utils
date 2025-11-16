package com.bleudev.ppl_utils.config;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;

import static com.bleudev.ppl_utils.util.RegistryUtils.getIdentifier;

public class PepelandUtilsYaclConfig {
    public static ConfigClassHandler<PepelandUtilsYaclConfig> HANDLER = ConfigClassHandler.createBuilder(PepelandUtilsYaclConfig.class)
        .id(getIdentifier("config_handler"))
        .serializer(config -> GsonConfigSerializerBuilder.create(config)
            .setPath(FabricLoader.getInstance().getConfigDir().resolve("ppl_utils_config.json5"))
            .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
            .setJson5(true)
            .build())
        .build();
    public static PepelandUtilsYaclConfig getDefaults() {
        return PepelandUtilsYaclConfig.HANDLER.defaults();
    }
    public static PepelandUtilsYaclConfig getConfig() {
        return PepelandUtilsYaclConfig.HANDLER.instance();
    }

    @SerialEntry
    public boolean lobby_button_enabled = true;
    @SerialEntry
    public int maxCount = 10;
}
