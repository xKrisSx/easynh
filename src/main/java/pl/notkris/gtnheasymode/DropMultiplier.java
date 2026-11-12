package pl.notkris.gtnheasymode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public final class DropMultiplier {

    private DropMultiplier() {}

    /**
     * Multiplies stack sizes in place. Fractional parts are rolled randomly (1.5x of 1 item = 1 or 2, avg 1.5).
     * Oversized stacks are split by max stack size.
     *
     * @param skipBlocks leave ItemBlock drops untouched (for ores that stay "natural" when placed back)
     */
    public static void multiply(List<ItemStack> drops, double mult, Random rng, boolean skipBlocks) {
        if (drops == null || drops.isEmpty() || mult == 1.0) return;

        List<ItemStack> result = new ArrayList<>(drops.size());
        for (ItemStack stack : drops) {
            if (stack == null || (skipBlocks && stack.getItem() instanceof ItemBlock)) {
                if (stack != null) result.add(stack);
                continue;
            }
            double exact = stack.stackSize * mult;
            int amount = (int) Math.floor(exact);
            if (rng.nextDouble() < exact - amount) amount++;

            int max = Math.max(1, stack.getMaxStackSize());
            while (amount > 0) {
                ItemStack copy = stack.copy();
                copy.stackSize = Math.min(amount, max);
                amount -= copy.stackSize;
                result.add(copy);
            }
        }
        drops.clear();
        drops.addAll(result);
    }
}
