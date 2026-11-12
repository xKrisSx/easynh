package pl.notkris.gtnheasymode.mixins.late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import gregtech.common.ores.BWOreAdapter;
import gregtech.common.ores.GTOreAdapter;
import gregtech.common.ores.GTPPOreAdapter;
import pl.notkris.gtnheasymode.Config;

/**
 * GT clamps fortune for big ores: {@code if (fortune > 3) fortune = 3;}. The literal 3 appears only in that line in
 * getBigOreDrops of all three adapters (checked against GT5U 5.09.54.265).
 */
@Mixin(value = { GTOreAdapter.class, BWOreAdapter.class, GTPPOreAdapter.class }, remap = false)
public abstract class OreFortuneCapMixin {

    @ModifyConstant(method = "getBigOreDrops", constant = @Constant(intValue = 3))
    private int gtnheasymode$fortuneCap(int original) {
        return Math.max(original, Config.gtOreFortuneCap);
    }
}
