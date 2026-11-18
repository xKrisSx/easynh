package pl.notkris.gtnheasymode.tinkers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import pl.notkris.gtnheasymode.Config;

/**
 * Minimum repair amount for Tinkers' tools, shared by the mixins on TiC's {@code ModToolRepair} and Tinkers'
 * Gregworks' {@code ModTGregRepair} (which replaces the TiC one in GTNH).
 * <p>
 * Vanilla formula: (50 * items + baseDurability * 0.4 * materialValue) * modifierPenalty * repairCountPenalty /
 * repairCost. For big tools (hammer, excavator...) that is only a few % per ingot.
 */
public final class RepairFloor {

    /** TiC part value of one ingot (shard = 1, block = 18). */
    private static final double INGOT_VALUE = 2.0;

    private RepairFloor() {}

    public static int apply(ItemStack tool, int materialValue, int increase) {
        if (Config.minRepairPercentPerIngot <= 0 || materialValue <= 0 || !tool.hasTagCompound()) return increase;

        NBTTagCompound tags = tool.getTagCompound()
            .getCompoundTag("InfiTool");
        int total = tags.getInteger("TotalDurability");
        if (total <= 0) total = tags.getInteger("BaseDurability");
        if (total <= 0) return increase;

        int minimum = (int) Math
            .ceil(total * (Config.minRepairPercentPerIngot / 100.0) * (materialValue / INGOT_VALUE));
        return Math.max(increase, minimum);
    }
}
