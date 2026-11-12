package pl.notkris.gtnheasymode.mixins.late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import iguanaman.iguanatweakstconstruct.leveling.LevelingLogic;
import pl.notkris.gtnheasymode.Config;

@Mixin(value = LevelingLogic.class, remap = false)
public abstract class LevelingLogicMixin {

    @ModifyVariable(method = "addXP", at = @At("HEAD"), argsOnly = true)
    private static long gtnheasymode$multiplyXp(long xp) {
        if (xp <= 0 || Config.toolXpMultiplier == 1.0) return xp;
        return Math.max(1L, Math.round(xp * Config.toolXpMultiplier));
    }
}
