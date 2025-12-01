package com.bleudev.ppl_utils.config;

import com.bleudev.ppl_utils.PplUtilsConst;

/**
 * Handles configuration migrations when mod updates change config structure.
 */
public class ConfigMigration {
    private static final String MIGRATION_VERSION_KEY = "config_version";
    private static final int CURRENT_CONFIG_VERSION = 1;
    
    /**
     * Performs all necessary migrations for the current config version.
     * Should be called after config is loaded but before validation.
     */
    public static void migrate() {
        int configVersion = getConfigVersion();
        
        if (configVersion < 1) {
            migrateToVersion1();
            setConfigVersion(1);
        }
        
        // Future migrations can be added here:
        // if (configVersion < 2) {
        //     migrateToVersion2();
        //     setConfigVersion(2);
        // }
    }
    
    /**
     * Migration to version 1: Handle removal of COMPASS enum value.
     */
    private static void migrateToVersion1() {
        // If lobby_button_style is null or invalid, reset to default
        if (PplUtilsConfig.lobby_button_style == null) {
            PplUtilsConst.LOGGER.info("Migrating config: resetting lobby_button_style to default");
            PplUtilsConfig.lobby_button_style = PplUtilsConfig.LobbyButtonStyle.PEPE;
        }
    }
    
    /**
     * Gets the current config version from stored data.
     * Returns 0 if version is not set (first time migration).
     */
    private static int getConfigVersion() {
        // For now, we check if migration is needed by checking for null values
        // In future, this could be stored in a separate config file or metadata
        return 0; // Always assume migration needed if not explicitly set
    }
    
    /**
     * Sets the config version after successful migration.
     */
    private static void setConfigVersion(int version) {
        // In future, this could be stored in a separate config file or metadata
        PplUtilsConst.LOGGER.debug("Config migrated to version {}", version);
    }
}

