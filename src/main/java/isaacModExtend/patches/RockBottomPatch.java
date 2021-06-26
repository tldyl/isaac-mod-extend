package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.FocusPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import isaacModExtend.relics.RockBottom;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
public class RockBottomPatch {
    @SpirePatch(
            clz = StrengthPower.class,
            method = "reducePower"
    )
    @SpirePatch(
            clz = DexterityPower.class,
            method = "reducePower"
    )
    @SpirePatch(
            clz = FocusPower.class,
            method = "reducePower"
    )
    public static class PatchReducePower {
        public static SpireReturn<Void> Prefix(AbstractPower power, int reduceAmount) {
            if (power.owner instanceof AbstractPlayer && AbstractDungeon.player.hasRelic(RockBottom.ID)) {
                AbstractDungeon.player.getRelic(RockBottom.ID).flash();
                AbstractDungeon.actionManager.addToTop(new TextAboveCreatureAction(AbstractDungeon.player, ApplyPowerAction.TEXT[1]));
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = RemoveSpecificPowerAction.class,
            method = "update"
    )
    public static class PatchUpdate {
        @SpireInsertPatch(rloc = 1)
        public static SpireReturn<Void> Insert(RemoveSpecificPowerAction action) {
            try {
                Field field = RemoveSpecificPowerAction.class.getDeclaredField("powerToRemove");
                field.setAccessible(true);
                String powerToRemove = (String) field.get(action);
                field = RemoveSpecificPowerAction.class.getDeclaredField("powerInstance");
                field.setAccessible(true);
                AbstractPower power = (AbstractPower) field.get(action);
                if (powerToRemove != null) {
                    power = action.target.getPower(powerToRemove);
                }
                if (power != null && power.owner instanceof AbstractPlayer && AbstractDungeon.player.hasRelic(RockBottom.ID)) {
                    if (power instanceof StrengthPower || power instanceof DexterityPower || power instanceof FocusPower) {
                        AbstractDungeon.player.getRelic(RockBottom.ID).flash();
                        AbstractDungeon.actionManager.addToTop(new TextAboveCreatureAction(AbstractDungeon.player, ApplyPowerAction.TEXT[1]));
                        action.isDone = true;
                        return SpireReturn.Return(null);
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return SpireReturn.Continue();
        }
    }
}
