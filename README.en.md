# GTNH Easy Mode

🇵🇱 [Polski](README.md) | 🇬🇧 English

An addon for **GregTech: New Horizons 2.9.0** that cuts down the grind without breaking progression. Every change can be tuned or turned off in the config. Works in singleplayer and on servers (on a server the mod must be installed on both client and server).

## What it changes

- **Faster GT recipes**: 4x shorter duration in all machines, multiblocks and the assembly line, which also means less total EU. Fuels and generators are left untouched.
- **More ore**: 2x drops from naturally generated GT / BartWorks / GT++ ores (small ores and the Ore Drilling Plant included). Fortune on GT ores works up to level VI. Silk touch and player-placed ores are not multiplied.
- **Tinkers' Construct**: 2x tool XP, repairs restore at least 10% durability per ingot, Lapis (Luck) can be applied twice (Fortune/Looting VI).
- **"GTNH Easy Mode" quest chapter**:
  - a list of all changes with the current config values,
  - Iron Hammer and Iron Lumber Axe (TiC) for iron ingots,
  - Healing Axe (Extra Utilities) for diamonds,
  - a Prospector's Scanner with huge durability for an Ore Finder Wand,
  - a repeatable Woven Cotton to Woven Silk trade (silk without bees).

## Installation

Put the jar from [Releases](../../releases) (or `build/libs/easynh.jar`) into your `mods/` folder.

## Configuration

`config/gtnheasymode.cfg` is created on first launch. Options are grouped into `recipes`, `ores`, `tinkers` and `quests`, and each one is described in the file. A multiplier of `1.0` disables that change. Changing recipe speed requires a restart.

## Building

Requires JDK 25.

```
./gradlew build           # jar in build/libs
./gradlew spotlessApply   # formatting (the build fails without it)
./gradlew runClient25     # dev client
```

Dependency versions in `dependencies.gradle` match the GTNH 2.9.0 manifest.
