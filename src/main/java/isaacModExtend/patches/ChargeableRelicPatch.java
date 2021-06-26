package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import isaacModExtend.relics.FourPointFiveVolt;
import relics.abstracrt.ChargeableRelic;

@SuppressWarnings("unused")
public class ChargeableRelicPatch {
    @SpirePatch(
            clz = ChargeableRelic.class,
            method = "onVictory"
    )
    public static class PatchOnVictory {
        public static SpireReturn<Void> Prefix(ChargeableRelic relic) {
            return AbstractDungeon.player.hasRelic(FourPointFiveVolt.ID) ? SpireReturn.Return() : SpireReturn.Continue();
        }
    }
}
