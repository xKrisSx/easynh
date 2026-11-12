package pl.notkris.gtnheasymode.mixins.late;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import pl.notkris.gtnheasymode.Config;
import tconstruct.modifiers.tools.ModToolRepair;

/**
 * Vanilla TiC repair: (50 * items + baseDurability * 0.4 * materialValue) * modifierPenalty * repairCountPenalty /
 * repairCost. For big tools (hammer, excavator...) that is only a few % per ingot. This enforces a floor.
 * Also applies to IguanaTweaks' ModLimitedToolRepair, which extends ModToolRepair.
 */
@Mixin(value = ModToolRepair.class, remap = false)
public abstract class ModToolRepairMixin {

    /** TiC part value of one ingot (shard = 1, block = 18). */
    private static final double INGOT_VALUE = 2.0;

    @Inject(method = "calculateIncrease", at = @At("RETURN"), cancellable = true)
    private void gtnheasymode$minimumRepair(ItemStack tool, int materialValue, int itemsUsed,
        CallbackInfoReturnable<Integer> cir) {
        if (Config.minRepairPercentPerIngot <= 0 || materialValue <= 0) return;

        NBTTagCompound tags = tool.getTagCompound()
            .getCompoundTag("InfiTool");
        int total = tags.getInteger("TotalDurability");
        if (total <= 0) total = tags.getInteger("BaseDurability");
        if (total <= 0) return;

        int minimum = (int) Math
            .ceil(total * (Config.minRepairPercentPerIngot / 100.0) * (materialValue / INGOT_VALUE));
        if (cir.getReturnValueI() < minimum) cir.setReturnValue(minimum);
    }
}
