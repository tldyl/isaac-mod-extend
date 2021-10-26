package isaacModExtend.patches;

import cards.SuicideKing;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rewards.RewardItem;

@SuppressWarnings("unused")
public class SuicideKingPatch {
    static boolean usingSuicideKing = false;

    @SpirePatch(
            clz = SuicideKing.class,
            method = "use"
    )
    public static class PatchUse {
        @SpireInsertPatch(rloc = 2)
        public static SpireReturn<Void> Insert(SuicideKing suicideKing, AbstractPlayer p, AbstractMonster m) {
            p.currentHealth = 0;
            usingSuicideKing = true;
            AbstractDungeon.actionManager.addToBottom(new DamageAction(p, new DamageInfo(p, 0, DamageInfo.DamageType.HP_LOSS), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
            AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
                @Override
                public void update() {
                    usingSuicideKing = false;
                    isDone = true;
                }
            });
            int relic = AbstractDungeon.treasureRng.random(1, 4);
            for (int i = 0; i < relic; i++) {
                AbstractDungeon.getCurrRoom().addRelicToRewards(AbstractDungeon.returnRandomRelicTier());
            }
            int card = AbstractDungeon.treasureRng.random(0, 3);
            for (int i = 0; i < card; i++) {
                AbstractDungeon.getCurrRoom().addCardToRewards();
            }
            int potion = AbstractDungeon.treasureRng.random(0, 3);
            for (int i = 0; i < potion; i++) {
                AbstractDungeon.getCurrRoom().rewards.add(new RewardItem(AbstractDungeon.returnRandomPotion()));
            }
            for (int i = 0; i < 10 - relic - card - potion; i++) {
                AbstractDungeon.getCurrRoom().rewards.add(new RewardItem(AbstractDungeon.treasureRng.random(35, 65)));//addGoldToRewards(AbstractDungeon.treasureRng.random(35, 65));
            }
            return SpireReturn.Return(null);
        }
    }
}
