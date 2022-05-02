package com.github.levoment.chestlootmodifier;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;

public class ConfigManager {

    public static final String CONFIG_FILE =  "chestlootmodifier_config.json";
    public static ConfigurationObject CURRENT_CONFIG;
    public static boolean SUCCESSFULLY_LOADED_CONFIG;

    public static void createConfigFile() {
        // Create the file in memory
        File configFile = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE).toFile();
        // If it doesn't exist in disk
        if (!configFile.exists()) {
            try {
                // Config file string
                String configFileText = "{\n" +
                        "  \"LoadPoolsAtRuntime\": false,\n" +
                        "  \"Names\": {\n" +
                        "    \"Common\": {\n" +
                        "      \"MinRolls\": 1,\n" +
                        "      \"MaxRolls\": 2\n" +
                        "    },\n" +
                        "    \"Uncommon\": {\n" +
                        "      \"MinRolls\": 1,\n" +
                        "      \"MaxRolls\": 2\n" +
                        "    },\n" +
                        "    \"Rare\": {\n" +
                        "      \"MinRolls\": 1,\n" +
                        "      \"MaxRolls\": 3\n" +
                        "    },\n" +
                        "    \"SuperRare\": {\n" +
                        "      \"MinRolls\": 2,\n" +
                        "      \"MaxRolls\": 4\n" +
                        "    }\n" +
                        "  },\n" +
                        "\n" +
                        "  \"ChestDefinitions\": {\n" +
                        "    \"Common\": [],\n" +
                        "    \"Uncommon\": [],\n" +
                        "    \"Rare\": [],\n" +
                        "    \"SuperRare\": []\n" +
                        "  },\n" +
                        "\n" +
                        "  \"LootDefinitions\": {\n" +
                        "    \"Common\": [],\n" +
                        "    \"Uncommon\": [],\n" +
                        "    \"Rare\": [],\n" +
                        "    \"SuperRare\": []\n" +
                        "  }\n" +
                        "}";
                // Create the file writer to write the file
                FileWriter configFileWritter = new FileWriter(configFile);
                // Write the text and close the writer
                configFileWritter.write(configFileText);
                configFileWritter.close();
                // Create the file
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void readConfigFile() {
        // Create the variable to contain the config file in memory
        File configFile = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE).toFile();
        if (configFile.exists()) {
            // Create the Gson instance
            Gson gson = new Gson();
            try {
                // Create a reader for the configuration file
                Reader configReader = Files.newBufferedReader(configFile.toPath());
                // Create an object from the config file
                CURRENT_CONFIG = gson.fromJson(configReader, ConfigurationObject.class);
                if (CURRENT_CONFIG.ChestDefinitions == null) {
                    ChestLootModifierMod.LOGGER.error("[Chest Loot Modifier Mod] ChestDefinitions does not exist or is malformed in the configuration file: " + configFile.getName() + "");
                } else if (CURRENT_CONFIG.LootDefinitions == null) {
                    ChestLootModifierMod.LOGGER.error("[Chest Loot Modifier Mod] LootDefinitions does not exist or is malformed in the configuration file: " + configFile.getName() + "");
                } else if (CURRENT_CONFIG.Names == null) {
                    ChestLootModifierMod.LOGGER.error("[Chest Loot Modifier Mod] Names does not exist or is malformed in the configuration file: " + configFile.getName() + "");
                } else {
                    SUCCESSFULLY_LOADED_CONFIG = true;
                }
            } catch (IOException e) {
                ChestLootModifierMod.LOGGER.error("[Chest Loot Modifier Mod] IO error when reading configuration file:");
                e.printStackTrace();
                System.exit(-1);
            } catch (JsonSyntaxException jsonSyntaxException) {
                ChestLootModifierMod.LOGGER.error("[Chest Loot Modifier Mod] JSON syntax error when reading configuration file (" + configFile.getName() + "):");
                jsonSyntaxException.printStackTrace();
                System.exit(-1);
            } catch (JsonParseException jsonParseException) {
                ChestLootModifierMod.LOGGER.error("[Chest Loot Modifier Mod] JSON parse error when reading configuration file (" + configFile.getName() + "):");
                jsonParseException.printStackTrace();
                System.exit(-1);
            }
        } else ChestLootModifierMod.LOGGER.error("No configuration file found for Chest Loot Modifier Mod in: " + configFile.getPath());
    }
}
