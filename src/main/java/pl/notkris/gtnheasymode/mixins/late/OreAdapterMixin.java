package pl.notkris.gtnheasymode.mixins.late;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import gregtech.common.ores.BWOreAdapter;
import gregtech.common.ores.GTOreAdapter;
import gregtech.common.ores.GTPPOreAdapter;
import gregtech.common.ores.OreInfo;
import pl.notkris.gtnheasymode.Config;
import pl.notkris.gtnheasymode.DropMultiplier;

/**
 * Every way of breaking a GT ore (player, miner, ore drilling plant) ends up in IOreAdapter#getOreDrops.
 */
@Mixin(value = { GTOreAdapter.class, BWOreAdapter.class, GTPPOreAdapter.class }, remap = false)
public abstract class OreAdapterMixin {

    @Inject(method = "getOreDrops", at = @At("RETURN"))
    private void gtnheasymode$multiplyDrops(Random random, OreInfo<?> info, boolean silktouch, int fortune,
        CallbackInfoReturnable<ArrayList<ItemStack>> cir) {
        // Player-placed ores are not natural, so re-placing and re-mining cannot duplicate them.
        if (silktouch || info == null || !info.isNatural) return;

        double mult = info.isSmall ? Config.smallOreDropMultiplier : Config.oreDropMultiplier;
        // GT++ ores always report isNatural, so never multiply their block drops (would allow infinite duplication).
        boolean skipBlocks = (Object) this instanceof GTPPOreAdapter;
        DropMultiplier.multiply(cir.getReturnValue(), mult, random, skipBlocks);
    }
}
