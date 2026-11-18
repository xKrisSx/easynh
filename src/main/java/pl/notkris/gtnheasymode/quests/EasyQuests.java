package pl.notkris.gtnheasymode.quests;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import betterquesting.api.enums.EnumQuestVisibility;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.IQuestLine;
import betterquesting.api.utils.BigItemStack;
import betterquesting.questing.QuestDatabase;
import betterquesting.questing.QuestInstance;
import betterquesting.questing.QuestLine;
import betterquesting.questing.QuestLineDatabase;
import betterquesting.questing.QuestLineEntry;
import bq_standard.rewards.RewardItem;
import bq_standard.tasks.TaskCheckbox;
import bq_standard.tasks.TaskRetrieval;
import cpw.mods.fml.common.registry.GameRegistry;
import detrav.enums.IDDetraveMetaGeneratedTool01;
import detrav.items.DetravMetaGeneratedTool01;
import gregtech.api.enums.Materials;
import gregtech.api.items.MetaGeneratedTool;
import pl.notkris.gtnheasymode.Config;
import pl.notkris.gtnheasymode.GTNHEasyMode;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.tools.TinkerTools;

/**
 * Adds a small "GTNH Easy Mode" chapter to the quest book: an info quest listing what the addon changes, iron ingots
 * for an iron Tinkers' Hammer / Lumber Axe, diamonds for Extra Utilities' Healing Axe, an Ore Finder Wand for a
 * high-durability Prospector's Scanner (LV), and (repeatable) Woven Cotton for Forestry's Woven Silk.
 * <p>
 * Called whenever BetterQuesting (re)loads its quest database, right after it was cleared, so the quests survive
 * default database reloads and quest progress is restored for them like for any other quest.
 */
public final class EasyQuests {

    private static final UUID QUEST_LINE_ID = id("questline");
    private static final UUID HAMMER_QUEST_ID = id("quest.iron_hammer");
    private static final UUID LUMBER_AXE_QUEST_ID = id("quest.iron_lumber_axe");
    private static final UUID SILK_QUEST_ID = id("quest.woven_silk");
    private static final UUID INFO_QUEST_ID = id("quest.info");
    private static final UUID HEALING_AXE_QUEST_ID = id("quest.healing_axe");
    private static final UUID PROSPECTOR_QUEST_ID = id("quest.prospector_scanner");

    /** Forestry craftingMaterial metas: pulsatingDust, pulsatingMesh, silkWisp, wovenSilk, ... */
    private static final int WOVEN_SILK_META = 3;

    private EasyQuests() {}

    private static UUID id(String name) {
        return UUID.nameUUIDFromBytes((GTNHEasyMode.MODID + ":" + name).getBytes(StandardCharsets.UTF_8));
    }

