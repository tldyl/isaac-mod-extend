package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import isaacModExtend.relics.BookOfVirtues;
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
}
