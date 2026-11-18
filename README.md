# GTNH Easy Mode

🇵🇱 Polski | 🇬🇧 [English](README.en.md)

Addon do **GregTech: New Horizons 2.9.0**, który łagodzi grind, ale nie wywraca progresji. Każdą zmianę da się dostroić albo wyłączyć w configu. Działa w singleplayerze i na serwerze (wtedy mod musi być u klienta i na serwerze).

## Co zmienia

- **Szybsze receptury GT**: 4x krótszy czas we wszystkich maszynach, multiblokach i assembly line, a przez to też mniej EU łącznie. Paliwa i generatory zostają bez zmian.
- **Więcej rudy**: 2x drop z naturalnych rud GT / BartWorks / GT++ (także małych i z Ore Drilling Plant). Fortune na rudach GT działa do poziomu VI. Silk touch i rudy postawione przez gracza nie są mnożone.
- **Tinkers' Construct**: 2x EXP narzędzi, naprawa przywraca co najmniej 10% wytrzymałości na sztabkę, Lapis (Luck) można nałożyć dwa razy (Fortune/Looting VI).
- **Woven Backpacki z Forestry**: zwykły plecak (Miner's, Digger's, Forester's, Hunter's, Adventurer's, Builder's) na środku stołu i 8 Woven Silk dookoła daje wersję Woven (45 slotów). Zawartość plecaka zostaje.
- **Rozdział questów „GTNH Easy Mode”**:
  - lista wszystkich zmian z aktualnymi wartościami z configu,
  - Iron Hammer i Iron Lumber Axe (TiC) za sztabki żelaza,
  - Healing Axe (Extra Utilities) za diamenty,
  - Prospector's Scanner z bardzo dużą wytrzymałością za Ore Finder Wand,
  - powtarzalna wymiana Woven Cotton na Woven Silk (jedwab bez pszczół),
  - powtarzalna wymiana 4 dowolnych circuitów danego tieru (LV–UHV) na lootbag tego tieru, odblokowywana po wejściu na kolejny tier (np. LV po „Tier 2 (MV)”).

## Instalacja

Wrzuć jar z [Releases](../../releases) (albo `build/libs/easynh.jar`) do folderu `mods/`.

## Konfiguracja

Plik `config/gtnheasymode.cfg` tworzy się przy pierwszym uruchomieniu. Opcje są podzielone na sekcje `recipes`, `ores`, `tinkers` i `quests`, każda ma opis w pliku. Mnożnik `1.0` wyłącza daną zmianę. Zmiana szybkości receptur wymaga restartu.

## Budowanie

Wymaga JDK 25.

```
./gradlew build           # jar w build/libs
./gradlew spotlessApply   # formatowanie (bez tego build nie przejdzie)
./gradlew runClient25     # klient deweloperski
```

Wersje zależności w `dependencies.gradle` odpowiadają manifestowi GTNH 2.9.0.
