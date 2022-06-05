package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import relics.PillsDrop;
import rewards.Pill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PillsDropPatch {
    @SpirePatch(
            clz = PillsDrop.class,
            method = "rollEffect"
    )
    public static class PatchRollEffect {
        public static SpireReturn<Void> Prefix(PillsDrop relic) {
            Pill.Color[] colors = Pill.Color.values();
            List<Pill.Color> colorList = new ArrayList<>();
            for (Pill.Color color : colors) {
                colorList.add(color);
            }

            Pill.Effect[] effects = Pill.Effect.values();
            List<Pill.Effect> effectList = new ArrayList<>();
            for (Pill.Effect effect : effects) {
                effectList.add(effect);
            }
            effectList.add(PillPatch.DecRetroVision);

            int size = colorList.size();
            PillsDrop.pillEffect.clear();
            for(int i = 0; i < size; i++) {
                PillsDrop.pillEffect.put(
                        colorList.remove(AbstractDungeon.miscRng.random(colorList.size() - 1)),
                        effectList.remove(AbstractDungeon.miscRng.random(effectList.size() - 1))
                );
            }
            return SpireReturn.Return(null);
        }
    }

    @SpirePatch(
            clz = PillsDrop.class,
            method = "onEquip"
    )
    public static class PatchOnEquip {
        public static SpireReturn<Void> Prefix(PillsDrop relic) {
            PatchRollEffect.Prefix(relic);
            relic.tips.clear();
            relic.tips.add(new PowerTip(relic.name, relic.getUpdatedDescription()));
            return SpireReturn.Return(null);
        }
    }
}
