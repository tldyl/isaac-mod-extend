package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import powers.ConceitedPower;

public class ConceitedPowerPatch {
    @SpirePatch(
            clz = ConceitedPower.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class PatchConstructor {
        public static void Postfix(ConceitedPower power, AbstractCreature owner, int bladeAmt) {
            power.type = AbstractPower.PowerType.DEBUFF;
        }
    }

    @SpirePatch(
            clz = ConceitedPower.class,
            method = "atStartOfTurn"
    )
    public static class PatchAtStartOfTurn {
        public static void Prefix(ConceitedPower power) {
            AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(power.owner, power.owner, power, 1));
        }
    }
}
