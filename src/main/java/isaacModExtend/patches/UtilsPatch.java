package isaacModExtend.patches;

import basemod.abstracts.CustomRelic;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import isaacModExtend.relics.BookOfVirtues;
import mymod.IsaacMod;
import utils.Utils;

import java.util.List;

@SuppressWarnings("unused")
public class UtilsPatch {
    @SpirePatch(
            clz = Utils.class,
            method = "areBookworm"
    )
    public static class PatchAreBookworm {
        @SpireInsertPatch(rloc = 1, localvars = {"set"})
        public static void Insert(@ByRef(type = "java.util.List") Object[] _set) {
            List<String> set = (List<String>) _set[0];
            set.add(BookOfVirtues.ID);
        }
    }

    @SpirePatch(
            clz = Utils.class,
            method = "getRandomRelicRng",
            paramtypez = {}
    )
    public static class PatchGetRandomRelicRng {
        public static SpireReturn<CustomRelic> Prefix() {
            String[] rnd = Utils.getRandomRelicsRng(IsaacMod.relics.size(), 1);
            if (rnd.length > 0) {
                String relic = rnd[0];
                AbstractRelic relic1 = IsaacModPatch.PatchGetRelic.Prefix(relic).get();
                Utils.removeFromPool(relic1);
                Utils.removeRelicString(relic);
                return SpireReturn.Return((CustomRelic) relic1);
            }
            return SpireReturn.Return(null);
        }
    }
}
