package pl.notkris.gtnheasymode;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {

    public static final String CATEGORY_RECIPES = "recipes";
    public static final String CATEGORY_ORES = "ores";
    public static final String CATEGORY_TINKERS = "tinkers";

    // Recipes
    public static double recipeSpeedMultiplier = 4.0;
    public static String[] recipeMapBlacklist = { "gt.recipe.dieselgeneratorfuel",
        "gt.recipe.extremedieselgeneratorfuel", "gt.recipe.gasturbinefuel", "gt.recipe.thermalgeneratorfuel",
        "gt.recipe.semifluidgeneratorfuels", "gt.recipe.semifluidboilerfuels", "gt.recipe.plasmageneratorfuels",
        "gt.recipe.magicfuels", "gt.recipe.rocketenginefuel", "gt.recipe.largeboilerfakefuels",
        "gt.recipe.RTGgenerators", "gt.recipe.smallnaquadahreactor", "gt.recipe.largenaquadahreactor",
        "gt.recipe.hugenaquadahreactor", "gt.recipe.extrahugenaquadahreactor", "gt.recipe.fluidnaquadahreactor",
        "gt.recipe.fissionfuel", "gt.recipe.lftr", "gt.recipe.thermalboiler", "gt.recipe.solartower",
        "gt.recipe.ic2nuke", "kubatech.htgrrecipes", "bw.fuels.acidgens", "gg.recipe.naquadah_reactor" };
    public static boolean logRecipeMaps = false;
    public static boolean enableWovenBackpackRecipes = true;

    // Ores
    public static double oreDropMultiplier = 2.0;
    public static double smallOreDropMultiplier = 2.0;
    public static int gtOreFortuneCap = 6;

    // Tinkers
    public static double toolXpMultiplier = 2.0;
    public static double minRepairPercentPerIngot = 10.0;
    public static int lapisMaxStacks = 2;

    // Quests
    public static final String CATEGORY_QUESTS = "quests";
    public static boolean enableInfoQuest = true;
    public static boolean enableToolQuests = true;
    public static int hammerQuestIronCost = 16;
    public static int lumberAxeQuestIronCost = 12;
    public static boolean enableHealingAxeQuest = true;
    public static int healingAxeQuestDiamondCost = 8;
    public static boolean enableProspectorQuest = true;
    public static String prospectorQuestMaterial = "Orichalcum";
    public static String prospectorQuestDurabilityMaterial = "Infinity";
    public static int prospectorQuestWandCost = 1;
    public static boolean enableSilkQuest = true;
    public static int silkQuestCottonCost = 64;
    public static int silkQuestSilkReward = 8;

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        recipeSpeedMultiplier = configuration
            .get(
                CATEGORY_RECIPES,
                "recipeSpeedMultiplier",
                recipeSpeedMultiplier,
                "GregTech recipe durations are divided by this value (2.0 = twice as fast, also halves total EU cost). "
                    + "1.0 disables. Requires restart.",
                1.0,
                64.0)
            .getDouble();
        recipeMapBlacklist = configuration.getStringList(
            "recipeMapBlacklist",
            CATEGORY_RECIPES,
            recipeMapBlacklist,
            "Recipe map names (case-insensitive, exact) that are not sped up. Fuel/generator maps use duration as "
                + "burn time, so speeding them up would make fuels worse. Maps using GT's FuelBackend and "
                + "energy-producing recipes (negative EU/t) are always skipped. Use logRecipeMaps to see names.");
        logRecipeMaps = configuration.getBoolean(
            "logRecipeMaps",
            CATEGORY_RECIPES,
            logRecipeMaps,
            "Log every recipe map name and whether it was sped up (useful for tuning the blacklist).");
        enableWovenBackpackRecipes = configuration.getBoolean(
            "enableWovenBackpackRecipes",
            CATEGORY_RECIPES,
            enableWovenBackpackRecipes,
            "Adds crafting table recipes upgrading Forestry's Miner's, Digger's, Forester's, Hunter's, Adventurer's "
                + "and Builder's Backpack to the Woven version: the backpack in the middle, 8 Woven Silk around it. "
                + "Contents are kept.");

        oreDropMultiplier = configuration
            .get(
                CATEGORY_ORES,
                "oreDropMultiplier",
                oreDropMultiplier,
                "Multiplier for drops of naturally generated GregTech / BartWorks / GT++ ores (players, miners, "
                    + "ore drilling plants). Silk touch and player-placed ores are not affected. 1.0 disables.",
                0.0,
                64.0)
            .getDouble();
        smallOreDropMultiplier = configuration
            .get(
                CATEGORY_ORES,
                "smallOreDropMultiplier",
                smallOreDropMultiplier,
                "Same as oreDropMultiplier, but for small ores.",
                0.0,
                64.0)
            .getDouble();
        gtOreFortuneCap = configuration.getInt(
            "gtOreFortuneCap",
            CATEGORY_ORES,
            gtOreFortuneCap,
            3,
            100,
            "GregTech normally ignores Fortune above III on big ores. Fortune levels up to this value now count. "
                + "Only matters with oreDropSystem=FortuneItem (GTNH default). 3 = vanilla GT behaviour.");

        toolXpMultiplier = configuration
            .get(
                CATEGORY_TINKERS,
                "toolXpMultiplier",
                toolXpMultiplier,
                "Multiplier for XP gained by Tinkers' tools (IguanaTweaks leveling). 1.0 disables.",
                0.0,
                100.0)
            .getDouble();
        minRepairPercentPerIngot = configuration.get(
            CATEGORY_TINKERS,
            "minRepairPercentPerIngot",
            minRepairPercentPerIngot,
            "Minimum % of total durability restored per ingot (or ingot-equivalent) when repairing a Tinkers' tool. "
                + "Better vanilla results are kept. 0 disables.",
            0.0,
            100.0)
            .getDouble();
        lapisMaxStacks = configuration.getInt(
            "lapisMaxStacks",
            CATEGORY_TINKERS,
            lapisMaxStacks,
            1,
            10,
            "How many times the Lapis (Luck) modifier can be applied. Each 450 lapis costs one modifier slot and "
                + "gives up to +3 Fortune/Looting (2 = Fortune VI at 900 lapis). 1 = vanilla Tinkers. Requires restart.");

        enableInfoQuest = configuration.getBoolean(
            "enableInfoQuest",
            CATEGORY_QUESTS,
            enableInfoQuest,
            "Adds an info quest (just a checkbox) listing everything this addon changes, with current config values.");
        enableToolQuests = configuration.getBoolean(
            "enableToolQuests",
            CATEGORY_QUESTS,
            enableToolQuests,
            "Adds a 'GTNH Easy Mode' quest chapter: trade iron ingots for an iron Tinkers' Hammer and Lumber Axe.");
        hammerQuestIronCost = configuration
            .getInt("hammerQuestIronCost", CATEGORY_QUESTS, hammerQuestIronCost, 1, 256, "Iron ingots for the Hammer.");
        lumberAxeQuestIronCost = configuration.getInt(
            "lumberAxeQuestIronCost",
            CATEGORY_QUESTS,
            lumberAxeQuestIronCost,
            1,
            256,
            "Iron ingots for the Lumber Axe.");
        enableHealingAxeQuest = configuration.getBoolean(
            "enableHealingAxeQuest",
            CATEGORY_QUESTS,
            enableHealingAxeQuest,
            "Adds a quest trading diamonds for an Extra Utilities Healing Axe.");
        healingAxeQuestDiamondCost = configuration.getInt(
            "healingAxeQuestDiamondCost",
            CATEGORY_QUESTS,
            healingAxeQuestDiamondCost,
            1,
            256,
            "Diamonds for the Healing Axe.");
        enableProspectorQuest = configuration.getBoolean(
            "enableProspectorQuest",
            CATEGORY_QUESTS,
            enableProspectorQuest,
            "Adds a quest trading an Ore Finder Wand (IFU) for a Prospector's Scanner (LV) made of a strong material.");
        prospectorQuestMaterial = configuration.getString(
            "prospectorQuestMaterial",
            CATEGORY_QUESTS,
            prospectorQuestMaterial,
            "GregTech material the Prospector's Scanner is made of. Its tool quality sets the scan range: "
                + "quality 0-2 = 1x1 chunk (Orichalcum, Vulcanite, Iron), 3-6 = 3x3 (Steel, Neutronium, Tritanium), "
                + "Infinity (17) = 9x9.");
        prospectorQuestDurabilityMaterial = configuration.getString(
            "prospectorQuestDurabilityMaterial",
            CATEGORY_QUESTS,
            prospectorQuestDurabilityMaterial,
            "The scanner gets the durability it would have if made of this material, without changing its range "
                + "(tool durability: Infinity 2621440, Tritanium 1435392, Neutronium 655360, Steel 512). "
                + "Empty = durability of prospectorQuestMaterial.");
        prospectorQuestWandCost = configuration.getInt(
            "prospectorQuestWandCost",
            CATEGORY_QUESTS,
            prospectorQuestWandCost,
            1,
            64,
            "Ore Finder Wands for the Prospector's Scanner.");
        enableSilkQuest = configuration.getBoolean(
            "enableSilkQuest",
            CATEGORY_QUESTS,
            enableSilkQuest,
            "Adds a repeatable quest trading Woven Cotton (Pam's HarvestCraft) for Woven Silk (Forestry).");
        silkQuestCottonCost = configuration
            .getInt("silkQuestCottonCost", CATEGORY_QUESTS, silkQuestCottonCost, 1, 1024, "Woven Cotton per trade.");
        silkQuestSilkReward = configuration
            .getInt("silkQuestSilkReward", CATEGORY_QUESTS, silkQuestSilkReward, 1, 64, "Woven Silk per trade.");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
