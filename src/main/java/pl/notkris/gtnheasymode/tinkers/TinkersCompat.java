package pl.notkris.gtnheasymode.tinkers;

import pl.notkris.gtnheasymode.Config;
import pl.notkris.gtnheasymode.GTNHEasyMode;
import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.modifiers.tools.ModLapis;
import tconstruct.tools.TinkerTools;

/** Only class-loaded when Tinkers' Construct is present. */
public final class TinkersCompat {

    private TinkersCompat() {}

    public static void postInit() {
        if (Config.lapisMaxStacks > 1) replaceLapis();
    }

    private static void replaceLapis() {
        ModLapis original = TinkerTools.modLapis;
        if (original == null) {
            GTNHEasyMode.LOG.warn("TinkerTools.modLapis is null, cannot make Lapis stackable");
            return;
        }
        ModLapisStacked stacked = new ModLapisStacked(original, Config.lapisMaxStacks);
        // TActiveOmniMod calls TinkerTools.modLapis.midStreamModify directly, so the static field must change too.
        TinkerTools.modLapis = stacked;
        ModifyBuilder.instance.itemModifiers.replaceAll(m -> m == original ? stacked : m);
        GTNHEasyMode.LOG.info(
            "Lapis modifier stackable {}x (max {} lapis, Fortune/Looting up to {})",
            Config.lapisMaxStacks,
            ModLapisStacked.STACK * Config.lapisMaxStacks,
            3 * Config.lapisMaxStacks);
    }
}
