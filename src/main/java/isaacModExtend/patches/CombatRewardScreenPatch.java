package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import isaacModExtend.relics.VanishingTwin;

public class CombatRewardScreenPatch {
    @SpirePatch(
            clz = CombatRewardScreen.class,
            method = "setupItemReward"
    )
    public static class PatchSetupItemReward {
        @SpireInsertPatch(rloc = 29)
        public static void Insert(CombatRewardScreen screen) {
            if (AbstractDungeon.player.hasRelic(VanishingTwin.ID) && ((VanishingTwin)AbstractDungeon.player.getRelic(VanishingTwin.ID)).triggered && AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss && AbstractDungeon.actNum < 3) {
                screen.rewards.add(new RewardItem());
                screen.rewards.add(new RewardItem(AbstractDungeon.returnRandomRelic(AbstractRelic.RelicTier.BOSS)));
            }
        }
    }
}
