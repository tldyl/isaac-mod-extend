package isaacModExtend.patches;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import isaacModExtend.monsters.pet.SoulWisp;
import isaacModExtend.relics.BookOfVirtues;

@SuppressWarnings("unused")
public class SaveAndContinuePatch {
    @SpirePatch(
            clz = SaveAndContinue.class,
            method = "deleteSave"
    )
    public static class PatchDeleteSave {
        public static void Prefix(AbstractPlayer p) {
            HushsDoorPatch.PatchUpdate.chanceToAngel = 50;
            HushsDoorPatch.PatchUpdate.dealWithDevil = false;
            for (SoulWisp wisp : BookOfVirtues.soulWisps) {
                BaseMod.unsubscribe(wisp);
                wisp.dispose();
            }
            for (int i=0;i<SoulWisp.wispAmount;i++) {
                SoulWisp.wispAlive[i] = false;
            }
            BookOfVirtues.soulWisps.clear();
        }
    }
}
