package pl.notkris.gtnheasymode.mixins.late;

import java.io.File;

import net.minecraft.command.ICommandSender;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import betterquesting.commands.admin.QuestCommandDefaults;
import pl.notkris.gtnheasymode.quests.EasyQuests;

/**
 * Every way BetterQuesting loads a quest database (world start, version update, /bq_admin default load) goes through
 * these two methods. Injecting before quest progress is restored keeps progress on our quests too.
 */
@Mixin(value = QuestCommandDefaults.class, remap = false)
public abstract class QuestCommandDefaultsMixin {

    @Inject(
        method = "load",
        at = @At(value = "INVOKE", target = "Lbetterquesting/questing/QuestDatabase;clear()V", shift = At.Shift.AFTER))
    private static void gtnheasymode$addQuests(ICommandSender sender, String databaseName, File dataDir,
        boolean loadWorldSettings, CallbackInfo ci) {
        EasyQuests.inject();
    }

    @Inject(
        method = "loadLegacy",
        at = @At(
            value = "INVOKE",
            target = "Lbetterquesting/questing/QuestLineDatabase;readFromNBT(Lnet/minecraft/nbt/NBTTagList;Z)V",
            shift = At.Shift.AFTER))
    private static void gtnheasymode$addQuestsLegacy(ICommandSender sender, String databaseName, File legacyFile,
        boolean loadWorldSettings, CallbackInfo ci) {
        EasyQuests.inject();
    }
}
