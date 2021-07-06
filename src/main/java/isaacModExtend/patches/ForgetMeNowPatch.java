package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import relics.ForgetMeNow;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
public class ForgetMeNowPatch {
    @SpirePatch(
            clz = ForgetMeNow.class,
            method = "onRightClick"
    )
    public static class PatchOnRightClick {
        public static void Postfix(ForgetMeNow relic) {
            try {
                Class<?> cls = Class.forName("actlikeit.savefields.BehindTheScenesActNum");
                Field field = cls.getDeclaredField("bc");
                field.setAccessible(true);
                Object bc = field.get(null);
                field = cls.getDeclaredField("actNum");
                field.setAccessible(true);
                int actNum = (Integer) field.get(bc);
                field.set(bc, --actNum);
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
