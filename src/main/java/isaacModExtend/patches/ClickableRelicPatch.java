package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import isaacModExtend.interfaces.PreRightClickRelicSubscriber;
import relics.abstracrt.ClickableRelic;

public class ClickableRelicPatch {
    @SpirePatch(
            clz = ClickableRelic.class,
            method = "update"
    )
    public static class PatchUpdate {
        @SpireInsertPatch(rloc = 12)
        public static void Insert(ClickableRelic relic) {
            for (AbstractRelic relic1 : AbstractDungeon.player.relics) {
                if (relic1 instanceof PreRightClickRelicSubscriber) {
                    ((PreRightClickRelicSubscriber) relic1).preRightClickRelic(relic);
                }
            }
        }
    }
}