    public static void inject() {
        if (!Config.enableInfoQuest && !Config.enableToolQuests
            && !Config.enableHealingAxeQuest
            && !Config.enableProspectorQuest
            && !Config.enableSilkQuest) return;
        try {
            IQuestLine line = new QuestLine();
            line.setProperty(NativeProps.NAME, "GTNH Easy Mode");
            line.setProperty(
                NativeProps.DESC,
                "Small shortcuts added by the GTNH Easy Mode addon: early big Tinkers' tools and silk without bees.");
            line.setProperty(NativeProps.VISIBILITY, EnumQuestVisibility.ALWAYS);
            ItemStack chapterIcon = null;
            int added = 0;

            if (Config.enableInfoQuest) {
                ItemStack book = new ItemStack(Items.book);
                chapterIcon = book;
                addQuest(line, INFO_QUEST_ID, "What Easy Mode Changes", infoText(), null, book, false, 0, 0);
                added++;
            }

            if (Config.enableToolQuests) {
                ItemStack hammer = buildIronTool(
                    TinkerTools.hammerHead,
                    TinkerTools.toughRod,
                    TinkerTools.largePlate,
                    TinkerTools.largePlate);
                ItemStack lumberAxe = buildIronTool(
                    TinkerTools.broadAxeHead,
                    TinkerTools.toughRod,
                    TinkerTools.largePlate,
                    TinkerTools.toughBinding);
                if (hammer == null || lumberAxe == null) {
                    GTNHEasyMode.LOG.warn("Could not build iron Tinkers' tools, tool quests not added");
                } else {
                    if (chapterIcon == null) chapterIcon = hammer;
                    addQuest(
                        line,
                        HAMMER_QUEST_ID,
                        "Iron Hammer",
                        "Mining one block at a time gets old fast. Hand over some iron and take a ready-made "
                            + "Iron Hammer - it mines 3x3.",
                        iron(Config.hammerQuestIronCost),
                        hammer,
                        false,
                        0,
                        48);
                    addQuest(
                        line,
                        LUMBER_AXE_QUEST_ID,
                        "Iron Lumber Axe",
                        "Chopping trees log by log? Hand over some iron and take a ready-made Iron Lumber Axe "
                            + "- it fells whole trees.",
                        iron(Config.lumberAxeQuestIronCost),
                        lumberAxe,
                        false,
                        48,
                        48);
                    added += 2;
                }
            }

            if (Config.enableHealingAxeQuest) {
                Item healingAxe = GameRegistry.findItem("ExtraUtilities", "defoliageAxe");
                if (healingAxe == null) {
                    GTNHEasyMode.LOG.warn("Healing Axe (Extra Utilities) not found, healing axe quest not added");
                } else {
                    ItemStack axe = new ItemStack(healingAxe);
                    if (chapterIcon == null) chapterIcon = axe;
                    addQuest(
                        line,
                        HEALING_AXE_QUEST_ID,
                        "Healing Axe",
                        "A symbolic fee for a very handy axe. Hitting a creature heals it at the cost of some of "
                            + "your health (undead take four times the damage instead), and while held it slowly "
                            + "refills your hunger.",
                        new BigItemStack(Items.diamond, Config.healingAxeQuestDiamondCost).setOreDict("gemDiamond"),
                        axe,
                        false,
                        96,
                        48);
                    added++;
                }
            }

            if (Config.enableProspectorQuest) {
                Item wand = GameRegistry.findItem("ifu", "ifu_buildingKit");
                ItemStack scanner = buildProspector();
                if (wand == null || scanner == null) {
                    GTNHEasyMode.LOG.warn(
                        "Ore Finder Wand (IFU) not found or could not build a {} Prospector's Scanner, "
                            + "prospector quest not added",
                        Config.prospectorQuestMaterial);
                } else {
                    if (chapterIcon == null) chapterIcon = scanner;
                    int range = prospectorRange(scanner);
                    addQuest(
                        line,
                        PROSPECTOR_QUEST_ID,
                        "Ore finder upgrade",
                        "The Ore Finder Wand only sees what is right around you. Hand it over and take a "
                            + "Prospector's Scanner (LV) instead - it maps every ore in a "
                            + range
                            + "x"
                            + range
                            + " chunk area and will practically never break.",
                        new BigItemStack(wand, Config.prospectorQuestWandCost),
                        scanner,
                        false,
                        144,
                        48);
                    added++;
                }
            }

            if (Config.enableSilkQuest) {
                Item wovenCotton = GameRegistry.findItem("harvestcraft", "wovencottonItem");
                Item forestryMaterial = GameRegistry.findItem("Forestry", "craftingMaterial");
                if (wovenCotton == null || forestryMaterial == null) {
                    GTNHEasyMode.LOG
                        .warn("Woven Cotton (HarvestCraft) or Woven Silk (Forestry) not found, silk quest not added");
                } else {
                    ItemStack wovenSilk = new ItemStack(forestryMaterial, Config.silkQuestSilkReward, WOVEN_SILK_META);
                    if (chapterIcon == null) chapterIcon = wovenSilk;
                    addQuest(
                        line,
                        SILK_QUEST_ID,
                        "Silk Without Bees",
                        "Not a fan of bees? Trade Woven Cotton for Woven Silk instead. "
                            + "Repeatable as often as you like.",
                        new BigItemStack(wovenCotton, Config.silkQuestCottonCost),
                        wovenSilk,
                        true,
                        0,
                        96);
                    added++;
                }
            }

            if (added == 0) return;
            line.setProperty(NativeProps.ICON, new BigItemStack(chapterIcon));
            QuestLineDatabase.INSTANCE.put(QUEST_LINE_ID, line);
            // Right after the first chapter ("And So It Begins"), so it's easy to spot.
            QuestLineDatabase.INSTANCE.setOrderIndex(QUEST_LINE_ID, 1);
            GTNHEasyMode.LOG.info("Added GTNH Easy Mode quest chapter with {} quests", added);
        } catch (Exception e) {
            GTNHEasyMode.LOG.error("Failed to add GTNH Easy Mode quests", e);
        }
    }

