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
import bq_standard.tasks.TaskRetrieval;
import cpw.mods.fml.common.registry.GameRegistry;
import pl.notkris.gtnheasymode.Config;
import pl.notkris.gtnheasymode.GTNHEasyMode;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.tools.TinkerTools;

/**
 * Adds a small "GTNH Easy Mode" chapter to the quest book: trade iron ingots for an iron Tinkers' Hammer / Lumber Axe,
 * and (repeatable) Woven Cotton for Forestry's Woven Silk.
 * <p>
 * Called whenever BetterQuesting (re)loads its quest database, right after it was cleared, so the quests survive
 * default database reloads and quest progress is restored for them like for any other quest.
 */
public final class EasyQuests {

    private static final UUID QUEST_LINE_ID = id("questline");
    private static final UUID HAMMER_QUEST_ID = id("quest.iron_hammer");
    private static final UUID LUMBER_AXE_QUEST_ID = id("quest.iron_lumber_axe");
    private static final UUID SILK_QUEST_ID = id("quest.woven_silk");

    /** Forestry craftingMaterial metas: pulsatingDust, pulsatingMesh, silkWisp, wovenSilk, ... */
    private static final int WOVEN_SILK_META = 3;

    private EasyQuests() {}

    private static UUID id(String name) {
        return UUID.nameUUIDFromBytes((GTNHEasyMode.MODID + ":" + name).getBytes(StandardCharsets.UTF_8));
    }

    public static void inject() {
        if (!Config.enableToolQuests && !Config.enableSilkQuest) return;
        try {
            IQuestLine line = new QuestLine();
            line.setProperty(NativeProps.NAME, "GTNH Easy Mode");
            line.setProperty(
                NativeProps.DESC,
                "Small shortcuts added by the GTNH Easy Mode addon: early big Tinkers' tools and silk without bees.");
            line.setProperty(NativeProps.VISIBILITY, EnumQuestVisibility.ALWAYS);
            ItemStack chapterIcon = null;
            int added = 0;

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
                    chapterIcon = hammer;
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
                        0);
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
                        0);
                    added += 2;
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
                        48);
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

    private static BigItemStack iron(int amount) {
        return new BigItemStack(Items.iron_ingot, amount).setOreDict("ingotIron");
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

    private static void addQuest(IQuestLine line, UUID questId, String name, String desc, BigItemStack cost,
        ItemStack rewardStack, boolean repeatable, int x, int y) {
        IQuest quest = new QuestInstance();
        quest.setProperty(NativeProps.NAME, name);
        quest.setProperty(NativeProps.DESC, desc);
        quest.setProperty(NativeProps.ICON, new BigItemStack(rewardStack));
        quest.setProperty(NativeProps.VISIBILITY, EnumQuestVisibility.ALWAYS);
        // 0 = can be repeated right after claiming, -1 = one-time
        quest.setProperty(NativeProps.REPEAT_TIME, repeatable ? 0 : -1);

        TaskRetrieval task = new TaskRetrieval();
        task.consume = true;
        task.requiredItems.add(cost);
        quest.getTasks()
            .add(0, task);

        RewardItem reward = new RewardItem();
        reward.items.add(new BigItemStack(rewardStack.copy()));
        quest.getRewards()
            .add(0, reward);

        QuestDatabase.INSTANCE.put(questId, quest);
        line.put(questId, new QuestLineEntry(x, y));
    }
}
