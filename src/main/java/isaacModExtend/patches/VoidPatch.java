package isaacModExtend.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import isaacModExtend.IsaacModExtend;
import relics.Void;

@SuppressWarnings("unused")
public class VoidPatch {
    @SpirePatch(
            clz = Void.class,
            method = "onVictory"
    )
    public static class PatchOnVictory {
        public static void Postfix(Void relic) {
            for (AbstractRelic relic1 : relic.relicList) {
                relic1.onVictory();
            }
        }
    }

    @SpirePatch(
            clz = Void.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class PatchConstructor {
        public static void Postfix(Void relic) {
            relic.maxCharge = 4;
        }
    }

    @SpirePatch(
            clz = Void.class,
            method = "getUpdatedDescription"
    )
    public static class PatchGetUpdatedDescription {
        public static SpireReturn<String> Prefix(Void relic) {
            int strength = ReflectionHacks.getPrivate(relic, Void.class, "strength");
            int dexterity = ReflectionHacks.getPrivate(relic, Void.class, "dexterity");
            int focus = ReflectionHacks.getPrivate(relic, Void.class, "focus");
            String[] DESCRIPTIONS = CardCrawlGame.languagePack.getRelicStrings(IsaacModExtend.makeID("Void")).DESCRIPTIONS;
            if (strength == 0 && dexterity == 0 && focus == 0) {
                return SpireReturn.Return(DESCRIPTIONS[0]);
            } else {
                String result = DESCRIPTIONS[0];
                if (strength != 0) {
                    result = result + DESCRIPTIONS[1] + strength + DESCRIPTIONS[4];
                }

                if (dexterity != 0) {
                    result = result + DESCRIPTIONS[2] + dexterity + DESCRIPTIONS[4];
                }

                if (focus != 0) {
                    result = result + DESCRIPTIONS[3] + focus;
                }

                return SpireReturn.Return(relic.description = result);
            }
        }
    }
}
