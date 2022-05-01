package com.github.levoment.chestlootmodifier;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChestLootModifierMod implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("chestlootmodifier");

    @Override
    public void onInitialize() {
        // Create the config file if it doesn't exist
        ConfigManager.createConfigFile();
        // Read the config file if it exists
        ConfigManager.readConfigFile();
    }
}