    /** Built from the current config, so it always matches what is actually active. */
    private static String infoText() {
        StringBuilder sb = new StringBuilder();
        sb.append("The GTNH Easy Mode addon slightly relaxes the rules. This quest is just information - ")
            .append("tick the box when you have read it.\n\n");

        sb.append("\u00a76Changed\u00a7r\n");
        if (Config.recipeSpeedMultiplier > 1.0) {
            sb.append("- GregTech recipes run ")
                .append(fmt(Config.recipeSpeedMultiplier))
                .append("x faster (all machines, multiblocks, assembly line and research), so they also cost ")
                .append(fmt(Config.recipeSpeedMultiplier))
                .append("x less EU in total. Fuels and generators are not affected.\n");
        }
        if (Config.toolXpMultiplier != 1.0) {
            sb.append("- Tinkers' tools gain ")
                .append(fmt(Config.toolXpMultiplier))
                .append("x XP.\n");
        }
        if (Config.minRepairPercentPerIngot > 0) {
            sb.append("- Repairing a Tinkers' tool restores at least ")
                .append(fmt(Config.minRepairPercentPerIngot))
                .append("% of its durability per ingot.\n");
        }
        if (Config.lapisMaxStacks > 1) {
            sb.append("- The Tinkers' Lapis (Luck) modifier can be applied ")
                .append(Config.lapisMaxStacks)
                .append(" times (")
                .append(450 * Config.lapisMaxStacks)
                .append(" lapis, Fortune/Looting up to ")
                .append(3 * Config.lapisMaxStacks)
                .append("). Each 450 lapis costs one modifier slot.\n");
        }

        sb.append("\n\u00a76Drop rates\u00a7r\n");
        sb.append("- Naturally generated GregTech / BartWorks / GT++ ores: ")
            .append(fmt(Config.oreDropMultiplier))
            .append("x drops (players, miners and the Ore Drilling Plant).\n");
        sb.append("- Small ores: ")
            .append(fmt(Config.smallOreDropMultiplier))
            .append("x drops.\n");
        sb.append("- Fortune on big GT ores counts up to level ")
            .append(Config.gtOreFortuneCap)
            .append(" (normally 3).\n");
        sb.append("- Silk touch and player-placed ores are never multiplied, so ores can't be duplicated.\n");

        sb.append("\n\u00a76Added\u00a7r\n");
        if (Config.enableWovenBackpackRecipes) {
            sb.append("- Crafting table recipe for Woven Backpacks (Miner's, Digger's, Forester's, Hunter's, ")
                .append("Adventurer's, Builder's): the normal backpack in the middle, 8 Woven Silk around it. ")
                .append("Its contents are kept.\n");
        }
        sb.append("- This quest chapter, trading simple materials for:\n");
        if (Config.enableToolQuests) {
            sb.append("  - Iron Hammer (")
                .append(Config.hammerQuestIronCost)
                .append(" iron) and Iron Lumber Axe (")
                .append(Config.lumberAxeQuestIronCost)
                .append(" iron)\n");
        }
        if (Config.enableHealingAxeQuest) {
            sb.append("  - Healing Axe (")
                .append(Config.healingAxeQuestDiamondCost)
                .append(" diamonds)\n");
        }
        if (Config.enableProspectorQuest) {
            sb.append("  - Prospector's Scanner (LV) (")
                .append(Config.prospectorQuestWandCost)
                .append(" Ore Finder Wand)");
            ItemStack scanner = buildProspector();
            if (scanner != null) {
                int range = prospectorRange(scanner);
                sb.append(": ")
                    .append(Config.prospectorQuestMaterial)
                    .append(", scans ")
                    .append(range)
                    .append("x")
                    .append(range)
                    .append(range == 1 ? " chunk" : " chunks")
                    .append(", ")
                    .append(String.format("%,d", MetaGeneratedTool.getToolMaxDamage(scanner)))
                    .append(" durability");
                if (!Config.prospectorQuestDurabilityMaterial.isEmpty()) {
                    sb.append(" (as if made of ")
                        .append(Config.prospectorQuestDurabilityMaterial)
                        .append(")");
                }
            }
            sb.append("\n");
        }
        if (Config.enableSilkQuest) {
            sb.append("  - Woven Silk, repeatable (")
                .append(Config.silkQuestCottonCost)
                .append(" Woven Cotton -> ")
                .append(Config.silkQuestSilkReward)
                .append(" Woven Silk)\n");
        }
        sb.append("\nAll values can be changed in config/gtnheasymode.cfg.");
        return sb.toString();
    }

