package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.helpers.AsyncSaver;

@SuppressWarnings("unused")
public class AsyncSaverPatch {
    @SpirePatch(
            clz = AsyncSaver.class,
            method = "save"
    )
    public static class PatchSave {
        @SpireInsertPatch(rloc = 1, localvars = {"enableAsyncSave"})
        public static void Insert(String filepath, String data, @ByRef boolean[] _enableAsyncSave) {
            _enableAsyncSave[0] = false;
        }
    }
}
