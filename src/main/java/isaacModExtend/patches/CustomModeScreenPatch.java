package isaacModExtend.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.custom.CustomMod;
import com.megacrit.cardcrawl.screens.custom.CustomModeScreen;
import com.megacrit.cardcrawl.trials.CustomTrial;
import isaacModExtend.daily.mods.Challenge45;

import java.lang.reflect.Field;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class CustomModeScreenPatch {
    @SpirePatch(
            clz = CustomModeScreen.class,
            method = "initializeMods"
    )
    public static class InitializeModsPatch {
        public static void Postfix(CustomModeScreen screen) {
            try {
                Field field = CustomModeScreen.class.getDeclaredField("modList");
                field.setAccessible(true);
                ArrayList<CustomMod> mods = (ArrayList<CustomMod>) field.get(screen);
                mods.add(0, new CustomMod(Challenge45.ID, "b", false));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @SpirePatch(
            clz = CustomModeScreen.class,
            method = "addNonDailyMods"
    )
    public static class AddNonDailyModsPatch {
        public static void Prefix(CustomModeScreen screen, CustomTrial trial, ArrayList<String> modIds) {
            for (String modId : modIds) {
                if (modId.equals(Challenge45.ID)) {
                    trial.addDailyMod(modId);
                    break;
                }
            }
        }
    }
}
