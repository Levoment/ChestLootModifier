package com.github.levoment.chestlootmodifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChestLootModifierMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("chestlootmodifier");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		// Create the config file if it doesn't exist
		ConfigManager.createConfigFile();
		// Read the config file if it exists
		ConfigManager.readConfigFile();
		// Regex to match everything before a parenthesis
		Pattern beforeParenthesisPattern = Pattern.compile("^.*?(?=\\()", Pattern.CASE_INSENSITIVE);
		Pattern betweenParenthesisPattern = Pattern.compile("(?<=\\().*?(?=\\))", Pattern.CASE_INSENSITIVE);

		LootTableLoadingCallback.EVENT.register((resourceManager, manager, id, supplier, setter) -> {
			// If the configuration was loaded successfully
			if (ConfigManager.SUCCESSFULLY_LOADED_CONFIG) {
				String idString = id.toString();
				ConfigManager.CURRENT_CONFIG.ChestDefinitions.forEach((key, chestIDs) -> {
					// If the chest ID matches the current id being registered
					if (chestIDs.contains(idString)) {
						// Check if there is a loot definition for the chest
						if (ConfigManager.CURRENT_CONFIG.LootDefinitions.containsKey(key)) {
							List<String> lootList = ConfigManager.CURRENT_CONFIG.LootDefinitions.get(key);
							// Check if the list is not empty
							if (lootList.size() > 0) {
								// Go through all the loot definitions for this chest in the config file
								for(String itemID : lootList) {
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
												} else {
													// Try to get the percentage
													try {
														int percentageInt = Integer.parseInt(firstMatch);
														if (percentageInt > 0 && percentageInt < 101) {
															double percentageDouble = (double) percentageInt / 100.0;
															float percentage = (float) (Math.round(percentageDouble * Math.pow(10, 1)) / Math.pow(10, 1));
															// Try to get the number of items to put on the chest
															try {
																int itemNumberInt = Integer.parseInt(secondMatch);

																// Create the pool builder
																LootPool poolBuilder = FabricLootPoolBuilder.builder().
																		withEntry(ItemEntry.builder(currentItem).build())
																		.withCondition(RandomChanceLootCondition.builder(percentage).build())
																		.withFunction(SetCountLootFunction.builder(ConstantLootNumberProvider.create(itemNumberInt)).build())
																		.build();
																// Add the item to the pool of items for the current chest
																supplier.withPool(poolBuilder);

															} catch (NumberFormatException numberFormatException) {
																ChestLootModifierMod.LOGGER.error("[Chest Loot Modifier Mod] The provided number: '" + secondMatch + "' in " + itemID + " could not be converted to an integer.");
															}
														} else {
															ChestLootModifierMod.LOGGER.error("[Chest Loot Modifier Mod] The provided percentage: '" + firstMatch + "' in " + itemID + "needs to be more than 0 and less than 101");
														}
													} catch (NumberFormatException numberFormatException) {
														ChestLootModifierMod.LOGGER.error("[Chest Loot Modifier Mod] The provided number: '" + firstMatch + "' in " + itemID + " could not be converted to an integer.");
													}
												}
											} else {
												ChestLootModifierMod.LOGGER.error("[Chest Loot Modifier Mod] Item: '" + wholeMatch + "' is missing a percentage loot chance and or amount for the item between parenthesis.");
											}
										} catch (InvalidIdentifierException invalidIdentifierException) {
											ChestLootModifierMod.LOGGER.error("[Chest Loot Modifier Mod] The game could not find the item with identifier: '" + wholeMatch + "'");
										}
									}
								}
							} else {
								ChestLootModifierMod.LOGGER.error("[Chest Loot Modifier Mod] There is no loot defined for " + key + " in the config file LootDefinition object");
							}
						} else {
							ChestLootModifierMod.LOGGER.error("[Chest Loot Modifier Mod] There is no loot defined for " + key + " in the config file LootDefinition object");
						}


					}
				});
			}
		});

	}
}
