package com.github.levoment.chestlootmodifier.mixins;

import com.github.levoment.chestlootmodifier.ChestLootModifierMod;
import com.github.levoment.chestlootmodifier.ConfigManager;
import com.github.levoment.chestlootmodifier.RarityObject;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(LootableContainerBlockEntity.class)
public class LootableContainerBlockEntityMixin {

    private LootTable chestLootModifierModModifiedLootTable;
    private boolean addPool;

    @Shadow
    Identifier lootTableId;

    @Inject(method = "checkLootInteraction(Lnet/minecraft/entity/player/PlayerEntity;)V", at = @At("HEAD"))
    public void checkLootInteractionMixinCallback(PlayerEntity player, CallbackInfo ci) {
        this.addPool = false;
        if (this.lootTableId != null && ((LootableContainerBlockEntity) ((Object) this)).getWorld().getServer() != null) {
            MinecraftServer minecraftServer = ((LootableContainerBlockEntity) ((Object) this)).getWorld().getServer();
            // Regex to match everything before a parenthesis
            Pattern beforeParenthesisPattern = Pattern.compile("^.*?(?=\\()", Pattern.CASE_INSENSITIVE);
            Pattern betweenParenthesisPattern = Pattern.compile("(?<=\\().*?(?=\\))", Pattern.CASE_INSENSITIVE);
            // Create the config file if it doesn't exist
            ConfigManager.createConfigFile();
            // Read the config file
            ConfigManager.readConfigFile();

            // If the configuration was loaded successfully
            if (ConfigManager.SUCCESSFULLY_LOADED_CONFIG) {
                ConfigManager.CURRENT_CONFIG.ChestDefinitions.forEach((key, chestIDs) -> {
                    // If the chest ID matches the current id being registered
                    if (chestIDs.contains(lootTableId.toString())) {
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
                                                        } catch (NumberFormatException numberFormatException) {
                                                            ChestLootModifierMod.LOGGER.error("[Chest Loot Modifier Mod] The provided number: '" + secondMatch + "' in " + itemID + " could not be converted to an integer.");
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
                                // Add the item to the pool of items for the current chest
                                LootManager lootManager = minecraftServer.getLootManager();
                                LootTable lootTable = lootManager.getTable(this.lootTableId);
                                this.chestLootModifierModModifiedLootTable = FabricLootSupplierBuilder.of(lootTable).withPool(currentLootPool).build();
                                this.addPool = true;
                            } else {
                                ChestLootModifierMod.LOGGER.error("[Chest Loot Modifier Mod] There is no loot defined for " + key + " in the config file LootDefinition object");
                            }
                        } else {
                            ChestLootModifierMod.LOGGER.error("[Chest Loot Modifier Mod] There is no loot defined for " + key + " in the config file LootDefinition object");
                        }

                    }
                });
            }
        }
    }

    @ModifyVariable(method = "checkLootInteraction(Lnet/minecraft/entity/player/PlayerEntity;)V", at = @At(value = "STORE", id = "lootTable"))
    public LootTable modifyLootTable(LootTable lootTable) {
        if (this.addPool) {
            return this.chestLootModifierModModifiedLootTable;
        } else {
            return lootTable;
        }
    }
}