    private static String fmt(double value) {
        return value == Math.rint(value) ? String.valueOf((long) value) : String.valueOf(value);
    }

    private static BigItemStack iron(int amount) {
        return new BigItemStack(Items.iron_ingot, amount).setOreDict("ingotIron");
    }

    private static Materials material(String name) {
        if (name == null || name.isEmpty()) return null;
        Materials material = Materials.get(name);
        return material == null || material == Materials._NULL ? null : material;
    }

    /**
     * Scanner made of {@code prospectorQuestMaterial} (which sets its look and range), with the durability it would
     * have if made of {@code prospectorQuestDurabilityMaterial}. GT stores the max damage in the stack's NBT, so the
     * override sticks.
     */
    private static ItemStack buildProspector() {
        DetravMetaGeneratedTool01 tool = DetravMetaGeneratedTool01.INSTANCE;
        Materials material = material(Config.prospectorQuestMaterial);
        if (tool == null || material == null) return null;
        ItemStack scanner = tool
            .getToolWithStats(IDDetraveMetaGeneratedTool01.ProspectorScannerLV.ID, 1, material, Materials.Steel, null);
        if (scanner == null) return null;

        Materials durability = material(Config.prospectorQuestDurabilityMaterial);
        if (durability != null && scanner.hasTagCompound()) {
            long maxDamage = (long) (100L * durability.mDurability
                * tool.getToolStats(scanner)
                    .getMaxDurabilityMultiplier());
            scanner.getTagCompound()
                .getCompoundTag("GT.ToolStats")
                .setLong("MaxDamage", maxDamage);
        }
        return scanner;
    }

    /** Side length in chunks, computed like Detrav's BehaviourDetravToolProspector. */
    private static int prospectorRange(ItemStack scanner) {
        int size = DetravMetaGeneratedTool01.INSTANCE.getHarvestLevel(scanner, "") / 2 + scanner.getItemDamage() / 4;
        if (size % 2 == 0) size++;
        return size;
    }

    private static ItemStack buildIronTool(Item head, Item handle, Item accessory, Item extra) {
        int iron = TinkerTools.MaterialID.Iron;
        return ToolBuilder.instance.buildTool(
            new ItemStack(head, 1, iron),
            new ItemStack(handle, 1, iron),
            new ItemStack(accessory, 1, iron),
            new ItemStack(extra, 1, iron),
            "");
    }

    /**
     * {@code cost == null} makes an info-only quest (checkbox, no reward); {@code rewardStack} is then just the icon.
     */
    private static void addQuest(IQuestLine line, UUID questId, String name, String desc, BigItemStack cost,
        ItemStack rewardStack, boolean repeatable, int x, int y) {
        IQuest quest = new QuestInstance();
        quest.setProperty(NativeProps.NAME, name);
        quest.setProperty(NativeProps.DESC, desc);
        quest.setProperty(NativeProps.ICON, new BigItemStack(rewardStack));
        quest.setProperty(NativeProps.VISIBILITY, EnumQuestVisibility.ALWAYS);
        // 0 = can be repeated right after claiming, -1 = one-time
        quest.setProperty(NativeProps.REPEAT_TIME, repeatable ? 0 : -1);

        if (cost == null) {
            quest.getTasks()
                .add(0, new TaskCheckbox());
        } else {
            TaskRetrieval task = new TaskRetrieval();
            task.consume = true;
            task.requiredItems.add(cost);
            quest.getTasks()
                .add(0, task);

            RewardItem reward = new RewardItem();
            reward.items.add(new BigItemStack(rewardStack.copy()));
            quest.getRewards()
                .add(0, reward);
        }

        QuestDatabase.INSTANCE.put(questId, quest);
        line.put(questId, new QuestLineEntry(x, y));
    }
}
