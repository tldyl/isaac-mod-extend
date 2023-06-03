package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import isaacModExtend.powers.PhantomPower;

public class LoseHPActionPatch {
    @SpirePatch(
            clz = LoseHPAction.class,
            method = "update"
    )
    public static class PatchUpdate {
        public static SpireReturn<Void> Prefix(LoseHPAction action) {
            if (action.target == action.source && action.target.hasPower(PhantomPower.POWER_ID)) {
                action.isDone = true;
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}
