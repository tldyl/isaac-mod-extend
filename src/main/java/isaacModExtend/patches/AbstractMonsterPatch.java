package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import isaacModExtend.interfaces.PreSetMoveIntent;
import monsters.abstracrt.AbstractPet;

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

    @SpirePatch(
            clz = AbstractMonster.class,
            method = "applyBackAttack"
    )
    public static class PatchApplyBackAttack {
        public static SpireReturn<Boolean> Prefix(AbstractMonster m) {
            if (m instanceof AbstractPet) {
                return SpireReturn.Return(false);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = AbstractMonster.class,
            method = "setMove",
            paramtypez = {
                    String.class,
                    byte.class,
                    AbstractMonster.Intent.class,
                    int.class,
                    int.class,
                    boolean.class
            }
    )
    public static class PatchSetMove {
        public static boolean preventNextInvoke = false;

        public static SpireReturn<Void> Prefix(AbstractMonster monster, String moveName, byte nextMove, AbstractMonster.Intent intent, int baseDamage, int multiplier, boolean isMultiDamage) {
            if (preventNextInvoke) {
                preventNextInvoke = false;
                return SpireReturn.Return(null);
            }
            for (AbstractPower power : monster.powers) {
                if (power instanceof PreSetMoveIntent) {
                    preventNextInvoke |= ((PreSetMoveIntent) power).receivePreSetMove();
                }
            }
            return SpireReturn.Continue();
        }
    }
}
