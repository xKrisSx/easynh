package pl.notkris.gtnheasymode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import pl.notkris.gtnheasymode.tinkers.TinkersCompat;

@Mod(
    modid = GTNHEasyMode.MODID,
    version = Tags.VERSION,
    name = "GTNH Easy Mode",
    acceptedMinecraftVersions = "[1.7.10]",
    dependencies = "required-after:gregtech")
public class GTNHEasyMode {

    public static final String MODID = "gtnheasymode";
    public static final Logger LOG = LogManager.getLogger(MODID);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        LOG.info(
            "GTNH Easy Mode {}: recipes {}x faster, ores {}x / small ores {}x (fortune cap {}), tool XP {}x, "
                + "min repair {}%/ingot, lapis stacks {}",
            Tags.VERSION,
            Config.recipeSpeedMultiplier,
            Config.oreDropMultiplier,
            Config.smallOreDropMultiplier,
            Config.gtOreFortuneCap,
            Config.toolXpMultiplier,
            Config.minRepairPercentPerIngot,
            Config.lapisMaxStacks);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance()
            .bus()
            .register(new ClientTickHandler());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (Loader.isModLoaded("TConstruct")) {
            TinkersCompat.postInit();
        }
        if (Config.enableWovenBackpackRecipes && Loader.isModLoaded("Forestry")) {
            BackpackRecipes.register();
        }
    }

    // Recipe durations are changed only after *every* mod finished loading: several mods (e.g. GT++) copy recipes
    // between maps in FMLLoadCompleteEvent, and copies made from already sped-up recipes would get sped up twice.

    @Mod.EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
        RecipeSpeedup.apply("server about to start");
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        // Catches recipes registered during server start; already processed recipes are skipped.
        RecipeSpeedup.apply("server started");
    }

    /** On a client connected to a dedicated server this keeps NEI in sync with the server's durations. */
    public static class ClientTickHandler {

        private boolean done;

        @SubscribeEvent
        public void onClientTick(TickEvent.ClientTickEvent event) {
            if (done || event.phase != TickEvent.Phase.END) return;
            done = true;
            RecipeSpeedup.apply("client first tick");
        }
    }
}
