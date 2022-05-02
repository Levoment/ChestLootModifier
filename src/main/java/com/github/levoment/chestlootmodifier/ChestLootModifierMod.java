package com.github.levoment.chestlootmodifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.item.Item;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChestLootModifierMod implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("chestlootmodifier");
    private boolean addPool = false;
    private LootTable chestLootModifierModModifiedLootTable;

    @Override
    public void onInitialize() {
        // Create the config file if it doesn't exist
        ConfigManager.createConfigFile();
        // Read the config file if it exists
        ConfigManager.readConfigFile();



        LootTableLoadingCallback.EVENT.register((resourceManager, manager, id, supplier, setter) -> {
            this.addPool = false;
            // Regex to match everything before a parenthesis
            Pattern beforeParenthesisPattern = Pattern.compile("^.*?(?=\\()", Pattern.CASE_INSENSITIVE);
            Pattern betweenParenthesisPattern = Pattern.compile("(?<=\\().*?(?=\\))", Pattern.CASE_INSENSITIVE);
            // Create the config file if it doesn't exist
            ConfigManager.createConfigFile();
            // Read the config file
            ConfigManager.readConfigFile();

            // If the configuration was loaded successfully
            if (ConfigManager.SUCCESSFULLY_LOADED_CONFIG) {
                // Return if LoadPoolsAtRuntime is false
                if (ConfigManager.CURRENT_CONFIG.loadPoolsAtRuntime()) return;

                ConfigManager.CURRENT_CONFIG.ChestDefinitions.forEach((key, chestIDs) -> {
                    // If the chest ID matches the current id being registered
                    if (chestIDs.contains(id.toString())) {
                        // Check if there is a loot definition for the chest
                        if (ConfigManager.CURRENT_CONFIG.LootDefinitions.containsKey(key)) {
                            // Get the Loot Name
                            Map<String, RarityObject> rarityObjects = ConfigManager.CURRENT_CONFIG.getNames();
                            // Create variables for the min and max roll for this rarity pool
                            Integer rarityMinRolls = null;
                            Integer rarityMaxRolls = null;
                            if (rarityObjects.containsKey(key)) {
                                rarityMinRolls = rarityObjects.get(key).getMinRolls();
                                rarityMaxRolls = rarityObjects.get(key).getMaxRolls();


                            } else {
                                ChestLootModifierMod.LOGGER.error("[Chest Loot Modifier Mod] The provided rarity named: '" + key + "' in LootDefinitions could not be found in 'Names' of the json config.");
                                addPool = false;
                            }

                            // Build the pool and add it
                            List<String> lootList = ConfigManager.CURRENT_CONFIG.LootDefinitions.get(key);
                            // Check if the list is not empty
                            if (lootList.size() > 0) {
                                LootPool currentLootPool = FabricLootPoolBuilder.builder().build();
                                // Go through all the loot definitions for this chest in the config file
                                for (String itemID : lootList) {

                                    // Try to match the item part
                                    Matcher matcher = beforeParenthesisPattern.matcher(itemID);
                                    if (matcher.find()) {
                                        String wholeMatch = matcher.group();
                                        // Try to get the item from the identifier
                                        try {
                                            // Try to get the given item
                                            Item currentItem = Registry.ITEM.get(new Identifier(matcher.group()));
                                            // Get the part of the percentage and amount conditions for the item
                                            Matcher numberMatcher = betweenParenthesisPattern.matcher(itemID);
                                            if (numberMatcher.find()) {
                                                String firstMatch = numberMatcher.group(0);
                                                String secondMatch = null;
                                                if (numberMatcher.find()) {
                                                    secondMatch = numberMatcher.group(0);
                                                }

                                                if (firstMatch == null || firstMatch.isBlank() || secondMatch == null || secondMatch.isBlank()) {
                                                    ChestLootModifierMod.LOGGER.error("[Chest Loot Modifier Mod] Item: '" + itemID + "' doesn't have a properly formatted number " +
                                                            "for the percentage loot chance and or amount for the item between parenthesis. " +
                                                            "Both, the percentage and number, must be integers. An example would be: minecraft:stone_sword(45)(1). Where 45 " +
                                                            "would be 45% chance of appearing among other loot in the chest and 1 is the amount of stone swords to put on the chest " +
                                                            "should a stone word appear on it.");
                                                    addPool = false;
                                                } else {
                                                    // Try to get the item count
                                                    try {
                                                        int itemCount = Integer.parseInt(firstMatch);
                                                        // Try to get the weight of the item
                                                        try {
                                                            int itemWeight = Integer.parseInt(secondMatch);
                                                            // Create the pool builder
                                                            currentLootPool = FabricLootPoolBuilder.of(currentLootPool).
                                                                    withEntry(ItemEntry.builder(currentItem).weight(itemWeight).build())
                                                                    .withFunction(SetCountLootFunction.builder(ConstantLootNumberProvider.create(itemCount)).build())
                                                                    .rolls(UniformLootNumberProvider.create(rarityMinRolls, rarityMaxRolls))
                                                                    .build();
                                                            addPool = true;
                                                        } catch (NumberFormatException numberFormatException) {
                                                            ChestLootModifierMod.LOGGER.error("[Chest Loot Modifier Mod] The provided number: '" + secondMatch + "' in " + itemID + " could not be converted to an integer.");
                                                            addPool = false;
                                                        }

                                                    } catch (NumberFormatException numberFormatException) {
                                                        ChestLootModifierMod.LOGGER.error("[Chest Loot Modifier Mod] The provided number: '" + firstMatch + "' in " + itemID + " could not be converted to an integer.");
                                                        addPool = false;
                                                    }
                                                }
                                            } else {
                                                ChestLootModifierMod.LOGGER.error("[Chest Loot Modifier Mod] Item: '" + wholeMatch + "' is missing a percentage loot chance and or amount for the item between parenthesis.");
                                                addPool = false;
                                            }
                                        } catch (InvalidIdentifierException invalidIdentifierException) {
                                            ChestLootModifierMod.LOGGER.error("[Chest Loot Modifier Mod] The game could not find the item with identifier: '" + wholeMatch + "'");
                                            addPool = false;
                                        }
                                    }
                                }
                                // Add the item to the pool of items for the current chest
                                if (this.addPool) supplier.withPool(currentLootPool).build();
                            } else {
                                ChestLootModifierMod.LOGGER.error("[Chest Loot Modifier Mod] There is no loot defined for " + key + " in the config file LootDefinition object");
                                addPool = false;
                            }
                        } else {
                            ChestLootModifierMod.LOGGER.error("[Chest Loot Modifier Mod] There is no loot defined for " + key + " in the config file LootDefinition object");
                            addPool = false;
                        }
                    }
                });
            }
        });
    }
}
