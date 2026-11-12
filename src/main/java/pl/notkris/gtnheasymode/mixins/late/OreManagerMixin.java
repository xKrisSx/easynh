package pl.notkris.gtnheasymode.mixins.late;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import gregtech.common.ores.GTPPOreAdapter;
import gregtech.common.ores.IOreAdapter;
import gregtech.common.ores.OreInfo;
import gregtech.common.ores.OreManager;
import pl.notkris.gtnheasymode.Config;
import pl.notkris.gtnheasymode.DropMultiplier;

/**
 * The Ore Drilling Plant mines through OreManager.mineBlock with silktouch = true and gets ore blocks. OreAdapterMixin
 * skips silk touch (players must not multiply with silk touch), so machine silk mining is handled here. Dropped GT/BW
 * ore blocks are non-natural, so mining them again is not multiplied again.
 */
@Mixin(value = OreManager.class, remap = false)
public abstract class OreManagerMixin {

    @Redirect(
        method = "mineBlock",
        at = @At(
            value = "INVOKE",
            target = "Lgregtech/common/ores/IOreAdapter;getOreDrops(Ljava/util/Random;Lgregtech/common/ores/OreInfo;ZI)Ljava/util/ArrayList;"))
    private static ArrayList<ItemStack> gtnheasymode$machineSilkDrops(IOreAdapter<?> adapter, Random random,
        OreInfo<?> info, boolean silktouch, int fortune) {
        ArrayList<ItemStack> drops = adapter.getOreDrops(random, info, silktouch, fortune);
        if (silktouch && info.isNatural) {
            double mult = info.isSmall ? Config.smallOreDropMultiplier : Config.oreDropMultiplier;
            DropMultiplier.multiply(drops, mult, random, adapter instanceof GTPPOreAdapter);
        }
        return drops;
    }
}
