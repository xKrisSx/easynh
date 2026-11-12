package pl.notkris.gtnheasymode;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.maps.FuelBackend;
import gregtech.api.util.GTRecipe;

/**
 * Divides the duration of GregTech recipes by {@link Config#recipeSpeedMultiplier}.
 * <p>
 * Can be called multiple times (load complete, server start); every recipe object is only ever modified once, so
 * recipes registered late get picked up without double-dipping the ones already processed.
 */
public final class RecipeSpeedup {

    private static final Set<Object> PROCESSED = Collections.newSetFromMap(new IdentityHashMap<>());

    private RecipeSpeedup() {}

    public static synchronized void apply(String phase) {
        double mult = Config.recipeSpeedMultiplier;
        if (mult <= 1.0) return;

        Set<String> blacklist = new java.util.HashSet<>();
        for (String s : Config.recipeMapBlacklist) {
            if (s != null && !s.trim()
                .isEmpty()) blacklist.add(
                    s.trim()
                        .toLowerCase(Locale.ROOT));
        }

        int changed = 0;
        Map<String, Integer> perMap = new TreeMap<>();
        // sorted for readable logs
        for (Map.Entry<String, RecipeMap<?>> entry : new TreeMap<>(RecipeMap.ALL_RECIPE_MAPS).entrySet()) {
            String name = entry.getKey();
            RecipeMap<?> map = entry.getValue();
            boolean skip = blacklist.contains(name.toLowerCase(Locale.ROOT)) || map.getBackend() instanceof FuelBackend;
            if (Config.logRecipeMaps && PROCESSED.add(map)) {
                GTNHEasyMode.LOG.info("Recipe map {} -> {}", name, skip ? "skipped" : "sped up");
            }
            if (skip) continue;

            for (GTRecipe recipe : map.getAllRecipes()) {
                if (recipe.mDuration <= 0 || recipe.mEUt < 0) continue;
                if (!PROCESSED.add(recipe)) continue;
                recipe.mDuration = scale(recipe.mDuration, mult);
                changed++;
                perMap.merge(name, 1, Integer::sum);
            }
        }

        // Assembly line recipes are stored separately from the NEI-facing recipe map.
        for (GTRecipe.RecipeAssemblyLine recipe : GTRecipe.RecipeAssemblyLine.sAssemblylineRecipes) {
            if (!PROCESSED.add(recipe)) continue;
            if (recipe.mDuration > 0) recipe.mDuration = scale(recipe.mDuration, mult);
            if (recipe.mResearchTime > 0) recipe.mResearchTime = scale(recipe.mResearchTime, mult);
            changed++;
            perMap.merge("assembly line (real recipes)", 1, Integer::sum);
        }

        GTNHEasyMode.LOG.info("[{}] Sped up {} GregTech recipes by {}x", phase, changed, mult);
        if (Config.logRecipeMaps) {
            perMap.forEach((map, count) -> GTNHEasyMode.LOG.info("  {}: {}", map, count));
        }
    }

    private static int scale(int duration, double mult) {
        return (int) Math.max(1, Math.ceil(duration / mult));
    }
}
