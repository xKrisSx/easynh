package pl.notkris.gtnheasymode.mixins.late;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import pl.notkris.gtnheasymode.tinkers.RepairFloor;
import vexatos.tgregworks.integration.modifiers.ModTGregRepair;

/**
 * Tinkers' Gregworks (present in GTNH) registers its own repair modifier, a copy of TiC's ModToolRepair that does not
 * extend it - IguanaTweaks uses it (ModLimitedTGregRepair) for both Tool Station and crafting grid repairs.
 */
@Mixin(value = ModTGregRepair.class, remap = false)
public abstract class ModTGregRepairMixin {

    @Inject(method = "calculateIncrease", at = @At("RETURN"), cancellable = true)
    private void gtnheasymode$minimumRepair(ItemStack tool, int materialValue, int itemsUsed,
        CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(RepairFloor.apply(tool, materialValue, cir.getReturnValueI()));
    }
}
