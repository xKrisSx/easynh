package pl.notkris.gtnheasymode.tinkers;

import java.util.Arrays;
import java.util.List;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import tconstruct.library.modifier.ModificationInfo;
import tconstruct.library.tools.ToolCore;
import tconstruct.modifiers.tools.ModLapis;

/**
 * Lapis (Luck) modifier that can be applied several times. Every 450 lapis is one "stack" that costs one modifier
 * slot and gives up to 3 levels of Fortune/Looting: 100 / 300 / 450 lapis within a stack = +1 / +2 / +3 levels.
 * Two stacks = 900 lapis = Fortune VI.
 * <p>
 * Passive growth from using the tool (1/50 chance per use, as in vanilla TiC) never crosses into an unpaid stack.
 */
public class ModLapisStacked extends ModLapis {

    public static final int STACK = 450;

    private final int maxStacks;

    public ModLapisStacked(ModLapis original, int maxStacks) {
        super(
            original.effectIndex,
            original.stacks.toArray(new ItemStack[0]),
            original.increase.stream()
                .mapToInt(Integer::intValue)
                .toArray());
        this.maxStacks = maxStacks;
        this.max = STACK * maxStacks;
    }

    public static int luckLevel(int amount) {
        int level = (amount / STACK) * 3;
        int rem = amount % STACK;
        if (rem >= 300) level += 2;
        else if (rem >= 100) level += 1;
        return level;
    }

    private static int ceilDiv(int a, int b) {
        return (a + b - 1) / b;
    }

    /** Stacks already paid for with a modifier slot. */
    private int paidStacks(NBTTagCompound tags) {
        if (!tags.hasKey(key)) return 0;
        return Math.max(1, ceilDiv(tags.getIntArray(key)[0], STACK));
    }

    private int extraSlotsNeeded(NBTTagCompound tags, int current, int add) {
        int needed = Math.max(1, ceilDiv(current + add, STACK));
        return Math.max(0, needed - paidStacks(tags));
    }

    @Override
    protected boolean canModify(ItemStack tool, ItemStack[] input) {
        if (!(tool.getItem() instanceof ToolCore toolItem)) return false;
        if (!validType(toolItem)) return false;

        NBTTagCompound tags = tool.getTagCompound()
            .getCompoundTag("InfiTool");
        if (tags.getBoolean("Silk Touch")) return false;

        int current = tags.hasKey(key) ? tags.getIntArray(key)[0] : 0;
        int add = matchingAmount(input, tool).total();
        if (current + add > max) return false;

        return tags.getInteger("Modifiers") >= extraSlotsNeeded(tags, current, add);
    }

    @Override
    public void modify(ItemStack[] input, ItemStack tool) {
        NBTTagCompound tags = tool.getTagCompound()
            .getCompoundTag("InfiTool");

        int current = tags.hasKey(key) ? tags.getIntArray(key)[0] : 0;
        ModificationInfo info = matchingAmount(input, tool, max);
        int add = info.total();
        int extra = extraSlotsNeeded(tags, current, add);

        if (!tags.hasKey(key)) {
            tags.setBoolean(key, true);
            int tooltipIndex = addToolTip(tool, "§9Luck", "§9Lapis (0/" + max + ")");
            tags.setIntArray(key, new int[] { 0, tooltipIndex });
        }
        tags.setInteger("Modifiers", tags.getInteger("Modifiers") - extra);
        tags.setIntArray("ToRemove", info.toRemove());

        int[] keyPair = tags.getIntArray(key);
        keyPair[0] += add;
        tags.setIntArray(key, keyPair);

        int level = luckLevel(keyPair[0]);
        if (level > 0) {
            List<String> traits = Arrays.asList(((ToolCore) tool.getItem()).getTraits());
            if (traits.contains("weapon")) raiseEnchantment(tool, Enchantment.looting, level);
            if (traits.contains("harvest")) raiseEnchantment(tool, Enchantment.fortune, level);
        }
        updateTooltip(tool, keyPair);
    }

    @Override
    public void midStreamModify(ItemStack tool, ToolCore toolItem) {
        NBTTagCompound tags = tool.getTagCompound()
            .getCompoundTag("InfiTool");
        if (!tags.hasKey(key)) return;

        int[] keyPair = tags.getIntArray(key);
        int cap = Math.min(max, paidStacks(tags) * STACK);
        if (keyPair[0] < cap && random.nextInt(50) == 0) {
            keyPair[0] += 1;
            tags.setIntArray(key, keyPair);
            updateTooltip(tool, keyPair);
        }

        // Same chance-based early upgrades as vanilla TiC, applied within the current stack.
        int amount = keyPair[0];
        int base = (amount / STACK) * 3;
        int rem = amount % STACK;
        int target = luckLevel(amount);
        if (amount / STACK < maxStacks) {
            if (rem >= 350) {
                if (random.nextInt(1000 - (rem - 300)) == 0) target = base + 3;
            } else if (rem >= 125) {
                if (random.nextInt(600 - (rem - 175)) == 0) target = base + 2;
            } else if (rem >= 10) {
                if (random.nextInt(250 - (rem - 25)) == 0) target = base + 1;
            }
        }
        if (target <= 0) return;

        List<String> traits = Arrays.asList(toolItem.getTraits());
        if (traits.contains("weapon")) raiseEnchantment(tool, Enchantment.looting, target);
        if (traits.contains("harvest")) raiseEnchantment(tool, Enchantment.fortune, target);
    }

    /** Never downgrades: vanilla's chance-based upgrades may already be ahead of the deterministic level. */
    private void raiseEnchantment(ItemStack tool, Enchantment enchant, int level) {
        if (EnchantmentHelper.getEnchantmentLevel(enchant.effectId, tool) < level) {
            addEnchantment(tool, enchant, level);
        }
    }

    private void updateTooltip(ItemStack tool, int[] keyPair) {
        NBTTagCompound tags = tool.getTagCompound()
            .getCompoundTag("InfiTool");
        tags.setString("ModifierTip" + keyPair[1], "§9Lapis (" + keyPair[0] + "/" + max + ")");
    }
}
