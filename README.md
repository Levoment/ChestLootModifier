# Chest Loot Modifier

![Chest Loot Modifier Icon](./src/main/resources/assets/chestlootmodifier/icon.png)

## About

Minecraft mod for modifying the items that appear in chests in Minecraft

In this mod, the game needs to be restarted for the changes in the configuration to 
take effect and the changes will apply only to chests that have not been generated yet
in the world.

## Configuration

A file named `chestlootmodifier_config.json` needs to exist on the `config` folder of the 
Minecraft instance that the mod is loaded on. The mod will automatically create a template 
for this file during initialization if the file doesn't exist. That means that if the file 
gets corrupted, you can always delete it and the mod will re-create a template for it.

A sample file looks like this:

```json
{
  
  "Names": [
    "Common",
    "Uncommon",
    "Rare",
    "SuperRare"
  ],
  
  "ChestDefinitions": {
    "Common": ["minecraft:chests/spawn_bonus_chest", "minecraft:chests/village/village_mason", "minecraft:chests/simple_dungeon"],
    "Uncommon": ["minecraft:chests/desert_pyramid", "minecraft:chests/pillager_outpost", "minecraft:chests/ruined_portal"],
    "Rare": ["minecraft:chests/bastion_treasure", "minecraft:chests/stronghold_library"],
    "SuperRare": ["minecraft:chests/woodland_mansion", "minecraft:chests/end_city_treasure"]
  },

  "LootDefinitions": {
    "Common": ["minecraft:golden_sword(100)(3)", "minecraft:golden_pickaxe(100)(12)", "minecraft:coal_ore(100)(16)"],
    "Uncommon": ["minecraft:diamond(70)(4)", "minecraft:diamond(90)(2)", "minecraft:diamond_sword(85)(1)"],
    "Rare": ["minecraft:netherite_axe(80)(1)", "minecraft:netherite_ingot(95)(8)"],
    "SuperRare": ["minecraft:elytra(99)(1)", "minecraft:shulker_box(90)(2)"]
  }
  
}
```

`Names` are the names used to identify the kind of loot. This can be anything, but whatever 
they are, they must be the same on `ChestDefinitions` and `LootDefinitions`.

`ChestDefinitions` these are the chests that will apply to a loot. For instance, which 
chests will get Common, Uncommon, Rare, and SuperRare loot. These must be the fully 
qualified Minecraft Identifiers for the chests.

`LootDefinition` these are which items will apply to each type of loot. For instance, 
which items will apply to Common loot, Uncommon loot, etc. 

- **The number in the first 
parenthesis** is the number that will indicate the percentage chance of the item appearing 
in the chest. To make it always appear, set it to 100. Note that if the game fills the 
chest with other items, even a 100 number could make the item not appear in the chest. 

- **The number in the second parenthesis** is the number range of items that the game should 
put on the chest. The game will not always put the specified number. It will put anything 
within that range. The game could put 0 of the item, the number specified, or a number in 
between.

## Issues
The mod has a lot of logic to print to the console when something fails. If the game crashes 
or if the loot doesn't seem to have been applied to the chests, create an issue.

## License

The Unlicense
