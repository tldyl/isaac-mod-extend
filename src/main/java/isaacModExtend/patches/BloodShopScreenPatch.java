package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import screen.BloodShopScreen;

@SuppressWarnings("unused")
public class BloodShopScreenPatch {
    @SpirePatch(
            clz = BloodShopScreen.class,
            method = "update"
    )
    public static class PatchUpdate {
        @SpireInsertPatch(rloc = 61)
        public static void Insert(BloodShopScreen screen) {
            HushsDoorPatch.PatchUpdate.dealWithDevil = true;
        }
    }

    @SpirePatch(
            clz = BloodShopScreen.class,
            method = "purgeCard"
    )
    public static class PatchPurgeCard {
        public static void Prefix() {
            HushsDoorPatch.PatchUpdate.dealWithDevil = true;
        }
    }
}
