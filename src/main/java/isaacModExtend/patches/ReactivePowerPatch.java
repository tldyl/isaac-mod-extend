package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.ReactivePower;

public class ReactivePowerPatch {
    @SpirePatch(
            clz = ReactivePower.class,
            method = "onAttacked",
            paramtypez = {
                    DamageInfo.class,
                    int.class
            }
    )
    public static class PatchOnAttacked {
        public static SpireReturn<Integer> Prefix(ReactivePower power, DamageInfo info, int damageAmount) {
            if (info.owner != AbstractDungeon.player) {
                return SpireReturn.Return(damageAmount);
            }
            return SpireReturn.Continue();
        }
    }
}
