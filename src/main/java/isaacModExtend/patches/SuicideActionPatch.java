package isaacModExtend.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.SuicideAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import isaacModExtend.powers.PhantomPower;

public class SuicideActionPatch {
    @SpirePatch(
            clz = SuicideAction.class,
            method = "update"
    )
    public static class PatchUpdate {
        public static SpireReturn<Void> Prefix(SuicideAction action) {
            if (((AbstractMonster) ReflectionHacks.getPrivate(action, SuicideAction.class, "m")).hasPower(PhantomPower.POWER_ID)) {
                action.isDone = true;
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}
