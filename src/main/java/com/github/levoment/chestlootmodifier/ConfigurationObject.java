package com.github.levoment.chestlootmodifier;

import java.util.List;
import java.util.Map;

public class ConfigurationObject {

    public Map<String, RarityObject> Names;
    public Map<String, List<String>> ChestDefinitions;
    public Map<String, List<String>> LootDefinitions;

    public ConfigurationObject(Map<String, RarityObject> names, Map<String, List<String>> chestDefinitions, Map<String, List<String>> lootDefinitions) {
        this.Names = names;
        this.ChestDefinitions = chestDefinitions;
        this.LootDefinitions = lootDefinitions;
    }

    public Map<String, RarityObject> getNames() {
        return Names;
    }

    public void setNames(Map<String, RarityObject> names) {
       this.Names = names;
    }

    public Map<String, List<String>> getChestDefinitions() {
        return ChestDefinitions;
    }

    public void setChestDefinitions(Map<String, List<String>> chestDefinitions) {
        ChestDefinitions = chestDefinitions;
    }

    public Map<String, List<String>> getLootDefinitions() {
        return LootDefinitions;
    }

    public void setLootDefinitions(Map<String, List<String>> lootDefinitions) {
        LootDefinitions = lootDefinitions;
    }
}
