package isaacModExtend.patches;

import blood.BloodStorePotion;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

@SuppressWarnings("unused")
public class BloodStorePotionPatch {
    @SpirePatch(
            clz = BloodStorePotion.class,
            method = "update"
    )
    public static class PatchUpdate {
        @SpireInsertPatch(rloc = 21)
        public static void Insert(BloodStorePotion storePotion, float rugY) {
            HushsDoorPatch.PatchUpdate.dealWithDevil = true;
        }
    }
}
