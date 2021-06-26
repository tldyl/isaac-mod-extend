package isaacModExtend.patches;

import blood.BloodStoreRelic;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;

@SuppressWarnings("unused")
public class BloodStoreRelicPatch {
    @SpirePatch(
            clz = BloodStoreRelic.class,
            method = "update"
    )
    public static class PatchUpdate {
        @SpireInsertPatch(rloc = 23)
        public static void Insert(BloodStoreRelic bloodStoreRelic, float rugY) {
            HushsDoorPatch.PatchUpdate.dealWithDevil = true;
        }
    }
}
