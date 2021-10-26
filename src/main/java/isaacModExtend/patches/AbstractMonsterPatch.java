package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

@SuppressWarnings("unused")
public class AbstractMonsterPatch {
    @SpirePatch(
            clz = AbstractMonster.class,
            method = "damage"
    )
    public static class PatchDamage {
        @SpireInsertPatch(rloc = 69, localvars = {"damageAmount"})
        public static void Insert(AbstractMonster m, DamageInfo info, @ByRef int[] _damageAmount) {
            if (info.owner != null && info.owner != m) {
                for (AbstractPower power : info.owner.powers) {
                    power.onInflictDamage(info, _damageAmount[0], m);
                }
            }
        }
    }
}
