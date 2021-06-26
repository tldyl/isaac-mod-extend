package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;

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
        }
    }
}
