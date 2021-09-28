package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import relics.Void;

@SuppressWarnings("unused")
public class VoidPatch {
    @SpirePatch(
            clz = Void.class,
            method = "onVictory"
    )
    public static class PatchOnVictory {
        public static void Postfix(Void relic) {
            for (AbstractRelic relic1 : relic.relicList) {
                relic1.onVictory();
            }
        }
    }
}
