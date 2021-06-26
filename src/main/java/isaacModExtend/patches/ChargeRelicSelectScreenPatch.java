package isaacModExtend.patches;

import basemod.abstracts.CustomRelic;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import mymod.IsaacMod;
import screen.ChargeRelicSelectScreen;
import screen.RelicSelectScreen;
import utils.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class ChargeRelicSelectScreenPatch {
    @SpirePatch(
            clz = ChargeRelicSelectScreen.class,
            method = "obtainRandomRelics"
    )
    public static class PatchObtainRandomRelics {
        public static SpireReturn<Void> Prefix(ChargeRelicSelectScreen screen, int num) {
            try {
                String[] rnd = Utils.getRandomRelics(IsaacMod.relics.size(), num);
                Method method = IsaacMod.class.getDeclaredMethod("getRelic", String.class);
                method.setAccessible(true);
                Field field = RelicSelectScreen.class.getDeclaredField("relics");
                field.setAccessible(true);
                for (String relic : rnd) {
                    CustomRelic rndRelic = (CustomRelic) method.invoke(null, relic);
                    ((ArrayList<AbstractRelic>)field.get(screen)).add(rndRelic.makeCopy());
                }
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | NoSuchFieldException var9) {
                var9.printStackTrace();
            }
            return SpireReturn.Return();
        }
    }
}
