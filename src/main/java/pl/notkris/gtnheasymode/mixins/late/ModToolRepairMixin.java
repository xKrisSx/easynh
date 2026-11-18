package pl.notkris.gtnheasymode.mixins.late;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import pl.notkris.gtnheasymode.tinkers.RepairFloor;
import tconstruct.modifiers.tools.ModToolRepair;

/**
 * Minimum repair per ingot for TiC's own repair modifier (also IguanaTweaks' ModLimitedToolRepair, which extends
 * it). Only used when Tinkers' Gregworks is absent - see {@link ModTGregRepairMixin}.
 */
@Mixin(value = ModToolRepair.class, remap = false)
public abstract class ModToolRepairMixin {

    @Inject(method = "calculateIncrease", at = @At("RETURN"), cancellable = true)
    private void gtnheasymode$minimumRepair(ItemStack tool, int materialValue, int itemsUsed,
        CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(RepairFloor.apply(tool, materialValue, cir.getReturnValueI()));
    }
}
