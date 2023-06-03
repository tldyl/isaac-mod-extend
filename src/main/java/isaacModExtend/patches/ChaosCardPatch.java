package isaacModExtend.patches;

import cards.ChaosCard;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import isaacModExtend.powers.PhantomPower;

public class ChaosCardPatch {
    @SpirePatch(
            clz = ChaosCard.class,
            method = "use"
    )
    public static class PatchUse {
        public static SpireReturn<Void> Prefix(ChaosCard card, AbstractPlayer p, AbstractMonster m) {
            if (m.hasPower(PhantomPower.POWER_ID)) {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(m, p, new StrengthPower(m, 20)));
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}
