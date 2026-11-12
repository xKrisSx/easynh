# GTNH Easy Mode

Prywatny addon do GregTech: New Horizons **2.9.0** z lekko złagodzonymi zasadami. Działa w singleplayerze i na serwerze. Na serwerze mod musi być zainstalowany po obu stronach, inaczej NEI pokaże stare czasy receptur.

## Co zmienia (domyślnie)

| Zmiana | Domyślnie | Jak |
|---|---|---|
| Czas receptur GT (wszystkie maszyny, multibloki, assembly line + research) | 2x szybciej, czyli też 2x mniej EU łącznie | po załadowaniu wszystkich modów dzieli `mDuration` w każdej `RecipeMap` |
| Drop z naturalnych rud GT / BartWorks / GT++ | 2x (małe rudy też 2x) | mixin w `IOreAdapter#getOreDrops`, przez który przechodzą gracz, minery i ore drilling plant |
| EXP narzędzi Tinkers (IguanaTweaks) | 2x | mixin w `LevelingLogic#addXP` |
| Naprawa narzędzi Tinkers | min. 10% max wytrzymałości na sztabkę | mixin w `ModToolRepair#calculateIncrease`; lepszy wynik z oryginalnego wzoru zostaje |
| Lapis (Luck) w Tinkers stackowalny | 2x: max 900 lapis, Fortune/Looting VI | podmiana `TinkerTools.modLapis` na `ModLapisStacked`; każde 450 lapis = 1 slot modyfikatora, progi 100/300/450 w każdej porcji |
| Limit fortune na rudach GT | 6 zamiast 3 | `@ModifyConstant` w `getBigOreDrops` (GT/BW/GT++); działa przy domyślnym `oreDropSystem=FortuneItem` |
| Rozdział questów „GTNH Easy Mode” | Iron Hammer za 16 sztabek żelaza, Iron Lumber Axe za 12 | mixin w `QuestCommandDefaults#load/loadLegacy` dodaje rozdział przy każdym (prze)ładowaniu bazy questów; narzędzia buduje `ToolBuilder` TiC |
| Quest „Silk Without Bees” (powtarzalny) | 64 Woven Cotton → 8 Woven Silk | ten sam rozdział questów; Woven Cotton z Pam's HarvestCraft, Woven Silk z Forestry |
| Ore Drilling Plant też dostaje mnożnik rud | 2x | ODP kopie z silk touch, więc obsługuje to osobny mixin w `OreManager#mineBlock` |

Pominięte celowo:
- **Mapy paliw i generatorów** (diesel, gas turbine, naquadah reactor, LFTR, HTGR…), bo tam czas oznacza czas spalania, więc „przyspieszenie” osłabiłoby paliwo. Pomijane są też receptury z ujemnym EU/t.
- **Silk touch i rudy postawione przez gracza**, żeby nie dało się duplikować rud. Ruda GT++ zawsze udaje „naturalną”, więc jej bloki (przy drop systemie Block) nie są mnożone.
- **Receptury generowane w locie** (replikator, mass fab, scanner/printer itp.) nie są w `RecipeMap`, więc ich nie przyspieszamy.

## Konfiguracja

`config/gtnheasymode.cfg` (tworzy się przy pierwszym starcie):
- `recipes`:
  - `recipeSpeedMultiplier`
  - `recipeMapBlacklist`
  - `logRecipeMaps`: wypisuje do logu nazwy wszystkich map i liczbę przyspieszonych receptur
- `ores`:
  - `oreDropMultiplier`
  - `smallOreDropMultiplier`
  - `gtOreFortuneCap`
- `tinkers`:
  - `toolXpMultiplier`
  - `minRepairPercentPerIngot`
  - `lapisMaxStacks`
- `quests`:
  - `enableToolQuests`
  - `hammerQuestIronCost`
  - `lumberAxeQuestIronCost`
  - `enableSilkQuest`
  - `silkQuestCottonCost`
  - `silkQuestSilkReward`

Ustawienie `1.0` (albo `0` dla naprawy) wyłącza daną zmianę. Zmiana czasu receptur wymaga restartu.

## Development

Wymaga JDK 25 (Gradle JVM w IntelliJ: `corretto-25`).

```
./gradlew build              # jar w build/libs (ten bez -dev/-sources wrzucasz do mods/)
./gradlew spotlessApply      # formatowanie, build pada bez tego
./gradlew runClient25        # klient dev z GT5U + TiC + Iguana
./gradlew runServer25        # serwer dev
```

Wersje zależności w `dependencies.gradle` odpowiadają manifestowi GTNH 2.9.0
(`DreamAssemblerXXL/releases/manifests/2.9.0.json`). Przy aktualizacji paczki podbij je stamtąd.
