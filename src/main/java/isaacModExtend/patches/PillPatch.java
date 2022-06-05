package isaacModExtend.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import isaacModExtend.IsaacModExtend;
import isaacModExtend.effects.RetroVisionEffect;
import relics.PillsDrop;
import rewards.Pill;

public class PillPatch {
    @SpireEnum
    public static Pill.Effect DecRetroVision;

    @SpirePatch(
            clz = Pill.class,
            method = "claimReward"
    )
    public static class PatchClaimReward {
        public static SpireReturn<Boolean> Prefix(Pill pill) {
            if (pill.effect == null && ReflectionHacks.getPrivate(pill, Pill.class, "color") != null) {
                pill.effect = PillsDrop.pillEffect.get(ReflectionHacks.getPrivate(pill, Pill.class, "color"));
            }
            if (pill.effect == DecRetroVision) {
                IsaacModExtend.globalEffect.add(new RetroVisionEffect());
                return SpireReturn.Return(true);
            }
            return SpireReturn.Continue();
        }
    }
}
