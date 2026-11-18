package pl.notkris.gtnheasymode.mixins;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

@LateMixin
public class LateMixinLoader implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.gtnheasymode.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        List<String> mixins = new ArrayList<>();
        if (loadedMods.contains("gregtech")) {
            mixins.add("OreAdapterMixin");
            mixins.add("OreManagerMixin");
            mixins.add("OreFortuneCapMixin");
        }
        if (loadedMods.contains("TConstruct")) {
            mixins.add("ModToolRepairMixin");
        }
        if (loadedMods.contains("TGregworks")) {
            mixins.add("ModTGregRepairMixin");
        }
        if (loadedMods.contains("betterquesting") && loadedMods.contains("TConstruct")) {
            mixins.add("QuestCommandDefaultsMixin");
        }
        if (loadedMods.contains("IguanaTweaksTConstruct")) {
            mixins.add("LevelingLogicMixin");
        }
        return mixins;
    }
}
