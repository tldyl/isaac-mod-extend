package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import monsters.Monstro;
import powers.ConceitedPower;

public class MonstroPatch {
    @SpirePatch(
            clz = Monstro.class,
            method = "takeTurn"
    )
    public static class PatchTakeTurn {
        public static SpireReturn<Void> Prefix(Monstro monstro) {
            if (monstro.nextMove == 1) {
                AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, monstro, new ConceitedPower(AbstractDungeon.player, 1)));
                AbstractDungeon.actionManager.addToBottom(new RollMoveAction(monstro));
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}
