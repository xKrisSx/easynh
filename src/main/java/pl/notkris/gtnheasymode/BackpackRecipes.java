package pl.notkris.gtnheasymode;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Crafting-table upgrade of Forestry's T1 backpacks (15 slots) to the Woven versions (45 slots): the backpack in the
 * middle, 8 Woven Silk around it. The contents (NBT) and mode (item damage) of the old backpack are kept.
 */
public final class BackpackRecipes {

    private static final String[] BACKPACKS = { "miner", "digger", "forester", "hunter", "adventurer", "builder" };

    /** Forestry craftingMaterial metas: pulsatingDust, pulsatingMesh, silkWisp, wovenSilk, ... */
    private static final int WOVEN_SILK_META = 3;

    private BackpackRecipes() {}

    public static void register() {
        Item material = GameRegistry.findItem("Forestry", "craftingMaterial");
        if (material == null) {
            GTNHEasyMode.LOG.warn("Woven Silk (Forestry) not found, woven backpack recipes not added");
            return;
        }
        ItemStack wovenSilk = new ItemStack(material, 1, WOVEN_SILK_META);
        int added = 0;
        for (String name : BACKPACKS) {
            Item t1 = GameRegistry.findItem("Forestry", name + "Bag");
            Item t2 = GameRegistry.findItem("Forestry", name + "BagT2");
            if (t1 == null || t2 == null) {
                GTNHEasyMode.LOG.warn("Forestry backpack '{}' not found, woven recipe not added", name);
                continue;
            }
            GameRegistry.addRecipe(
                new UpgradeRecipe(
                    new ItemStack(t2),
                    t1,
                    "WWW",
                    "WBW",
                    "WWW",
                    'W',
                    wovenSilk,
                    'B',
                    new ItemStack(t1, 1, OreDictionary.WILDCARD_VALUE)));
            added++;
        }
        GTNHEasyMode.LOG.info("Added {} woven backpack recipes", added);
    }

    private static class UpgradeRecipe extends ShapedOreRecipe {

        private final Item source;

        UpgradeRecipe(ItemStack result, Item source, Object... recipe) {
            super(result, recipe);
            this.source = source;
        }

        @Override
        public ItemStack getCraftingResult(InventoryCrafting inv) {
            ItemStack result = super.getCraftingResult(inv);
            for (int i = 0; i < inv.getSizeInventory(); i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack != null && stack.getItem() == source) {
                    result.setItemDamage(stack.getItemDamage());
                    if (stack.hasTagCompound()) {
                        result.setTagCompound(
                            (NBTTagCompound) stack.getTagCompound()
                                .copy());
                    }
                    break;
                }
            }
            return result;
        }
    }
}
