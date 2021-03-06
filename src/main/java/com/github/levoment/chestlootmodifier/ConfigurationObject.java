package com.github.levoment.chestlootmodifier;

import java.util.List;
import java.util.Map;

public class ConfigurationObject {

    public boolean LoadPoolsAtRuntime;
    public Map<String, RarityObject> Names;
    public Map<String, List<String>> ChestDefinitions;
    public Map<String, List<String>> LootDefinitions;

    public ConfigurationObject(boolean loadPoolsAtRuntime, Map<String, RarityObject> names, Map<String, List<String>> chestDefinitions, Map<String, List<String>> lootDefinitions) {
        this.LoadPoolsAtRuntime = loadPoolsAtRuntime;
        this.Names = names;
        this.ChestDefinitions = chestDefinitions;
        this.LootDefinitions = lootDefinitions;
    }

    public boolean loadPoolsAtRuntime() {
        return LoadPoolsAtRuntime;
    }

    public void setLoadPoolsAtRuntime(boolean loadPoolsAtRuntime) {
        LoadPoolsAtRuntime = loadPoolsAtRuntime;
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
