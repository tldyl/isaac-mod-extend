package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.powers.AbstractPower;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
public class ReducePowerActionPatch {
    @SpirePatch(
            clz = ReducePowerAction.class,
            method = "update"
    )
    public static class PatchUpdate {
        public static void Prefix(ReducePowerAction action) {
            try {
                Field field;
                field = AbstractGameAction.class.getDeclaredField("duration");
                field.setAccessible(true);
                float duration = (float) field.get(action);
                field = AbstractGameAction.class.getDeclaredField("startDuration");
                field.setAccessible(true);
                float startDuration = (float) field.get(action);
                if (duration == startDuration) {
                    field = ReducePowerAction.class.getDeclaredField("powerID");
                    field.setAccessible(true);
                    String powerID = (String) field.get(action);
                    AbstractPower reduceMe = action.target.getPower(powerID);
                    if (reduceMe != null) {
                        if (action.amount >= reduceMe.amount) {
                            reduceMe.reducePower(action.amount);
                            reduceMe.updateDescription();
                        }
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
